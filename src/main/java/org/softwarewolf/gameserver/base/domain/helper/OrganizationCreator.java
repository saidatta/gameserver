package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.GameDataType;
import org.softwarewolf.gameserver.base.domain.Organization;

public class OrganizationCreator {
	public Organization organization;
	public List<Organization> organizationsInCampaign;
	public String organizationTreeJson;
	public String addGameDataTypeId;
	public String removeGameDataTypeId;
	public String forwardingUrl;
	public List<GameDataType> gameDataTypesInCampaign;
	public List<GameDataType> otherGameDataTypes;
	
	public OrganizationCreator() {
		organization = new Organization();
	}

	public Organization getOrganization() {
		return organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public List<Organization> getOrganizationsInCampaign() {
		return organizationsInCampaign;
	}
	
	public void setOrganizationsInCampaign(List<Organization> organizationsInCampaign) {
		this.organizationsInCampaign = organizationsInCampaign;
	}
	
	public String getOrganizationTreeJson() {
		return organizationTreeJson;
	}
	
	public void setOrganizationTreeJson(String organizationTreeJson) {
		this.organizationTreeJson = organizationTreeJson;
	}
	
	public String getAddGameDataTypeId() {
		return addGameDataTypeId;
	}
	
	public void setAddGameDataTypeId(String addGameDataTypeId) {
		this.addGameDataTypeId = addGameDataTypeId;
	}
	
	public String getRemoveGameDataTypeId() {
		return removeGameDataTypeId;
	}
	
	public void setRemoveGameDataTypeId(String removeGameDataTypeId) {
		this.removeGameDataTypeId = removeGameDataTypeId;
	}

	public String getForwardingUrl() {
		return forwardingUrl;
	}
	
	public List<GameDataType> getGameDataTypesInCampaign() {
		return gameDataTypesInCampaign;
	}
	
	public void setGameDataTypesInCampaign(List<GameDataType> gameDataTypesInCampaign) {
		this.gameDataTypesInCampaign = gameDataTypesInCampaign;
	}

	public List<GameDataType> getOtherGameDataTypes() {
		return otherGameDataTypes;
	}
	
	public void setOtherGameDataTypes(List<GameDataType> otherGameDataTypes) {
		this.otherGameDataTypes = otherGameDataTypes;
	}
	
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
}
