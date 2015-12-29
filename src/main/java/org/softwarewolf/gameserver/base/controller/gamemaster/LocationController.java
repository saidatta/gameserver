package org.softwarewolf.gameserver.base.controller.gamemaster;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.Location;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.LocationCreator;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.CampaignService;
import org.softwarewolf.gameserver.base.service.OrganizationRankService;
import org.softwarewolf.gameserver.base.service.OrganizationService;
import org.softwarewolf.gameserver.base.service.LocationService;
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
public class LocationController {
			
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@Autowired
	protected LocationService locationService;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected OrganizationRankService organizationRankService;
	
	@Autowired
	protected UserService userService;

	private static final String CAMPAIGN_ID = "campaignId";

	@RequestMapping(value = "/editLocation", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String editLocation(HttpSession session, final LocationCreator locationCreator, 
			final FeFeedback feFeedback, @RequestParam(value="id", required= false) String locationId) {
		// If we haven't selected a campaign, get to the menu!
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		// In this case, the location object just has the id and name, we need everything here.
		Location location = null;
		if (locationId != null && locationId != "" && locationId != "0") {
			location = locationCreator.getLocation();
			location.setId(locationId);
			location = locationService.findOne(location.getId());
		} else {
			location = new Location();
			feFeedback.setInfo2("You are creating a new location");
		}
		locationService.initLocationCreator(location, locationCreator, campaignId, ControllerHelper.EDIT_LOCATION);
		return ControllerHelper.EDIT_LOCATION;
	}
	
	/**
	 * Ajax call to get just the data on a location when a user clicks on a location
	 * in the edit location drop-down
	 * @param session
	 * @param locationCreator
	 * @param feFeedback
	 * @param locationId
	 * @param location
	 * @return
	 */
	@RequestMapping(value = "/getLocation", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	@ResponseBody
	public String editlocationId(HttpSession session, final LocationCreator locationCreator, 
			final FeFeedback feFeedback, @RequestParam(value="hiddenLocationId", required= true) String locationId, 
			@ModelAttribute("location") Location location) {
		// If we haven't selected a campaign, get to the menu!
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		// id = 0 is add a new location
		if ("0".equals(locationId) || locationId == null) {
			location = new Location();
			feFeedback.setInfo2("You are creating a new location");
		} else if (!("".equals(locationId))) {
			location = locationService.findOne(locationId);
			feFeedback.setInfo2("You are editing " + location.getName());
		}
		// ToDo: Add error handling for no location found
		ObjectMapper objectMapper = new ObjectMapper();
		String out = "{}";
		if (location != null) {
			try {
				out = objectMapper.writeValueAsString(location);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return out;
	}
	
	@RequestMapping(value = "/editLocation", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postEditLocation(HttpSession session, final LocationCreator locationCreator, 
			final FeFeedback feFeedback) {
		// If we haven't selected a campaign, get to the menu!
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		Location location = locationCreator.getLocation();
		if ("".equals(location.getParentId())) {
			location.setParentId(null);
		}
		String errorMsg = validateLocation(location);
		if (errorMsg.length() > 0) {
			feFeedback.setError(errorMsg.toString());
			locationService.initLocationCreator(location.getId(), locationCreator, location.getCampaignId(), locationCreator.getForwardingUrl());
			return ControllerHelper.EDIT_LOCATION;
		}
		
		try {
			locationService.saveLocation(location);
			String locationId = null;
			locationService.initLocationCreator(locationId, locationCreator, campaignId, locationCreator.getForwardingUrl());
			feFeedback.setInfo("Success, you've created " + location.getName());
			feFeedback.setInfo2("You are creating a new location");
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			String locationId = null;
			locationService.initLocationCreator(locationId, locationCreator, campaignId, locationCreator.getForwardingUrl());
			feFeedback.setInfo2("You are creating a new location");
			return ControllerHelper.EDIT_LOCATION;
		}
		return locationCreator.getForwardingUrl();
	}

	private String validateLocation(final Location location) {
		StringBuilder errorMsg = new StringBuilder(); 
		if (location.getName().isEmpty()) {
			addMessage(errorMsg, "You must have a location name.");
		}
		if (location.getGameDataTypeId() == null || location.getGameDataTypeId().isEmpty()) {
			addMessage(errorMsg, "You must have a location type.");
		}
		if (location.getDescription().isEmpty()) {
			addMessage(errorMsg, "You must have a description.");
		}
//		if (location.getParentId().isEmpty()) {
//			addMessage(errorMsg, "You must have a parent location");
//		}
		return errorMsg.toString();
	}
	
	private void addMessage(StringBuilder builder, String message) {
		if (builder.length() > 0) {
			builder.append("\n");
		}
		builder.append(message);
	}
		
}