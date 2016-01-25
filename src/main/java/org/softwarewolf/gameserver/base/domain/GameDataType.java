package org.softwarewolf.gameserver.base.domain;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;

public interface GameDataType {
	public String getId();	
	public void setId(String id);
	
	public String getName();	
	public void setName(String name);
	
	public String getDescription();
	public void setDescription(String description);
	
	public String getCampaignId();	
	public void setCampaignId(String campaignId);
	
	public ObjectTag createTag(String parentId);
	
	@Override
	public int hashCode();
	@Override
	public boolean equals(Object obj);
	
}
