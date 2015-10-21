package org.softwarewolf.gameserver.base.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;
import javax.ws.rs.PathParam;

import org.codehaus.jackson.map.ObjectMapper;
import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.Territory;
import org.softwarewolf.gameserver.base.domain.TerritoryType;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationRankCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.base.domain.helper.SelectCampaignHelper;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryCreator;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryTypeCreator;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.CampaignService;
import org.softwarewolf.gameserver.base.service.OrganizationService;
import org.softwarewolf.gameserver.base.service.TerritoryService;
import org.softwarewolf.gameserver.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/gamemaster")
public class GamemasterController {
	private static final String CAMPAIGN_HOME = "/gamemaster/campaignHome";
	private static final String USER_MENU = "/user/menu";
	private static final String CREATE_TERRITORY = "/gamemaster/createType";
	private static final String CREATE_TERRITORY_TYPE = "/gamemaster/createTerritoryType";
			
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@Autowired
	protected TerritoryService territoryService;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected UserService userService;

	private static final String CAMPAIGN_ID = "campaignId";
	
	@RequestMapping(value = "/selectCampaign", method = RequestMethod.GET)
	@Secured({"USER"})
	public String selectCampaign(final SelectCampaignHelper selectCampaignHelper) {
		UserDetails userDetails =
				 (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
		String userId = userService.getUserIdFromUsername(username);
		campaignService.initSelectCampaignHelperByGM(selectCampaignHelper, userId);

		return "/user/selectCampaign";
	}
	
	@RequestMapping(value = "/selectCampaign", method = RequestMethod.POST)
	@Secured({"USER"})
	public String selectCampaign(HttpSession session, final SelectCampaignHelper selectCampaignHelper) {
		String campaignId = selectCampaignHelper.getSelectedCampaignId(); 
		String campaignName = selectCampaignHelper.getSelectedCampaignName();
		session.setAttribute(CAMPAIGN_ID, campaignId);
		session.setAttribute("campaignName", campaignName);
		
		return USER_MENU;
	}

	@RequestMapping(value = "/createTerritoryType", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String createTerritoryType(HttpSession session, final TerritoryTypeCreator territoryTypeCreator,
			final TerritoryCreator territoryCreator, final OrganizationCreator organizationCreator, 
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return USER_MENU;
		}
		
		String forwardingUrl = CREATE_TERRITORY_TYPE;
		TerritoryType territoryType = new TerritoryType();
		territoryService.initTerritoryTypeCreator(territoryType, territoryTypeCreator, campaignId, forwardingUrl);
		return forwardingUrl;
	}
	
	@RequestMapping(value = "/createTerritoryType", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postTerritoryType(HttpSession session, final TerritoryTypeCreator territoryTypeCreator,
			final TerritoryCreator territoryCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback) {
		TerritoryType territoryType = territoryTypeCreator.getTerritoryType();
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String forwardingUrl = territoryTypeCreator.getForwardingUrl();
		if (campaignId != null) {
			territoryType.addCampaign(campaignId);
		}
		try {
			territoryService.saveTerritoryType(territoryType);
			territoryService.initTerritoryTypeCreator(new TerritoryType(), territoryTypeCreator, campaignId, forwardingUrl);
			feFeedback.setInfo("Success, you have created a territory type");
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/addTerritoryTypeToCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addTerritoryTypeToCampaign(HttpSession session, final TerritoryTypeCreator territoryTypeCreator,
			final TerritoryCreator territoryCreator, final OrganizationCreator organizationCreator, 
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String addTerritoryTypeId = territoryTypeCreator.getAddGameDataTypeId();
		String forwardingUrl = territoryTypeCreator.getForwardingUrl();
		try {
			if (addTerritoryTypeId != null) {
				TerritoryType territoryType = territoryService.getTerritoryTypeById(addTerritoryTypeId);
				territoryType.addCampaign(campaignId);
				territoryService.saveTerritoryType(territoryType);
				territoryService.initTerritoryTypeCreator(new TerritoryType(), territoryTypeCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/removeTerritoryTypeFromCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String removeTerritoryTypeFromCampaign(HttpSession session, final TerritoryTypeCreator territoryTypeCreator,
			final TerritoryCreator territoryCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String removeTerritoryTypeId = territoryTypeCreator.getRemoveGameDataTypeId();
		String forwardingUrl = territoryTypeCreator.getForwardingUrl();
		try {
			if (removeTerritoryTypeId != null) {
				TerritoryType territoryType = territoryService.getTerritoryTypeById(removeTerritoryTypeId);
				territoryType.removeCampaign(campaignId);
				territoryService.saveTerritoryType(territoryType);
				territoryService.initTerritoryTypeCreator(new TerritoryType(), territoryTypeCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/createTerritory", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String createTerritory(HttpSession session, final TerritoryCreator territoryCreator, 
			final TerritoryTypeCreator territoryTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return USER_MENU;
		}
		
		String forwardingUrl = "/gamemaster/createTerritory";
		territoryService.initTerritoryCreator(territoryCreator, campaignId, forwardingUrl);
		territoryCreator.setForwardingUrl(forwardingUrl);
		territoryService.initTerritoryTypeCreator(new TerritoryType(), territoryTypeCreator, campaignId, forwardingUrl);
		return forwardingUrl;
	}
	
	@RequestMapping(value = "/createTerritory", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postTerritory(HttpSession session, final TerritoryCreator territoryCreator, 
			final TerritoryTypeCreator territoryTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback) {
		Territory territory = territoryCreator.getTerritory();
		StringBuilder errorMsg = new StringBuilder(); 
		if (territory.getName().isEmpty()) {
			errorMsg.append("You must have a territory name.");
		}
		if (territory.getGameDataTypeId() == null || territory.getGameDataTypeId().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have a territory type.");
		}
		if (territory.getDescription().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have a description.");
		}
		if (territory.getParentId().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have a parent territory");
		}
		if (errorMsg.length() > 0) {
			feFeedback.setError(errorMsg.toString());
			return "/gamemaster/createTerritory";
		}
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId != null) {
			territory.setCampaignId(campaignId);
		}
		try {
			territoryService.saveTerritory(territory);
			territoryService.initTerritoryCreator(territoryCreator, campaignId, territoryCreator.getForwardingUrl());
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return "/gamemaster/createTerritory";
		}
		return "/gamemaster/territoryCreated";
	}

	@RequestMapping(value = "/addTerritoryTypeToTerritory", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addTerritoryTypeToTerritory(HttpSession session, final TerritoryCreator territoryCreator, 
			final TerritoryTypeCreator territoryTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String addTerritoryTypeId = territoryCreator.getAddGameDataTypeId();
		String forwardingUrl = territoryCreator.getForwardingUrl();
		try {
			if (addTerritoryTypeId != null) {
				TerritoryType territoryType = territoryService.getTerritoryTypeById(addTerritoryTypeId);
				if (territoryType == null) {
					feFeedback.setError("Invalid territory type.");
					return forwardingUrl;
				}
				territoryCreator.getTerritory().setGameDataTypeId(addTerritoryTypeId);
				territoryService.initTerritoryCreator(territoryCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/createOrganization", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String createOrganization(HttpSession session, final OrganizationCreator organizationCreator, 
			final OrganizationTypeCreator organizationTypeCreator, final TerritoryCreator territoryCreator,
			final TerritoryTypeCreator territoryTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return USER_MENU;
		}
		
		String forwardingUrl = "/gamemaster/createOrganization";
		organizationService.initOrganizationCreator(organizationCreator, campaignId, forwardingUrl);
		organizationCreator.setForwardingUrl(forwardingUrl);
		organizationService.initOrganizationTypeCreator(new OrganizationType(), organizationTypeCreator, campaignId, forwardingUrl);
		return forwardingUrl;
	}
	
	@RequestMapping(value = "/createOrganization", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postOrganization(HttpSession session, final OrganizationCreator organizationCreator, 
			final OrganizationTypeCreator organizationTypeCreator, final TerritoryCreator territoryCreator,
			final TerritoryTypeCreator territoryTypeCreator, final FeFeedback feFeedback) {
		Organization organization = organizationCreator.getOrganization();
		StringBuilder errorMsg = new StringBuilder(); 
		if (organization.getName().isEmpty()) {
			errorMsg.append("You must have a organization name.");
		}
		if (organization.getGameDataTypeId() == null || organization.getGameDataTypeId().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have an organization type.");
		}
		if (organization.getDescription().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have a description.");
		}
		if (organization.getParentId().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have a parent organization.");
		}
		if (errorMsg.length() > 0) {
			feFeedback.setError(errorMsg.toString());
			return CREATE_TERRITORY;
		}
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId != null) {
			organization.setCampaignId(campaignId);
		}
		try {
			organizationService.saveOrganization(organization);
			organizationService.initOrganizationCreator(organizationCreator, campaignId, organizationCreator.getForwardingUrl());
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return "/gamemaster/createOrganization";
		}
		return "/gamemaster/organizationCreated";
	}

	@RequestMapping(value = "/createOrganizationType", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String createOrganizationType(HttpSession session, final OrganizationTypeCreator organizationTypeCreator,
			final OrganizationCreator organizationCreator, final TerritoryCreator territoryCreator, 
			final TerritoryTypeCreator territoryTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return USER_MENU;
		}
		
		String forwardingUrl = "/gamemaster/createOrganizationType";
		OrganizationType organizationType = new OrganizationType();
		organizationService.initOrganizationTypeCreator(organizationType, organizationTypeCreator, campaignId, forwardingUrl);
		return forwardingUrl;
	}
	
	@RequestMapping(value = "/addOrganizationTypeToCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addOrganizationTypeToCampaign(HttpSession session, final OrganizationTypeCreator organizationTypeCreator,
			final OrganizationCreator organizationCreator, final TerritoryCreator territoryCreator, 
			final TerritoryTypeCreator territoryTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String addOrganizationTypeId = organizationTypeCreator.getAddGameDataTypeId();
		String forwardingUrl = organizationTypeCreator.getForwardingUrl();
		try {
			if (addOrganizationTypeId != null) {
				OrganizationType organizationType = organizationService.getOrganizationTypeById(addOrganizationTypeId);
				organizationType.addCampaign(campaignId);
				organizationService.saveOrganizationType(organizationType);
				organizationService.initOrganizationTypeCreator(new OrganizationType(), organizationTypeCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/removeOrganizationTypeFromCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String removeOrganizationTypeFromCampaign(HttpSession session, final OrganizationTypeCreator organizationTypeCreator,
			final OrganizationCreator organizationCreator, final TerritoryCreator territoryCreator, 
			final TerritoryTypeCreator territoryTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String removeOrganizationTypeId = organizationTypeCreator.getRemoveGameDataTypeId();
		String forwardingUrl = organizationTypeCreator.getForwardingUrl();
		try {
			if (removeOrganizationTypeId != null) {
				OrganizationType organizationType = organizationService.getOrganizationTypeById(removeOrganizationTypeId);
				organizationType.removeCampaign(campaignId);
				organizationService.saveOrganizationType(organizationType);
				organizationService.initOrganizationTypeCreator(new OrganizationType(), organizationTypeCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/createOrganizationType", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postOrganizationType(HttpSession session, final OrganizationTypeCreator organizationTypeCreator,
			final OrganizationCreator organizationCreator, final TerritoryCreator territoryCreator,
			final TerritoryTypeCreator territoryTypeCreator, final FeFeedback feFeedback) {
		OrganizationType organizationType = organizationTypeCreator.getOrganizationType();
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String forwardingUrl = organizationTypeCreator.getForwardingUrl();
		if (campaignId != null) {
			organizationType.addCampaign(campaignId);
		}
		try {
			organizationService.saveOrganizationType(organizationType);
			organizationService.initOrganizationTypeCreator(new OrganizationType(), organizationTypeCreator, campaignId, forwardingUrl);
			feFeedback.setInfo("Success, you have created a territory type");
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/addOrganizationTypeToOrganization", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addOrganizationTypeToOrganization(HttpSession session, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, final TerritoryCreator territoryCreator,
			final TerritoryTypeCreator territoryTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String addOrganizationTypeId = organizationCreator.getAddGameDataTypeId();
		String forwardingUrl = organizationCreator.getForwardingUrl();
		try {
			if (addOrganizationTypeId != null) {
				OrganizationType organizationType = organizationService.getOrganizationTypeById(addOrganizationTypeId);
				if (organizationType == null) {
					feFeedback.setError("Invalid organization type.");
					return forwardingUrl;
				}
				organizationCreator.getOrganization().setGameDataTypeId(addOrganizationTypeId);
				organizationService.initOrganizationCreator(organizationCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

//	@RequestMapping(value = "/removeOrganizationRankFromCampaign", method = RequestMethod.POST)
//	@Secured({"GAMEMASTER"})
//	public String removeOrganizationRankFromCampaign(HttpSession session, final OrganizationCreator organizationCreator,
//			final FeFeedback feFeedback) {
//		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
//		String removeOrganizationTypeId = organizationCreator.getRemoveGameDataTypeId();
//		String forwardingUrl = organizationCreator.getForwardingUrl();
//		try {
//			if (removeOrganizationTypeId != null) {
//				OrganizationType organizationType = organizationService.getOrganizationTypeById(removeOrganizationTypeId);
//				organizationType.removeCampaign(campaignId);
//				organizationService.saveOrganizationType(organizationType);
//				organizationService.initOrganizationCreator(organizationCreator, campaignId, forwardingUrl);
//			}
//		} catch (IllegalArgumentException e) {
//			feFeedback.setError(e.getMessage());
//			return forwardingUrl;
//		}
//		return forwardingUrl;
//	}
//
//	@RequestMapping(value = "/createOrganizationRank", method = RequestMethod.GET)
//	@Secured({"GAMEMASTER"})
//	public String createOrganizationRank(HttpSession session, final OrganizationCreator organizationCreator, 
//			final FeFeedback feFeedback) {
//		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
//		String forwardingUrl = "/gamemaster/createOrganizationType";
//		organizationService.initOrganizationCreator(organizationCreator, campaignId, forwardingUrl);
//		organizationCreator.setForwardingUrl(forwardingUrl);
//		return forwardingUrl;
//	}
//	
//	@RequestMapping(value = "/addOrganizationRankToCampaign", method = RequestMethod.POST)
//	@Secured({"GAMEMASTER"})
//	public String addOrganizationRankToCampaign(HttpSession session, final OrganizationCreator organizationCreator,
//			final FeFeedback feFeedback) {
//		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
//		String addOrganizationTypeId = organizationCreator.getAddGameDataTypeId();
//		String forwardingUrl = organizationCreator.getForwardingUrl();
//		try {
//			if (addOrganizationTypeId != null) {
//				OrganizationType organizationType = organizationService.getOrganizationTypeById(addOrganizationTypeId);
//				organizationType.addCampaign(campaignId);
//				organizationService.saveOrganizationType(organizationType);
//				organizationService.initOrganizationCreator(organizationCreator, campaignId, forwardingUrl);
//			}
//		} catch (IllegalArgumentException e) {
//			feFeedback.setError(e.getMessage());
//			return forwardingUrl;
//		}
//		return forwardingUrl;
//	}

//	@RequestMapping(value = "/createOrganizationRank", method = RequestMethod.POST)
//	@Secured({"GAMEMASTER"})
//	public String postOrganizationRank(HttpSession session, final OrganizationCreator organizationCreator) {
//		OrganizationType organizationType = (OrganizationType)organizationCreator.getGameDataType();
//		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
//		String forwardingUrl = organizationCreator.getForwardingUrl();
//		if (campaignId != null) {
//			organizationType.addCampaign(campaignId);
//		}
//		try {
//			organizationService.saveOrganizationType(organizationType);
//			organizationService.initOrganizationCreator(organizationCreator, campaignId, forwardingUrl);
//		} catch (IllegalArgumentException e) {
//			organizationCreator.setError(e.getMessage());
//			return forwardingUrl;
//		}
//		return forwardingUrl;
//	}

//	@RequestMapping(value = "/addOrganizationRankToOrganization", method = RequestMethod.POST)
//	@Secured({"GAMEMASTER"})
//	public String addOrganizationRankToOrganization(HttpSession session, final OrganizationCreator organizationCreator) {
//		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
//		String addOrganizationTypeId = organizationCreator.getAddGameDataTypeId();
//		String forwardingUrl = organizationCreator.getForwardingUrl();
//		try {
//			if (addOrganizationTypeId != null) {
//				TerritoryType territoryType = territoryService.getTerritoryTypeById(addOrganizationTypeId);
//				if (territoryType == null) {
//					organizationCreator.error = "Invalid territory type.";
//					return forwardingUrl;
//				}
//				organizationCreator.getOrganization().setGameDataTypeId(addOrganizationTypeId);
//				organizationService.initOrganizationCreator(organizationCreator, campaignId, forwardingUrl);
//			}
//		} catch (IllegalArgumentException e) {
//			organizationCreator.setError(e.getMessage());
//			return forwardingUrl;
//		}
//		return forwardingUrl;
//	}
	
	@RequestMapping(value = "/campaignHome", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String campaignHome(HttpSession session, TerritoryCreator territoryCreator, 
			TerritoryTypeCreator territoryTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return USER_MENU;
		}
		territoryService.initTerritoryCreator(territoryCreator, campaignId, CAMPAIGN_HOME);
		territoryService.initTerritoryTypeCreator(new TerritoryType(), territoryTypeCreator, campaignId, CAMPAIGN_HOME);

		organizationService.initOrganizationCreator(organizationCreator, campaignId, CAMPAIGN_HOME);
		organizationService.initOrganizationTypeCreator(new OrganizationType(), organizationTypeCreator, campaignId, CAMPAIGN_HOME);

		return CAMPAIGN_HOME;
	}
	

	@RequestMapping(value = "/createOrganizationRank", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String createOrganizationRank(HttpSession session, final OrganizationRankCreator organizationRankCreator, 
			final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return USER_MENU;
		}
		
		String forwardingUrl = "/gamemaster/createOrganizationRank";
		organizationRankCreator.setForwardingUrl(forwardingUrl);
		organizationService.initOrganizationRankCreator(organizationRankCreator, campaignId, forwardingUrl);
		return forwardingUrl;
	}
	
	@RequestMapping(value = "/createOrganizationRank", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postOrganizationRank(HttpSession session, final OrganizationRankCreator organizationRankCreator, 
			final FeFeedback feFeedback) {
		OrganizationRank organizationRank = organizationRankCreator.getOrganizationRank();
		StringBuilder errorMsg = new StringBuilder(); 
		if (organizationRank.getName().isEmpty()) {
			errorMsg.append("You must have a territory name.");
		}
		if (organizationRank.getDescription().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have a description.");
		}
		if (organizationRank.getParentId().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have a parent territory");
		}
		if (errorMsg.length() > 0) {
			feFeedback.setError(errorMsg.toString());
			return "/gamemaster/createOrganizationRank";
		}
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId != null) {
			organizationRank.setCampaignId(campaignId);
		}
		try {
			organizationService.saveOrganizationRank(organizationRank);
			organizationService.initOrganizationRankCreator(organizationRankCreator, campaignId, organizationRankCreator.getForwardingUrl());
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return "/gamemaster/createOrganizationRank";
		}
		return "/gamemaster/territoryCreated";
	}

	@RequestMapping(value = "/editTerritory", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String editTerritory(HttpSession session, final TerritoryCreator territoryCreator, 
			final TerritoryTypeCreator territoryTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback,
			@RequestParam(value="id", required= false) String territoryId) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return USER_MENU;
		}
		
		String forwardingUrl = "/gamemaster/editTerritory";
		// In this case, the territory object just has the id and name, we need everything here.
		if (territoryId != null && territoryId != "") {
			Territory territory = territoryCreator.getTerritory();
			territory.setId(territoryId);
			territory = territoryService.findOneTerritory(territory.getId());
		}
		territoryService.initTerritoryCreator(territoryCreator, campaignId, forwardingUrl);
		territoryCreator.setForwardingUrl(forwardingUrl);
		territoryService.initTerritoryTypeCreator(new TerritoryType(), territoryTypeCreator, campaignId, forwardingUrl);
		return forwardingUrl;
	}
	
	@RequestMapping(value = "/getTerritory", method = RequestMethod.GET)
//	@SendToUser(value = ClientMessagingEndpoints.MODEL_CONTROLLER, broadcast = false)
	@Secured({"GAMEMASTER"})
	@ResponseBody
	public String editTerritoryId(HttpSession session, final TerritoryCreator territoryCreator, 
			final TerritoryTypeCreator territoryTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback,
			@RequestParam(value="hiddenTerritoryId", required= false) String territoryId, @ModelAttribute("territory") Territory territory) {
		
		// In this case, the territory object just has the id and name, we need everything here.
		if (territoryId != null && territoryId != "") {
			territory = territoryService.findOneTerritory(territoryId);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		String out = "{}";
		if (territory != null) {
			try {
				out = objectMapper.writeValueAsString(territory);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return out;
	}
	
	@RequestMapping(value = "/editTerritory", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postEditTerritory(HttpSession session, final TerritoryCreator territoryCreator, 
			final TerritoryTypeCreator territoryTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback) {
		Territory territory = territoryCreator.getTerritory();
		StringBuilder errorMsg = new StringBuilder(); 
		if (territory.getName().isEmpty()) {
			errorMsg.append("You must have a territory name.");
		}
		if (territory.getGameDataTypeId() == null || territory.getGameDataTypeId().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have a territory type.");
		}
		if (territory.getDescription().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have a description.");
		}
		if (territory.getParentId().isEmpty()) {
			if (errorMsg.length() > 0) {
				errorMsg.append("\n");
			}
			errorMsg.append("You must have a parent territory");
		}
		if (errorMsg.length() > 0) {
			feFeedback.setError(errorMsg.toString());
			return "/gamemaster/createTerritory";
		}
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId != null) {
			territory.setCampaignId(campaignId);
		}
		try {
			territoryService.saveTerritory(territory);
			territoryService.initTerritoryCreator(territoryCreator, campaignId, territoryCreator.getForwardingUrl());
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return "/gamemaster/createTerritory";
		}
		return "/gamemaster/territoryCreated";
	}

	
}