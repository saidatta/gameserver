package org.softwarewolf.gameserver.base.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.helper.HierarchyJsonBuilder;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.repository.OrganizationRepository;
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
	public OrganizationTypeService organizationTypeService;
	
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
			Organization parent = findOne(organization.getParentId());
			if (parent != null && parent.getName() != "ROOT") {
				organization.setParentName(parent.getName());
			} 
		} else {
			organization.setParentName("ROOT");
		}
		List<Organization> organizations = organizationRepository.findAllByKeyValue("campaignId", campaignId);
		// Add a dummy Organization to the list for selecting the option of adding a new organization
		Organization addNewOrganization = new Organization();
		addNewOrganization.setId("0");
		addNewOrganization.setName("Add a new organization");
		organizations.add(0, addNewOrganization);
		organizationCreator.setOrganizationsInCampaign(organizations);
		
		List<OrganizationType> organizationTypesInCampaign = organizationTypeService.getOrganizationTypesInCampaign(campaignId);
		Map<String, OrganizationType> map = new HashMap<String, OrganizationType>();
		// Build a map of all org types, key = id, value = OrganizationType
		for (OrganizationType ot : organizationTypesInCampaign) map.put(ot.getId(), ot);
		// Add the OrganizationType.name to the List of OrganizationObjects in the creator 
		for (Organization currentOrg : organizations) {
			String gameDataTypeId = currentOrg.getGameDataTypeId();
			// Skip ROOT
			if (gameDataTypeId != null) {
				String dtId = currentOrg.getGameDataTypeId();
				String gameDataTypeName = map.get(dtId).getName();
				currentOrg.setGameDataTypeName(gameDataTypeName);
			}
		}
		// Set the OrganizationType.name in the Organization in the creator also using the map
		String currentOTId = organization.getGameDataTypeId();
		if (currentOTId != null) {
			OrganizationType currentOT = map.get(currentOTId);
			if (currentOT != null) {
				organization.setGameDataTypeName(currentOT.getName());
			}
		}
		// Add a dummy OrganizationType to the list for selecting the option of adding a new organization
		OrganizationType addNew = new OrganizationType();
		addNew.setId("0");
		addNew.setName("Add new organization type");
		organizationTypesInCampaign.add(0, addNew);
		organizationCreator.setOrganizationTypesInCampaign(organizationTypesInCampaign);
	}
	
	public Organization saveOrganization(Organization organization) {
		if (organization.getId() == null || organization.getId() == "") {
			organization.setId(null);
			Organization existingOrganization = organizationRepository.findOneByNameAndCampaignId(organization.getName(), organization.getCampaignId());
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
		return organization;
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

		Map<String, String> organizationTypeNameMap = organizationTypeService.getOrganizationTypeNameMap();
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

	public Organization findOne(String organizationId) {
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
		    OrganizationType type = organizationTypeService.getOrganizationTypeById(organization.getGameDataTypeId());
		    organization.setGameDataTypeName(type.getName());
		}
		return organization;
	}

	public List<Organization> findAllByCampaignId(String campaignId) {
		return organizationRepository.findAllByKeyValue("campaignId", campaignId);
	}

}
