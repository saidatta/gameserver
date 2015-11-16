package org.softwarewolf.gameserver.base.controller.gamemaster;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationRankCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.CampaignService;
import org.softwarewolf.gameserver.base.service.OrganizationRankService;
import org.softwarewolf.gameserver.base.service.OrganizationService;
import org.softwarewolf.gameserver.base.service.OrganizationTypeService;
import org.softwarewolf.gameserver.base.service.TerritoryService;
import org.softwarewolf.gameserver.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/gamemaster")
public class OrganizationTypeController {
			
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@Autowired
	protected TerritoryService territoryService;
	
	@Autowired
	protected OrganizationService organizationService;
	
	@Autowired
	public OrganizationTypeService organizationTypeService;
	
	@Autowired
	protected OrganizationRankService organizationRankService;
	
	@Autowired
	protected UserService userService;

	private static final String CAMPAIGN_ID = "campaignId";
	
	@RequestMapping(value = "/editOrganizationType", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String getOrganizationType(HttpSession session, final FeFeedback feFeedback,
			OrganizationTypeCreator organizationTypeCreator,
			@RequestParam(value="organizationTypeId", required= true) String organizationTypeId) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		if (organizationTypeId == "0") {
			organizationTypeId = null;
		}
		organizationTypeService.initOrganizationTypeCreator(organizationTypeId, organizationTypeCreator, campaignId, ControllerHelper.EDIT_ORGANIZATION);
		
		return "/gamemaster/createOrganizationType";
	}
	
	@RequestMapping(value = "/editOrganizationType", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String editOrganizationType(HttpSession session, final FeFeedback feFeedback,
			OrganizationTypeCreator organizationTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationRankCreator organizationRankCreator) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		OrganizationType organizationType = organizationTypeCreator.getOrganizationType();
		OrganizationType oldType = organizationTypeService.findOneByName(organizationType.getName());
		List<String> campaignList = new ArrayList<>();
		if (oldType != null) {
			organizationType.setId(oldType.getId());
			campaignList = organizationType.getCampaignList();
		}
		if (!campaignList.contains(campaignId)) {
			campaignList.add(campaignId);
		}
		organizationType.setCampaignList(campaignList);
		organizationTypeService.saveOrganizationType(organizationType);
		organizationTypeService.initOrganizationTypeCreator(organizationType.getId(), organizationTypeCreator, campaignId, ControllerHelper.EDIT_ORGANIZATION);
		organizationService.initOrganizationCreator(organizationCreator.getOrganization(), organizationCreator, campaignId, 
				organizationCreator.getForwardingUrl());
		
		return organizationCreator.getForwardingUrl();
	}

	@RequestMapping(value = "/addOrganizationTypeToCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addOrganizationTypeToCampaign(HttpSession session, final OrganizationTypeCreator organizationTypeCreator,
			final OrganizationCreator organizationCreator, final OrganizationRankCreator organizationRankCreator, 
			final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String addOrganizationTypeId = organizationTypeCreator.getAddGameDataTypeId();
		String forwardingUrl = organizationTypeCreator.getForwardingUrl();
		try {
			if (addOrganizationTypeId != null) {
				OrganizationType organizationType = organizationTypeService.getOrganizationTypeById(addOrganizationTypeId);
				organizationType.addCampaign(campaignId);
				organizationTypeService.saveOrganizationType(organizationType);
				organizationTypeService.initOrganizationTypeCreator(addOrganizationTypeId, organizationTypeCreator, 
						campaignId, organizationTypeCreator.getForwardingUrl());
				organizationRankService.initOrganizationRankCreator(null, null, organizationRankCreator, campaignId, 
						organizationRankCreator.getForwardingUrl());
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return ControllerHelper.CREATE_ORGANIZATION_TYPE;
	}

	@RequestMapping(value = "/removeOrganizationTypeFromCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String removeOrganizationTypeFromCampaign(HttpSession session, final OrganizationTypeCreator organizationTypeCreator,
			final OrganizationCreator organizationCreator, final OrganizationRankCreator organizationRankCreator, 
			final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		String removeOrganizationTypeId = organizationTypeCreator.getRemoveGameDataTypeId();
		String forwardingUrl = organizationTypeCreator.getForwardingUrl();
		try {
			if (removeOrganizationTypeId != null) {
				OrganizationType organizationType = organizationTypeService.getOrganizationTypeById(removeOrganizationTypeId);
				organizationType.removeCampaign(campaignId);
				organizationTypeService.saveOrganizationType(organizationType);
				organizationTypeService.initOrganizationTypeCreator(removeOrganizationTypeId, organizationTypeCreator, 
						campaignId, organizationTypeCreator.getForwardingUrl());
				organizationRankService.initOrganizationRankCreator(null, null, organizationRankCreator, campaignId,
						organizationRankCreator.getForwardingUrl());
			}
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return forwardingUrl;
		}
		return ControllerHelper.CREATE_ORGANIZATION_TYPE;
	}
}