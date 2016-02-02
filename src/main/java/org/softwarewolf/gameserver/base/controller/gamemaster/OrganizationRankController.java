package org.softwarewolf.gameserver.base.controller.gamemaster;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
public class OrganizationRankController {
			
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@Autowired
	protected LocationService locationService;
	
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
	public String editOrganizationRank(HttpSession session,  
			final OrganizationRankCreator organizationRankCreator, final FeFeedback feFeedback, 
			@RequestParam(value="id", required= false) String organizationRankId) {
		// If we haven't selected a campaign, get to the menu!
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		organizationRankService.initOrganizationRankCreator(organizationRankId, organizationRankCreator, campaignId, ControllerHelper.EDIT_ORGANIZATION_RANK);
		String organizationId = null;
		// organizationRank id 0 = add new organizationRank;
		if ("0".equals(organizationRankId) || organizationRankId == null) {
			feFeedback.setUserStatus("You are editing a new organization rank");
		} else {
			String name = organizationRankCreator.getOrganizationRank().getName();
			feFeedback.setUserStatus("You are editing " + name);
		}
		
		OrganizationRank orgRank = organizationRankCreator.getOrganizationRank();
		String orgRankName = null;
		if (orgRank != null && orgRank.getName() != "ROOT" && orgRank.getName() != "") {
			orgRankName = orgRank.getName();
		}
		organizationRankService.initOrganizationRankCreator(organizationId, orgRankName, organizationRankCreator, campaignId, ControllerHelper.EDIT_ORGANIZATION_RANK);

		return ControllerHelper.EDIT_ORGANIZATION_RANK;
	}
	
	@RequestMapping(value = "/getOrganizationRank", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	@ResponseBody
	public String getOrganizationRank(HttpSession session,
			final FeFeedback feFeedback, @RequestParam(value="id", required=false) String id, 
			@ModelAttribute("organizationRank") OrganizationRank organizationRank) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		// id = 0 is add a new organization
		if (id == null || "0".equals(id)) {
			organizationRank = new OrganizationRank();
		} else if (!("".equals(id))) {
			organizationRank = organizationRankService.get(id);
		}
		// ToDo: Add error handling for no location found
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

	@RequestMapping(value = "/selectOrganization", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	@ResponseBody
	public String selectOrganization(HttpSession session,
			final FeFeedback feFeedback, @RequestParam(value="id", required=true) String organizationId, 
			@ModelAttribute("organizationRankCreator") OrganizationRankCreator organizationRankCreator) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		
		List<OrganizationRank> orgRankList = organizationRankService.getOrganizationRankList(organizationId);
		organizationRankCreator.setOrganizationRanksInOrganization(orgRankList);
		String orgRankTree = organizationRankService.getOrganizationRankTree(campaignId, organizationId);
		organizationRankCreator.setOrganizationRankTreeJson(orgRankTree);
		
		ObjectMapper objectMapper = new ObjectMapper();
		String out = "{}";
		
		if (!orgRankList.isEmpty()) {
			try {
				out = objectMapper.writeValueAsString(organizationRankCreator);
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
			final FeFeedback feFeedback) {
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
			return ControllerHelper.EDIT_ORGANIZATION_RANK;
		}

		organizationRank.setCampaignId(campaignId);
		try {
			if ("".equals(organizationRank.getId())) {
				organizationRank.setId(null);
			} 
			organizationRank = organizationRankService.saveOrganizationRank(organizationRank);
			organizationRankService.initOrganizationRankCreator(null, null, 
					organizationRankCreator, campaignId, organizationRankCreator.getForwardingUrl());
			feFeedback.setInfo("Success, you have added organization rank " + organizationRank.getName());
			feFeedback.setUserStatus("You are creating a new organization rank");
		} catch (IllegalArgumentException e) {
			feFeedback.setError(e.getMessage());
			return ControllerHelper.EDIT_ORGANIZATION_RANK;
		}
		return ControllerHelper.EDIT_ORGANIZATION_RANK;
	}
}