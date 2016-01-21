package org.softwarewolf.gameserver.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
/*		
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
			ObjectTag tag = orgType.createTag();
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
			ObjectTag tag = locationType.createTag();
			if (!excludeTags.contains(tag)) {
				unselectedTagMap.put(tag.getObjectId(), tag);
			}
			allTags.put(tag.getClassName(), tag);
		}
*/		
		List<ObjectTag> locationTags = createLocationTags(locationTypeList, locationList);
		List<ObjectTag> organizationTags = createOrganizationTags(orgTypeList, orgList, orgRankList);
		locationTags.addAll(organizationTags);
		Map<String, ObjectTag> newMap = new HashMap<>();
		for (ObjectTag tag : locationTags) {
			newMap.put(tag.getObjectId(), tag);
		}
		return newMap;
//		return unselectedTagMap;
	}
	
	public List<ObjectTag> createLocationTags(List<LocationType> locationTypeList, List<Location> locationList) {
		List<ObjectTag> locationTagList = new ArrayList<>();
		
		// The parent map is of type objectId/objectTag
		Map<String, ObjectTag> locationParentMap = new HashMap<>();
		Iterator<LocationType> ltIter = locationTypeList.iterator();
		while (ltIter.hasNext()) {
			LocationType locationType = ltIter.next();
			String displayName = locationType.getName() + "(Location Type)";
			String className = locationType.getClass().getSimpleName();
			// Root type
			ObjectTag locationTypeTag = new ObjectTag(className, locationType.getId(), displayName, 
					locationType.getCampaignId(), locationType.getId(), className, "Root");
			locationTagList.add(locationTypeTag);
			locationParentMap.put(locationTypeTag.getObjectId(), locationTypeTag);
		}
		
		Iterator<Location> locIter = locationList.iterator();
		while (locIter.hasNext()) {
			Location location = locIter.next();
			ObjectTag parentTag = locationParentMap.get(location.getGameDataTypeId());
			parentTag.addChildTag(location.getId());
			String parentName = parentTag.getTagName();
			parentName = parentName.substring(0, parentName.lastIndexOf("("));
			String displayName = location.getName() + "(" + parentName + ")";
			String className = location.getClass().getSimpleName();
			// In this case, the GameDataType of a Location IS it's parent
			ObjectTag locationTag = new ObjectTag(className, location.getId(), displayName,
					location.getCampaignId(), parentTag.getObjectId(), parentTag.getClassName(), parentTag.getObjectId());
			parentTag.addChildTag(locationTag.getObjectId());
			locationTagList.add(locationTag);
		}
		
		return locationTagList;
	}
	
	public List<ObjectTag> createOrganizationTags(List<OrganizationType> organizationTypeList, 
			List<Organization> organizationList, List<OrganizationRank> organizationRankList) {
		List<ObjectTag> organizationTagList = new ArrayList<>();
		
		// The parent map is of type objectId/objectTag
		Map<String, ObjectTag> orgTypeParentMap = new HashMap<>();
		Iterator<OrganizationType> otIter = organizationTypeList.iterator();
		while (otIter.hasNext()) {
			OrganizationType organizationType = otIter.next();
			String displayName = organizationType.getName() + "(Organization Type)";
			String className = organizationType.getClass().getSimpleName();
			// Root type
			ObjectTag organizationTypeTag = new ObjectTag(className, organizationType.getId(), displayName, 
					organizationType.getCampaignId(), organizationType.getId(), className, "Root");
			organizationTagList.add(organizationTypeTag);
			orgTypeParentMap.put(organizationTypeTag.getObjectId(), organizationTypeTag);
		}
		
		Map<String, ObjectTag> orgParentMap = new HashMap<>();
		Iterator<Organization> orgIter = organizationList.iterator();
		while (orgIter.hasNext()) {
			Organization organization = orgIter.next();
			ObjectTag parentTag = orgTypeParentMap.get(organization.getGameDataTypeId());
			parentTag.addChildTag(organization.getId());
			String parentName = parentTag.getTagName();
			parentName = parentName.substring(0, parentName.lastIndexOf("("));
			String displayName = organization.getName() + "(" + parentName + ")";
			String className = organization.getClass().getSimpleName();
			// In this case the GameDataType of an Organization IS it's parent
			ObjectTag organizationTag = new ObjectTag(className, organization.getId(), displayName,
					organization.getCampaignId(), parentTag.getObjectId(), parentTag.getClassName(), parentTag.getObjectId());
			parentTag.addChildTag(organizationTag.getObjectId());
			organizationTagList.add(organizationTag);
			orgParentMap.put(organizationTag.getObjectId(), organizationTag);
		}
		
		Iterator<OrganizationRank> orgRankIter = organizationRankList.iterator();
		while (orgRankIter.hasNext()) {
			OrganizationRank organizationRank = orgRankIter.next();
			ObjectTag parentTag = orgParentMap.get(organizationRank.getOrganizationId());
			parentTag.addChildTag(organizationRank.getId());
			String parentName = parentTag.getTagName();
			parentName = parentName.substring(0, parentName.lastIndexOf("("));
			String displayName = organizationRank.getName() + "(" + parentName + ")";
			String className = organizationRank.getClass().getSimpleName();
			// In this case the parent Organization of an OrganizationRank IS it's parent 
			ObjectTag organizationRankTag = new ObjectTag(className, organizationRank.getId(), displayName,
					organizationRank.getCampaignId(), parentTag.getObjectId(), parentTag.getClassName(), parentTag.getObjectId());
			parentTag.addChildTag(organizationRankTag.getObjectId());
			organizationTagList.add(organizationRankTag);
		}

		return organizationTagList;
	}
	
	public Map<String, Object> createObjectTagTree(Map<String, ObjectTag> objectTagMap, String campaignId) throws Exception {
		// Create a hash map of all Tags for fast retrieval
		ObjectTag root = new ObjectTag(ROOT, ROOT, ROOT, campaignId, null, null, null);
		objectTagMap.put(ROOT, root);

		HierarchyJsonBuilder rootBuilder = new HierarchyJsonBuilder(root.getObjectId(), root.getTagName(),
				root.getGameDataTypeId(), root.getTagName());

		for (ObjectTag tag : objectTagMap.values()) {
			String parentId = tag.getParentId();
			if (tag.getParentId() != null) {
				ObjectTag parentTag = objectTagMap.get(tag.getParentId());
				parentTag.addChildTag(tag.getObjectId());
			}
//			if (parentId == ROOT) {
//				root.addChildTag(tag.getObjectId());
//			} else if (tag.getObjectId() != "Root") {
//				ObjectTag parentTag = objectTagMap.get(tag.getParentId());
//				parentTag.addChildTag(tag.getObjectId());
//			}
		}

		rootBuilder = buildHierarchy(rootBuilder, objectTagMap);
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> out = mapper.convertValue(rootBuilder, Map.class);
		return out;
	}	

	// Here the objectTagMap is a map of the objectId/ObjectTag
	private HierarchyJsonBuilder buildHierarchy(HierarchyJsonBuilder parent, Map<String, ObjectTag> objectTagMap) {
		ObjectTag objectTag = objectTagMap.get(parent.getId());
		if (objectTag.hasChildren()) {
			List<String> childrenList = getChildrenIdList(objectTag, objectTagMap);
			for (String childId : childrenList) {
				ObjectTag childObjectTag = objectTagMap.get(childId);
				HierarchyJsonBuilder child = new HierarchyJsonBuilder(childId, childObjectTag.getClassName(),
						childObjectTag.getGameDataTypeId(), childObjectTag.getTagName());
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
