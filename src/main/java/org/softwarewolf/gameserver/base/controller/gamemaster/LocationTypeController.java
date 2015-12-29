package org.softwarewolf.gameserver.base.controller.gamemaster;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.Location;
import org.softwarewolf.gameserver.base.domain.LocationType;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.base.domain.helper.LocationCreator;
import org.softwarewolf.gameserver.base.domain.helper.LocationTypeCreator;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.CampaignService;
import org.softwarewolf.gameserver.base.service.OrganizationRankService;
import org.softwarewolf.gameserver.base.service.OrganizationService;
import org.softwarewolf.gameserver.base.service.LocationService;
import org.softwarewolf.gameserver.base.service.LocationTypeService;
import org.softwarewolf.gameserver.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/gamemaster")
public class LocationTypeController {
			
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@Autowired
	protected LocationService locationService;
	
	@Autowired
	protected LocationTypeService locationTypeService;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	protected OrganizationRankService organizationRankService;
	
	@Autowired
	protected UserService userService;

	private static final String CAMPAIGN_ID = "campaignId";
	
	@RequestMapping(value = "/createLocationType", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String createLocationType(HttpSession session, final LocationTypeCreator locationTypeCreator,
			final LocationCreator locationCreator, final OrganizationCreator organizationCreator, 
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback,
			@RequestParam(value="forwardingUrl", required= false) String forwardingUrl) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		if (forwardingUrl == null || forwardingUrl.isEmpty()) {
			forwardingUrl = ControllerHelper.CREATE_LOCATION_TYPE;
		}
		locationTypeService.initLocationTypeCreator(null, locationTypeCreator, campaignId, forwardingUrl);
		return ControllerHelper.CREATE_LOCATION_TYPE;
	}
	
	@RequestMapping(value = "/createLocationType", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postLocationType(HttpSession session, final LocationTypeCreator locationTypeCreator,
			final LocationCreator locationCreator, final FeFeedback feFeedback) {
		LocationType locationType = locationTypeCreator.getLocationType();
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String forwardingUrl = locationTypeCreator.getForwardingUrl();
		if (campaignId != null) {
			locationType.addCampaign(campaignId);
		}
		try {
			locationTypeService.saveLocationType(locationType);
			locationTypeService.initLocationTypeCreator(locationType.getId(), locationTypeCreator, campaignId, forwardingUrl);
			Location location = locationCreator.getLocation();
			String locationId = location.getId();
			locationService.initLocationCreator(locationId, locationCreator, campaignId, locationCreator.getForwardingUrl());
			feFeedback.setInfo("Success, you have created a location type");
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/addLocationTypeToCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addLocationTypeToCampaign(HttpSession session, final LocationTypeCreator locationTypeCreator,
			final LocationCreator locationCreator, final OrganizationCreator organizationCreator, 
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String addLocationTypeId = locationTypeCreator.getAddGameDataTypeId();
		String forwardingUrl = locationTypeCreator.getForwardingUrl();
		try {
			if (addLocationTypeId != null) {
				LocationType locationType = locationTypeService.findOne(addLocationTypeId);
				locationType.addCampaign(campaignId);
				locationTypeService.saveLocationType(locationType);
				locationTypeService.initLocationTypeCreator(locationType.getId(), locationTypeCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/removeLocationTypeFromCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String removeLocationTypeFromCampaign(HttpSession session, final LocationTypeCreator locationTypeCreator,
			final LocationCreator locationCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String removeLocationTypeId = locationTypeCreator.getRemoveGameDataTypeId();
		String forwardingUrl = locationTypeCreator.getForwardingUrl();
		try {
			if (removeLocationTypeId != null) {
				LocationType locationType = locationTypeService.findOne(removeLocationTypeId);
				locationType.removeCampaign(campaignId);
				locationTypeService.saveLocationType(locationType);
				locationTypeService.initLocationTypeCreator(null, locationTypeCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/addLocationTypeToLocation", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addLocationTypeToLocation(HttpSession session, final LocationCreator locationCreator, 
			final LocationTypeCreator locationTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String addLocationTypeId = locationCreator.getAddGameDataTypeId();
		String forwardingUrl = locationCreator.getForwardingUrl();
		try {
			if (addLocationTypeId != null) {
				LocationType locationType = locationTypeService.findOne(addLocationTypeId);
				if (locationType == null) {
					feFeedback.setError("Invalid location type.");
					return forwardingUrl;
				}
				locationCreator.getLocation().setGameDataTypeId(addLocationTypeId);
				locationService.initLocationCreator(locationCreator.getLocation().getId(), locationCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}
}