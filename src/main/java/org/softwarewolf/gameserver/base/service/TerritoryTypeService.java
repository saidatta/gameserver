package org.softwarewolf.gameserver.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.base.domain.GameDataType;
import org.softwarewolf.gameserver.base.domain.TerritoryType;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryTypeCreator;
import org.softwarewolf.gameserver.base.repository.TerritoryTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TerritoryTypeService {
	@Autowired
	protected TerritoryTypeRepository territoryTypeRepository;
	
	public List<TerritoryType> getAllTerritoryTypes() {
		List<TerritoryType> territoryTypeList = territoryTypeRepository.findAll();
		if (territoryTypeList == null) {
			territoryTypeList = new ArrayList<>();
		}
		return territoryTypeList;
	}
	
	public List<TerritoryType> getTerritoryTypesInCampaign(String campaignId) {
		List<String> campaignIds = new ArrayList<>();
		campaignIds.add(campaignId);
		List<TerritoryType> territoryTypesInCampaign = null;
		territoryTypesInCampaign = territoryTypeRepository.findAllByKeyValues("campaignList", campaignIds.toArray());
		return territoryTypesInCampaign;
	}

	public TerritoryType findOne(String id) {
		TerritoryType territoryType = territoryTypeRepository.findOne(id);
		return territoryType;
	}
	
	
	public void saveTerritoryType(TerritoryType territoryType) {
		if (territoryType.getId() == null) {
			TerritoryType existingTerritoryType = territoryTypeRepository.findOneByName(territoryType.getName());
			if (existingTerritoryType != null) {
				throw new IllegalArgumentException("Territory type " + territoryType.getName() + " already exists");
			}
		}
		territoryTypeRepository.save(territoryType);
	}	
	
	public void delete(String territoryTypeId) {
		if (territoryTypeId != null) {
			territoryTypeRepository.delete(territoryTypeId);
		}
	}	
	
	public void delete(TerritoryType territoryType) {
		if (territoryType != null) {
			territoryTypeRepository.delete(territoryType);
		}
	}	
	
	public Map<String, String> getTerritoryTypeNameMap() {
		Map<String, String> ttNameMap = new HashMap<>();
		List<TerritoryType> territoryTypeList = territoryTypeRepository.findAll();
		for (TerritoryType territoryType : territoryTypeList) {
			ttNameMap.put(territoryType.getId(), territoryType.getName());
		}
		
		return ttNameMap;
	}
	
	public void initTerritoryTypeCreator(String territoryTypeId, TerritoryTypeCreator territoryTypeCreator, 
			String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			territoryTypeCreator.setForwardingUrl(forwardingUrl);
		}

		TerritoryType territoryType = null;
		if (territoryTypeId != null) {
			territoryType = territoryTypeRepository.findOne(territoryTypeId);
		} else {
			territoryType = new TerritoryType();
		}
		territoryTypeCreator.setTerritoryType(territoryType);
		List<TerritoryType> territoryTypesInCampaign = null;
		List<GameDataType> gameDataTypesInCampaign = new ArrayList<>();
		List<GameDataType> otherGameDataTypes = new ArrayList<>();
		List<TerritoryType> allTerritoryTypes = territoryTypeRepository.findAll();
		if (allTerritoryTypes != null) {
			if (campaignId != null) {
				List<String> campaignIds = new ArrayList<>();
				campaignIds.add(campaignId);
				territoryTypesInCampaign = territoryTypeRepository.findAllByKeyValue("campaignList", campaignId);
				TerritoryType addNewTT = new TerritoryType();
				addNewTT.setId("0");
				addNewTT.setName("Add new territory type");
				gameDataTypesInCampaign.add(addNewTT);
				for (TerritoryType tt : territoryTypesInCampaign) {
					gameDataTypesInCampaign.add(tt);
				}
				territoryTypeCreator.setGameDataTypesInCampaign(gameDataTypesInCampaign);
			} else {
				territoryTypeCreator.setGameDataTypesInCampaign(new ArrayList<>());
			}
			
			List<GameDataType> allGameDataTypes = new ArrayList<>();
			for (TerritoryType tt : allTerritoryTypes) {
				allGameDataTypes.add(tt);
			}
			if (territoryTypeCreator.getGameDataTypesInCampaign().isEmpty()) {
				territoryTypeCreator.setOtherGameDataTypes(allGameDataTypes);
			} else {
				for (TerritoryType tType : allTerritoryTypes) {
					if (!territoryTypesInCampaign.contains(tType)) {
						otherGameDataTypes.add(tType);
					}
				}
			}
		}
		territoryTypeCreator.setCampaignId(campaignId);
		territoryTypeCreator.setGameDataTypesInCampaign(gameDataTypesInCampaign);
		territoryTypeCreator.setOtherGameDataTypes(otherGameDataTypes);
	}

	
}
