package org.softwarewolf.gameserver.base.controller;

import org.softwarewolf.gameserver.base.repository.CampaignRepository;
import org.softwarewolf.gameserver.base.repository.SimpleGrantedAuthorityRepository;
import org.softwarewolf.gameserver.base.repository.TerritoryRepository;
import org.softwarewolf.gameserver.base.repository.TerritoryTypeRepository;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private SimpleGrantedAuthorityRepository sgaRepo;
	
	@Autowired 
	private CampaignRepository campaignRepo;
	
	@Autowired 
	private TerritoryTypeRepository territoryTypeRepo;
	
	@Autowired 
	private TerritoryRepository territoryRepo;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "/login";
	}

	@RequestMapping(value = "/user/menu", method = RequestMethod.GET)
	public String getMenu() {
		return "/user/menu";
	}
	
	
}
