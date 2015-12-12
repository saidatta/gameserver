package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Folio;

public class FolioCreator {
	private Folio page;
	private List<ObjectTag> allTags;
	
	public FolioCreator() {
		page = new Folio();
	}
	
	public Folio getPage() {
		 return page;
	}

	public void setPage(Folio page) {
		this.page = page;
	}
	
	public List<ObjectTag> getAllTags() {
		return allTags;
	}
	
	public void setAllTags(List<ObjectTag> allTags) {
		this.allTags = allTags;
	}
}
