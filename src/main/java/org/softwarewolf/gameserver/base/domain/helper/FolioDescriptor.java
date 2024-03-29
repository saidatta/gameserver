package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

/**
 * Folios can be assembled into collections for multiple purposes. This abbreviated object
 * is for use when creating those collections. A folio knows how to create a descriptor.
 * @author tmanchester
 *
 */
public class FolioDescriptor {
	String folioTitle;
	String folioId;
	List<ObjectTag> tags;
	
	public FolioDescriptor(String folioTitle, String folioId, List<ObjectTag> tags) {
		this.folioTitle = folioTitle;
		this.folioId = folioId;
		this.tags = tags;
	}
	
	public String getFolioTitle() {
		return folioTitle;
	}
	
	public void setFolioTitle(String folioTitle) {
		this.folioTitle = folioTitle;
	}
	
	public String getFolioId() {
		return folioId;
	}
	
	public void setFolioId(String folioId) {
		this.folioId = folioId;
	}
	
	public List<ObjectTag> getTags() {
		return tags;
	}
	
	public void setTags(List<ObjectTag> tags) {
		this.tags = tags;
	}
	
	public void addTag(ObjectTag tag) {
		if (!tags.contains(tag)) {
			tags.add(tag);
		}
	}
}