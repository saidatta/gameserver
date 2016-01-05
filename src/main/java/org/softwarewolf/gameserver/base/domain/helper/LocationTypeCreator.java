package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.GameDataType;
import org.softwarewolf.gameserver.base.domain.LocationType;

public class LocationTypeCreator {
	public LocationType locationType;
	public String campaignId;
	String addGameDataTypeId;
	String removeGameDataTypeId;
	public List<GameDataType> gameDataTypesInCampaign;
//	public List<GameDataType> otherGameDataTypes;
	public String forwardingUrl;
	
	public LocationTypeCreator() {
		locationType = new LocationType();
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public LocationType getLocationType() {
		return locationType;
	}
	
	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
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

//	public List<GameDataType> getOtherGameDataTypes() {
//		return otherGameDataTypes;
//	}
//	
//	public void setOtherGameDataTypes(List<GameDataType> otherGameDataTypes) {
//		this.otherGameDataTypes = otherGameDataTypes;
//	}
//	
	public String getForwardingUrl() {
		return forwardingUrl;
	}
	
	public void setForwardingUrl(String forwardingUrl) {
		this.forwardingUrl = forwardingUrl;
	}
	
}
