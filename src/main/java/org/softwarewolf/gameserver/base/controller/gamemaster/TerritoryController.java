package org.softwarewolf.gameserver.base.controller.gamemaster;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.Territory;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryCreator;
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

	@RequestMapping(value = "/editTerritory", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String editTerritory(HttpSession session, final TerritoryCreator territoryCreator, 
			final FeFeedback feFeedback, @RequestParam(value="id", required= false) String territoryId) {
		// If we haven't selected a campaign, get to the menu!
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		// In this case, the territory object just has the id and name, we need everything here.
		Territory territory = null;
		if (territoryId != null && territoryId != "" && territoryId != "0") {
			territory = territoryCreator.getTerritory();
			territory.setId(territoryId);
			territory = territoryService.findOneTerritory(territory.getId());
		} else {
			territory = new Territory();
			feFeedback.setInfo2("You are creating a new territory");
		}
		territoryService.initTerritoryCreator(territory, territoryCreator, campaignId, ControllerHelper.EDIT_TERRITORY);
		return ControllerHelper.EDIT_TERRITORY;
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
		if ("0".equals(territoryId) || territoryId == null) {
			territory = new Territory();
			feFeedback.setInfo2("You are creating a new territory");
		} else if (!("".equals(territoryId))) {
			territory = territoryService.findOneTerritory(territoryId);
			feFeedback.setInfo2("You are editing " + territory.getName());
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
			return ControllerHelper.EDIT_TERRITORY;
		}
		
		try {
			String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
			territoryService.saveTerritory(territory);
			String territoryId = null;
			territoryService.initTerritoryCreator(territoryId, territoryCreator, campaignId, territoryCreator.getForwardingUrl());
			feFeedback.setInfo("Success, you've created " + territory.getName());
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return ControllerHelper.EDIT_TERRITORY;
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