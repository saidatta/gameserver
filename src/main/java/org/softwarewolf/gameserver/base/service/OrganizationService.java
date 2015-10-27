package org.softwarewolf.gameserver.base.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.softwarewolf.gameserver.base.domain.GameDataType;
import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.helper.HierarchyJsonBuilder;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.base.repository.OrganizationRepository;
import org.softwarewolf.gameserver.base.repository.OrganizationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

@Service
public class OrganizationService {
	public static final String ROOT = "ROOT";
	private static final Logger logger = Logger.getLogger(OrganizationService.class.getSimpleName());
	
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
	
	public List<Organization> getAllTerritories() {
		List<Organization> organizationList = organizationRepository.findAll();
		if (organizationList == null) {
			organizationList = new ArrayList<>();
		}
		return organizationList;
	}

	// This needs to be modified to filter based on campaign id
	public List<Organization> getTerritoriesInCampaign() {
		List<Organization> organizationList = organizationRepository.findAll();
		if (organizationList == null) {
			organizationList = new ArrayList<>();
		}
		return organizationList;
	}

	public List<OrganizationType> getOrganizationTypesInCampaign(String campaignId) {
		List<String> campaignIds = new ArrayList<>();
		campaignIds.add(campaignId);
		List<OrganizationType> organizationTypesInCampaign = null;
		organizationTypesInCampaign = organizationTypeRepository.findAllByKeyValues("campaignList", campaignIds.toArray());
		return organizationTypesInCampaign;
	}

	public OrganizationType getOrganizationTypeById(String id) {
		OrganizationType organizationType = organizationTypeRepository.findOne(id);
		return organizationType;
	}
	
	public void initOrganizationCreator(String organizationId, OrganizationCreator organizationCreator, 
			String campaignId, String forwardingUrl) {

		Organization organization = null;
		if (organizationId != null) {
			organization = organizationRepository.findOne(organizationId);
			if (organization == null) {
				throw new RuntimeException("Can not find a organization for id " + organizationId);
			}
		} else {
			organization = new Organization();
			organization.setParentName(ROOT);
			organization.setParentId(ROOT);
		}
		
		initOrganizationCreator(organization, organizationCreator, campaignId, forwardingUrl);
	}
	
	public void initOrganizationCreator(Organization organization, OrganizationCreator organizationCreator, 
			String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			organizationCreator.setForwardingUrl(forwardingUrl);
		}
		try {
			String json = createOrganizationTree(campaignId);
			organizationCreator.setOrganizationTreeJson(json); 
		} catch (Exception e) {
			logger.fine(e.getMessage());
		}

		if (campaignId != null && organization.getCampaignId() == null) {
			organization.setCampaignId(campaignId);
		}
		organizationCreator.setOrganization(organization);

		if (organization.getParentId() != null) {
			Organization parent = findOneOrganization(organization.getParentId());
			if (parent != null && parent.getName() != "ROOT") {
				organization.setParentName(parent.getName());
			} 
		} else {
			organization.setParentName("ROOT");
		}
		List<Organization> organizations = organizationRepository.findAllByKeyValue("campaignId", campaignId);
		// Add a dummy organization to the list for selecting the option of adding a new organization
		Organization addNewOrganization = new Organization();
		addNewOrganization.setId("0");
		addNewOrganization.setName("Add a new organization");
		organizations.add(0, addNewOrganization);
		organizationCreator.setOrganizationsInCampaign(organizations);
		
