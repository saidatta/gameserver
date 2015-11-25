package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TagBase implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	protected String id;
	protected String name;
	protected String campaignId;

	public TagBase() {
	}
	
	public TagBase(String name, String campaignId) {
		this.name = name;
		this.campaignId = campaignId;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public ObjectTag createTag() {
		return new ObjectTag(this.getClass().getSimpleName(), this.getId(), this.getName(), this.getCampaignId());
	}
}
