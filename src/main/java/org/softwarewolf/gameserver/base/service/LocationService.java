package org.softwarewolf.gameserver.base.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.softwarewolf.gameserver.base.domain.Location;
import org.softwarewolf.gameserver.base.domain.LocationType;
import org.softwarewolf.gameserver.base.domain.helper.HierarchyJsonBuilder;
import org.softwarewolf.gameserver.base.domain.helper.LocationCreator;
import org.softwarewolf.gameserver.base.repository.LocationRepository;
import org.softwarewolf.gameserver.base.repository.LocationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

@Service
public class LocationService {
	public static final String ROOT = "ROOT";
	private static final Logger logger = Logger.getLogger(LocationService.class.getSimpleName());
	
	@Autowired
	protected LocationRepository locationRepository;
	
	@Autowired
	protected LocationTypeRepository locationTypeRepository;
	
	@Autowired
	protected LocationTypeService locationTypeService;
	
	public List<Location> getAllLocations() {
		List<Location> locationList = locationRepository.findAll();
		if (locationList == null) {
			locationList = new ArrayList<>();
		}
		return locationList;
	}

	public List<Location> getLocationsInCampaign(String campaignId) {
		List<Location> locationList = locationRepository.findByCampaignId(campaignId);
		if (locationList == null) {
			locationList = new ArrayList<>();
		}
		return locationList;
	}

	public void initLocationCreator(String locationId, LocationCreator locationCreator, 
			String campaignId, String forwardingUrl) {

		Location location = null;
		if (locationId != null) {
			location = locationRepository.findOne(locationId);
			if (location == null) {
				throw new RuntimeException("Can not find a location for id " + locationId);
			}
		} else {
			location = new Location();
			location.setParentName(ROOT);
			location.setParentId(ROOT);
		}
		
		initLocationCreator(location, locationCreator, campaignId, forwardingUrl);
	}
	
	public void initLocationCreator(Location location, LocationCreator locationCreator, 
			String campaignId, String forwardingUrl) {
		if (forwardingUrl != null) {
			locationCreator.setForwardingUrl(forwardingUrl);
		}
		try {
			String json = createLocationTree(campaignId);
			locationCreator.setLocationTreeJson(json); 
		} catch (Exception e) {
			logger.fine(e.getMessage());
		}

		if (campaignId != null && location.getCampaignId() == null) {
			location.setCampaignId(campaignId);
		}
		locationCreator.setLocation(location);

		if (location.getParentId() != null) {
			Location parent = findOne(location.getParentId());
			if (parent != null && parent.getName() != "ROOT") {
				location.setParentName(parent.getName());
			} 
		} else {
			location.setParentName("ROOT");
		}
		List<Location> locations = locationRepository.findAllByKeyValue("campaignId", campaignId);
		// Add a dummy location to the list for selecting the option of adding a new location
		Location addNewLocation = new Location();
		addNewLocation.setId("0");
		addNewLocation.setName("Add a new location");
		locations.add(0, addNewLocation);
		locationCreator.setLocationsInCampaign(locations);
		
		List<LocationType> locationTypesInCampaign = locationTypeRepository.findAllByKeyValue("campaignId", campaignId);
		LocationType addNew = new LocationType();
		addNew.setId("0");
		addNew.setName("Add new location type");
		locationTypesInCampaign.add(0, addNew);
		locationCreator.setLocationTypesInCampaign(locationTypesInCampaign);
	}
	
	public void saveLocation(Location location) {
		if (location.getId() == null) {
			Location existingLocation = locationRepository.findOneByName(location.getName());
			if (existingLocation != null) {
				throw new IllegalArgumentException("Location " + location.getName() + " already exists");
			}
		}
		String parentLocationId = location.getParentId();
		if (parentLocationId == null || parentLocationId.equals(ROOT)) {
			location.setParentId(null);
		}
		location = locationRepository.save(location);
		if (location.getParentId() != null) {
			Location parentLocation = locationRepository.findOne(parentLocationId);
			if (parentLocation == null) {
				throw new IllegalArgumentException("Could not find parent location");
			}
			if (!canSetLocationParent(location.getId(), parentLocation.getId())) {
				throw new IllegalArgumentException("Illegal parent location");
			}
			parentLocation.addChildId(location.getId());
			locationRepository.save(parentLocation);
		}
	}
	
	public boolean canSetLocationParent(String locationId, String parentId) {
		return !locationId.equals(parentId);
	}

	/**
	 * The current location can only appear as a child once 
	 * @param location
	 * @param childId
	 * @return
	 */
	public boolean canSetLocationChild(String locationId, String childId) {
		if (locationId.equals(childId)) {
			return false;
		} 
		List<Location> locationList = locationRepository.findAll();
		if (locationList != null && !locationList.isEmpty()) {
			for (Location location : locationList) {
				if (location.getChildrenIdList().contains(childId)) {
					return false;
				}
			}
		}
		return true;
	}

	public String createLocationTree(String campaignId) throws Exception {
		// Create a hash map of all Locations for fast retrieval
		List<Location> locationList = locationRepository.findByCampaignId(campaignId);
		Map<String, Location> locationMap = new HashMap<>();
		Location root = new Location(ROOT, campaignId);
		root.setId(ROOT);
		root.setGameDataTypeId(ROOT);
		root.setGameDataTypeName(ROOT);
		locationMap.put(ROOT, root);
		// Populate the map of location nodes
		for (Location location : locationList) {
			if (location.getId() != "ROOT" && location.getParentId() == null) {
				root.addChildId(location.getId());
				location.setParentId(ROOT);
			}
			locationMap.put(location.getId(), location);
		} 

		HierarchyJsonBuilder rootBuilder = new HierarchyJsonBuilder(root.getId(), root.getName(),
				root.getGameDataTypeId(), root.getGameDataTypeName());

		Map<String, String> locationTypeNameMap = locationTypeService.getLocationTypeNameMap();
		rootBuilder = buildHierarchy(rootBuilder, locationMap, locationTypeNameMap);
				
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
	
	private HierarchyJsonBuilder buildHierarchy(HierarchyJsonBuilder parent, Map<String, Location> locationMap, 
			Map<String, String> locationTypeNameMap) {
		Location location = locationMap.get(parent.getId());
		if (location.hasChildren()) {
			for (String childId : location.getChildrenIdList()) {
				Location childLocation = locationMap.get(childId);
				childLocation.setGameDataTypeName(locationTypeNameMap.get(childLocation.getGameDataTypeId()));
				HierarchyJsonBuilder child = new HierarchyJsonBuilder(childId, childLocation.getName(),
						childLocation.getGameDataTypeId(), childLocation.getDisplayName());
				if (childLocation.hasChildren()) {
					child = buildHierarchy(child, locationMap, locationTypeNameMap);
				}
				parent.addChild(child);
			}
		}
		
		return parent;
	}

	public Location findOne(String locationId) {
		Location location = locationRepository.findOne(locationId);
		if (location == null) {
			return null;
		}
		String parentId = location.getParentId();
		if ("ROOT".equals(parentId) || parentId == "") {
			location.setParentId(null);
			parentId = null;
		}
		if (parentId != null) {
			Location parent = locationRepository.findOne(parentId);
		    location.setParentName(parent.getName());
		}
		String locationTypeId = location.getGameDataTypeId();
		if (locationTypeId != null) {
		    LocationType type = locationTypeRepository.findOne(location.getGameDataTypeId());
		    location.setGameDataTypeName(type.getName());
		}
		return location;
	}
}
