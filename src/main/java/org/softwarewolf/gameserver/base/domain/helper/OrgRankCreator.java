package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.OrgRank;

public class OrgRankCreator {
	public OrgRank orgRank;
	public List<OrgRank> orgRanksInCampaign;
	public String orgRankTreeJson;
	public String addGameDataTypeId;
	public String removeGameDataTypeId;
	public String forwardingUrl;
	
	public OrgRankCreator() {
		orgRank = new OrgRank();
	}

	public OrgRank getOrgRank() {
		return orgRank;
	}
	
	public void setOrgRank(OrgRank orgRank) {
		this.orgRank = orgRank;
	}

	public List<OrgRank> getOrgRanksInCampaign() {
		return orgRanksInCampaign;
	}
	
	public void setOrgRanksInCampaign(List<OrgRank> orgRanksInCampaign) {
		this.orgRanksInCampaign = orgRanksInCampaign;
	}
	
	public String getOrgRankTreeJson() {
		return orgRankTreeJson;
	}
	
	public void setOrgRankTreeJson(String orgRankTreeJson) {
		this.orgRankTreeJson = orgRankTreeJson;
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
