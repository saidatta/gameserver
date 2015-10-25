package org.softwarewolf.gameserver.base.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.softwarewolf.gameserver.base.domain.GameDataType;
import org.softwarewolf.gameserver.base.domain.Territory;
import org.softwarewolf.gameserver.base.domain.TerritoryType;
import org.softwarewolf.gameserver.base.domain.helper.HierarchyJsonBuilder;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryCreator;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryTypeCreator;
import org.softwarewolf.gameserver.base.repository.TerritoryRepository;
import org.softwarewolf.gameserver.base.repository.TerritoryTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

@Service
public class TerritoryService {
	public static final String ROOT = "ROOT";
	private static final Logger logger = Logger.getLogger(TerritoryService.class.getSimpleName());
	
	@Autowired
	protected TerritoryRepository territoryRepository;
	
	@Autowired
	protected TerritoryTypeRepository territoryTypeRepository;
	
	public List<TerritoryType> getAllTerritoryTypes() {
		List<TerritoryType> territoryTypeList = territoryTypeRepository.findAll();
		if (territoryTypeList == null) {
			territoryTypeList = new ArrayList<>();
		}
		return territoryTypeList;
	}
	
	public List<Territory> getAllTerritories() {
		List<Territory> territoryList = territoryRepository.findAll();
		if (territoryList == null) {
			territoryList = new ArrayList<>();
		}
		return territoryList;
	}

	// This needs to be modified to filter based on campaign id
	public List<Territory> getTerritoriesInCampaign() {
		List<Territory> territoryList = territoryRepository.findAll();
		if (territoryList == null) {
			territoryList = new ArrayList<>();
		}
		return territoryList;
	}

	public List<TerritoryType> getTerritoryTypesInCampaign(String campaignId) {
		List<String> campaignIds = new ArrayList<>();
		campaignIds.add(campaignId);
		List<TerritoryType> territoryTypesInCampaign = null;
		territoryTypesInCampaign = territoryTypeRepository.findAllByKeyValues("campaignList", campaignIds.toArray());
		return territoryTypesInCampaign;
	}

	public TerritoryType getTerritoryTypeById(String id) {
		TerritoryType territoryType = territoryTypeRepository.findOne(id);
		return territoryType;
	}
	
	public void initTerritoryCreator(String territoryId, TerritoryCreator territoryCreator, 
			String campaignId, String forwardingUrl) {

		Territory territory = null;
		if (territoryId != null) {
			territory = territoryRepository.findOne(territoryId);
			if (territory == null) {
				throw new RuntimeException("Can not find a territory for id " + territoryId);
			}
		} else {
			territory = new Territory();
			territory.setParentName(ROOT);
			territory.setParentId(ROOT);
		}
		
		initTerritoryCreator(territory, territoryCreator, campaignId, forwardingUrl);
	}
	
	public void initTerritoryCreator(Territory territory, TerritoryCreator territoryCreator, 
			String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			territoryCreator.setForwardingUrl(forwardingUrl);
		}
		try {
			String json = createTerritoryTree(campaignId);
			territoryCreator.setTerritoryTreeJson(json); 
		} catch (Exception e) {
			logger.fine(e.getMessage());
		}

		if (campaignId != null && territory.getCampaignId() == null) {
			territory.setCampaignId(campaignId);
		}
		territoryCreator.setTerritory(territory);

		if (territory.getParentId() != null) {
			Territory parent = findOneTerritory(territory.getParentId());
			if (parent != null && parent.getName() != "ROOT") {
				territory.setParentName(parent.getName());
			} 
		} else {
			territory.setParentName("ROOT");
		}
		List<Territory> territories = territoryRepository.findAllByKeyValue("campaignId", campaignId);
		territoryCreator.setTerritoriesInCampaign(territories);
		
		List<TerritoryType> territoryTypesInCampaign = territoryTypeRepository.findAllByKeyValue("campaignList", campaignId);
		TerritoryType addNew = new TerritoryType();
		addNew.setId("0");
		addNew.setName("Add new territory type");
		territoryTypesInCampaign.add(0, addNew);
		territoryCreator.setTerritoryTypesInCampaign(territoryTypesInCampaign);
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
	
