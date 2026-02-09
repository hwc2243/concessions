package com.concessions.common.network.dto;

import java.util.List;

import com.concessions.dto.OrderDTO;

public class OrderQueueGetAllResponseDTO {

	protected List<OrderDTO> orders;
	
	public OrderQueueGetAllResponseDTO() {
		// TODO Auto-generated constructor stub
	}

	public List<OrderDTO> getOrders() {
		return orders;
	}

	public void setOrders(List<OrderDTO> orders) {
		this.orders = orders;
	}
}
