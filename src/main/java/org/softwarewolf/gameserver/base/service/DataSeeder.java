package org.softwarewolf.gameserver.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.base.domain.Campaign;
import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.Territory;
import org.softwarewolf.gameserver.base.domain.TerritoryType;
import org.softwarewolf.gameserver.base.domain.User;
import org.softwarewolf.gameserver.base.repository.CampaignRepository;
import org.softwarewolf.gameserver.base.repository.OrganizationRankRepository;
import org.softwarewolf.gameserver.base.repository.OrganizationRepository;
import org.softwarewolf.gameserver.base.repository.OrganizationTypeRepository;
import org.softwarewolf.gameserver.base.repository.SimpleGrantedAuthorityRepository;
import org.softwarewolf.gameserver.base.repository.TerritoryRepository;
import org.softwarewolf.gameserver.base.repository.TerritoryTypeRepository;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DataSeeder {
	private static final String ROLE_USER = "ROLE_USER";
	private static final String ROLE_ADMIN = "ROLE_ADMIN";
	private static final String ROLE_GAMEMASTER = "ROLE_GAMEMASTER";
	private static final String ADMIN = "admin";
	private static final String USER = "user";
	private static final String GM = "gm";
	private static final String SWORD_AND_SORCERY = "Sword and Sorcery";
	private static final String SPACE_OPERA = "Space Opera";
	private static final String MODERN = "Modern";
	private static final String KINGDOM = "Kingdom";
	private static final String RIVAL_KINGDOM = "Rival Kingdom";
	private static final String MAGIC_KINGDOM = "Magic Kingdom";
	private static final String MAGIC_COUNTY = "Magic County";
	private static final String MAGIC_CITY = "Magic City";
	private static final String MAGIC_TOWN = "Magic Town";
	private static final String MODERN_KINGDOM = "Modern Kingdom";
	private static final String MODERN_COUNTY = "Modern County";
	private static final String MODERN_CITY = "Modern City";
	private static final String MODERN_SPACE_STATION = "Modern Space Station";
	private static final String COUNTY = "County";
	private static final String CITY = "City";
	private static final String TOWN = "Town";
	private static final String MERCHANTS_GUILD = "Merchants Guild";
	private static final String COVEN = "A witches coven";
	private static final String BLOOD_MOON = "The Blood Moon Coven";
	private static final String SPACE_STATION = "Space Station";
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SimpleGrantedAuthorityRepository sgaRepo;
	
	@Autowired 
	private CampaignRepository campaignRepo;
	
	@Autowired 
	private TerritoryTypeRepository territoryTypeRepo;
	
	@Autowired 
	private TerritoryRepository territoryRepo;
	
	@Autowired
	private TerritoryService territoryService;
	
	@Autowired 
	private OrganizationTypeRepository organizationTypeRepo;
	
	@Autowired 
	private OrganizationRepository organizationRepo;
	
	@Autowired 
	private OrganizationRankRepository organizationRankRepo;
	
	public void cleanRepos() {
		sgaRepo.deleteAll();
		userRepo.deleteAll();
		campaignRepo.deleteAll();
		territoryRepo.deleteAll();
		territoryTypeRepo.deleteAll();
		organizationRepo.deleteAll();
		organizationTypeRepo.deleteAll();
		organizationRankRepo.deleteAll();
	}
	
	public void seedData() {
		Map<String, SimpleGrantedAuthority> roleMap = seedRoles();
		Map<String, User> userMap = seedUsers(roleMap);
		Map<String, Campaign> campaignMap = seedCampaign(userMap);
		Map<String, TerritoryType> territoryTypeMap = seedTerritoryType(campaignMap);
		seedTerritories(campaignMap, territoryTypeMap);
		Map<String, OrganizationType> organizationTypeMap = seedOrganizationType(campaignMap);
		Map<String, Organization> organizationMap = seedOrganizations(campaignMap, organizationTypeMap);
		seedOrganizationRanks(campaignMap, organizationMap);
	}
	
	private Map<String, SimpleGrantedAuthority> seedRoles() {
		SimpleGrantedAuthority roleAdmin = sgaRepo.findByRole(ROLE_ADMIN);
		SimpleGrantedAuthority roleUser = sgaRepo.findByRole(ROLE_USER);
		SimpleGrantedAuthority roleGamemaster = sgaRepo.findByRole(ROLE_GAMEMASTER);
	
		if (roleAdmin == null) {
			roleAdmin = new SimpleGrantedAuthority(ROLE_ADMIN);
			roleAdmin = sgaRepo.save(roleAdmin);
		}
		if (roleUser == null) {
			roleUser = new SimpleGrantedAuthority(ROLE_USER);
			roleUser = sgaRepo.save(roleUser);
		}
		if (roleGamemaster == null) {
			roleGamemaster = new SimpleGrantedAuthority(ROLE_GAMEMASTER);
			roleGamemaster = sgaRepo.save(roleGamemaster);
		}
		Map<String, SimpleGrantedAuthority> roles = new HashMap<>();
		roles.put(ROLE_ADMIN, roleAdmin);
		roles.put(ROLE_USER, roleUser);
		roles.put(ROLE_GAMEMASTER, roleGamemaster);
		
		return roles;
	}
	
	private Map<String, User> seedUsers(Map<String, SimpleGrantedAuthority> roleMap) {
		Map<String, User> userMap = new HashMap<>();

		List<SimpleGrantedAuthority> roleList = new ArrayList<>();
		roleList.add(roleMap.get(ROLE_USER));
		saveUser(USER, roleList, userMap);
		
		roleList.add(roleMap.get(ROLE_GAMEMASTER));
		saveUser(GM, roleList, userMap);
		
		roleList.add(roleMap.get(ROLE_ADMIN));
		saveUser(ADMIN, roleList, userMap);


		return userMap;
	}
	
	private void saveUser(String name, List<SimpleGrantedAuthority> roleList, Map<String, User> userMap) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		User user = userRepo.findOneByUsername(name);
		if (user == null) {
			user = new User();
			user.setUsername(name);
			user.setFirstName(name);
			String userPwd = encoder.encode(name);
			user.setPassword(userPwd);
			user.setEmail("dm_tim@yahoo.com");
			user.setAccountNonExpired(true);
			user.setAccountNonLocked(true);
			user.setCredentialsNonExpired(true);
			user.setEnabled(true);
			for (SimpleGrantedAuthority auth : roleList) {
				user.addSimpleGrantedAuthority(auth);
			}
			user = userRepo.save(user);
			userMap.put(name, user);
		} else {
			System.out.println("Found the user in the db: " + user.toString());
		}
	}
	
	private Map<String, Campaign> seedCampaign(Map<String, User> userMap) {
		Map<String, Campaign> campaignMap = new HashMap<>();
		String gmId = (userMap.get(GM).getId());

		saveCampaign(SWORD_AND_SORCERY, "Generic sword and sorcery campaign", gmId, campaignMap);
		saveCampaign(SPACE_OPERA, "Generic space opera campaign", gmId, campaignMap);
		saveCampaign(MODERN, "Generic modern campaign", gmId, campaignMap);
		
		return campaignMap;
	}
	
	private void saveCampaign(String name, String description, String ownerId, Map<String, Campaign> campaignMap) {
		Campaign campaign = campaignRepo.findOneByName(name);
		if (campaign == null) {
			campaign = new Campaign();
			campaign.setName(name);
			campaign.setDescription(description);
			campaign.setOwnerId(ownerId);
			campaign = campaignRepo.save(campaign);
			campaignMap.put(name, campaign);
		}
	}
	
	private Map<String, TerritoryType> seedTerritoryType(Map<String, Campaign> campaignMap) {
		Map<String, TerritoryType> territoryTypeMap = new HashMap<>();
		List<String> campaignIdList = new ArrayList<>();
		campaignIdList.add(campaignMap.get(SWORD_AND_SORCERY).getId());
		campaignIdList.add(campaignMap.get(SPACE_OPERA).getId());
		campaignIdList.add(campaignMap.get(MODERN).getId());
		
		saveTerritoryType(KINGDOM, "A typical kingdom.", campaignIdList, territoryTypeMap);
		saveTerritoryType(COUNTY, "A subdivision of a kingdom.", campaignIdList, territoryTypeMap);
		saveTerritoryType(CITY, "A collection of people and dwellings.", campaignIdList, territoryTypeMap);
		saveTerritoryType(TOWN, "Like a city but smaller.", campaignIdList, territoryTypeMap);

		campaignIdList.remove(campaignMap.get(SWORD_AND_SORCERY).getId());
		campaignIdList.add(campaignMap.get(SPACE_OPERA).getId());
		
		saveTerritoryType(SPACE_STATION, "A typical space station.", campaignIdList, territoryTypeMap);
		
		return territoryTypeMap;
	}
	
	private void saveTerritoryType(String name, String description, List<String> campaignIdList, Map<String, TerritoryType> territoryTypeMap) {
		TerritoryType territoryType = territoryTypeRepo.findOneByName(name);
		if (territoryType == null) {
			territoryType = new TerritoryType();
			territoryType.setName(name);
			territoryType.setDescription(description);
			territoryType.setCampaignList(campaignIdList);
			territoryType = territoryTypeRepo.save(territoryType);
			territoryTypeMap.put(name, territoryType);
		}
	}
	
	private void seedTerritories(Map<String, Campaign> campaignMap, Map<String, TerritoryType> territoryTypeMap) {
		territoryRepo.deleteAll();
		String sAndSCampaignId = campaignMap.get(SWORD_AND_SORCERY).getId();
		Territory magicKingdom = findAndSave(MAGIC_KINGDOM, sAndSCampaignId, territoryTypeMap.get(KINGDOM).getId(), 
				territoryTypeMap.get(KINGDOM).getName(), campaignMap, "A magic kingdom", null);

		Territory magicCounty = findAndSave(MAGIC_COUNTY, sAndSCampaignId, territoryTypeMap.get(COUNTY).getId(),
				territoryTypeMap.get(COUNTY).getName(), campaignMap, "A magic county", magicKingdom);

		findAndSave(MAGIC_CITY, sAndSCampaignId, territoryTypeMap.get(CITY).getId(), 
				territoryTypeMap.get(CITY).getName(), campaignMap, "A magic city", magicCounty);

		findAndSave(MAGIC_TOWN, sAndSCampaignId, territoryTypeMap.get(TOWN).getId(), 
				territoryTypeMap.get(TOWN).getName(), campaignMap, "A magic city", magicCounty);

		findAndSave(RIVAL_KINGDOM, sAndSCampaignId, territoryTypeMap.get(KINGDOM).getId(), 
				territoryTypeMap.get(KINGDOM).getName(), campaignMap, "A rival kingdom", null);

		/* MODERN */
		String modernCampaignId = campaignMap.get(MODERN).getId();
		Territory modernKingdom = findAndSave(MODERN_KINGDOM, modernCampaignId, territoryTypeMap.get(KINGDOM).getId(), 
				territoryTypeMap.get(KINGDOM).getName(), campaignMap, "A modern kingdom", null);

		Territory modernCounty = findAndSave(MODERN_COUNTY, modernCampaignId, territoryTypeMap.get(COUNTY).getId(), 
				territoryTypeMap.get(COUNTY).getName(), campaignMap, "A modern county", modernKingdom);

		findAndSave(MODERN_CITY, modernCampaignId, territoryTypeMap.get(CITY).getId(), 
				territoryTypeMap.get(CITY).getName(), campaignMap, "A modern city", modernCounty);

		findAndSave(MODERN_SPACE_STATION, modernCampaignId, territoryTypeMap.get(SPACE_STATION).getId(), 
				territoryTypeMap.get(SPACE_STATION).getName(), campaignMap, "A modern space station", null);

		/* SPACE OPERA */
		String spaceOperaCampaignId = campaignMap.get(SPACE_OPERA).getId();
		findAndSave(SPACE_STATION, spaceOperaCampaignId, territoryTypeMap.get(SPACE_STATION).getId(),
				territoryTypeMap.get(SPACE_STATION).getName(), campaignMap, "A space station", null);
	}
	
	private Territory findAndSave(String name, String campaignId, String territoryTypeId, String territoryTypeName, 
			Map<String, Campaign> campaignMap, String description, Territory parent) {
		Territory territory = territoryRepo.findOneByNameAndCampaignId(name, campaignId);
		if (territory == null) {
			territory = new Territory(name, campaignId);
			territory.setDescription(description);
			territory.setGameDataTypeId(territoryTypeId);
			territory.setGameDataTypeName(territoryTypeName);
			// Need to get an id on territory so all the parent/child links can be set
			String parentId = null;
			if (parent != null) {
				parentId = parent.getId();
			}
			territory.setParentId(parentId);
			territory = territoryRepo.save(territory);
			if (parent != null) {
				parent.addChildId(territory.getId());
				parent = territoryRepo.save(parent);
			}
		}
		return territory;
	}
	
	private Map<String, OrganizationType> seedOrganizationType(Map<String, Campaign> campaignMap) {
		Map<String, OrganizationType> organizationTypeMap = new HashMap<>();
		List<String> campaignIdList = new ArrayList<>();
		campaignIdList.add(campaignMap.get(SWORD_AND_SORCERY).getId());
		campaignIdList.add(campaignMap.get(SPACE_OPERA).getId());
		campaignIdList.add(campaignMap.get(MODERN).getId());
		
		saveOrganizationType(KINGDOM, "A typical kingdom.", campaignIdList, organizationTypeMap);
		saveOrganizationType(COUNTY, "A subdivision of a kingdom.", campaignIdList, organizationTypeMap);
		saveOrganizationType(CITY, "A collection of people and dwellings.", campaignIdList, organizationTypeMap);
		saveOrganizationType(TOWN, "Like a city but smaller.", campaignIdList, organizationTypeMap);
		saveOrganizationType(MERCHANTS_GUILD, "Mercantile guild.", campaignIdList, organizationTypeMap);
		saveOrganizationType(COVEN, "A witches coven.", campaignIdList, organizationTypeMap);

		campaignIdList.remove(campaignMap.get(SWORD_AND_SORCERY).getId());
		campaignIdList.add(campaignMap.get(SPACE_OPERA).getId());
		
		saveOrganizationType(SPACE_STATION, "A typical space station.", campaignIdList, organizationTypeMap);
		
		return organizationTypeMap;
	}
	
	private void saveOrganizationType(String name, String description, List<String> campaignIdList, Map<String, OrganizationType> organizationTypeMap) {
		OrganizationType organizationType = organizationTypeRepo.findOneByName(name);
		if (organizationType == null) {
			organizationType = new OrganizationType();
			organizationType.setName(name);
			organizationType.setDescription(description);
			organizationType.setCampaignList(campaignIdList);
			organizationType = organizationTypeRepo.save(organizationType);
			organizationTypeMap.put(name, organizationType);
		}
	}
	
	private Map<String, Organization> seedOrganizations(Map<String, Campaign> campaignMap, Map<String, OrganizationType> organizationTypeMap) {
		organizationRepo.deleteAll();
		Map<String, Organization> orgMap = new HashMap<>();
		String sAndSCampaignId = campaignMap.get(SWORD_AND_SORCERY).getId();
		Organization kingdom = findAndSaveOrganization(KINGDOM + " of Midland", sAndSCampaignId, organizationTypeMap.get(KINGDOM).getId(), 
				organizationTypeMap.get(KINGDOM).getName(), campaignMap, "A kingdom", null, orgMap);

		Organization county = findAndSaveOrganization("Kirkwall " + COUNTY, sAndSCampaignId, organizationTypeMap.get(COUNTY).getId(),
				organizationTypeMap.get(COUNTY).getName(), campaignMap, "A county", kingdom, orgMap);

		findAndSaveOrganization("Morningstar", sAndSCampaignId, organizationTypeMap.get(CITY).getId(), 
				organizationTypeMap.get(CITY).getName(), campaignMap, "A city", county, orgMap);

		findAndSaveOrganization("Markham", sAndSCampaignId, organizationTypeMap.get(TOWN).getId(), 
				organizationTypeMap.get(TOWN).getName(), campaignMap, "A city", county, orgMap);

		findAndSaveOrganization("Golden Road Trading League", sAndSCampaignId, organizationTypeMap.get(KINGDOM).getId(), 
				organizationTypeMap.get(MERCHANTS_GUILD).getName(), campaignMap, "A merchants guild", null, orgMap);

		findAndSaveOrganization(BLOOD_MOON, sAndSCampaignId, organizationTypeMap.get(COVEN).getId(), 
				organizationTypeMap.get(COVEN).getName(), campaignMap, "A witches coven", null, orgMap);

		/* MODERN */
		String modernCampaignId = campaignMap.get(MODERN).getId();
		Organization modernKingdom = findAndSaveOrganization("Kalibah", modernCampaignId, organizationTypeMap.get(KINGDOM).getId(), 
				organizationTypeMap.get(KINGDOM).getName(), campaignMap, "A modern kingdom", null, orgMap);

		Organization modernCounty = findAndSaveOrganization("Kent", modernCampaignId, organizationTypeMap.get(COUNTY).getId(), 
				organizationTypeMap.get(COUNTY).getName(), campaignMap, "A modern county", modernKingdom, orgMap);

		findAndSaveOrganization("York", modernCampaignId, organizationTypeMap.get(CITY).getId(), 
				organizationTypeMap.get(CITY).getName(), campaignMap, "A modern city", modernCounty, orgMap);

		findAndSaveOrganization("Space Station Group", modernCampaignId, organizationTypeMap.get(SPACE_STATION).getId(), 
				organizationTypeMap.get(SPACE_STATION).getName(), campaignMap, "A modern space station", null, orgMap);

		/* SPACE OPERA */
		String spaceOperaCampaignId = campaignMap.get(SPACE_OPERA).getId();
		findAndSaveOrganization(SPACE_STATION + " Omega", spaceOperaCampaignId, organizationTypeMap.get(SPACE_STATION).getId(),
				organizationTypeMap.get(SPACE_STATION).getName(), campaignMap, "A space station", null, orgMap);
		
		return orgMap;
	}
	
	private void seedOrganizationRanks(Map<String, Campaign> campaignMap, Map<String, Organization> organizationMap) {
		String sAndSCampaignId = campaignMap.get(SWORD_AND_SORCERY).getId();
		Organization org = organizationMap.get(KINGDOM + " of Midland");
		findAndSaveOrganizationRank("King", sAndSCampaignId, org.getId(),  
				campaignMap, "The ruler of the kingdom", null);
		
		String modernCampaignId = campaignMap.get(MODERN).getId();
		org = organizationMap.get("Kent");
		findAndSaveOrganizationRank("Sheriff of Kent", modernCampaignId, org.getId(), 
				campaignMap, "The local sheriff", null);

		String spaceOperaCampaignId = campaignMap.get(SPACE_OPERA).getId();
		org = organizationMap.get("Space Station Group");
		findAndSaveOrganizationRank("Space Station Commander", spaceOperaCampaignId, org.getId(), 
				campaignMap, "The commander of the space station", null);
	}
	
	private Organization findAndSaveOrganization(String name, String campaignId, String organizationTypeId, String organizationTypeName, 
			Map<String, Campaign> campaignMap, String description, Organization parent, Map<String, Organization> orgMap) {
		Organization organization = organizationRepo.findOneByNameAndCampaignId(name, campaignId);
		if (organization == null) {
			organization = new Organization(name, campaignId);
			organization.setDescription(description);
			organization.setGameDataTypeId(organizationTypeId);
			organization.setGameDataTypeName(organizationTypeName);
			// Need to get an id on territory so all the parent/child links can be set
			String parentId = null;
			if (parent != null) {
				parentId = parent.getId();
			}
			organization.setParentId(parentId);
			organization = organizationRepo.save(organization);
			orgMap.put(organization.getName(), organization);
			if (parent != null) {
				parent.addChildId(organization.getId());
				parent = organizationRepo.save(parent);
			}
		}
		return organization;
	}	

	private OrganizationRank findAndSaveOrganizationRank(String name, String campaignId, String organizationId, 
			Map<String, Campaign> campaignMap, String description, Organization parent) {
		OrganizationRank organizationRank = organizationRankRepo.findOneByNameAndOrganizationId(name, campaignId);
		if (organizationRank == null) {
			organizationRank = new OrganizationRank(name, campaignId, organizationId);
			organizationRank.setDescription(description);
			// Need to get an id on territory so all the parent/child links can be set
			String parentId = null;
			if (parent != null) {
				parentId = parent.getId();
			}
			organizationRank.setParentId(parentId);
			organizationRank = organizationRankRepo.save(organizationRank);
			if (parent != null) {
				parent.addChildId(organizationRank.getId());
				parent = organizationRepo.save(parent);
			}
		}
		return organizationRank;
	}	

}
