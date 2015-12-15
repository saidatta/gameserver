package org.softwarewolf.gameserver.base.domain.helper;

import org.softwarewolf.gameserver.base.domain.Folio;

public class FolioCreator {
	private Folio folio;
	private String allTags;
	private String selectedTags;
	
	public FolioCreator() {
		folio = new Folio();
	}
	
	public Folio getFolio() {
		 return folio;
	}

	public void setFolio(Folio folio) {
		this.folio = folio;
	}
	
	public String getAllTags() {
		return allTags;
	}
	
	public void setAllTags(String allTags) {
		this.allTags = allTags;
	}

	public String getSelectedTags() {
		return selectedTags;
	}
	
	public void setSelectedTags(String selectedTags) {
		this.selectedTags = selectedTags;
	}
	
}
