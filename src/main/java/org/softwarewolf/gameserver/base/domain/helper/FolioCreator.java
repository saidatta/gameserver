package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Folio;

public class FolioCreator {
	private Folio folio;
	private String unassignedTags;
	private String selectedTags;
	private String removeTag;
	List<FolioDescriptor> folioDescriptorList;
	
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
	
	public List<FolioDescriptor> getFolioDescriptorList() {
		return folioDescriptorList;
	}
	public void setFolioDescriptorList(List<FolioDescriptor> folioDescriptorList) {
		this.folioDescriptorList = folioDescriptorList;
	}
}
