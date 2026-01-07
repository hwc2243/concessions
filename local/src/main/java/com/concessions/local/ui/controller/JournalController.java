package com.concessions.local.ui.controller;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.client.model.Journal;
import com.concessions.client.model.Order;
import com.concessions.client.rest.JournalRestClient;
import com.concessions.client.service.JournalService;
import com.concessions.client.service.OrderService;
import com.concessions.client.service.ServiceException;
import com.concessions.common.dto.JournalSummaryDTO;
import com.concessions.dto.StatusType;
import com.concessions.local.network.dto.JournalDTO;
import com.concessions.local.network.dto.JournalMapper;
import com.concessions.local.network.dto.OrderDTO;
import com.concessions.local.server.model.ServerApplicationModel;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.JournalNotifier;
import com.concessions.local.ui.controller.OrderController.OrderListener;
import com.concessions.local.ui.view.JournalOrdersPanel;
import com.concessions.local.ui.view.JournalPanel;
import com.concessions.local.util.ListChunker;
import com.concessions.local.util.MoneyUtil;

import jakarta.annotation.PostConstruct;

@Component
public class JournalController {

	private static final Logger logger = LoggerFactory.getLogger(JournalController.class);

	private static final int BATCH_SIZE = 10;
	
	@Autowired
	protected ApplicationFrame applicationFrame;

	@Autowired
	protected JournalNotifier journalNotifier;
	
	@Autowired
	protected JournalService journalService;
	
	@Autowired
	protected JournalRestClient journalRestClient;

	@Autowired
	protected OrderController orderController;
	
	@Autowired
	protected OrderService orderService;

	@Autowired
	protected ServerApplicationModel model;

	public JournalController() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	protected void postConstruct () {
		// HWC TODO this should be part of orderController
		journalNotifier.addJournalListener(orderController);
	}
	
	public void initialize () {
		try {
			List<Journal> journals = journalService.findAllByStatus(StatusType.OPEN);
			if (journals.size() == 1) {
				open(JournalMapper.toDto(journals.iterator().next()));
			}
		} catch (ServiceException ex) {
			ex.printStackTrace();
		}
	}
	
	public void view() {
		try {
			List<JournalDTO> journals = journalService.findAll()
					.stream()
					.map(JournalMapper::toDto)
					.collect(Collectors.toList());
			JournalPanel journalPanel = new JournalPanel(this, journals);
			journalNotifier.addJournalListener(journalPanel);
			applicationFrame.setMainContent(journalPanel);
		} catch (ServiceException ex) {
			ex.printStackTrace();
			return;
		}
	}

	public void viewOrders(JournalDTO journal) {
		List<Order> orders = orderService.findByJournalId(journal.getId());
		if (orders.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No orders in journal.", "Information",
					JOptionPane.INFORMATION_MESSAGE);
		} else {
			JournalOrdersPanel ordersPanel = new JournalOrdersPanel(orders);

			// 2. Create the JDialog
			JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(applicationFrame),
					"Orders for Journal: " + journal.getId(), Dialog.ModalityType.APPLICATION_MODAL); // Ensure modality

			dialog.setLayout(new BorderLayout());

			// 3. Create the OK button panel
			JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			JButton okButton = new JButton("OK");
			okButton.addActionListener(e -> dialog.dispose());
			buttonPanel.add(okButton);

			// Set default action to OK button on Enter key
			dialog.getRootPane().setDefaultButton(okButton);

			// 4. Assemble the dialog
			dialog.add(ordersPanel, BorderLayout.CENTER);
			dialog.add(buttonPanel, BorderLayout.SOUTH);

