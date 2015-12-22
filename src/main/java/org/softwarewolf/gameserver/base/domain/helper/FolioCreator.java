package org.softwarewolf.gameserver.base.domain.helper;

import org.softwarewolf.gameserver.base.domain.Folio;

public class FolioCreator {
	private Folio folio;
	private String unassignedTags;
	private String selectedTags;
	private String removeTag;
	private String addTag;
	
	public FolioCreator() {
		folio = new Folio();
	}
	
	public Folio getFolio() {
		 return folio;
	}
	public void setFolio(Folio folio) {
		this.folio = folio;
	}
	
	public String getUnassignedTags() {
		return unassignedTags;
	}
	public void setUnassignedTags(String unassignedTags) {
		this.unassignedTags = unassignedTags;
	}

	public String getSelectedTags() {
		return selectedTags;
	}
	public void setSelectedTags(String selectedTags) {
		this.selectedTags = selectedTags;
	}
	
	public String getRemoveTag() {
		return removeTag;
	}
	public void setRemoveTag(String removeTag) {
		this.removeTag = removeTag;
	}

	public String getAddTag() {
		return addTag;
	}
	public void setAddTag(String addTag) {
		this.addTag = addTag;
	}
}
