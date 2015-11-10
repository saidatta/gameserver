package org.softwarewolf.gameserver.base.controller;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.softwarewolf.gameserver.base.domain.Territory;
import org.softwarewolf.gameserver.base.domain.TerritoryType;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryCreator;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryTypeCreator;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.CampaignService;
import org.softwarewolf.gameserver.base.service.OrganizationRankService;
import org.softwarewolf.gameserver.base.service.OrganizationService;
import org.softwarewolf.gameserver.base.service.TerritoryService;
import org.softwarewolf.gameserver.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/gamemaster")
public class TerritoryController {
	private static final String USER_MENU = "/user/menu";
	private static final String EDIT_TERRITORY = "/gamemaster/editTerritory";
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
	protected OrganizationRankService organizationRankService;
	
	@Autowired
	protected UserService userService;

	private static final String CAMPAIGN_ID = "campaignId";
	
	@RequestMapping(value = "/createTerritoryType", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String createTerritoryType(HttpSession session, final TerritoryTypeCreator territoryTypeCreator,
			final TerritoryCreator territoryCreator, final OrganizationCreator organizationCreator, 
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback,
			@RequestParam(value="forwardingUrl", required= false) String forwardingUrl) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return USER_MENU;
		}
		
		if (forwardingUrl == null || forwardingUrl.isEmpty()) {
			forwardingUrl = CREATE_TERRITORY_TYPE;
		}
		territoryService.initTerritoryTypeCreator(null, territoryTypeCreator, campaignId, forwardingUrl);
		return CREATE_TERRITORY_TYPE;
	}
	
	@RequestMapping(value = "/createTerritoryType", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postTerritoryType(HttpSession session, final TerritoryTypeCreator territoryTypeCreator,
			final TerritoryCreator territoryCreator, final FeFeedback feFeedback) {
		TerritoryType territoryType = territoryTypeCreator.getTerritoryType();
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String forwardingUrl = territoryTypeCreator.getForwardingUrl();
		if (campaignId != null) {
			territoryType.addCampaign(campaignId);
		}
		try {
			territoryService.saveTerritoryType(territoryType);
			territoryService.initTerritoryTypeCreator(territoryType.getId(), territoryTypeCreator, campaignId, forwardingUrl);
			Territory territory = territoryCreator.getTerritory();
			String territoryId = territory.getId();
			territoryService.initTerritoryCreator(territoryId, territoryCreator, campaignId, territoryCreator.getForwardingUrl());
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
				territoryService.initTerritoryTypeCreator(territoryType.getId(), territoryTypeCreator, campaignId, forwardingUrl);
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
				territoryService.initTerritoryTypeCreator(null, territoryTypeCreator, campaignId, forwardingUrl);
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
		String territoryId = null;
		territoryService.initTerritoryCreator(territoryId, territoryCreator, campaignId, forwardingUrl);
		territoryCreator.setForwardingUrl(forwardingUrl);
		territoryService.initTerritoryTypeCreator(null, territoryTypeCreator, campaignId, forwardingUrl);
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
			territoryService.initTerritoryCreator(territory.getId(), territoryCreator, campaignId, territoryCreator.getForwardingUrl());
			feFeedback.setInfo("You have successfully created a territory");
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return "/gamemaster/createTerritory";
		}
		return territoryCreator.getForwardingUrl();
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
				territoryService.initTerritoryCreator(territoryCreator.getTerritory().getId(), territoryCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/editTerritory", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String editTerritory(HttpSession session, final TerritoryCreator territoryCreator, 
			final FeFeedback feFeedback, @RequestParam(value="id", required= false) String territoryId) {
		// If we haven't selected a campaign, get to the menu!
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return USER_MENU;
		}
		
		// In this case, the territory object just has the id and name, we need everything here.
		if (territoryId != null && territoryId != "") {
			Territory territory = territoryCreator.getTerritory();
			territory.setId(territoryId);
			territory = territoryService.findOneTerritory(territory.getId());
		}
		territoryService.initTerritoryCreator(territoryId, territoryCreator, campaignId, EDIT_TERRITORY);
		return EDIT_TERRITORY;
	}
	
	/**
	 * Ajax call to get just the data on a territory when a user clicks on a territory
	 * in the edit territory drop-down
	 * @param session
	 * @param territoryCreator
	 * @param territoryTypeCreator
	 * @param organizationCreator
	 * @param organizationTypeCreator
	 * @param feFeedback
	 * @param territoryId
	 * @param territory
	 * @return
	 */
	@RequestMapping(value = "/getTerritory", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	@ResponseBody
	public String editTerritoryId(HttpSession session, final TerritoryCreator territoryCreator, 
			final FeFeedback feFeedback, @RequestParam(value="hiddenTerritoryId", required= true) String territoryId, 
			@ModelAttribute("territory") Territory territory) {
		
		// id = 0 is add a new territory
		if ("0".equals(territoryId)) {
			territory = new Territory();
		} else if (!("".equals(territoryId))) {
			territory = territoryService.findOneTerritory(territoryId);
		}
		// ToDo: Add error handling for no territory found
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
			final FeFeedback feFeedback) {
		Territory territory = territoryCreator.getTerritory();

		String errorMsg = validateTerritory(territory);
		if (errorMsg.length() > 0) {
			feFeedback.setError(errorMsg.toString());
			territoryService.initTerritoryCreator(territory.getId(), territoryCreator, territory.getCampaignId(), territoryCreator.getForwardingUrl());
			return EDIT_TERRITORY;
		}
		
		try {
			String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
			territoryService.saveTerritory(territory);
			String territoryId = null;
			territoryService.initTerritoryCreator(territoryId, territoryCreator, campaignId, territoryCreator.getForwardingUrl());
			feFeedback.setInfo("Success, you've created " + territory.getName());
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return EDIT_TERRITORY;
		}
		return territoryCreator.getForwardingUrl();
	}

	private String validateTerritory(final Territory territory) {
		StringBuilder errorMsg = new StringBuilder(); 
		if (territory.getName().isEmpty()) {
			addMessage(errorMsg, "You must have a territory name.");
		}
		if (territory.getGameDataTypeId() == null || territory.getGameDataTypeId().isEmpty()) {
			addMessage(errorMsg, "You must have a territory type.");
		}
		if (territory.getDescription().isEmpty()) {
			addMessage(errorMsg, "You must have a description.");
		}
		if (territory.getParentId().isEmpty()) {
			addMessage(errorMsg, "You must have a parent territory");
		}
		return errorMsg.toString();
	}
	
	private void addMessage(StringBuilder builder, String message) {
		if (builder.length() > 0) {
			builder.append("\n");
		}
		builder.append(message);
	}
		
}