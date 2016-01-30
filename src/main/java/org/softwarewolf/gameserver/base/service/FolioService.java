package org.softwarewolf.gameserver.base.service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.softwarewolf.gameserver.base.domain.Folio;
import org.softwarewolf.gameserver.base.domain.Location;
import org.softwarewolf.gameserver.base.domain.LocationType;
import org.softwarewolf.gameserver.base.domain.Organization;
import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.softwarewolf.gameserver.base.domain.OrganizationType;
import org.softwarewolf.gameserver.base.domain.helper.FolioCreator;
import org.softwarewolf.gameserver.base.domain.helper.FolioDescriptor;
import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.softwarewolf.gameserver.base.domain.helper.SelectFolioCreator;
import org.softwarewolf.gameserver.base.repository.FolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

@Service
public class FolioService implements Serializable {
	@Autowired
	private FolioRepository folioRepository;

	@Autowired
	private OrganizationService organizationService;
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

	public Folio save(Folio folio) throws Exception {
		String errorList = validateFolio(folio);
		if (errorList.length() > 0) {
			throw new Exception(errorList);
		}
		return folioRepository.save(folio);
	}
	
	public String validateFolio(Folio folio) {
		String errorList = "";
		if (folio == null) {
			errorList = "Null folio.";
			return errorList;
		}
		if (folio.getTitle() == null || folio.getTitle().isEmpty()) {
			errorList = "Title may not be empty.";
		}
		if (folio.getContent() == null || folio.getContent().isEmpty()) {
			if (errorList.length() > 0) {
				errorList += "\n";
			}
			errorList += "Content may not be empty.";
		}
		return errorList;
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
		} else {
			folioCreator.setSelectedTags("{}");
		}

		Map<String, ObjectTag> unselectedTagMap = objectTagService.createTagMap(campaignId, selectedTags);
		Map<String, Object> unassignedTags = new HashMap<>();
		try {
			unassignedTags = objectTagService.createObjectTagTree(unselectedTagMap, campaignId);
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
		
		folioCreator.setFolioDescriptorList(getFolioDescriptorList(null));
	}
	
	public void deleteAll() {
		folioRepository.deleteAll();
	}
	
	public Folio removeTagFromFolio(String folioId, String tagId) {
		Folio folio = folioRepository.findOne(folioId);
		if (folio != null) {
			folio.removeTag(tagId);
		}
		return folioRepository.save(folio);
	}

	public Folio addTagToFolio(String folioId, String className, String tagId) {
		Folio folio = folioRepository.findOne(folioId);
		if (folio != null && className != null && tagId != null) {
			ObjectTag tag = null;
			if ("LocationType".equals(className)) {
				LocationType locationType = locationTypeService.findOne(tagId);
				tag = locationType.createTag();
			} else if ("Location".equals(className)) {
				Location location = locationService.findOne(tagId);
				tag = location.createTag(location.getParentId());
			} else if ("OrganizationType".equals(className)) {
				OrganizationType organizationType = organizationTypeService.findOne(tagId);
				tag = organizationType.createTag();
			} else if ("Organization".equals(className)) {
				Organization organization = organizationService.findOne(tagId);
				tag = organization.createTag(organization.getParentId());
			} else if ("OrganizationRank".equals(className)) {
				OrganizationRank organizationRank = organizationRankService.findOne(tagId);
				tag = organizationRank.createTag(organizationRank.getParentId());
			}
			folio.addTag(tag);
		}
		return folioRepository.save(folio);
	}

	public List<FolioDescriptor> getFolioDescriptorList(List<String> excludeIds) {
		List<FolioDescriptor> folioDescriptorList = new ArrayList<>();
		if (excludeIds == null) {
			excludeIds = new ArrayList<>();
		}
		List<Folio> folioList = folioRepository.findAll();
		if (folioList != null && folioList.size() > 0) {
			for (Folio folio: folioList) {
				if (!excludeIds.contains(folio.getId())) {
					folioDescriptorList.add(folio.createDescriptor());
				}
			}
		}
		return folioDescriptorList;
	}
	
	public Folio findOne(String id) {
		return folioRepository.findOne(id);
	}
	
	public void initSelectFolioCreator(String campaignId, SelectFolioCreator selectFolioCreator) {
		List<ObjectTag> excludeTags = new ArrayList<>();
		List<ObjectTag> allTags = objectTagService.createTagList(campaignId, excludeTags);
		
		String unselectedTags = selectFolioCreator.getUnselectedTags();
		if (unselectedTags == null) {
			unselectedTags = "";
		}
		String selectedTags = selectFolioCreator.getSelectedTags();
		if (selectedTags == null) {
			selectedTags = "";
		}
		ObjectMapper mapper = new ObjectMapper();
		JavaType listOfObjectTagsType = mapper.getTypeFactory().constructCollectionType(List.class, ObjectTag.class);
		List<ObjectTag> unselectedTagList = null;
		List<ObjectTag> selectedTagList = null;
		try {
			if (unselectedTags.isEmpty()) {
				unselectedTagList = new ArrayList<>();
			} else {
				unselectedTagList = mapper.readValue(unselectedTags, listOfObjectTagsType);
			}
			if (selectedTags.isEmpty()) {
				selectedTagList = new ArrayList<>();
			} else {
				selectedTagList = mapper.readValue(selectedTags, listOfObjectTagsType);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ObjectTag addTag = null;
		ObjectTag removeTag = null;
		boolean initUnselectedTags = (unselectedTags.isEmpty() && selectedTags.isEmpty());
		
		for(ObjectTag tag: allTags) {
			if (tag.getObjectId().equals(selectFolioCreator.getAddTagId())) {
				addTag = tag;
				selectFolioCreator.setAddTagId(null);
				selectFolioCreator.setAddTagClassName(null);
			} else if (tag.getObjectId().equals(selectFolioCreator.getRemoveTagId())) {
				removeTag = tag;
				selectFolioCreator.setRemoveTagId(null);
				selectFolioCreator.setRemoveTagClassName(null);
			}
		}
		if (initUnselectedTags) {
			unselectedTagList = allTags;
		}
		
		if (addTag != null) {
			unselectedTagList.remove(addTag);
			selectedTagList.add(addTag);
		}
		if (removeTag != null) {
			unselectedTagList.add(removeTag);
			selectedTagList.remove(removeTag);
		}
		try {
			selectFolioCreator.setUnselectedTags(mapper.writeValueAsString(unselectedTagList));
			selectFolioCreator.setSelectedTags(mapper.writeValueAsString(selectedTagList));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
