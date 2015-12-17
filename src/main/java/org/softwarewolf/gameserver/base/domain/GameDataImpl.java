package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class GameDataImpl extends TagBase implements GameData, Serializable {
	private static final long serialVersionUID = 1L;

	protected String description;
	protected String parentId;
	@Transient
	protected String parentName;
	protected List<String> childrenIdList;
	@Transient
	protected String displayName;

	public GameDataImpl() {
		childrenIdList = new ArrayList<>();
	}
	
	public GameDataImpl(String name, String campaignId) {
		this.name = name;
		this.campaignId = campaignId;
		childrenIdList = new ArrayList<>();
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
	
	// This usually should not be used
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public ObjectTag getTag() {
		throw new NotImplementedException();
	}
}
