package org.softwarewolf.gameserver.base.domain.helper;

import java.util.ArrayList;
import java.util.List;

public class SelectFolioCreator {
	private List<ObjectTag> unselectedTagList;
	private List<ObjectTag> selectedTagList;
	private String addTagId;
	private String addTagClassName;
	private String removeTagId;
	private String removeTagClassName;
	
	public SelectFolioCreator() {
		unselectedTagList = new ArrayList<>();
		selectedTagList = new ArrayList<>();
	}
	
	public List<ObjectTag> getUnselectedTagList() {
		return unselectedTagList;
	}
	public void setUnselectedTagList(List<ObjectTag> unselectedTagList) {
		this.unselectedTagList = unselectedTagList;
	}
	public List<ObjectTag> addToUnselectedTagList(ObjectTag addTag) {
		if (!unselectedTagList.contains(addTag)) {
			unselectedTagList.add(addTag);
		}
		return unselectedTagList;
	}
	public List<ObjectTag> removeFromUnselectedTagList(ObjectTag removeTag) {
		unselectedTagList.remove(removeTag);
		return unselectedTagList;
	}

	public List<ObjectTag> getSelectedTagList() {
		return selectedTagList;
	}
	public void setSelectedTagList(List<ObjectTag> selectedTagList) {
		this.selectedTagList = selectedTagList;
	}
	public List<ObjectTag> addToSelectedTagList(ObjectTag addTag) {
		if (!selectedTagList.contains(addTag)) {
			selectedTagList.add(addTag);
		}
		return selectedTagList;
	}
	public List<ObjectTag> removeFromSelectedTagList(ObjectTag removeTag) {
		selectedTagList.remove(removeTag);
		return selectedTagList;
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
}

