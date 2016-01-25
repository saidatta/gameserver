package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;

import org.apache.commons.lang.NotImplementedException;
import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class GameDataTypeImpl implements GameDataType, Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String name;
	private String description;
	private String campaignId;
	
	public GameDataTypeImpl() {
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
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
		
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public ObjectTag createTag() {
		throw new NotImplementedException();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((campaignId == null) ? 0 : campaignId.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameDataTypeImpl other = (GameDataTypeImpl) obj;
		if (campaignId == null) {
			if (other.campaignId != null)
				return false;
		} else if (!campaignId.equals(other.campaignId))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	

}
