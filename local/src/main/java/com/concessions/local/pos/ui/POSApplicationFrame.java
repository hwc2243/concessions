package com.concessions.local.pos.ui;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.local.base.ui.AbstractFrame;
import com.concessions.local.pos.model.POSApplicationModel;

import jakarta.annotation.PostConstruct;

@Component
public class POSApplicationFrame extends AbstractFrame implements PropertyChangeListener {

	@Autowired
	protected POSApplicationModel model;
	
	public POSApplicationFrame() {
		super("Concessions Management POS");
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
