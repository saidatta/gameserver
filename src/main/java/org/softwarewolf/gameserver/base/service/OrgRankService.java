package org.softwarewolf.gameserver.base.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.base.domain.OrgRank;
import org.softwarewolf.gameserver.base.domain.helper.HierarchyJsonBuilder;
import org.softwarewolf.gameserver.base.domain.helper.OrgRankCreator;
import org.softwarewolf.gameserver.base.repository.OrgRankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

@Service
public class OrgRankService {
	public static final String ROOT = "ROOT";
	
	@Autowired
	protected OrgRankRepository orgRankRepository;
	
	public List<OrgRank> getAllTerritories() {
		List<OrgRank> orgRankList = orgRankRepository.findAll();
		if (orgRankList == null) {
			orgRankList = new ArrayList<>();
		}
		return orgRankList;
	}

	// This needs to be modified to filter based on campaign id
	public List<OrgRank> getTerritoriesInCampaign() {
		List<OrgRank> orgRankList = orgRankRepository.findAll();
		if (orgRankList == null) {
			orgRankList = new ArrayList<>();
		}
		return orgRankList;
	}
	public void initOrgRankCreator(OrgRankCreator orgRankCreator, String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			orgRankCreator.setForwardingUrl(forwardingUrl);
		}
		try {
			String json = createOrgRankTree(campaignId);
			orgRankCreator.setOrgRankTreeJson(json); 
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		OrgRank orgRank = new OrgRank();
		if (campaignId != null) {
			orgRank.setCampaignId(campaignId);
		}
		orgRankCreator.setOrgRank(orgRank);

		List<OrgRank> territories = orgRankRepository.findAllByKeyValue("campaignId", campaignId);
		orgRankCreator.setOrgRanksInCampaign(territories);

	}
	
	public void saveOrgRank(OrgRank orgRank) {
		if (orgRank.getId() == null) {
			OrgRank existingOrgRank = orgRankRepository.findOneByName(orgRank.getName());
			if (existingOrgRank != null) {
				throw new IllegalArgumentException("OrgRank " + orgRank.getName() + " already exists");
			}
		}
		String parentOrgRankId = orgRank.getParentId();
		if (parentOrgRankId.equals(ROOT)) {
			orgRank.setParentId(null);
		}
		orgRank = orgRankRepository.save(orgRank);
		if (orgRank.getParentId() != null) {
			OrgRank parentOrgRank = orgRankRepository.findOne(parentOrgRankId);
			if (parentOrgRank == null) {
				throw new IllegalArgumentException("Could not find parent orgRank");
			}
			if (!canSetOrgRankParent(orgRank.getId(), parentOrgRank.getId())) {
				throw new IllegalArgumentException("Illegal parent orgRank");
			}
			parentOrgRank.addChildId(orgRank.getId());
			orgRankRepository.save(parentOrgRank);
		}
	}
	
	public boolean canSetOrgRankParent(String orgRankId, String parentId) {
		return !orgRankId.equals(parentId);
	}

	/**
	 * The current orgRank can only appear as a child once 
	 * @param orgRank
	 * @param childId
	 * @return
	 */
	public boolean canSetOrgRankChild(String orgRankId, String childId) {
		if (orgRankId.equals(childId)) {
			return false;
		} 
		List<OrgRank> orgRankList = orgRankRepository.findAll();
		if (orgRankList != null && !orgRankList.isEmpty()) {
			for (OrgRank orgRank : orgRankList) {
				if (orgRank.getChildrenIdList().contains(childId)) {
					return false;
				}
			}
		}
		return true;
	}

	public String createOrgRankTree(String campaignId) throws Exception {
		// Create a hash map of all Territories for fast retrieval
		List<OrgRank> orgRankList = orgRankRepository.findByCampaignId(campaignId);
		Map<String, OrgRank> orgRankMap = new HashMap<>();
		OrgRank root = new OrgRank(ROOT, campaignId);
		root.setId(ROOT);
		root.setGameDataTypeId(ROOT);
		root.setGameDataTypeName(ROOT);
		orgRankMap.put(ROOT, root);
		// Populate the map of orgRank nodes
		for (OrgRank orgRank : orgRankList) {
			if (orgRank.getId() != "ROOT" && orgRank.getParentId() == null) {
				root.addChildId(orgRank.getId());
				orgRank.setParentId(ROOT);
			}
			orgRankMap.put(orgRank.getId(), orgRank);
		} 

		HierarchyJsonBuilder rootBuilder = new HierarchyJsonBuilder(root.getId(), root.getName(),
				root.getGameDataTypeId(), root.getGameDataTypeName());

		rootBuilder = buildHierarchy(rootBuilder, orgRankMap);
				
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
	
	private HierarchyJsonBuilder buildHierarchy(HierarchyJsonBuilder parent, Map<String, OrgRank> orgRankMap) {
		OrgRank orgRank = orgRankMap.get(parent.getId());
		if (orgRank.hasChildren()) {
			for (String childId : orgRank.getChildrenIdList()) {
				OrgRank childOrgRank = orgRankMap.get(childId);
				HierarchyJsonBuilder child = new HierarchyJsonBuilder(childId, childOrgRank.getName(),
						childOrgRank.getGameDataTypeId(), childOrgRank.getDisplayName());
				if (childOrgRank.hasChildren()) {
					child = buildHierarchy(child, orgRankMap);
				}
				parent.addChild(child);
			}
		}
		
		return parent;
	}
}