			// 5. Configure and show the dialog
			dialog.pack();
			// Ensure a reasonable minimum size, otherwise the table might look too cramped
			dialog.setMinimumSize(new Dimension(500, 300));
			dialog.setLocationRelativeTo(applicationFrame);
			dialog.setVisible(true);
		}
	}

	public void change(JournalDTO journal) {
		journalNotifier.notifyJournalChanged(journal);
	}
	
	public void close() {
		JournalDTO journal = model.getJournal();
		try {
			if (journal == null) {
				List<Journal> journals = journalService.findNotClosedJournals();
				if (journals.isEmpty()) {
					JOptionPane.showMessageDialog(null, "No open journal found.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (journals.size() > 1) {
					JOptionPane.showMessageDialog(null, "Multiple incomplete journals found.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				journal = JournalMapper.toDto(journals.get(0));
			}
			close(journal);
		} catch (ServiceException ex) {
			ex.printStackTrace();
		}

	}

	public void close (JournalDTO journal) {
		try {
			journal.setStatus(StatusType.CLOSE);
			journal.setEndTs(LocalDateTime.now());
			journalService.update(JournalMapper.fromDto(journal));
			journalNotifier.notifyJournalClosed(journal);
			
			// HWC TODO add logic to check if network is connected and if so attempt sync
			sync(journal);
		} catch (ServiceException ex) {
			ex.printStackTrace();
		}
	}

	public void open() {
		JournalDTO journal = model.getJournal();
		try {
			if (journal == null) {
				List<Journal> journals = journalService.findNotClosedJournals();
				if (journals.isEmpty()) {
					JOptionPane.showMessageDialog(null, "No open journal found.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (journals.size() > 1) {
					JOptionPane.showMessageDialog(null, "Multiple incomplete journals found.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				journal = JournalMapper.toDto(journals.get(0));
			}
			if (journal.getStatus() == null) {
				journal.setStatus(StatusType.OPEN);
			}
			switch (journal.getStatus()) {
			case NEW:
			case SUSPEND:
				journal.setStatus(StatusType.OPEN);
				journalService.update(JournalMapper.fromDto(journal));
			case OPEN:
				open(journal);
				break;
			case CLOSE:
				JOptionPane.showMessageDialog(null, "The current journal has been closed and can not be reopned.",
						"Error", JOptionPane.ERROR_MESSAGE);
				break;
			}
		} catch (ServiceException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to locate journal to open", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void open (JournalDTO journal) {
		logger.info("Opening journal : " + journal.getId());
		try {
			Journal journalEntity = journalService.recalcJournal(JournalMapper.fromDto(journal));
			journalEntity.setStatus(StatusType.OPEN);
			journalEntity = journalService.update(journalEntity);
			journal = JournalMapper.toDto(journalEntity);
			model.setJournal(journal);
			journalNotifier.notifyJournalOpened(journal);
		} catch (ServiceException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to open journal", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void start() {
		try {
			// HWC TODO
			// this should be find new, opened or suspended journals
			List<Journal> incompleteJournals = journalService.findNotClosedJournals();
			if (!incompleteJournals.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Can not start a new journal until existing journal is closed.",
						"Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			JournalDTO journal = JournalMapper.toDto(journalService.newInstance());
			logger.info("Creating journal : " + journal.getId());
			model.setJournal(journal);
			journalNotifier.notifyJournalStarted(journal);
		} catch (ServiceException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to start new journal", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void suspend() {
		JournalDTO journal = model.getJournal();
		try {
			if (journal == null) {
				List<Journal> journals = journalService.findNotClosedJournals();
				if (journals.isEmpty()) {
					JOptionPane.showMessageDialog(null, "No open journal found.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				} else if (journals.size() > 1) {
					JOptionPane.showMessageDialog(null, "Multiple open journals found.", "Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				journal = JournalMapper.toDto(journals.get(0));
			}
			switch (journal.getStatus()) {
			case NEW:
			case OPEN:
				suspend(journal);
				break;
			case SUSPEND:
				JOptionPane.showMessageDialog(null, "Journal is already suspended.", "Information",
						JOptionPane.INFORMATION_MESSAGE);
				return;

			case CLOSE:
				JOptionPane.showMessageDialog(null, "Journal is already closed.", "Error", JOptionPane.ERROR_MESSAGE);
				return;

			}
		} catch (ServiceException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to search for open journal", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void suspend (JournalDTO journal) {
		logger.info("Suspending journal : " + journal.getId());
		journal.setStatus(StatusType.SUSPEND);
		try {
			journalService.update(JournalMapper.fromDto(journal));
			journalNotifier.notifyJournalSuspended(journal);
		} catch (ServiceException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to suspend journal", "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	public void sync (JournalDTO journal) {
		if (journal.getStatus() != StatusType.CLOSE ) {
			JOptionPane.showMessageDialog(null, "Journal has to be closed before syncing", "Error",
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		// add logic to handle syncing to the server
		try {
			List<Order> orders = orderService.findByJournalId(journal.getId());
			// HWC TODO should this be done in parallel, everything is setup to support it
			JournalSummaryDTO summary = orders.stream()
				    .collect(
				        // Supplier: Create a new OrderSummary object (The initial container)
				        JournalSummaryDTO::new, 
				        
				        // Accumulator: Add one Order's data to the container
				        (s, order) -> { 
				            s.incrementCount();
				            s.addToTotal(order.getOrderTotal());
				        }, 
				        
				        // Combiner: Merge two containers (Used if running in parallel)
				        JournalSummaryDTO::merge
				    );
			
			Journal journalEntity = JournalMapper.fromDto(journal);
			CompletableFuture<Journal> futureJournal = journalRestClient.create(journalEntity);
			Journal createdJournal = futureJournal.join();
			
			List<List<Order>> batches = ListChunker.chunkList(orders, BATCH_SIZE);
			
			CompletableFuture<Journal> futureChunkedOrders;
			for (List<Order> batch :  batches) {
				futureChunkedOrders = journalRestClient.syncOrders(createdJournal, batch);
				futureChunkedOrders.join();
			}
			
			futureJournal = journalRestClient.reconcile(journalEntity, summary);
			journalEntity = futureJournal.get();
			journalService.update(journalEntity);
			journalNotifier.notifyJournalSynced(journal);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

}
