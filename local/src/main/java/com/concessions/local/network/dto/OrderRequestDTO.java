package com.concessions.local.network.dto;

public class OrderRequestDTO extends AbstractDeviceRequestDTO {

	protected OrderDTO order;
	
	public OrderRequestDTO() {
	}

	public OrderDTO getOrder() {
		return order;
	}

	public void setOrder(OrderDTO order) {
		this.order = order;
	}

}
