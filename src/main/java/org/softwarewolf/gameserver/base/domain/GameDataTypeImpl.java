package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class GameDataTypeImpl implements GameDataType, Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String name;
	private String description;
	private List<String> campaignList;
	
	public GameDataTypeImpl() {
		campaignList = new ArrayList<>();
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
		
	public List<String> getCampaignList() {
		return campaignList;
	}
	
	public void setCampaignList(List<String> campaignList) {
		if (campaignList == null) {
			this.campaignList = new ArrayList<>();
		} else { 
			this.campaignList = campaignList;
		}
	}
	
	public void addCampaign(String campaignId) {
		if (!campaignList.contains(campaignId)) {
			campaignList.add(campaignId);
		}
	}
	
	public void removeCampaign(String campaignId) {
		if (campaignId != null) {
			campaignList.remove(campaignId);
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((campaignList == null) ? 0 : campaignList.hashCode());
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
		if (campaignList == null) {
			if (other.campaignList != null)
				return false;
		} else if (!campaignList.equals(other.campaignList))
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
