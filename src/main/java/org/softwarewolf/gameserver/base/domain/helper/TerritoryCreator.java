package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Territory;

public class TerritoryCreator {
	public Territory territory;
	public List<Territory> territoriesInCampaign;
	public String territoryTreeJson;
	public String addGameDataTypeId;
	public String removeGameDataTypeId;
	public String forwardingUrl;
	
	public TerritoryCreator() {
		territory = new Territory();
	}

	public Territory getTerritory() {
		return territory;
	}
	
	public void setTerritory(Territory territory) {
		this.territory = territory;
	}

	public List<Territory> getTerritoriesInCampaign() {
		return territoriesInCampaign;
	}
	
	public void setTerritoriesInCampaign(List<Territory> territoriesInCampaign) {
		this.territoriesInCampaign = territoriesInCampaign;
	}
	
	public String getTerritoryTreeJson() {
		return territoryTreeJson;
	}
	
	public void setTerritoryTreeJson(String territoryTreeJson) {
		this.territoryTreeJson = territoryTreeJson;
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
