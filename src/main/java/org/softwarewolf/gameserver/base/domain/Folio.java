package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class is for holding the html pages created by users of the app and
 * associating them with meta data.
 * @author tmanchester
 */
@Document
public class Folio implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	private String title;
	private String campaignId;
	private String content;
	private List<ObjectTag> tags;
	
	public Folio() { }
	public Folio(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public List<ObjectTag> getTags() {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		return tags;
	}
	
	public void setTags(List<ObjectTag> tags) {
		this.tags = tags;
	}
	
	public void addTag(String className, String tagId) {
		
	}
	
	public void addTag(ObjectTag tag) {
		if (tags == null) {
			tags = new ArrayList<>();
		}
		boolean foundTag = false;
		for (ObjectTag currentTag : tags) {
			if (currentTag.getObjectId().equals(tag.getObjectId())) {
				currentTag.setClassName(tag.getClassName());
				currentTag.setTagName(tag.getTagName());
				foundTag = true;
				break;
			}
		}
		if (!foundTag) {
			tags.add(tag);
		}
	}

	public void removeTag(String objectId) {
		if (tags != null) {
			for (ObjectTag currentTag : tags) {
				if (currentTag.getObjectId().equals(objectId)) {
					tags.remove(currentTag);
					break;
				}
			}
		}
	}

}
