package com.concessions.local.base.model;

import com.concessions.local.network.dto.JournalDTO;
import com.concessions.local.network.dto.MenuDTO;

public interface POSModel {
	public MenuDTO getMenu ();
	
	public void setMenu (MenuDTO menu);
	
	public JournalDTO getJournal ();
	
	public void setJournal (JournalDTO journal);
}
