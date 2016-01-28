package org.softwarewolf.gameserver.base.controller.gamemaster;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.Folio;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
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
		feFeedback.setInfo2("You are editing " + folio.getTitle());
		return ControllerHelper.EDIT_FOLIO;
	}
	
	@RequestMapping(value = "/getFolio/{folioId}", method = RequestMethod.GET)
	@Secured({"USER"})
	public String getFolio(HttpSession session, @PathVariable String folioId, 
			FolioCreator folioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioService.findOne(folioId);
		folioService.initFolioCreator(folioCreator, folio);
		feFeedback.setInfo2("You are editing '" + folio.getTitle() + "'");
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

	@RequestMapping(value = "/addTagToFolio/{folioId}/{className}/{tagId}", method = RequestMethod.GET)
	@Secured({"USER"})
	public String addTagToFolio(HttpSession session, FolioCreator folioCreator, 
			@PathVariable String folioId, @PathVariable String className, @PathVariable String tagId, 
			final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = null;
		if (folioId != null) {
			folio = folioService.findOne(folioId);
		}
		if (folio == null) {
			folio = new Folio();
			folio.setCampaignId(campaignId);
			folio.setTitle("Placeholder title");
		} 
		if (folio.getId() == null) {
			try {
				folio = folioService.save(folio);
			} catch (Exception e) {
				String errorMessage = e.getMessage();
				feFeedback.setError(errorMessage);
				folioService.initFolioCreator(folioCreator, folio);
				return ControllerHelper.EDIT_FOLIO;
			}
		}
		folio = folioService.addTagToFolio(folio.getId(), className, tagId);
		folioService.initFolioCreator(folioCreator, folio);
		feFeedback.setInfo("You have modified folio " + folio.getTitle());

		return ControllerHelper.EDIT_FOLIO;
	}

	@RequestMapping(value = "/editFolio", method = RequestMethod.POST)
	@Secured({"USER"})
	public String postEditPage(HttpSession session, FolioCreator folioCreator, 
			final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		

		Folio folio = folioCreator.getFolio();
		try {
			String selectedTags = folioCreator.getSelectedTags();
			if (selectedTags != null && !selectedTags.isEmpty()) {
				selectedTags = selectedTags.replaceAll("[", "");
				selectedTags = selectedTags.replaceAll("{", "");
				selectedTags = selectedTags.replaceAll("}", "");
				selectedTags = selectedTags.replaceAll("]", "");
				String[] splitStringArray = selectedTags.split(",");				
				List<ObjectTag> objectTagList = new ArrayList<>();
				for (String string : splitStringArray) {
					ObjectMapper mapper = new ObjectMapper();
					ObjectTag tag = mapper.convertValue(string, ObjectTag.class);
					objectTagList.add(tag);
				}
				folio.setTags(objectTagList);
			}
			folio = folioService.save(folio);
			folioService.initFolioCreator(folioCreator, folio);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			feFeedback.setError(errorMessage);
			folioService.initFolioCreator(folioCreator, folio);
			return ControllerHelper.EDIT_FOLIO;
		}
		
		feFeedback.setInfo("You have modified folio " + folio.getTitle());
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