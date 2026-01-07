package com.concessions.local.base.model;

import com.concessions.dto.MenuDTO;
import com.concessions.local.network.dto.JournalDTO;

public interface POSModel {
	public MenuDTO getMenu ();
	
	public void setMenu (MenuDTO menu);
	
	public JournalDTO getJournal ();
	
	public void setJournal (JournalDTO journal);
}
