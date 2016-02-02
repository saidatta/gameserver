package org.softwarewolf.gameserver.base.domain.helper;

public class SelectFolioCreator {
	private String unselectedTags;
	private String selectedTags;
	private String addTagId;
	private String addTagClassName;
	private String removeTagId;
	private String removeTagClassName;
	private String folioDescriptorList;
	
	public SelectFolioCreator() {
	}
	
	public String getUnselectedTags() {
		return unselectedTags;
	}
	public void setUnselectedTags(String unselectedTags) {
		this.unselectedTags = unselectedTags;
	}
	
	public String getSelectedTags() {
		return selectedTags;
	}
	public void setSelectedTags(String selectedTags) {
		this.selectedTags = selectedTags;
	}
	
	public String getAddTagId() {
		return addTagId;
	}
	public void setAddTagId(String addTagId) {
		this.addTagId = addTagId;
	}
	
	public String getAddTagClassName() {
		return addTagClassName;
	}
	public void setAddTagClassName(String addTagClassName) {
		this.addTagClassName = addTagClassName;
	}
	
	public String getRemoveTagId() {
		return removeTagId;
	}
	public void setRemoveTagId(String removeTagId) {
		this.removeTagId = removeTagId;
	}
	
	public String getRemoveTagClassName() {
		return removeTagClassName;
	}
	public void setRemoveTagClassName(String removeTagClassName) {
		this.removeTagClassName = removeTagClassName;
	}

	public String getFolioDescriptorList() {
		return folioDescriptorList;
	}
	public void setFolioDescriptorList(String folioDescriptorList) {
		this.folioDescriptorList = folioDescriptorList;
	}
}

