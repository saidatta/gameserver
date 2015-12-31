package org.softwarewolf.gameserver.base.controller.gamemaster;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.Folio;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationCreator;
import org.softwarewolf.gameserver.base.domain.helper.OrganizationTypeCreator;
import org.softwarewolf.gameserver.base.domain.helper.FolioCreator;
import org.softwarewolf.gameserver.base.domain.helper.LocationCreator;
import org.softwarewolf.gameserver.base.domain.helper.LocationTypeCreator;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.CampaignService;
import org.softwarewolf.gameserver.base.service.OrganizationRankService;
import org.softwarewolf.gameserver.base.service.OrganizationService;
import org.softwarewolf.gameserver.base.service.OrganizationTypeService;
import org.softwarewolf.gameserver.base.service.FolioService;
import org.softwarewolf.gameserver.base.service.LocationService;
import org.softwarewolf.gameserver.base.service.LocationTypeService;
import org.softwarewolf.gameserver.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gamemaster")
public class GamemasterController {
			
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
	public OrganizationTypeService organizationTypeService;
	
	@Autowired
	protected OrganizationRankService organizationRankService;
	
	@Autowired
	protected UserService userService;
	
	@Autowired
	protected FolioService folioService;

	private static final String CAMPAIGN_ID = "campaignId";
	
	@RequestMapping(value = "/ckeditor", method = RequestMethod.GET)
	@Secured({"USER"})
	public String ckeditor() {

		return "/gamemaster/ckeditor";
	}

	@RequestMapping(value = "/editFolio", method = RequestMethod.GET)
	@Secured({"USER"})
	public String editFolio(HttpSession session, FolioCreator folioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioService.findAll().get(0);
		folioService.initFolioCreator(folioCreator, folio);
		return ControllerHelper.EDIT_FOLIO;
	}
	
	@RequestMapping(value = "/removeTagFromFolio/{folioId}/{tagId}", method = RequestMethod.GET)
	@Secured({"USER"})
	public String removeTagFromFolio(HttpSession session, FolioCreator folioCreator, 
			@PathVariable String folioId, @PathVariable String tagId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioService.removeTagFromFolio(folioId, tagId);
		folioService.initFolioCreator(folioCreator, folio);
		feFeedback.setInfo("You have modified folio " + folio.getTitle());
		return ControllerHelper.EDIT_FOLIO;
	}

	@RequestMapping(value = "/addTagToFolio/{folioId}/{tagId}", method = RequestMethod.GET)
	@Secured({"USER"})
	public String addTagFromFolio(HttpSession session, FolioCreator folioCreator, 
			@PathVariable String folioId, @PathVariable String tagId, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioService.addTagToFolio(folioId, tagId);
		folioService.initFolioCreator(folioCreator, folio);
		feFeedback.setInfo("You have modified folio " + folio.getTitle());
		return ControllerHelper.EDIT_FOLIO;
	}

	@RequestMapping(value = "/editFolio", method = RequestMethod.POST)
	@Secured({"USER"})
	public String postEditPage(HttpSession session, Folio page) {

		return ControllerHelper.EDIT_FOLIO;
	}

	@RequestMapping(value = "/campaignHome", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String campaignHome(HttpSession session, LocationCreator locationCreator, 
			LocationTypeCreator locationTypeCreator, final OrganizationCreator organizationCreator,
			final OrganizationTypeCreator organizationTypeCreator, FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}
		String locationId = null;
		locationService.initLocationCreator(locationId, locationCreator, campaignId, ControllerHelper.CAMPAIGN_HOME);
		locationTypeService.initLocationTypeCreator(null, locationTypeCreator, campaignId, ControllerHelper.CAMPAIGN_HOME);

		String organizationId = null;
		organizationService.initOrganizationCreator(organizationId, organizationCreator, campaignId, ControllerHelper.CAMPAIGN_HOME);
		organizationTypeService.initOrganizationTypeCreator(null, organizationTypeCreator, campaignId,ControllerHelper. CAMPAIGN_HOME);

		return ControllerHelper.CAMPAIGN_HOME;
	}

}