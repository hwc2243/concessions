package com.concessions.local.base;

import java.awt.Desktop;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.springframework.beans.factory.annotation.Autowired;

import com.concessions.local.network.Messenger;
import com.formdev.flatlaf.FlatLightLaf;

public abstract class AbstractApplication {

	@Autowired
	protected Messenger messenger;
	
	public AbstractApplication() {
		// TODO Auto-generated constructor stub
	}

	protected abstract void showAboutDialog (JFrame frame);
	
	protected abstract boolean performQuit ();
	
	protected static void initializeLaF (String shortName)
	{
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			try {
				System.setProperty("apple.awt.application.name", shortName);
				System.setProperty("apple.laf.useScreenMenuBar", "true");
			    UIManager.setLookAndFeel(new FlatLightLaf());
			} catch( Exception ex ) {
			    System.err.println( "Failed to initialize LaF" );
			}
		}
	}
	
	protected void setupDesktopHandler (JFrame ownerFrame) {
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Desktop.Action.APP_ABOUT)) {
                desktop.setAboutHandler(e -> {
                    showAboutDialog(ownerFrame);
                });
            }
            if (desktop.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
                desktop.setQuitHandler(new QuitHandler() {
                    @Override
                    public void handleQuitRequestWith(QuitEvent e, java.awt.desktop.QuitResponse response) {
                    	if (performQuit()) {
                    		response.performQuit();
                    	}
                    	else {
                    		response.cancelQuit();
                    	}
                    }
                });
            }
        }
    }
}
