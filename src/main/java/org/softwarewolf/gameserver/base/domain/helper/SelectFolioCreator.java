package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

public class SelectFolioCreator {
	private List<ObjectTag> unselectedTagList;
	private List<ObjectTag> selectedTagList;
	private String removeTag;
	
	public SelectFolioCreator() {
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
	
	public String getRemoveTag() {
		return removeTag;
	}
	public void setRemoveTag(String removeTag) {
		this.removeTag = removeTag;
	}
}