		List<OrganizationType> organizationTypesInCampaign = organizationTypeRepository.findAllByKeyValue("campaignList", campaignId);
		OrganizationType addNew = new OrganizationType();
		addNew.setId("0");
		addNew.setName("Add new organization type");
		organizationTypesInCampaign.add(0, addNew);
		organizationCreator.setOrganizationTypesInCampaign(organizationTypesInCampaign);
	}
	
	public void saveOrganizationType(OrganizationType organizationType) {
		if (organizationType.getId() == null) {
			OrganizationType existingOrganizationType = organizationTypeRepository.findOneByName(organizationType.getName());
			if (existingOrganizationType != null) {
				throw new IllegalArgumentException("Organization type " + organizationType.getName() + " already exists");
			}
		}
		organizationTypeRepository.save(organizationType);
	}	
	
	public void saveOrganization(Organization organization) {
		if (organization.getId() == null) {
			Organization existingOrganization = organizationRepository.findOneByName(organization.getName());
			if (existingOrganization != null) {
				throw new IllegalArgumentException("Organization " + organization.getName() + " already exists");
			}
		}
		String parentOrganizationId = organization.getParentId();
		if (parentOrganizationId.equals(ROOT)) {
			organization.setParentId(null);
		}
		organization = organizationRepository.save(organization);
		if (organization.getParentId() != null) {
			Organization parentOrganization = organizationRepository.findOne(parentOrganizationId);
			if (parentOrganization == null) {
				throw new IllegalArgumentException("Could not find parent organization");
			}
			if (!canSetOrganizationParent(organization.getId(), parentOrganization.getId())) {
				throw new IllegalArgumentException("Illegal parent organization");
			}
			parentOrganization.addChildId(organization.getId());
			organizationRepository.save(parentOrganization);
		}
	}
	
	public boolean canSetOrganizationParent(String organizationId, String parentId) {
		return !organizationId.equals(parentId);
	}

	/**
	 * The current organization can only appear as a child once 
	 * @param organization
	 * @param childId
	 * @return
	 */
	public boolean canSetOrganizationChild(String organizationId, String childId) {
		if (organizationId.equals(childId)) {
			return false;
		} 
		List<Organization> organizationList = organizationRepository.findAll();
		if (organizationList != null && !organizationList.isEmpty()) {
			for (Organization organization : organizationList) {
				if (organization.getChildrenIdList().contains(childId)) {
					return false;
				}
			}
		}
		return true;
	}

	public String createOrganizationTree(String campaignId) throws Exception {
		// Create a hash map of all Territories for fast retrieval
		List<Organization> organizationList = organizationRepository.findByCampaignId(campaignId);
		Map<String, Organization> organizationMap = new HashMap<>();
		Organization root = new Organization(ROOT, campaignId);
		root.setId(ROOT);
		root.setGameDataTypeId(ROOT);
		root.setGameDataTypeName(ROOT);
		organizationMap.put(ROOT, root);
		// Populate the map of organization nodes
		for (Organization organization : organizationList) {
			if (organization.getId() != "ROOT" && organization.getParentId() == null) {
				root.addChildId(organization.getId());
				organization.setParentId(ROOT);
			}
			organizationMap.put(organization.getId(), organization);
		} 

		HierarchyJsonBuilder rootBuilder = new HierarchyJsonBuilder(root.getId(), root.getName(),
				root.getGameDataTypeId(), root.getGameDataTypeName());

		Map<String, String> organizationTypeNameMap = getOrganizationTypeNameMap();
		rootBuilder = buildHierarchy(rootBuilder, organizationMap, organizationTypeNameMap);
				
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
	
	private Map<String, String> getOrganizationTypeNameMap() {
		Map<String, String> ttNameMap = new HashMap<>();
		List<OrganizationType> organizationTypeList = organizationTypeRepository.findAll();
		for (OrganizationType organizationType : organizationTypeList) {
			ttNameMap.put(organizationType.getId(), organizationType.getName());
		}
		
		return ttNameMap;
	}
	
	private HierarchyJsonBuilder buildHierarchy(HierarchyJsonBuilder parent, Map<String, Organization> organizationMap, 
			Map<String, String> organizationTypeNameMap) {
		Organization organization = organizationMap.get(parent.getId());
		if (organization.hasChildren()) {
			for (String childId : organization.getChildrenIdList()) {
				Organization childOrganization = organizationMap.get(childId);
				childOrganization.setGameDataTypeName(organizationTypeNameMap.get(childOrganization.getGameDataTypeId()));
				HierarchyJsonBuilder child = new HierarchyJsonBuilder(childId, childOrganization.getName(),
						childOrganization.getGameDataTypeId(), childOrganization.getDisplayName());
				if (childOrganization.hasChildren()) {
					child = buildHierarchy(child, organizationMap, organizationTypeNameMap);
				}
				parent.addChild(child);
			}
		}
		
		return parent;
	}

	public void initOrganizationTypeCreator(String organizationTypeId, OrganizationTypeCreator organizationTypeCreator, 
			String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			organizationTypeCreator.setForwardingUrl(forwardingUrl);
		}

		OrganizationType organizationType = null;
		if (organizationTypeId != null) {
			organizationType = organizationTypeRepository.findOne(organizationTypeId);
		} else {
			organizationType = new OrganizationType();
		}
		organizationTypeCreator.setOrganizationType(organizationType);
		List<OrganizationType> organizationTypesInCampaign = null;
		List<GameDataType> gameDataTypesInCampaign = new ArrayList<>();
		List<GameDataType> otherGameDataTypes = new ArrayList<>();
		List<OrganizationType> allOrganizationTypes = organizationTypeRepository.findAll();
		if (allOrganizationTypes != null) {
			if (campaignId != null) {
				List<String> campaignIds = new ArrayList<>();
				campaignIds.add(campaignId);
				organizationTypesInCampaign = organizationTypeRepository.findAllByKeyValue("campaignList", campaignId);
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
			if (organizationTypeCreator.getGameDataTypesInCampaign().isEmpty()) {
				organizationTypeCreator.setOtherGameDataTypes(allGameDataTypes);
			} else {
				for (OrganizationType tType : allOrganizationTypes) {
					if (!organizationTypesInCampaign.contains(tType)) {
						otherGameDataTypes.add(tType);
					}
				}
			}
		}
		organizationTypeCreator.setCampaignId(campaignId);
		organizationTypeCreator.setGameDataTypesInCampaign(gameDataTypesInCampaign);
		organizationTypeCreator.setOtherGameDataTypes(otherGameDataTypes);
	}

	public Organization findOneOrganization(String organizationId) {
		Organization organization = organizationRepository.findOne(organizationId);
		if (organization == null) {
			return null;
		}
		String parentId = organization.getParentId();
		if (parentId != null) {
			Organization parent = organizationRepository.findOne(parentId);
		    organization.setParentName(parent.getName());
		}
		String organizationTypeId = organization.getGameDataTypeId();
		if (organizationTypeId != null) {
		    OrganizationType type = organizationTypeRepository.findOne(organization.getGameDataTypeId());
		    organization.setGameDataTypeName(type.getName());
		}
		return organization;
	}
}
