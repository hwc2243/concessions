package com.concessions.local.pos;

import javax.swing.UIManager;

import com.formdev.flatlaf.FlatLightLaf;

public class POSApplication {

	public POSApplication() {
		// TODO Auto-generated constructor stub
	}

	public void execute ()
	{
	}
	
	public static void main(String[] args) {
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			try {
				System.setProperty("apple.awt.application.name", "Concessions");
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			    UIManager.setLookAndFeel(new FlatLightLaf());
			} catch( Exception ex ) {
			    System.err.println( "Failed to initialize LaF" );
			}
		}
		
		POSApplication application = new POSApplication();
		application.execute();
	}
}
