package org.softwarewolf.gameserver.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.base.domain.GameDataType;
import org.softwarewolf.gameserver.base.domain.LocationType;
import org.softwarewolf.gameserver.base.domain.helper.LocationTypeCreator;
import org.softwarewolf.gameserver.base.repository.LocationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocationTypeService {
	@Autowired
	protected LocationTypeRepository locationTypeRepository;
	
	public List<LocationType> getAllLocationTypes() {
		List<LocationType> locationTypeList = locationTypeRepository.findAll();
		if (locationTypeList == null) {
			locationTypeList = new ArrayList<>();
		}
		return locationTypeList;
	}
	
	public List<LocationType> getLocationTypesInCampaign(String campaignId) {
		List<LocationType> locationTypesInCampaign = locationTypeRepository.findAllByKeyValue("campaignId", campaignId);
		return locationTypesInCampaign;
	}

	public LocationType findOne(String id) {
		LocationType locationType = locationTypeRepository.findOne(id);
		return locationType;
	}
	
	
	public void saveLocationType(LocationType locationType) {
		if (locationType.getId() == null) {
			LocationType existingLocationType = locationTypeRepository.findOneByNameAndCampaignId(locationType.getName(), 
					locationType.getCampaignId());
			if (existingLocationType != null) {
				throw new IllegalArgumentException("Location type " + locationType.getName() + " already exists");
			}
		}
		locationTypeRepository.save(locationType);
	}	
	
	public void delete(String locationTypeId) {
		if (locationTypeId != null) {
			locationTypeRepository.delete(locationTypeId);
		}
	}	
	
	public void delete(LocationType locationType) {
		if (locationType != null) {
			locationTypeRepository.delete(locationType);
		}
	}	
	
	public Map<String, String> getLocationTypeNameMap() {
		Map<String, String> ltNameMap = new HashMap<>();
		List<LocationType> locationTypeList = locationTypeRepository.findAll();
		for (LocationType locationType : locationTypeList) {
			ltNameMap.put(locationType.getId(), locationType.getName());
		}
		
		return ltNameMap;
	}
	
	public void initLocationTypeCreator(String locationTypeId, LocationTypeCreator locationTypeCreator, 
			String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			locationTypeCreator.setForwardingUrl(forwardingUrl);
		}

		LocationType locationType = null;
		if (locationTypeId != null) {
			locationType = locationTypeRepository.findOne(locationTypeId);
		} else {
			locationType = new LocationType();
		}
		locationTypeCreator.setLocationType(locationType);
		List<LocationType> locationTypesInCampaign = null;
		List<GameDataType> gameDataTypesInCampaign = new ArrayList<>();
		List<GameDataType> otherGameDataTypes = new ArrayList<>();
		List<LocationType> allLocationTypes = locationTypeRepository.findAll();
		if (allLocationTypes != null) {
			if (campaignId != null) {
				List<String> campaignIds = new ArrayList<>();
				campaignIds.add(campaignId);
				locationTypesInCampaign = locationTypeRepository.findAllByKeyValue("campaignId", campaignId);
				LocationType addNewLT = new LocationType();
				addNewLT.setId("0");
				addNewLT.setName("Add new location type");
				gameDataTypesInCampaign.add(addNewLT);
				for (LocationType lt : locationTypesInCampaign) {
					gameDataTypesInCampaign.add(lt);
				}
				locationTypeCreator.setGameDataTypesInCampaign(gameDataTypesInCampaign);
			} else {
				locationTypeCreator.setGameDataTypesInCampaign(new ArrayList<>());
			}
			
			List<GameDataType> allGameDataTypes = new ArrayList<>();
			for (LocationType tt : allLocationTypes) {
				allGameDataTypes.add(tt);
			}
			if (locationTypeCreator.getGameDataTypesInCampaign().isEmpty()) {
				locationTypeCreator.setOtherGameDataTypes(allGameDataTypes);
			} else {
				for (LocationType tType : allLocationTypes) {
					if (!locationTypesInCampaign.contains(tType)) {
						otherGameDataTypes.add(tType);
					}
				}
			}
		}
		locationTypeCreator.setCampaignId(campaignId);
		locationTypeCreator.setGameDataTypesInCampaign(gameDataTypesInCampaign);
		locationTypeCreator.setOtherGameDataTypes(otherGameDataTypes);
	}

	
}
