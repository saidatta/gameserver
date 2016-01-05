package org.softwarewolf.gameserver.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.softwarewolf.gameserver.base.domain.GameDataType;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.base.repository.OrganizationRepository;
import org.softwarewolf.gameserver.base.repository.OrganizationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationTypeService {
	public static final String ROOT = "ROOT";
	private static final Logger logger = Logger.getLogger(OrganizationTypeService.class.getSimpleName());
	
	@Autowired
	protected OrganizationRepository organizationRepository;
	
	@Autowired
	protected OrganizationTypeRepository organizationTypeRepository;
	
	public List<OrganizationType> getAllOrganizationTypes() {
		List<OrganizationType> organizationTypeList = organizationTypeRepository.findAll();
		if (organizationTypeList == null) {
			organizationTypeList = new ArrayList<>();
		}
		return organizationTypeList;
	}
	
	public List<OrganizationType> getOrganizationTypesInCampaign(String campaignId) {
		List<String> campaignIds = new ArrayList<>();
		campaignIds.add(campaignId);
		List<OrganizationType> organizationTypesInCampaign = null;
		organizationTypesInCampaign = organizationTypeRepository.findAllByKeyValues("campaignId", campaignIds.toArray());
		return organizationTypesInCampaign;
	}

	public OrganizationType getOrganizationTypeById(String id) {
		OrganizationType organizationType = organizationTypeRepository.findOne(id);
		return organizationType;
	}
	
	public void saveOrganizationType(OrganizationType organizationType) {
		if (organizationType.getId() == null) {
			OrganizationType existingOrganizationType = organizationTypeRepository.findOneByNameAndCampaignId(organizationType.getName(), 
					organizationType.getCampaignId());
			if (existingOrganizationType != null) {
				throw new IllegalArgumentException("Organization type " + organizationType.getName() + " already exists");
			}
		}
		organizationTypeRepository.save(organizationType);
	}	

	Map<String, String> getOrganizationTypeNameMap() {
		Map<String, String> ttNameMap = new HashMap<>();
		List<OrganizationType> organizationTypeList = organizationTypeRepository.findAll();
		for (OrganizationType organizationType : organizationTypeList) {
			ttNameMap.put(organizationType.getId(), organizationType.getName());
		}
		
		return ttNameMap;
	}
	
	public void initOrganizationTypeCreator(String organizationTypeId, OrganizationTypeCreator organizationTypeCreator, 
			String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			organizationTypeCreator.setForwardingUrl(forwardingUrl);
		}

		OrganizationType organizationType = null;
		if (organizationTypeId != null && !organizationTypeId.equals("0")) {
			organizationType = organizationTypeRepository.findOne(organizationTypeId);
		} else {
			organizationType = new OrganizationType();
		}
		organizationType.setCampaignId(campaignId);
		organizationTypeCreator.setOrganizationType(organizationType);
		List<OrganizationType> organizationTypesInCampaign = null;
		List<GameDataType> gameDataTypesInCampaign = new ArrayList<>();
		List<GameDataType> otherGameDataTypes = new ArrayList<>();
		List<OrganizationType> allOrganizationTypes = organizationTypeRepository.findAll();
		if (allOrganizationTypes != null) {
			if (campaignId != null) {
				List<String> campaignIds = new ArrayList<>();
				campaignIds.add(campaignId);
				organizationTypesInCampaign = organizationTypeRepository.findAllByKeyValue("campaignId", campaignId);
				OrganizationType addNewTT = new OrganizationType();
				addNewTT.setId("0");
				addNewTT.setName("Add new organization type");
				gameDataTypesInCampaign.add(addNewTT);
				for (OrganizationType tt : organizationTypesInCampaign) {
					gameDataTypesInCampaign.add(tt);
				}
				organizationTypeCreator.setGameDataTypesInCampaign(gameDataTypesInCampaign);
			} else {
				organizationTypeCreator.setGameDataTypesInCampaign(new ArrayList<>());
			}
			
			List<GameDataType> allGameDataTypes = new ArrayList<>();
			for (OrganizationType tt : allOrganizationTypes) {
				allGameDataTypes.add(tt);
			}
			if (!organizationTypeCreator.getGameDataTypesInCampaign().isEmpty()) {
				for (OrganizationType tType : allOrganizationTypes) {
					if (!organizationTypesInCampaign.contains(tType)) {
						otherGameDataTypes.add(tType);
					}
				}
//			} else {
//				organizationTypeCreator.setOtherGameDataTypes(allGameDataTypes);
			}
		}
		organizationTypeCreator.setCampaignId(campaignId);
		organizationTypeCreator.setGameDataTypesInCampaign(gameDataTypesInCampaign);
//		organizationTypeCreator.setOtherGameDataTypes(otherGameDataTypes);
	}
	
	public OrganizationType findOneByNameAndCampaignId(String name, String campaignId) {
		return organizationTypeRepository.findOneByNameAndCampaignId(name, campaignId);
	}

}
