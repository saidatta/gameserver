package org.softwarewolf.gameserver.base.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.helper.HierarchyJsonBuilder;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationRankCreator;
import org.softwarewolf.gameserver.base.repository.OrganizationRankRepository;
import org.softwarewolf.gameserver.base.repository.OrganizationRepository;
import org.softwarewolf.gameserver.base.repository.OrganizationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.codehaus.jackson.map.ObjectMapper;

@Service
public class OrganizationRankService {
	public static final String ROOT = "ROOT";
	private static final Logger logger = Logger.getLogger(OrganizationRankService.class.getSimpleName());
	
	@Autowired
	protected OrganizationRankRepository organizationRankRepository;
	
	@Autowired
	protected OrganizationRepository organizationRepository;
	
	@Autowired
	protected OrganizationTypeRepository organizationTypeRepository;
	
	public List<OrganizationRank> getOrganizationRanksInOrganization(String organizationId) {
		List<OrganizationRank> organizationRankList = organizationRankRepository.findByOrganizationId(organizationId);
		if (organizationRankList == null) {
			organizationRankList = new ArrayList<>();
		}
		return organizationRankList;
	}

	public void initOrganizationRankCreator(String organizationId, String name, OrganizationRankCreator organizationRankCreator, 
			String campaignId, String forwardingUrl) {

		// init the named organization rank
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
			organizationRank.setOrganizationId(organizationId);
			organizationRank.setCampaignId(campaignId);
		}
		// init the list of org ranks
		List<OrganizationRank> organizationRanksInOrganization = null;
		if (organizationId != null) {
			organizationRanksInOrganization = getOrganizationRanksInOrganization(organizationId);
		} else {
			organizationRanksInOrganization = new ArrayList<>();
		}
		organizationRankCreator.setOrganizationRanksInOrganization(organizationRanksInOrganization);
		// init the list of organizations
		List<Organization> organizationsInCampaign = organizationRepository.findAllByKeyValue("campaignId", campaignId);
		organizationRankCreator.setOrganizationsInCampaign(organizationsInCampaign);
		
