package org.softwarewolf.gameserver.base.controller;

import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.softwarewolf.gameserver.base.service.DataSeeder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AppAdminController {
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private DataSeeder dataSeeder;
	
	@RequestMapping("/seedData")
	@Secured({"ADMIN"})
	public String seedDb() {
		dataSeeder.cleanRepos();
		dataSeeder.seedData();

		return "user/menu";
	}

	@RequestMapping("/getSettings")
	@Secured({"ADMIN"})
	public String changeAppSettings() {
		
		return "admin/settings";
	}
}