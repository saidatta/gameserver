package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OrganizationRank implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String name;
	private String description;
	private String organizationId;
	private String organizationName;
	private String campaignId;
	private String parentId;
	private List<String> childrenIdList;
	
	public OrganizationRank() {
		childrenIdList = new ArrayList<>();
	}
	
	public OrganizationRank(String name, String campaignId) {
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
		
	public String getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
		
	public String getOrganizationName() {
		return organizationName;
	}

	public void setOrganizationName(String organizationName) {
		this.organizationName = organizationName;
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
	
	public List<String> getChildrenIdList() {
		return childrenIdList;
	}
	
	public void setChildrenIdList(List<String> childrenIdList) {
		this.childrenIdList = childrenIdList;
	}

	public void addChildId(String childId) {
		if (!childrenIdList.contains(childId)) {
			childrenIdList.add(childId);
		}
	}

	public void removeChildId(String childId) {
		childrenIdList.remove(childId);
	}
	
	public boolean hasChildren() {
		return (childrenIdList != null && childrenIdList.size() > 0);
	}
	
}
