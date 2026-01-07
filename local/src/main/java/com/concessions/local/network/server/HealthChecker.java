package com.concessions.local.network.server;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.concessions.common.network.HealthCheckManager;
import com.concessions.common.network.Messenger;
import com.concessions.common.network.NetworkConstants;
import com.concessions.common.network.dto.SimpleResponseDTO;
import com.concessions.local.model.Device;
import com.concessions.local.service.DeviceService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
public class HealthChecker implements NetworkConstants {
	private static final Logger logger = LoggerFactory.getLogger(HealthChecker.class);

	@Value("${local.network.healhCheckInterval:60}")
	protected int healthCheckInterval = 60;

	@Autowired
	protected DeviceService deviceService = null;

	@Autowired
	protected Messenger messenger;

	private ScheduledExecutorService healthCheckScheduler;

	public HealthChecker() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void start() throws IOException {
		logger.info("Starting HealthChecker");
		healthCheckScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = Executors.defaultThreadFactory().newThread(r);
			t.setName("HealthChecker");
			t.setDaemon(true); // Allow the application to exit if this is the only remaining thread
			return t;
		});

		healthCheckScheduler.scheduleAtFixedRate(this::deviceHealthChecker, 60, // initial delay in seconds
				healthCheckInterval, // period in seconds
				TimeUnit.SECONDS);
	}
	
	@PreDestroy
	public void shutdown() {
		if (healthCheckScheduler != null) {
			healthCheckScheduler.shutdownNow();
		}
	}
	private void deviceHealthChecker() {
		logger.info("Executing scheduled device health check...");
		try {
			List<Device> localDevices = deviceService.findAll();

			if (localDevices == null || localDevices.isEmpty()) {
				logger.debug("No local devices found to check.");
				return;
			}

			for (Device device : localDevices) {
				String ip = device.getDeviceIp();
				Integer port = device.getDevicePort();
				String deviceId = device.getDeviceId();

				if (ip != null && !ip.trim().isEmpty() && port != null && port > 0) {
					logger.debug("Checking health for Device ID: {} at {}:{}", deviceId, ip, port);

					if (!performHealthCheck(device)) {
						device.setDeviceIp(null);
						device.setDevicePort(0);
						deviceService.update(device);
					}
				} else {
					logger.debug("Skipping health check for Device ID: {} - Missing IP/Port.", deviceId);
				}
			}
		} catch (Exception ex) {
			logger.error("Error during device health checker execution: {}", ex.getMessage(), ex);
		}
	}

	private boolean performHealthCheck(Device device) {
		try {
			messenger.sendRequest(device.getDeviceIp(), device.getDevicePort(), HEALTH_SERVICE,
					HEALTH_CHECK_ACTION, "", SimpleResponseDTO.class);
			logger.info("Successful health check for Device ID: {} at {}:{}", device.getDeviceId(), device.getDeviceIp(), device.getDevicePort());
			return true;
		} catch (Exception ex) {
			logger.error("Health check failure for Device ID: {} at {}:{} - {}", device.getDeviceId(),
					device.getDeviceIp(), device.getDevicePort(), ex.getMessage(), ex);
		}
		return false;
	}

}
