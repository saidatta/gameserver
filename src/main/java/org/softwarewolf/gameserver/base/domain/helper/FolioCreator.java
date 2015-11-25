package org.softwarewolf.gameserver.base.domain.helper;

import org.softwarewolf.gameserver.base.domain.Folio;

public class FolioCreator {
	private Folio page;
	
	public FolioCreator() {
		page = new Folio();
	}
	
	public Folio getPage() {
		 return page;
	}

	public void setPage(Folio page) {
		this.page = page;
	}
}
