package com.concessions.local.ui.action;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.concessions.client.model.Journal;
import com.concessions.client.model.StatusType;
import com.concessions.local.ui.ApplicationFrame;
import com.concessions.local.ui.controller.JournalController;
import com.concessions.local.ui.model.ApplicationModel;

@Component
public class ExitAction extends AbstractAction {
	
	private static final String SUSPEND_BUTTON = "Suspend";
    private static final String CLOSE_BUTTON = "Close";
    private static final String CANCEL_BUTTON = "Cancel";
    private static final Object[] OPTIONS = { CANCEL_BUTTON, CLOSE_BUTTON, SUSPEND_BUTTON };
    
	@Autowired
	protected ApplicationFrame frame;
	
	@Autowired
	protected ApplicationModel model;
	
	@Autowired
	protected JournalController journalController;

	public ExitAction() {
		super("Exit");
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Journal journal = model.getJournal();
		if (journal != null && journal.getStatus() == StatusType.OPEN) {
			int result = JOptionPane.showOptionDialog(
		            frame,
		            "There is an open journal, do you want to Suspend or Close the journal?",
		            "Confirm Exit",
		            JOptionPane.YES_NO_CANCEL_OPTION, // This specifies the internal structure for dialog types
		            JOptionPane.QUESTION_MESSAGE,
		            null, // icon
		            OPTIONS, // The array of custom button labels
		            OPTIONS[2] // The default button (Cancel)
		        );

			String selectedOption = (result >= 0 && result < OPTIONS.length) ? OPTIONS[result].toString() : CANCEL_BUTTON;

			switch (selectedOption) {
				case CLOSE_BUTTON:
					journalController.close(journal);
					System.exit(0);
					break;
				case SUSPEND_BUTTON:
					journalController.suspend(journal);
					System.exit(0);
					break;
				case CANCEL_BUTTON:
				default:
					return;
			}
		}
		else {
			System.exit(0);
		}
	}
}
