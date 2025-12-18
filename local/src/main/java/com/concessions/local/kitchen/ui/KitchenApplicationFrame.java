package com.concessions.local.kitchen.ui;

import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.base.ui.AbstractFrame;
import com.concessions.local.kitchen.model.KitchenApplicationModel;

import jakarta.annotation.PostConstruct;

@Component
public class KitchenApplicationFrame extends AbstractFrame implements PropertyChangeListener {

	@Autowired
	protected KitchenApplicationModel model;
	
	public KitchenApplicationFrame() {
		super("Concessions Management Kitchen");
	}

	@PostConstruct
	protected void initializeUI ()
	{
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(800, 400);
		model.addPropertyChangeListener(this);
		
		super.initializeUI();
	}
}
