package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Campaign;

public class SelectCampaignHelper {
	public List<Campaign> allCampaigns;
	public String selectedCampaignId;
	
	public List<Campaign> getAllCampaigns() {
		return allCampaigns;
	}
	
	public void setAllCampaigns(List<Campaign> allCampaigns) {
		this.allCampaigns = allCampaigns;
	}
	
	public String getSelectedCampaignId() {
		return selectedCampaignId;
	}
	
	public void setSelectedCampaignId(String selectedCampaignId) {
		this.selectedCampaignId = selectedCampaignId;
	}
	
	public String getSelectedCampaignName() {
		String campaignName = null;
		if (selectedCampaignId != null) {
			for (Campaign campaign : allCampaigns) {
				if (campaign.getId().equals(selectedCampaignId)) {
					campaignName = campaign.getName();
					break;
				}
			}
		}
		return campaignName;
	}

}
