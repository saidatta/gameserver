package org.softwarewolf.gameserver.base.domain;

import java.util.List;

public interface GameData {
	public String getId();	
	public void setId(String id);
	
	public String getName();	
	public void setName(String name);
	
	public String getDescription();
	public void setDescription(String description);
		
	public String getGameDataTypeId();
	public void setGameDataTypeId(String gameDataTypeId);
		
	public String getGameDataTypeName();
	public void setGameDataTypeName(String gameDataTypeName);
		
	public String getCampaignId();
	
	public void setCampaignId(String campaignId);
	
	public String getParentId();	
	public void setParentId(String parentId);

	public boolean hasParentId();
	
	public List<String> getChildrenIdList();
	
	public void setChildrenIdList(List<String> childrenIdList);

	public void addChildId(String childId);

	public void removeChildId(String childId);	
	
	public boolean hasChildren();
	
	@Override
	public int hashCode();
	@Override
	public boolean equals(Object obj);
	
}