		initOrganizationRankCreator(organizationRank, organizationRankCreator, campaignId, forwardingUrl);
	}
	
	public void initOrganizationRankCreator(String organizationRankId, OrganizationRankCreator organizationRankCreator, 
			String campaignId, String forwardingUrl) {

		OrganizationRank organizationRank = null;
		if (organizationRankId != null && !organizationRankId.isEmpty()) {
			organizationRank = read(organizationRankId);
			if (organizationRank == null) {
				throw new RuntimeException("Can not find a organization rank for organization rank id " + organizationRankId);
			}
		} else {
			organizationRank = new OrganizationRank();
			organizationRank.setParentName(ROOT);
			organizationRank.setParentId(ROOT);
			organizationRank.setOrganizationId(null);
			organizationRank.setCampaignId(campaignId);
		}
		
		initOrganizationRankCreator(organizationRank, organizationRankCreator, campaignId, forwardingUrl);
	}
	
	public void initOrganizationRankCreator(OrganizationRank organizationRank, OrganizationRankCreator organizationRankCreator, 
			String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			organizationRankCreator.setForwardingUrl(forwardingUrl);
		}
		String json = getOrganizationRankTree(campaignId, organizationRank.getOrganizationId());
		organizationRankCreator.setOrganizationRankTreeJson(json);

		organizationRankCreator.setOrganizationRank(organizationRank);

		if (organizationRank.getParentId() != null && organizationRank.getParentId() != "ROOT") {
			OrganizationRank parent = organizationRankRepository.findOne(organizationRank.getParentId());
			if (parent != null && parent.getName() != "ROOT") {
				organizationRank.setParentName(parent.getName());
			} 
		} else {
			organizationRank.setParentName("ROOT");
		}
		List<OrganizationRank> ranks = null;
  		if (organizationRank.getOrganizationId() != null ) {
			ranks = organizationRankRepository.findByCampaignIdAndOrganizationId(campaignId, organizationRank.getOrganizationId());
		} else {
			ranks = new ArrayList<>();
		}
		// Add a dummy organization to the list for selecting the option of adding a new organization
		OrganizationRank addNewOrganizationRank = new OrganizationRank();
		addNewOrganizationRank.setId("0");
		addNewOrganizationRank.setName("Add a new organization rank");
		ranks.add(0, addNewOrganizationRank);
		organizationRankCreator.setOrganizationRanksInOrganization(ranks);
	}
	
	public List<OrganizationRank> getOrganizationRankList(String organizationId) {
		List<OrganizationRank> ranks = organizationRankRepository.findByOrganizationId(organizationId);
		if (ranks == null) {
			ranks = new ArrayList<>();
		}
		return ranks;
	}
	
	public String getOrganizationRankTree(String campaignId, String organizationId) {
		String json = "{}";
		try {
			if (organizationId != null) {
				Map<String, Object> out = createOrganizationRankTree(campaignId, organizationId);
				ObjectMapper mapper = new ObjectMapper();
				json = mapper.writeValueAsString(out);
			}
		} catch (Exception e) {
			logger.fine(e.getMessage());
		}
		return json;
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

	public Map<String, Object> createOrganizationRankTree(String campaignId, String organizationId) throws Exception {
		// Create a hash map of all OrganizationRanks for fast retrieval
		List<OrganizationRank> organizationRankList = organizationRankRepository.findByOrganizationId(organizationId);
		Map<String, OrganizationRank> organizationRankMap = new HashMap<>();
		OrganizationRank root = new OrganizationRank(ROOT, campaignId, organizationId);
		root.setId(ROOT);
		root.setGameDataTypeId(ROOT);
		root.setGameDataTypeName(ROOT);
		organizationRankMap.put(ROOT, root);
		// Populate the map of organization nodes
		for (OrganizationRank organizationRank : organizationRankList) {
			if (organizationRank.getId() != "ROOT" && organizationRank.getParentId() == null) {
				root.addChildId(organizationRank.getId());
				organizationRank.setParentId(ROOT);
			}
			organizationRank.setDisplayName(organizationRank.getName());
			organizationRankMap.put(organizationRank.getId(), organizationRank);
		} 

		HierarchyJsonBuilder rootBuilder = new HierarchyJsonBuilder(root.getId(), root.getName(),
				root.getGameDataTypeId(), root.getGameDataTypeName());

		rootBuilder = buildHierarchy(rootBuilder, organizationRankMap);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> out = mapper.convertValue(rootBuilder, Map.class);
		return out;
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
	
	public OrganizationRank read(String id) {
		return organizationRankRepository.findOne(id);
	}

	public OrganizationRank get(String id) {
		OrganizationRank organizationRank = organizationRankRepository.findOne(id);
		if (organizationRank.getParentId() != null) {
			OrganizationRank parent = organizationRankRepository.findOne(organizationRank.getParentId());
			if (parent != null) {
				organizationRank.setParentName(parent.getName());
			} 
		} 
		if (organizationRank.getParentName() == null) {
			organizationRank.setParentName("ROOT");
		}
		return organizationRank;
	}

	public String getOrganizationAndRanks(String organizationId) {
		Map<String, Object> results = new HashMap<>();
		if (organizationId != null) {
			Organization org = organizationRepository.findOne(organizationId);		
			if (org != null) {
				OrganizationType orgType = organizationTypeRepository.findOne(org.getGameDataTypeId());
				if (orgType != null) {
					String organizationTypeName = orgType.getName(); 
					org.setGameDataTypeName(organizationTypeName);
				}
				results.put("organization", org);
				List<OrganizationRank> orgRankList = organizationRankRepository.findByOrganizationId(organizationId);
				if (orgRankList != null) {
					results.put("organizationRanks", orgRankList);
				}
				Map<String, Object> tree = new HashMap<>();
				try {
					tree = createOrganizationRankTree(org.getCampaignId(), organizationId);
					if (tree != null) {
						results.put("organizationRankTree", tree);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		String out = "{}";
		try {
			out = mapper.writeValueAsString(results);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return out;
	}
}
