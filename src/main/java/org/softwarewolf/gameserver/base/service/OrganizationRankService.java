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
	protected OrganizationRankRepository organizationRankRepository;
	
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

	public void initOrganizationRankCreator(String organizationId, String name, OrganizationRankCreator organizationRankCreator, 
			String campaignId, String forwardingUrl) {

		OrganizationRank organizationRank = null;
		if (organizationId != null && name != null) {
			organizationRank = organizationRankRepository.findOneByNameAndOrganizationId(name, organizationId);
			if (organizationRank == null) {
				throw new RuntimeException("Can not find a organization rank for organization id " + organizationId);
			}
		} else {
			organizationRank = new OrganizationRank();
			organizationRank.setParentName(ROOT);
			organizationRank.setParentId(ROOT);
		}
		
		initOrganizationRankCreator(organizationRank, organizationRankCreator, campaignId, forwardingUrl);
	}
	
	public void initOrganizationRankCreator(OrganizationRank organizationRank, OrganizationRankCreator organizationRankCreator, 
			String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			organizationRankCreator.setForwardingUrl(forwardingUrl);
		}
		try {
			if (organizationRank.getId() != null) {
				String json = createOrganizationRankTree(campaignId, organizationRank.getOrganizationId());
				organizationRankCreator.setOrganizationRankTreeJson(json);
			}
		} catch (Exception e) {
			logger.fine(e.getMessage());
		}

		if (campaignId != null && organizationRank.getCampaignId() == null) {
			organizationRank.setCampaignId(campaignId);
		}
		organizationRankCreator.setOrganizationRank(organizationRank);

		if (organizationRank.getParentId() != null) {
			OrganizationRank parent = organizationRankRepository.findOne(organizationRank.getParentId());
			if (parent != null && parent.getName() != "ROOT") {
				organizationRank.setParentName(parent.getName());
			} 
		} else {
			organizationRank.setParentName("ROOT");
		}
		List<OrganizationRank> ranks = organizationRankRepository.findAllByKeyValue("campaignId", campaignId);
		// Add a dummy organization to the list for selecting the option of adding a new organization
		OrganizationRank addNewOrganizationRank = new OrganizationRank();
		addNewOrganizationRank.setId("0");
		addNewOrganizationRank.setName("Add a new organization rank");
		ranks.add(0, addNewOrganizationRank);
		organizationRankCreator.setOrganizationRanksInCampaign(ranks);
	}
	
	public void saveOrganizationRank(OrganizationRank organizationRank) {
		String campaignId = organizationRank.getCampaignId();
		if (organizationRank.getId() == null) {
			OrganizationRank existingOrganizationRank = organizationRankRepository.findOneByNameAndOrganizationId(organizationRank.getName(), campaignId);
			if (existingOrganizationRank != null) {
				throw new IllegalArgumentException("OrganizationRank " + organizationRank.getName() + " already exists");
			}
		}
		String parentOrganizationRankId = organizationRank.getParentId();
		if (parentOrganizationRankId.equals(ROOT)) {
			organizationRank.setParentId(null);
		}
		organizationRank = organizationRankRepository.save(organizationRank);
		if (organizationRank.getParentId() != null) {
			OrganizationRank parentOrganizationRank = organizationRankRepository.findOne(organizationRank.getParentId());
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
	
	public boolean canSetOrganizationRankParent(String organizationId, String parentId) {
		return !organizationId.equals(parentId);
	}

	/**
	 * The current organization can only appear as a child once 
	 * @param organization
	 * @param childId
	 * @return
	 */
	public boolean canSetOrganizationRankChild(String organizationRankId, String childId, String campaignId) {
		if (organizationRankId.equals(childId)) {
			return false;
		} 
		List<OrganizationRank> organizationRankList = organizationRankRepository.findByCampaignId(campaignId);
		if (organizationRankList != null && !organizationRankList.isEmpty()) {
			for (OrganizationRank organizationRank : organizationRankList) {
				if (organizationRank.getChildrenIdList().contains(childId)) {
					return false;
				}
			}
		}
		return true;
	}

	public String createOrganizationRankTree(String campaignId, String organizationId) throws Exception {
		// Create a hash map of all OrganizationRanks for fast retrieval
		List<OrganizationRank> organizationRankList = organizationRankRepository.findByOrganizationId(organizationId);
		Map<String, OrganizationRank> organizationMap = new HashMap<>();
		OrganizationRank root = new OrganizationRank(ROOT, campaignId, organizationId);
		root.setId(ROOT);
		root.setGameDataTypeId(ROOT);
		root.setGameDataTypeName(ROOT);
		organizationMap.put(ROOT, root);
		// Populate the map of organization nodes
		for (OrganizationRank organizationRank : organizationRankList) {
			if (organizationRank.getId() != "ROOT" && organizationRank.getParentId() == null) {
				root.addChildId(organizationRank.getId());
				organizationRank.setParentId(ROOT);
			}
			organizationMap.put(organizationRank.getId(), organizationRank);
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

	public OrganizationRank findOneOrganizationRank(String organizationId, String name) {
		OrganizationRank organizationRank = organizationRankRepository.findOne(organizationId);
		if (organizationRank == null) {
			return null;
		}
		if (organizationRank.getParentId() != null) {
			OrganizationRank parent = organizationRankRepository.findOne(organizationRank.getParentId());
			organizationRank.setParentName(parent.getName());
		}
		return organizationRank;
	}
}
