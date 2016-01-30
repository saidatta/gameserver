package org.softwarewolf.gameserver.base.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.softwarewolf.gameserver.base.controller.helper.ControllerHelper;
import org.softwarewolf.gameserver.base.domain.helper.FeFeedback;
import org.softwarewolf.gameserver.base.domain.helper.SelectFolioCreator;
import org.softwarewolf.gameserver.base.service.FolioService;

@Controller
@RequestMapping("/shared")
public class FolioController {
	@Autowired
	protected FolioService folioService;
	
	@RequestMapping(value = "/selectFolio", method = RequestMethod.GET)
	public String selectFolio(HttpSession session, SelectFolioCreator selectFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioCreator); 
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/folio/addTagToSearch", method = RequestMethod.POST)
	public String addTagToSearch(HttpSession session, SelectFolioCreator selectFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioCreator); 
		return ControllerHelper.SELECT_FOLIO;
	}

	@RequestMapping(value = "/folio/removeTagFromSearch", method = RequestMethod.POST)
	public String removeTagFromSearch(HttpSession session, SelectFolioCreator selectFolioCreator, final FeFeedback feFeedback) {
		String campaignId = (String)session.getAttribute(ControllerHelper.CAMPAIGN_ID);
		if (campaignId == null) {
			return ControllerHelper.USER_MENU;
		}		
		
		folioService.initSelectFolioCreator(campaignId, selectFolioCreator); 
		return ControllerHelper.SELECT_FOLIO;
	}
}
