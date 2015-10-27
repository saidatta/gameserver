package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class GameDataImpl implements GameData, Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String name;
	private String description;
	private String gameDataTypeId;
	@Transient
	private String gameDataTypeName;
	private String campaignId;
	private String parentId;
	@Transient
	private String parentName;
	private List<String> childrenIdList;
	@Transient
	private String displayName;

	public GameDataImpl() {
		childrenIdList = new ArrayList<>();
	}
	
	public GameDataImpl(String name, String campaignId) {
		this.name = name;
		this.campaignId = campaignId;
		childrenIdList = new ArrayList<>();
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
		
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getParentId() {
		return parentId;
	}
	
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public boolean hasParentId() {
		if (parentId == null) {
			return false;
		}
		return true;
	}
	
	public String getParentName() {
		return parentName;
	}
	
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
	public List<String> getChildrenIdList() {
		return childrenIdList;
	}
	
	public void setChildrenIdList(List<String> childrenIdList) {
		this.childrenIdList = childrenIdList;
	}

	public boolean hasChildren() {
		return childrenIdList != null && childrenIdList.size() > 0;
	}
	
	public void addChildId(String childId) {
		childrenIdList.add(childId);
	}
	
	public void removeChildId(String childId) {
		childrenIdList.remove(childId);
	}

	public String getDisplayName() {
		if (getName() == null || getGameDataTypeName() == null) {
			return "";
		}
		StringBuilder displayNameBuilder = new StringBuilder(getName()).append("(").append(getGameDataTypeName()).append(")");
		return displayNameBuilder.toString();
	}
	
}
