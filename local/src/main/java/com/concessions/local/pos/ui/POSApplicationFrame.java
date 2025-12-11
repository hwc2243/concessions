package com.concessions.local.pos.ui;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class POSApplicationFrame extends JFrame {

	public POSApplicationFrame() {
		super("Concessions Management POS");
	}

	@PostConstruct
	protected void initializeUI ()
	{
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(800, 400);
	}
}
