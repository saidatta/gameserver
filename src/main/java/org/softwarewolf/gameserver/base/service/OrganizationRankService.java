package org.softwarewolf.gameserver.base.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.helper.HierarchyJsonBuilder;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationRankCreator;
import org.softwarewolf.gameserver.base.repository.OrganizationRankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

@Service
public class OrganizationRankService {
	public static final String ROOT = "ROOT";
	private static final Logger logger = Logger.getLogger(OrganizationRankService.class.getSimpleName());
	
	@Autowired
	protected OrganizationRankRepository organizationRepository;
	
	public List<OrganizationRank> getAllOrganizationRanks() {
		List<OrganizationRank> organizationList = organizationRepository.findAll();
		if (organizationList == null) {
			organizationList = new ArrayList<>();
		}
		return organizationList;
	}

	// This needs to be modified to filter based on campaign id
	public List<OrganizationRank> getOrganizationRanksInCampaign() {
		List<OrganizationRank> organizationList = organizationRepository.findAll();
		if (organizationList == null) {
			organizationList = new ArrayList<>();
		}
		return organizationList;
	}

	public void initOrganizationRankCreator(String organizationId, OrganizationRankCreator organizationRankCreator, 
			String campaignId, String forwardingUrl) {

		OrganizationRank organization = null;
		if (organizationId != null) {
			organization = organizationRepository.findOne(organizationId);
			if (organization == null) {
				throw new RuntimeException("Can not find a organization for id " + organizationId);
			}
		} else {
			organization = new OrganizationRank();
			organization.setParentName(ROOT);
			organization.setParentId(ROOT);
		}
		
		initOrganizationRankCreator(organization, organizationRankCreator, campaignId, forwardingUrl);
	}
	
	public void initOrganizationRankCreator(OrganizationRank organization, OrganizationRankCreator organizationRankCreator, 
			String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			organizationRankCreator.setForwardingUrl(forwardingUrl);
		}
		try {
			String json = createOrganizationRankTree(campaignId);
			organizationRankCreator.setOrganizationRankTreeJson(json); 
		} catch (Exception e) {
			logger.fine(e.getMessage());
		}

		if (campaignId != null && organization.getCampaignId() == null) {
			organization.setCampaignId(campaignId);
		}
		organizationRankCreator.setOrganizationRank(organization);

		if (organization.getParentId() != null) {
			OrganizationRank parent = findOneOrganizationRank(organization.getParentId());
			if (parent != null && parent.getName() != "ROOT") {
				organization.setParentName(parent.getName());
			} 
		} else {
			organization.setParentName("ROOT");
		}
		List<OrganizationRank> territories = organizationRepository.findAllByKeyValue("campaignId", campaignId);
		// Add a dummy organization to the list for selecting the option of adding a new organization
		OrganizationRank addNewOrganizationRank = new OrganizationRank();
		addNewOrganizationRank.setId("0");
		addNewOrganizationRank.setName("Add a new organization");
		territories.add(0, addNewOrganizationRank);
		organizationRankCreator.setOrganizationRanksInCampaign(territories);
	}
	
	public void saveOrganizationRank(OrganizationRank organization) {
		if (organization.getId() == null) {
			OrganizationRank existingOrganizationRank = organizationRepository.findOneByName(organization.getName());
			if (existingOrganizationRank != null) {
				throw new IllegalArgumentException("OrganizationRank " + organization.getName() + " already exists");
			}
		}
		String parentOrganizationRankId = organization.getParentId();
		if (parentOrganizationRankId.equals(ROOT)) {
			organization.setParentId(null);
		}
		organization = organizationRepository.save(organization);
		if (organization.getParentId() != null) {
			OrganizationRank parentOrganizationRank = organizationRepository.findOne(parentOrganizationRankId);
			if (parentOrganizationRank == null) {
				throw new IllegalArgumentException("Could not find parent organization");
			}
			if (!canSetOrganizationRankParent(organization.getId(), parentOrganizationRank.getId())) {
				throw new IllegalArgumentException("Illegal parent organization");
			}
			parentOrganizationRank.addChildId(organization.getId());
			organizationRepository.save(parentOrganizationRank);
		}
	}
	
	public boolean canSetOrganizationRankParent(String organizationId, String parentId) {
		return !organizationId.equals(parentId);
	}

	/**
	 * The current organization can only appear as a child once 
	 * @param organization
	 * @param childId
	 * @return
	 */
	public boolean canSetOrganizationRankChild(String organizationId, String childId) {
		if (organizationId.equals(childId)) {
			return false;
		} 
		List<OrganizationRank> organizationList = organizationRepository.findAll();
		if (organizationList != null && !organizationList.isEmpty()) {
			for (OrganizationRank organization : organizationList) {
				if (organization.getChildrenIdList().contains(childId)) {
					return false;
				}
			}
		}
		return true;
	}

	public String createOrganizationRankTree(String campaignId) throws Exception {
		// Create a hash map of all OrganizationRanks for fast retrieval
		List<OrganizationRank> organizationList = organizationRepository.findByCampaignId(campaignId);
		Map<String, OrganizationRank> organizationMap = new HashMap<>();
		OrganizationRank root = new OrganizationRank(ROOT, campaignId);
		root.setId(ROOT);
		root.setGameDataTypeId(ROOT);
		root.setGameDataTypeName(ROOT);
		organizationMap.put(ROOT, root);
		// Populate the map of organization nodes
		for (OrganizationRank organization : organizationList) {
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
	
	private HierarchyJsonBuilder buildHierarchy(HierarchyJsonBuilder parent, Map<String, OrganizationRank> organizationMap) {
		OrganizationRank organization = organizationMap.get(parent.getId());
		if (organization.hasChildren()) {
			for (String childId : organization.getChildrenIdList()) {
				OrganizationRank childOrganizationRank = organizationMap.get(childId);
//				childOrganizationRank.setGameDataTypeName(organizationTypeNameMap.get(childOrganizationRank.getGameDataTypeId()));
				HierarchyJsonBuilder child = new HierarchyJsonBuilder(childId, childOrganizationRank.getName(),
						childOrganizationRank.getGameDataTypeId(), childOrganizationRank.getDisplayName());
				if (childOrganizationRank.hasChildren()) {
					child = buildHierarchy(child, organizationMap);
				}
				parent.addChild(child);
			}
		}
		
		return parent;
	}

	public OrganizationRank findOneOrganizationRank(String organizationId) {
		OrganizationRank organizationRank = organizationRepository.findOne(organizationId);
		if (organizationRank == null) {
			return null;
		}
		String parentId = organizationRank.getParentId();
		if (parentId != null) {
			OrganizationRank parent = organizationRepository.findOne(parentId);
			organizationRank.setParentName(parent.getName());
		}
		String organizationTypeId = organizationRank.getGameDataTypeId();
		if (organizationTypeId != null) {
		}
		return organizationRank;
	}
}
