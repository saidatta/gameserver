package org.softwarewolf.gameserver.base.controller.gamemaster;

import org.softwarewolf.gameserver.base.domain.Campaign;
import org.softwarewolf.gameserver.base.domain.User;
import org.softwarewolf.gameserver.base.domain.helper.CampaignCreator;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.CampaignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gamemaster")
public class CampaignController {
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected CampaignService campaignService;

	@RequestMapping(value = "/createCampaign", method = RequestMethod.GET)
	@Secured({"GAMEMASTER"})
	public String getCampaignCreator(CampaignCreator campaignCreator) {
		UserDetails userDetails =
				 (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String userName = userDetails.getUsername();
		User user = userRepository.findOneByUsername(userName);
		
		campaignService.initCampaignCreator(campaignCreator, user);
		
		return "/gamemaster/createCampaign";		
	}
	
	@RequestMapping(value = "/createCampaign", method = RequestMethod.POST)
	@Secured({"GAMEMASTER"})
	public String postCampaign(@ModelAttribute CampaignCreator campaignCreator, BindingResult bindingResult) {
		Campaign campaign = campaignCreator.getCampaign();
		String ownerId = campaignCreator.getOwnerId();
		campaign.setOwnerId(ownerId);
		campaignService.saveCampaign(campaign);
		
		return "/gamemaster/campaignCreated";
	}
}