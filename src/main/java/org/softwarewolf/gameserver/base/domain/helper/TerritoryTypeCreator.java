package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.GameDataType;
import org.softwarewolf.gameserver.base.domain.TerritoryType;

public class TerritoryTypeCreator {
	public TerritoryType territoryType;
	public String campaignId;
	String addGameDataTypeId;
	String removeGameDataTypeId;
	public List<GameDataType> gameDataTypesInCampaign;
	public List<GameDataType> otherGameDataTypes;
	public String forwardingUrl;
	
	public TerritoryTypeCreator() {
		territoryType = new TerritoryType();
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public TerritoryType getTerritoryType() {
		return territoryType;
	}
	
	public void setTerritoryType(TerritoryType territoryType) {
		this.territoryType = territoryType;
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
	
	public String getForwardingUrl() {
		return forwardingUrl;
	}
	
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
	
}
