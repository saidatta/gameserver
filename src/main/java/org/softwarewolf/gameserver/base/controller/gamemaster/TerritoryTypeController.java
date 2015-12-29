package org.softwarewolf.gameserver.base.controller.gamemaster;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.Location;
import org.softwarewolf.gameserver.base.domain.TerritoryType;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.base.domain.helper.LocationCreator;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryTypeCreator;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.CampaignService;
import org.softwarewolf.gameserver.base.service.OrganizationRankService;
import org.softwarewolf.gameserver.base.service.OrganizationService;
import org.softwarewolf.gameserver.base.service.LocationService;
import org.softwarewolf.gameserver.base.service.TerritoryTypeService;
import org.softwarewolf.gameserver.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/gamemaster")
public class TerritoryTypeController {
			
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@Autowired
	protected LocationService locationService;
	
	@Autowired
	protected TerritoryTypeService territoryTypeService;
	
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
			final LocationCreator locationCreator, final OrganizationCreator organizationCreator, 
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback,
			@RequestParam(value="forwardingUrl", required= false) String forwardingUrl) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		if (forwardingUrl == null || forwardingUrl.isEmpty()) {
			forwardingUrl = ControllerHelper.CREATE_TERRITORY_TYPE;
		}
		territoryTypeService.initTerritoryTypeCreator(null, territoryTypeCreator, campaignId, forwardingUrl);
		return ControllerHelper.CREATE_TERRITORY_TYPE;
	}
	
	@RequestMapping(value = "/createTerritoryType", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postTerritoryType(HttpSession session, final TerritoryTypeCreator territoryTypeCreator,
			final LocationCreator locationCreator, final FeFeedback feFeedback) {
		TerritoryType territoryType = territoryTypeCreator.getTerritoryType();
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String forwardingUrl = territoryTypeCreator.getForwardingUrl();
		if (campaignId != null) {
			territoryType.addCampaign(campaignId);
		}
		try {
			territoryTypeService.saveTerritoryType(territoryType);
			territoryTypeService.initTerritoryTypeCreator(territoryType.getId(), territoryTypeCreator, campaignId, forwardingUrl);
			Location location = locationCreator.getLocation();
			String territoryId = location.getId();
			locationService.initLocationCreator(territoryId, locationCreator, campaignId, locationCreator.getForwardingUrl());
			feFeedback.setInfo("Success, you have created a location type");
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/addTerritoryTypeToCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addTerritoryTypeToCampaign(HttpSession session, final TerritoryTypeCreator territoryTypeCreator,
			final LocationCreator locationCreator, final OrganizationCreator organizationCreator, 
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String addTerritoryTypeId = territoryTypeCreator.getAddGameDataTypeId();
		String forwardingUrl = territoryTypeCreator.getForwardingUrl();
		try {
			if (addTerritoryTypeId != null) {
				TerritoryType territoryType = territoryTypeService.findOne(addTerritoryTypeId);
				territoryType.addCampaign(campaignId);
				territoryTypeService.saveTerritoryType(territoryType);
				territoryTypeService.initTerritoryTypeCreator(territoryType.getId(), territoryTypeCreator, campaignId, forwardingUrl);
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
			final LocationCreator locationCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String removeTerritoryTypeId = territoryTypeCreator.getRemoveGameDataTypeId();
		String forwardingUrl = territoryTypeCreator.getForwardingUrl();
		try {
			if (removeTerritoryTypeId != null) {
				TerritoryType territoryType = territoryTypeService.findOne(removeTerritoryTypeId);
				territoryType.removeCampaign(campaignId);
				territoryTypeService.saveTerritoryType(territoryType);
				territoryTypeService.initTerritoryTypeCreator(null, territoryTypeCreator, campaignId, forwardingUrl);
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return forwardingUrl;
	}

	@RequestMapping(value = "/addTerritoryTypeToTerritory", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addTerritoryTypeToTerritory(HttpSession session, final LocationCreator locationCreator, 
			final TerritoryTypeCreator territoryTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String addLocationTypeId = locationCreator.getAddGameDataTypeId();
		String forwardingUrl = locationCreator.getForwardingUrl();
		try {
			if (addLocationTypeId != null) {
				TerritoryType territoryType = territoryTypeService.findOne(addLocationTypeId);
				if (territoryType == null) {
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