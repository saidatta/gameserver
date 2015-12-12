package org.softwarewolf.gameserver.base.domain;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;

public interface GameDataType {
	public String getId();	
	public void setId(String id);
	
	public String getName();	
	public void setName(String name);
	
	public String getDescription();
	public void setDescription(String description);
	
	public List<String> getCampaignList();	
	public void setCampaignList(List<String> campaignList);
	
	public void addCampaign(String campaignId);
	public void removeCampaign(String campaignId);
	
	public ObjectTag createTag(String campaignId);
	
	@Override
	public int hashCode();
	@Override
	public boolean equals(Object obj);
	
}