	public void saveTerritory(Territory territory) {
		if (territory.getId() == null) {
			Territory existingTerritory = territoryRepository.findOneByName(territory.getName());
			if (existingTerritory != null) {
				throw new IllegalArgumentException("Territory " + territory.getName() + " already exists");
			}
		}
		String parentTerritoryId = territory.getParentId();
		if (parentTerritoryId.equals(ROOT)) {
			territory.setParentId(null);
		}
		territory = territoryRepository.save(territory);
		if (territory.getParentId() != null) {
			Territory parentTerritory = territoryRepository.findOne(parentTerritoryId);
			if (parentTerritory == null) {
				throw new IllegalArgumentException("Could not find parent territory");
			}
			if (!canSetTerritoryParent(territory.getId(), parentTerritory.getId())) {
				throw new IllegalArgumentException("Illegal parent territory");
			}
			parentTerritory.addChildId(territory.getId());
			territoryRepository.save(parentTerritory);
		}
	}
	
	public boolean canSetTerritoryParent(String territoryId, String parentId) {
		return !territoryId.equals(parentId);
	}

	/**
	 * The current territory can only appear as a child once 
	 * @param territory
	 * @param childId
	 * @return
	 */
	public boolean canSetTerritoryChild(String territoryId, String childId) {
		if (territoryId.equals(childId)) {
			return false;
		} 
		List<Territory> territoryList = territoryRepository.findAll();
		if (territoryList != null && !territoryList.isEmpty()) {
			for (Territory territory : territoryList) {
				if (territory.getChildrenIdList().contains(childId)) {
					return false;
				}
			}
		}
		return true;
	}

	public String createTerritoryTree(String campaignId) throws Exception {
		// Create a hash map of all Territories for fast retrieval
		List<Territory> territoryList = territoryRepository.findByCampaignId(campaignId);
		Map<String, Territory> territoryMap = new HashMap<>();
		Territory root = new Territory(ROOT, campaignId);
		root.setId(ROOT);
		root.setGameDataTypeId(ROOT);
		root.setGameDataTypeName(ROOT);
		territoryMap.put(ROOT, root);
		// Populate the map of territory nodes
		for (Territory territory : territoryList) {
			if (territory.getId() != "ROOT" && territory.getParentId() == null) {
				root.addChildId(territory.getId());
				territory.setParentId(ROOT);
			}
			territoryMap.put(territory.getId(), territory);
		} 

		HierarchyJsonBuilder rootBuilder = new HierarchyJsonBuilder(root.getId(), root.getName(),
				root.getGameDataTypeId(), root.getGameDataTypeName());

		Map<String, String> territoryTypeNameMap = getTerritoryTypeNameMap();
		rootBuilder = buildHierarchy(rootBuilder, territoryMap, territoryTypeNameMap);
				
		//ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		ObjectWriter ow = new ObjectMapper().writer();
		String json = null;
		try {
			json = ow.writeValueAsString(rootBuilder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	private Map<String, String> getTerritoryTypeNameMap() {
		Map<String, String> ttNameMap = new HashMap<>();
		List<TerritoryType> territoryTypeList = territoryTypeRepository.findAll();
		for (TerritoryType territoryType : territoryTypeList) {
			ttNameMap.put(territoryType.getId(), territoryType.getName());
		}
		
		return ttNameMap;
	}
	
	private HierarchyJsonBuilder buildHierarchy(HierarchyJsonBuilder parent, Map<String, Territory> territoryMap, 
			Map<String, String> territoryTypeNameMap) {
		Territory territory = territoryMap.get(parent.getId());
		if (territory.hasChildren()) {
			for (String childId : territory.getChildrenIdList()) {
				Territory childTerritory = territoryMap.get(childId);
				childTerritory.setGameDataTypeName(territoryTypeNameMap.get(childTerritory.getGameDataTypeId()));
				HierarchyJsonBuilder child = new HierarchyJsonBuilder(childId, childTerritory.getName(),
						childTerritory.getGameDataTypeId(), childTerritory.getDisplayName());
				if (childTerritory.hasChildren()) {
					child = buildHierarchy(child, territoryMap, territoryTypeNameMap);
				}
				parent.addChild(child);
			}
		}
		
		return parent;
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

	public Territory findOneTerritory(String territoryId) {
		Territory territory = territoryRepository.findOne(territoryId);
		if (territory == null) {
			return null;
		}
		String parentId = territory.getParentId();
		if (parentId != null) {
			Territory parent = territoryRepository.findOne(parentId);
		    territory.setParentName(parent.getName());
		}
		String territoryTypeId = territory.getGameDataTypeId();
		if (territoryTypeId != null) {
		    TerritoryType type = territoryTypeRepository.findOne(territory.getGameDataTypeId());
		    territory.setGameDataTypeName(type.getName());
		}
		return territory;
	}
}
