package org.softwarewolf.gameserver.base.domain.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.base.domain.Campaign;

public class CampaignCreator {
	public List<UserListItem> gamemasters;
	public String ownerId;
	public String ownerName;
	public Campaign campaign;
	
	public List<UserListItem> getGamemasters() {
		return gamemasters;
	}
	
	public void setGamemasters(List<UserListItem> gamemasters) {
		this.gamemasters = gamemasters;
	}
	
	public void addGameMaster(Map<String, String> gamemaster) {
		if (gamemasters == null) {
			gamemasters = new ArrayList<UserListItem>();
		}
		String key = (String)gamemaster.keySet().toArray()[0];
		String value = gamemaster.get(key);
		for (UserListItem item : gamemasters) {
			if (item.id.equals(key)) {
				item.displayName = value;
				return;
			}
		}

		UserListItem newItem = new UserListItem(key, value);
		gamemasters.add(newItem);
	}
	
	public void removeGamemaster(String gamemasterUserId) {
		if (gamemasters != null) {
			gamemasters.remove(gamemasterUserId);
		}
	}
	
	public String getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public String getOwnerName() {
		return ownerName;
	}
	
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	
	public Campaign getCampaign() {
		return campaign;
	}
	
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
}
