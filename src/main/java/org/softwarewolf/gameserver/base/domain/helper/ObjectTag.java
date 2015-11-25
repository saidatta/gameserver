package org.softwarewolf.gameserver.base.domain.helper;

import java.io.Serializable;

public class ObjectTag implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** The name of the class, Organization, Territory, etc. */
	private String className;
	/** The id of the instance of the class */
	private String objectId;
	/** The name of the instance of the class */
	private String tagName;
	private String campaignId;
	
	public ObjectTag() { }
	
	public ObjectTag(String className, String objectId, String tagName, String campaignId) {
		this.className = className;
		this.objectId = objectId;
		this.tagName = tagName;
		this.campaignId = campaignId;
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
}
