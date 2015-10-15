package org.softwarewolf.gameserver.base.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.base.domain.GameDataType;
import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.helper.HierarchyJsonBuilder;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationRankCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.base.repository.OrganizationRankRepository;
import org.softwarewolf.gameserver.base.repository.OrganizationRepository;
import org.softwarewolf.gameserver.base.repository.OrganizationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

@Service
public class OrganizationService {
	public static final String ROOT = "ROOT";
	
	@Autowired
	protected OrganizationRepository organizationRepository;
	
	@Autowired
	protected OrganizationTypeRepository organizationTypeRepository;
	
	@Autowired
	protected OrganizationRankRepository organizationRankRepository;
	
	public List<OrganizationType> getAllOrganizationTypes() {
		List<OrganizationType> organizationTypeList = organizationTypeRepository.findAll();
		if (organizationTypeList == null) {
			organizationTypeList = new ArrayList<>();
		}
		return organizationTypeList;
	}
	
	public List<Organization> getAllOrganizations() {
		List<Organization> organizationList = organizationRepository.findAll();
		if (organizationList == null) {
			organizationList = new ArrayList<>();
		}
		return organizationList;
	}

	// This needs to be modified to filter based on campaign id
	public List<Organization> getOrganizationsInCampaign() {
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
	
	public void initOrganizationCreator(OrganizationCreator organizationCreator, String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			organizationCreator.setForwardingUrl(forwardingUrl);
		}
		try {
			String json = createOrganizationTree(campaignId);
			organizationCreator.setOrganizationTreeJson(json); 
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		Organization organization = new Organization();
		if (campaignId != null) {
			organization.setCampaignId(campaignId);
		}
		organizationCreator.setOrganization(organization);

		List<Organization> organizations = organizationRepository.findAllByKeyValue("campaignId", campaignId);
		organizationCreator.setOrganizationsInCampaign(organizations);

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
		// Create a hash map of all Organizations for fast retrieval
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

		rootBuilder = buildHierarchy(rootBuilder, organizationMap);
				
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
	
	private HierarchyJsonBuilder buildHierarchy(HierarchyJsonBuilder parent, Map<String, Organization> organizationMap) {
		Organization organization = organizationMap.get(parent.getId());
		if (organization.hasChildren()) {
			for (String childId : organization.getChildrenIdList()) {
				Organization childOrganization = organizationMap.get(childId);
				HierarchyJsonBuilder child = new HierarchyJsonBuilder(childId, childOrganization.getName(),
						childOrganization.getGameDataTypeId(), childOrganization.getDisplayName());
				if (childOrganization.hasChildren()) {
					child = buildHierarchy(child, organizationMap);
				}
				parent.addChild(child);
			}
		}
		
		return parent;
	}

	private HierarchyJsonBuilder buildRankHierarchy(HierarchyJsonBuilder parent, Map<String, OrganizationRank> organizationRankMap) {
		OrganizationRank organizationRank = organizationRankMap.get(parent.getId());
		if (organizationRank.hasChildren()) {
			for (String childId : organizationRank.getChildrenIdList()) {
				OrganizationRank childOrganizationRank = organizationRankMap.get(childId);
				HierarchyJsonBuilder child = new HierarchyJsonBuilder(childId, childOrganizationRank.getName(),
						null, childOrganizationRank.getName());
				if (childOrganizationRank.hasChildren()) {
					child = buildRankHierarchy(child, organizationRankMap);
				}
				parent.addChild(child);
			}
		}
		
		return parent;
	}
	
	public void initOrganizationTypeCreator(OrganizationType organizationType, 
			OrganizationTypeCreator organizationTypeCreator, String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			organizationTypeCreator.setForwardingUrl(forwardingUrl);
		}

		if (organizationType != null) {
			organizationTypeCreator.setOrganizationType(organizationType);
		} else {
			organizationTypeCreator.setOrganizationType(new OrganizationType());
		}
		List<OrganizationType> organizationTypesInCampaign = null;
		List<GameDataType> gameDataTypesInCampaign = new ArrayList<>();
		List<GameDataType> otherGameDataTypes = new ArrayList<>();
		List<OrganizationType> allOrganizationTypes = organizationTypeRepository.findAll();
		if (allOrganizationTypes != null) {
			if (campaignId != null) {
				List<String> campaignIds = new ArrayList<>();
				campaignIds.add(campaignId);
				organizationTypesInCampaign = organizationTypeRepository.findAllByKeyValue("campaignList", campaignId);
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

	public List<OrganizationRank> getAllOrganizationRanks() {
		List<OrganizationRank> organizationRankList = organizationRankRepository.findAll();
		if (organizationRankList == null) {
			organizationRankList = new ArrayList<>();
		}
		return organizationRankList;
	}

	// This needs to be modified to filter based on campaign id
	public List<OrganizationRank> getOrganizationRanksInCampaign() {
		List<OrganizationRank> organizationRankList = organizationRankRepository.findAll();
		if (organizationRankList == null) {
			organizationRankList = new ArrayList<>();
		}
		return organizationRankList;
	}


	
	public void initOrganizationRankCreator(OrganizationRankCreator organizationRankCreator, String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			organizationRankCreator.setForwardingUrl(forwardingUrl);
		}
		try {
			String json = createOrganizationRankTree(campaignId);
			organizationRankCreator.setOrganizationRankTreeJson(json); 
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		OrganizationRank organizationRank = new OrganizationRank();
		if (campaignId != null) {
			organizationRank.setCampaignId(campaignId);
		}
		organizationRankCreator.setOrganizationRank(organizationRank);

		List<OrganizationRank> organizationRanks = organizationRankRepository.findAllByKeyValue("campaignId", campaignId);
		organizationRankCreator.setOrganizationRanksInCampaign(organizationRanks);
	}
	
	public void saveOrganizationRank(OrganizationRank organizationRank) {
		if (organizationRank.getId() == null) {
			OrganizationRank existingOrganizationRank = organizationRankRepository.findOneByName(organizationRank.getName());
			if (existingOrganizationRank != null) {
				throw new IllegalArgumentException("Organization " + organizationRank.getName() + " already exists");
			}
		}
		String parentOrganizationRankId = organizationRank.getParentId();
		if (parentOrganizationRankId.equals(ROOT)) {
			organizationRank.setParentId(null);
		}
		organizationRank = organizationRankRepository.save(organizationRank);
		if (organizationRank.getParentId() != null) {
			OrganizationRank parentOrganizationRank = organizationRankRepository.findOne(parentOrganizationRankId);
			if (parentOrganizationRank == null) {
				throw new IllegalArgumentException("Could not find parent organization rank");
			}
			if (!canSetOrganizationRankParent(organizationRank.getId(), parentOrganizationRank.getId())) {
				throw new IllegalArgumentException("Illegal parent organization rank");
			}
			parentOrganizationRank.addChildId(organizationRank.getId());
			organizationRankRepository.save(parentOrganizationRank);
		}
	}
	
	public boolean canSetOrganizationRankParent(String organizationRankId, String parentId) {
		return !organizationRankId.equals(parentId);
	}

	/**
	 * The current organization can only appear as a child once 
	 * @param organization
	 * @param childId
	 * @return
	 */
	public boolean canSetOrganizationRankChild(String organizationRankId, String childId) {
		if (organizationRankId.equals(childId)) {
			return false;
		} 
		List<OrganizationRank> organizationRankList = organizationRankRepository.findAll();
		if (organizationRankList != null && !organizationRankList.isEmpty()) {
			for (OrganizationRank organizationRank : organizationRankList) {
				if (organizationRank.getChildrenIdList().contains(childId)) {
					return false;
				}
			}
		}
		return true;
	}

	public String createOrganizationRankTree(String campaignId) throws Exception {
		// Create a hash map of all OrganizationRanks for fast retrieval
		List<OrganizationRank> organizationRankList = organizationRankRepository.findByCampaignId(campaignId);
		Map<String, OrganizationRank> organizationRankMap = new HashMap<>();
		OrganizationRank root = new OrganizationRank(ROOT, campaignId);
		root.setId(ROOT);
		organizationRankMap.put(ROOT, root);
		// Populate the map of organization nodes
		for (OrganizationRank organizationRank : organizationRankList) {
			if (organizationRank.getId() != "ROOT" && organizationRank.getParentId() == null) {
				root.addChildId(organizationRank.getId());
				organizationRank.setParentId(ROOT);
			}
			organizationRankMap.put(organizationRank.getId(), organizationRank);
		} 

		HierarchyJsonBuilder rootBuilder = new HierarchyJsonBuilder(root.getId(), root.getName(), null, null);

		rootBuilder = buildRankHierarchy(rootBuilder, organizationRankMap);
				
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
}
