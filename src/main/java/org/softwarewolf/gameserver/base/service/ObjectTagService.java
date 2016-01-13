package org.softwarewolf.gameserver.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.Location;
import org.softwarewolf.gameserver.base.domain.LocationType;
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
	private LocationService locationService;
	@Autowired
	private LocationTypeService locationTypeService;
	
	/**
	 * This is not scalable. I need a better way to do this.
	 * @param campaignId
	 * @param excludeTags
	 * @return
	 */
	public Map<String, ObjectTag> createTagList(String campaignId, List<ObjectTag> excludeTags) {
		if (excludeTags == null) {
			excludeTags = new ArrayList<>();
		}
		List<Organization> orgList = organizationService.findAllByCampaignId(campaignId);
		List<OrganizationRank> orgRankList = organizationRankService.findAllByCampaignId(campaignId);
		List<OrganizationType> orgTypeList = organizationTypeService.getOrganizationTypesInCampaign(campaignId);
		List<Location> locationList = locationService.getLocationsInCampaign(campaignId);
		List<LocationType> locationTypeList = locationTypeService.getLocationTypesInCampaign(campaignId);
		
		HashMap<String, ObjectTag> unselectedTagMap = new HashMap<>();
		HashMap<String, ObjectTag> allTags = new HashMap<>();
		for (Organization org : orgList) {
			ObjectTag tag = org.createTag();
			if (!excludeTags.contains(tag)) {
				unselectedTagMap.put(tag.getObjectId(), tag);
			}
			allTags.put(tag.getClassName(), tag);
		}
		for (OrganizationRank orgRank : orgRankList) {
			ObjectTag tag = orgRank.createTag();
			if (!excludeTags.contains(tag)) {
				unselectedTagMap.put(tag.getObjectId(), tag);
			}
			allTags.put(tag.getClassName(), tag);
		}
		for (OrganizationType orgType : orgTypeList) {
			ObjectTag tag = orgType.createTag(campaignId);
			if (!excludeTags.contains(tag)) {
				unselectedTagMap.put(tag.getObjectId(), tag);
			}
			allTags.put(tag.getClassName(), tag);
		}
		for (Location location : locationList) {
			ObjectTag tag = location.createTag();
			if (!excludeTags.contains(tag)) {
				unselectedTagMap.put(tag.getObjectId(), tag);
			}
			allTags.put(tag.getClassName(), tag);
		}
		for (LocationType locationType : locationTypeList) {
			ObjectTag tag = locationType.createTag(campaignId);
			if (!excludeTags.contains(tag)) {
				unselectedTagMap.put(tag.getObjectId(), tag);
			}
			allTags.put(tag.getClassName(), tag);
		}
		
		for (ObjectTag currentTag : unselectedTagMap.values()) {
			String parentName = null;			
			if (!currentTag.getClassName().endsWith("Type")) {
				parentName = currentTag.getClassName() + "Type";
				ObjectTag parent = allTags.get(parentName);
				if (parent != null) {
					parent.addChildTag(currentTag.getObjectId());
					currentTag.setParentId(parent.getObjectId());
				}
			}
		}
		
		return unselectedTagMap;
	}
	
	public Map<String, Object> createObjectTagTree(Map<String, ObjectTag> objectTagMap, String campaignId) throws Exception {
		// Create a hash map of all Tags for fast retrieval
		ObjectTag root = new ObjectTag(ROOT, ROOT, ROOT, campaignId, null, null, null);
		objectTagMap.put(ROOT, root);

		HierarchyJsonBuilder rootBuilder = new HierarchyJsonBuilder(root.getObjectId(), root.getTagName(),
				root.getGameDataTypeId(), root.getTagName());

		for (ObjectTag tag : objectTagMap.values()) {
			String parentId = tag.getGameDataTypeId();
			if (parentId == null && !ROOT.equals(tag.getObjectId())) {
				root.addChildTag(tag.getObjectId());
				tag.setParentId(ROOT);
			}
		}

		rootBuilder = buildHierarchy(rootBuilder, objectTagMap);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> out = mapper.convertValue(rootBuilder, Map.class);
		return out;
	}	

	private HierarchyJsonBuilder buildHierarchy(HierarchyJsonBuilder parent, Map<String, ObjectTag> objectTagMap) {
		ObjectTag objectTag = objectTagMap.get(parent.getId());
		if (objectTag.hasChildren()) {
			for (String childId : getChildrenIdList(objectTag, objectTagMap)) {
				ObjectTag childObjectTag = objectTagMap.get(childId);
//				childOrganizationRank.setGameDataTypeName(organizationTypeNameMap.get(childOrganizationRank.getGameDataTypeId()));
				String displayName = childObjectTag.getTagName();
				if (childObjectTag.getParentId() != null && !childObjectTag.getParentId().equals("Root")) {
					ObjectTag parentTag = objectTagMap.get(childObjectTag.getParentId());
					displayName += "(" + parentTag.getTagName() + ")";
				} else {
					displayName += "(" + splitCamelCase(childObjectTag.getClassName()) + ")"; 
				}
				HierarchyJsonBuilder child = new HierarchyJsonBuilder(childId, childObjectTag.getClassName(),
						childObjectTag.getGameDataTypeId(), displayName);
				if (childObjectTag.hasChildren()) {
					child = buildHierarchy(child, objectTagMap);
				}
				parent.addChild(child);
			}
		}
		
		return parent;
	}	
	
	static String splitCamelCase(String s) {
		return s.replaceAll(
			String.format("%s|%s|%s",
				 "(?<=[A-Z])(?=[A-Z][a-z])",
				 "(?<=[^A-Z])(?=[A-Z])",
				 "(?<=[A-Za-z])(?=[^A-Za-z])"
			), " "
		);
	}
	
	private List<String> getChildrenIdList(ObjectTag parent, Map<String, ObjectTag> objectTagMap) {
		List<String> childList = new ArrayList<>();
		
		String parentId = parent.getObjectId();
		for (ObjectTag tag : objectTagMap.values()) {
			if (parentId.equals(tag.getParentId())) {
				childList.add(tag.getObjectId());
			}
		}
		return childList;
		
	}
}
