package org.softwarewolf.gameserver.base.service;

import java.util.ArrayList;
import java.util.List;

import org.softwarewolf.gameserver.base.domain.Campaign;
import org.softwarewolf.gameserver.base.domain.User;
import org.softwarewolf.gameserver.base.domain.helper.CampaignCreator;
import org.softwarewolf.gameserver.base.domain.helper.SelectCampaignHelper;
import org.softwarewolf.gameserver.base.domain.helper.UserListItem;
import org.softwarewolf.gameserver.base.repository.CampaignRepository;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class CampaignService {
	@Autowired
	UserService userService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	CampaignRepository campaignRepository;
	
	public List<UserListItem> getGamemasters() {
		List<UserListItem> gamemasters = new ArrayList<>();
		List<User> userList = userRepository.findAll();
		for (User user : userList) {
			SimpleGrantedAuthority roleGamemaster = new SimpleGrantedAuthority("ROLE_GAMEMASTER");
			
			if (user.getAuthorities().contains(roleGamemaster)) {
				UserListItem item = new UserListItem(user.getId(), user.getUsername());
				gamemasters.add(item);
			}
		}
		return gamemasters;
	}
	
	public void initCampaignCreator(CampaignCreator campaignCreator, User user) {
		campaignCreator.setGamemasters(getGamemasters());
		campaignCreator.setOwnerId(user.getId());
		campaignCreator.setOwnerName(user.getUsername());
		campaignCreator.setCampaign(new Campaign(user.getId()));
	}
	
	public void saveCampaign(Campaign campaign) {
		campaignRepository.save(campaign);
	}
	
	public List<Campaign> getAllCampaigns() {
		List<Campaign> allCampaigns = campaignRepository.findAll();
		if (allCampaigns == null) {
			allCampaigns = new ArrayList<>();
		}
		return allCampaigns;
	}
	
	public void initSelectCampaignHelperByGM(SelectCampaignHelper selectCampaignHelper, String ownerId) {
		List<Campaign> campaigns = campaignRepository.findAllByKeyValue("ownerId", ownerId);
		selectCampaignHelper.setAllCampaigns(campaigns);
	}

	public void initSelectCampaignHelperByPlayer(SelectCampaignHelper selectCampaignHelper, String playerId) {
		selectCampaignHelper.setAllCampaigns(campaignRepository.findAll());
	}

	public void initSelectCampaignHelper(SelectCampaignHelper selectCampaignHelper) {
		selectCampaignHelper.setAllCampaigns(campaignRepository.findAll());
	}
	
	public List<Campaign> getAllCampaignsByGM(String ownerId) {
		return campaignRepository.findByOwnerId(ownerId);		
	}
}
