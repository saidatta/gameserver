package org.softwarewolf.gameserver.base.controller;

import javax.servlet.http.HttpSession;

import org.softwarewolf.gameserver.base.domain.helper.SelectCampaignHelper;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;
	
	@RequestMapping(value = "/selectCampaign", method = RequestMethod.GET)
	@Secured({"USER"})
	public String getCampaign(SelectCampaignHelper selectCampaignHelper) {
		campaignService.initSelectCampaignHelper(selectCampaignHelper);
		return "/user/selectCampaign";
	}
	
	@RequestMapping(value = "/selectCampaign", method = RequestMethod.POST)
	@Secured({"USER"})
	public String postCampaign(HttpSession session, final SelectCampaignHelper selectCampaignHelper) {
		selectCampaignHelper.setAllCampaigns(campaignService.getAllCampaigns());
		String campaignId = selectCampaignHelper.getSelectedCampaignId(); 
		String campaignName = selectCampaignHelper.getSelectedCampaignName();
		session.setAttribute("campaignId", campaignId);
		session.setAttribute("campaignName", campaignName);
		
		return "/user/menu";
	}

}