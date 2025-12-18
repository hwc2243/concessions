package com.concessions.local.ui;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.concessions.local.network.dto.JournalDTO;

@Component
public class JournalNotifier {

	private List<JournalListener> listeners = new ArrayList<>();

	public JournalNotifier() {
		// TODO Auto-generated constructor stub
	}
	public void addJournalListener(JournalListener listener) {
		listeners.add(listener);
	}

	public void removeJournalListener(JournalListener listener) {
		listeners.remove(listener);
	}

	public void notifyJournalClosed(JournalDTO journal) {
		listeners.stream().forEach(listener -> listener.journalClosed(journal));
	}

	public void notifyJournalChanged(JournalDTO journal) {
		listeners.stream().forEach(listener -> listener.journalChanged(journal));
	}

	public void notifyJournalOpened(JournalDTO journal) {
		listeners.stream().forEach(listener -> listener.journalOpened(journal));
	}

	public void notifyJournalStarted(JournalDTO journal) {
		listeners.stream().forEach(listener -> listener.journalStarted(journal));
	}

	public void notifyJournalSuspended(JournalDTO journal) {
		listeners.stream().forEach(listener -> listener.journalSuspended(journal));
	}
	
	public void notifyJournalSynced (JournalDTO journal) {
		listeners.stream().forEach(listener -> listener.journalSynced(journal));
	}

	public interface JournalListener {
		void journalClosed(JournalDTO journal);
		
		void journalChanged(JournalDTO journal);

		void journalOpened(JournalDTO journal);

		void journalStarted(JournalDTO journal);

		void journalSuspended(JournalDTO journal);
		
		void journalSynced(JournalDTO journal);
	}

}
