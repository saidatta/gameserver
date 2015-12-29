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
import org.softwarewolf.gameserver.base.domain.Location;
import org.softwarewolf.gameserver.base.domain.LocationType;
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
	private LocationService locationService;
	@Autowired
	private LocationTypeService locationTypeService;
	@Autowired
	private ObjectTagService objectTagService;
	
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
		List<ObjectTag> selectedTags = folio.getTags();
		ObjectMapper mapper = new ObjectMapper();
		if (selectedTags != null && !selectedTags.isEmpty()) {
			String json;
			try {
				json = mapper.writeValueAsString(selectedTags);
				folioCreator.setSelectedTags(json);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				folioCreator.setSelectedTags("{}");
			}
		}

		List<ObjectTag> tagList = objectTagService.createTagList(campaignId, selectedTags);
		Map<String, Object> unassignedTags = new HashMap<>();
		try {
			unassignedTags = objectTagService.createObjectTagTree(tagList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String json;
		try {
			json = mapper.writeValueAsString(unassignedTags);
			folioCreator.setUnassignedTags(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			folioCreator.setUnassignedTags("{}");
		}
	}
}
