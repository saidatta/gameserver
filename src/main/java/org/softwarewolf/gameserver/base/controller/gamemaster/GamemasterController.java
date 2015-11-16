package org.softwarewolf.gameserver.base.controller.gamemaster;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationRankCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.base.domain.helper.SelectCampaignHelper;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryCreator;
import org.softwarewolf.gameserver.base.domain.helper.TerritoryTypeCreator;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.CampaignService;
import org.softwarewolf.gameserver.base.service.OrganizationRankService;
import org.softwarewolf.gameserver.base.service.OrganizationService;
import org.softwarewolf.gameserver.base.service.OrganizationTypeService;
import org.softwarewolf.gameserver.base.service.TerritoryService;
import org.softwarewolf.gameserver.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/gamemaster")
public class GamemasterController {
			
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
	
	@RequestMapping(value = "/ckeditor", method = RequestMethod.GET)
	@Secured({"USER"})
	public String ckeditor() {

		return "/gamemaster/ckeditor";
	}

	
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
		
		return ControllerHelper.USER_MENU;
	}

	@RequestMapping(value = "/campaignHome", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String campaignHome(HttpSession session, TerritoryCreator territoryCreator, 
			TerritoryTypeCreator territoryTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		String territoryId = null;
		territoryService.initTerritoryCreator(territoryId, territoryCreator, campaignId, ControllerHelper.CAMPAIGN_HOME);
		territoryService.initTerritoryTypeCreator(null, territoryTypeCreator, campaignId, ControllerHelper.CAMPAIGN_HOME);

		String organizationId = null;
		organizationService.initOrganizationCreator(organizationId, organizationCreator, campaignId, ControllerHelper.CAMPAIGN_HOME);
		organizationTypeService.initOrganizationTypeCreator(null, organizationTypeCreator, campaignId,ControllerHelper. CAMPAIGN_HOME);

		return ControllerHelper.CAMPAIGN_HOME;
	}

	private String validateOrganization(final Organization organization) {
		StringBuilder errorMsg = new StringBuilder(); 
		if (organization.getName().isEmpty()) {
			addMessage(errorMsg, "You must have a organization name.");
		}
		if (organization.getGameDataTypeId() == null || organization.getGameDataTypeId().isEmpty()) {
			addMessage(errorMsg, "You must have a terrorganizationitory type.");
		}
		if (organization.getDescription().isEmpty()) {
			addMessage(errorMsg, "You must have a description.");
		}
		if (organization.getParentId().isEmpty()) {
			organization.setParentId("ROOT");
		}
		return errorMsg.toString();
	}
	
	private void addMessage(StringBuilder builder, String message) {
		if (builder.length() > 0) {
			builder.append("\n");
		}
		builder.append(message);
	}
	
	@RequestMapping(value = "/editOrganization", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String editOrganization(HttpSession session, final OrganizationCreator organizationCreator, 
			final OrganizationRankCreator organizationRankCreator, final FeFeedback feFeedback, 
			@RequestParam(value="id", required= false) String organizationId) {
		// If we haven't selected a campaign, get to the menu!
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		// organization id 0 = add new organization;
		if ("0".equals(organizationId)) {
			organizationId = null;
		}
		organizationService.initOrganizationCreator(organizationId, organizationCreator, campaignId, ControllerHelper.EDIT_ORGANIZATION);
		
		OrganizationRank orgRank = organizationRankCreator.getOrganizationRank();
		String orgRankName = null;
		if (orgRank != null && orgRank.getName() != "ROOT" && orgRank.getName() != "") {
			orgRankName = orgRank.getName();
		}
		organizationRankService.initOrganizationRankCreator(organizationId, orgRankName, organizationRankCreator, campaignId, ControllerHelper.EDIT_ORGANIZATION);

		return ControllerHelper.EDIT_ORGANIZATION;
	}
	
	/**
	 * Ajax call to get just the data on a organization when a user clicks on an organization
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
	@RequestMapping(value = "/getOrganization", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	@ResponseBody
	public String getOrganization(HttpSession session, final OrganizationCreator organizationCreator, 
			final FeFeedback feFeedback, @RequestParam(value="hiddenOrganizationId", required= true) String organizationId) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		String out = "{}";
		if (!("0".equals(organizationId))) {
			out = organizationRankService.getOrganizationAndRanks(organizationId);
		}
		return out;
	}
	
	@RequestMapping(value = "/editOrganization", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postEditOrganization(HttpSession session, final OrganizationCreator organizationCreator, 
			final OrganizationRankCreator organizationRankCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		Organization organization = organizationCreator.getOrganization();

		String errorMsg = validateOrganization(organization);
		if (errorMsg.length() > 0) {
			feFeedback.setError(errorMsg.toString());
			organizationService.initOrganizationCreator(organization.getId(), organizationCreator, organization.getCampaignId(), organizationCreator.getForwardingUrl());
			return ControllerHelper.EDIT_ORGANIZATION;
		}
		
		try {
			Organization org = organizationService.saveOrganization(organization);
			String organizationId = org.getId();
			organizationService.initOrganizationCreator(organizationId, organizationCreator, campaignId, organizationCreator.getForwardingUrl());
			organizationRankService.initOrganizationRankCreator(organizationId, null, organizationRankCreator, campaignId, organizationCreator.getForwardingUrl());
			feFeedback.setInfo("Success, you've created " + organization.getName());
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return ControllerHelper.EDIT_ORGANIZATION;
		}
		return organizationCreator.getForwardingUrl();
	}	
}