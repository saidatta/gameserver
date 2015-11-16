package org.softwarewolf.gameserver.base.controller.gamemaster;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationRankCreator;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/gamemaster")
public class OrganizationRankController {
			
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
	
	@RequestMapping(value = "/editOrganizationRank", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	@ResponseBody
	public String getOrganizationRank(HttpSession session,
			final FeFeedback feFeedback, @RequestParam(value="id", required= true) String id, 
			@ModelAttribute("organizationRank") OrganizationRank organizationRank) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		// id = 0 is add a new organization
		if ("0".equals(id)) {
			organizationRank = new OrganizationRank();
		} else if (!("".equals(id))) {
			organizationRank = organizationRankService.read(id);
		}
		// ToDo: Add error handling for no territory found
		ObjectMapper objectMapper = new ObjectMapper();
		String out = "{}";
		if (organizationRank != null) {
			try {
				out = objectMapper.writeValueAsString(organizationRank);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return out;
	}

	@RequestMapping(value = "/editOrganizationRank", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String editOrganizationRank(HttpSession session, final OrganizationRankCreator organizationRankCreator, 
			final FeFeedback feFeedback, final OrganizationCreator organizationCreator) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		OrganizationRank organizationRank = organizationRankCreator.getOrganizationRank();
		StringBuilder errorMsg = new StringBuilder(); 
		if (organizationRank.getName().isEmpty()) {
			errorMsg.append("You must have an organization rank name.");
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
			errorMsg.append("You must have a parent organization rank");
		}
		if (errorMsg.length() > 0) {
			feFeedback.setError(errorMsg.toString());
			return ControllerHelper.EDIT_ORGANIZATION;
		}

		organizationRank.setCampaignId(campaignId);
		try {
			Organization organization = organizationCreator.getOrganization();
			if ("".equals(organizationRank.getId())) {
				organizationRank.setId(null);
			}
			organizationRank.setOrganizationId(organization.getId());
			organizationRankService.saveOrganizationRank(organizationRank);
			organizationRankService.initOrganizationRankCreator(organizationRank.getOrganizationId(), organizationRank.getName(), 
					organizationRankCreator, campaignId, organizationRankCreator.getForwardingUrl());
			organizationService.initOrganizationCreator(organization, organizationCreator, campaignId, organizationCreator.getForwardingUrl());
			feFeedback.setInfo("Success, you have added organization rank " + organizationRank.getName());
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return ControllerHelper.EDIT_ORGANIZATION;
		}
		return ControllerHelper.EDIT_ORGANIZATION;
	}
}