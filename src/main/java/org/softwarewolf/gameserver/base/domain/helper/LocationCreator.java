package org.softwarewolf.gameserver.base.domain.helper;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Location;
import org.softwarewolf.gameserver.base.domain.TerritoryType;

public class LocationCreator {
	public Location location;
	public List<Location> locationsInCampaign;
	public List<TerritoryType> territoryTypesInCampaign;
	public String locationTreeJson;
	public String addGameDataTypeId;
	public String removeGameDataTypeId;
	public String forwardingUrl;
	
	public LocationCreator() {
		location = new Location();
	}

	public Location getLocation() {
		return location;
	}
	
	public void setLocation(Location location) {
		this.location = location;
	}

	public List<Location> getLocationsInCampaign() {
		return locationsInCampaign;
	}
	
	public void setLocationsInCampaign(List<Location> locationsInCampaign) {
		this.locationsInCampaign = locationsInCampaign;
	}
	
	public List<TerritoryType> getTerritoryTypesInCampaign() {
		return territoryTypesInCampaign;
	}
	
	public void setTerritoryTypesInCampaign(List<TerritoryType> territoryTypesInCampaign) {
		this.territoryTypesInCampaign = territoryTypesInCampaign;
	}
	
	public String getLocationTreeJson() {
		return locationTreeJson;
	}
	
	public void setLocationTreeJson(String locationTreeJson) {
		this.locationTreeJson = locationTreeJson;
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
