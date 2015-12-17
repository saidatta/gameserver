package org.softwarewolf.gameserver.base.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.softwarewolf.gameserver.base.domain.Folio;
import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.Territory;
import org.softwarewolf.gameserver.base.domain.TerritoryType;
import org.softwarewolf.gameserver.base.domain.helper.FolioCreator;
import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.softwarewolf.gameserver.base.repository.FolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolioService implements Serializable {
	@Autowired
	private FolioRepository folioRepository;

	@Autowired
	private OrganizationService orgainzationService;
	@Autowired
	private OrganizationRankService organizationRankService;
	@Autowired
	private OrganizationTypeService organizationTypeService;
	@Autowired
	private TerritoryService territoryService;
	@Autowired
	private TerritoryTypeService territoryTypeService;
	
	private static final long serialVersionUID = 1L;

	public Folio save(Folio folio) {
		return folioRepository.save(folio);
	}
	
	public List<Folio> findAll() {
		return folioRepository.findAll();
	}
	
	public void initFolioCreator(FolioCreator folioCreator, Folio folio) {
		String campaignId = folio.getCampaignId();
		folioCreator.setFolio(folio);
		List<Organization> orgList = orgainzationService.findAllByCampaignId(campaignId);
		List<OrganizationRank> orgRankList = organizationRankService.findAllByCampaignId(campaignId);
		List<OrganizationType> orgTypeList = organizationTypeService.getOrganizationTypesInCampaign(campaignId);
		List<Territory> territoryList = territoryService.getTerritoriesInCampaign(campaignId);
		List<TerritoryType> territoryTypeList = territoryTypeService.getTerritoryTypesInCampaign(campaignId);
		
		List<ObjectTag> tagsInFolio = folio.getTags();
		List<ObjectTag> tagList = new ArrayList<>();
		for (Organization org : orgList) {
			ObjectTag tag = org.createTag();
			if (!tagsInFolio.contains(tag)) {
				tagList.add(tag);
			}
		}
		for (OrganizationRank orgRank : orgRankList) {
			ObjectTag tag = orgRank.createTag();
			if (!tagsInFolio.contains(tag)) {
				tagList.add(orgRank.createTag());
			}
		}
		for (OrganizationType orgType : orgTypeList) {
			ObjectTag tag = orgType.createTag(campaignId);
			if (!tagsInFolio.contains(tag)) {
				tagList.add(orgType.createTag(campaignId));
			}
		}
		for (Territory territory : territoryList) {
			ObjectTag tag = territory.createTag();
			if (!tagsInFolio.contains(tag)) {
				tagList.add(territory.createTag());
			}
		}
		for (TerritoryType territoryType : territoryTypeList) {
			ObjectTag tag = territoryType.createTag(campaignId);
			if (!tagsInFolio.contains(tag)) {
				tagList.add(territoryType.createTag(campaignId));
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(tagList);
			folioCreator.setAllTags(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			folioCreator.setAllTags("{}");
		}

		if (folio != null) {
			tagList = folio.getTags();  
			if (tagList != null && !tagList.isEmpty()) {
				try {
					json = mapper.writeValueAsString(tagList);
					folioCreator.setSelectedTags(json);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					folioCreator.setAllTags("{}");
				}
			}
		}
	}
}
