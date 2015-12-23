package org.softwarewolf.gameserver.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.Territory;
import org.softwarewolf.gameserver.base.domain.TerritoryType;
import org.softwarewolf.gameserver.base.domain.helper.HierarchyJsonBuilder;
import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObjectTagService {
	private static final String ROOT = "Root";
	@Autowired
	private OrganizationService organizationService;
	@Autowired
	private OrganizationRankService organizationRankService;
	@Autowired
	private OrganizationTypeService organizationTypeService;
	@Autowired
	private TerritoryService territoryService;
	@Autowired
	private TerritoryTypeService territoryTypeService;
	
	/**
	 * This is not scalable. I need a better way to do this.
	 * @param campaignId
	 * @param excludeTags
	 * @return
	 */
	public List<ObjectTag> createTagList(String campaignId, List<ObjectTag> excludeTags) {
		if (excludeTags == null) {
			excludeTags = new ArrayList<>();
		}
		List<Organization> orgList = organizationService.findAllByCampaignId(campaignId);
		List<OrganizationRank> orgRankList = organizationRankService.findAllByCampaignId(campaignId);
		List<OrganizationType> orgTypeList = organizationTypeService.getOrganizationTypesInCampaign(campaignId);
		List<Territory> territoryList = territoryService.getTerritoriesInCampaign(campaignId);
		List<TerritoryType> territoryTypeList = territoryTypeService.getTerritoryTypesInCampaign(campaignId);
		
		List<ObjectTag> tagList = new ArrayList<>();
		for (Organization org : orgList) {
			ObjectTag tag = org.createTag();
			if (!excludeTags.contains(tag)) {
				tagList.add(tag);
			}
		}
		for (OrganizationRank orgRank : orgRankList) {
			ObjectTag tag = orgRank.createTag();
			if (!excludeTags.contains(tag)) {
				tagList.add(orgRank.createTag());
			}
		}
		for (OrganizationType orgType : orgTypeList) {
			ObjectTag tag = orgType.createTag(campaignId);
			if (!excludeTags.contains(tag)) {
				tagList.add(orgType.createTag(campaignId));
			}
		}
		for (Territory territory : territoryList) {
			ObjectTag tag = territory.createTag();
			if (!excludeTags.contains(tag)) {
				tagList.add(territory.createTag());
			}
		}
		for (TerritoryType territoryType : territoryTypeList) {
			ObjectTag tag = territoryType.createTag(campaignId);
			if (!excludeTags.contains(tag)) {
				tagList.add(territoryType.createTag(campaignId));
			}
		}
		return tagList;
	}
	
	public Map<String, Object> createObjectTagTree(List<ObjectTag> objectTagList) throws Exception {
		Map<String, ObjectTag> objectTagMap = new HashMap<>();
		if (objectTagList == null || objectTagList.isEmpty()) {
			return new HashMap<>();
		}
		
		String campaignId = objectTagList.get(0).getCampaignId();
		// Create a hash map of all Tags for fast retrieval
		ObjectTag root = new ObjectTag(ROOT, ROOT, ROOT, campaignId, null, null, null);
		objectTagMap.put(ROOT, root);
		// Populate the map of organization nodes
		for (ObjectTag objectTag : objectTagList) {
			if (!objectTag.getObjectId().equals(ROOT) && objectTag.getGameDataTypeId() == null) {
				objectTag.setGameDataTypeId(ROOT);
			}
			objectTagMap.put(objectTag.getObjectId(), objectTag);
		} 

		HierarchyJsonBuilder rootBuilder = new HierarchyJsonBuilder(root.getObjectId(), root.getTagName(),
				root.getGameDataTypeId(), root.getTagName());

		for (ObjectTag tag : objectTagMap.values()) {
			String parentId = tag.getGameDataTypeId();
			if (parentId != null) {
				ObjectTag parent = objectTagMap.get(parentId);
				if (parent != null) {
					parent.setHasChildren(true);
					tag.setParentId(parent.getObjectId());
				}
			}
		}

		rootBuilder = buildHierarchy(rootBuilder, objectTagMap);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> out = mapper.convertValue(rootBuilder, Map.class);
		return out;
	}	

	private HierarchyJsonBuilder buildHierarchy(HierarchyJsonBuilder parent, Map<String, ObjectTag> objectTagMap) {
		ObjectTag objectTag = objectTagMap.get(parent.getId());
		if (objectTag.getHasChildren()) {
			for (String childId : getChildrenIdList(objectTag, objectTagMap)) {
				ObjectTag childObjectTag = objectTagMap.get(childId);
//				childOrganizationRank.setGameDataTypeName(organizationTypeNameMap.get(childOrganizationRank.getGameDataTypeId()));
				String displayName = childObjectTag.getTagName();
				if (childObjectTag.getParentId() != null && !childObjectTag.getParentId().equals("Root")) {
					ObjectTag parentTag = objectTagMap.get(childObjectTag.getParentId());
					displayName += "(" + parentTag.getTagName() + ")";
				}
				HierarchyJsonBuilder child = new HierarchyJsonBuilder(childId, childObjectTag.getClassName(),
						childObjectTag.getGameDataTypeId(), displayName);
				if (childObjectTag.getHasChildren()) {
					child = buildHierarchy(child, objectTagMap);
				}
				parent.addChild(child);
			}
		}
		
		return parent;
	}	
	
	private List<String> getChildrenIdList(ObjectTag parent, Map<String, ObjectTag> objectTagMap) {
		List<String> childList = new ArrayList<>();
		
		String parentId = parent.getObjectId();
		for (ObjectTag tag : objectTagMap.values()) {
			if (parentId.equals(tag.getGameDataTypeId())) {
				childList.add(tag.getObjectId());
			}
		}
		return childList;
		
	}
}
