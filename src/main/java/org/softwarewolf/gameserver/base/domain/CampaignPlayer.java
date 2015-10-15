package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class CampaignPlayer implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String campaignId;
	private String playerName;
	private String playerId;
	
	public CampaignPlayer() {}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
		
	public String getPlayerId() {
		return playerId;
	}
	
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	
}
