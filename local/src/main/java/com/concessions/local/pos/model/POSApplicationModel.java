package com.concessions.local.pos.model;

import org.springframework.stereotype.Component;

import com.concessions.dto.MenuDTO;
import com.concessions.local.base.model.AbstractClientModel;
import com.concessions.local.base.model.POSModel;
import com.concessions.local.network.dto.JournalDTO;

@Component
public class POSApplicationModel extends AbstractClientModel implements POSModel {

	public static final String MENU = "menu";
	public static final String JOURNAL = "journal";
	
	protected MenuDTO menu = null;
	protected JournalDTO journal = null;
	
	public POSApplicationModel() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public MenuDTO getMenu() {
		return menu;
	}

	@Override
	public void setMenu(MenuDTO menu) {
		MenuDTO oldMenu = this.menu;
		this.menu = menu;
		this.firePropertyChange(MENU, oldMenu, menu);
	}

	@Override
	public JournalDTO getJournal() {
		return journal;
	}

	@Override
	public void setJournal(JournalDTO journal) {
		JournalDTO oldJournal = this.journal;
		this.journal = journal;
		this.firePropertyChange(JOURNAL, oldJournal, journal);
	}
}
