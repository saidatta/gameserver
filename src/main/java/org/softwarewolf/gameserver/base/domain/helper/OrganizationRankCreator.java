package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.GameDataType;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;

public class OrganizationRankCreator {
	public OrganizationRank organizationRank;
	public List<OrganizationRank> organizationRanksInCampaign;
	public String organizationRankTreeJson;
	public String addOrganizationRankId;
	public String removeOrganizationRankId;
	public String forwardingUrl;
	public List<OrganizationRank> otherOrganizationRanks;
	
	public OrganizationRankCreator() {
		organizationRank = new OrganizationRank();
	}

	public OrganizationRank getOrganizationRank() {
		return organizationRank;
	}
	
	public void setOrganizationRank(OrganizationRank organizationRank) {
		this.organizationRank = organizationRank;
	}

	public List<OrganizationRank> getOrganizationRanksInCampaign() {
		return organizationRanksInCampaign;
	}
	
	public void setOrganizationRanksInCampaign(List<OrganizationRank> organizationRanksInCampaign) {
		this.organizationRanksInCampaign = organizationRanksInCampaign;
	}
	
	public String getOrganizationRankTreeJson() {
		return organizationRankTreeJson;
	}
	
	public void setOrganizationRankTreeJson(String organizationRankTreeJson) {
		this.organizationRankTreeJson = organizationRankTreeJson;
	}
	
	public String getAddOrganizationRankId() {
		return addOrganizationRankId;
	}
	
	public void setAddOrganizationRankId(String addOrganizationRankId) {
		this.addOrganizationRankId = addOrganizationRankId;
	}
	
	public String getRemoveOrganizationRankId() {
		return removeOrganizationRankId;
	}
	
	public void setRemoveOrganizationRankId(String removeOrganizationRankId) {
		this.removeOrganizationRankId = removeOrganizationRankId;
	}

	public String getForwardingUrl() {
		return forwardingUrl;
	}

	public List<OrganizationRank> getOtherOrganizationRanks() {
		return otherOrganizationRanks;
	}
	
	public void setOtherOrganizationRanks(List<OrganizationRank> otherOrganizationRanks) {
		this.otherOrganizationRanks = otherOrganizationRanks;
	}
	
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
}
