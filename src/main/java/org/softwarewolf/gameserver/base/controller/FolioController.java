package org.softwarewolf.gameserver.base.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.SelectFolioCreator;
import org.softwarewolf.gameserver.base.domain.helper.ViewFolioCreator;
import org.softwarewolf.gameserver.base.service.FolioService;

@Controller
@RequestMapping("/shared")
public class FolioController {
	@Autowired
	protected FolioService folioService;
	
	@RequestMapping(value = "/selectFolio", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String selectFolio(HttpSession session, SelectFolioCreator selectFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioCreator);
		// Need to pick the correct forwarding url, this is just generic
		selectFolioCreator.setForwardingUrl(ControllerHelper.GET_FOLIO);
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/folio/addTagToSearch", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String addTagToSearch(HttpSession session, SelectFolioCreator selectFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioCreator); 
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/folio/removeTagFromSearch", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String removeTagFromSearch(HttpSession session, SelectFolioCreator selectFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioCreator); 
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/viewFolio", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String viewFolio(HttpSession session, ViewFolioCreator viewFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		viewFolioCreator.setForwardingUrl(ControllerHelper.VIEW_FOLIO);
		return ControllerHelper.VIEW_FOLIO;
	}

}
