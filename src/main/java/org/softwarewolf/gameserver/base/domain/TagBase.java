package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TagBase implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	protected String id;
	protected String name;
	protected String campaignId;
	protected String gameDataTypeId;
	@Transient
	protected String gameDataTypeName;

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
	
	public String getGameDataTypeId() {
		return gameDataTypeId;
	}
	public void setGameDataTypeId(String gameDataTypeId) {
		this.gameDataTypeId = gameDataTypeId;
	}
		
	public String getGameDataTypeName() {
		return gameDataTypeName;
	}
	public void setGameDataTypeName(String gameDataTypeName) {
		this.gameDataTypeName = gameDataTypeName;
	}
		
	
	public ObjectTag createTag(String parentId) {
		return new ObjectTag(this.getClass().getSimpleName(), this.getId(), this.getName(), 
				this.getCampaignId(), this.getGameDataTypeId(), this.getGameDataTypeName(), parentId);
	}
}
