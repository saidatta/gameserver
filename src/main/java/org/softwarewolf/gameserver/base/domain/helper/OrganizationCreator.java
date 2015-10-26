package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationType;

public class OrganizationCreator {
	public Organization organization;
	public List<Organization> organizationsInCampaign;
	public List<OrganizationType> organizationTypesInCampaign;
	public String organizationTreeJson;
	public String addGameDataTypeId;
	public String removeGameDataTypeId;
	public String forwardingUrl;
	
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
	
	public List<OrganizationType> getOrganizationTypesInCampaign() {
		return organizationTypesInCampaign;
	}
	
	public void setOrganizationTypesInCampaign(List<OrganizationType> organizationTypesInCampaign) {
		this.organizationTypesInCampaign = organizationTypesInCampaign;
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
	
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
}
