package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class is for holding the html pages created by users of the app and
 * associating them with meta data.
 * @author tmanchester
 */
@Document
public class Page implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	private String id;
	private String campaignId;
	private String page;
	private List<String> territories;
	private List<String> organizations;
	
	public Page() { }
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getPage() {
		return page;
	}
	
	public void setPage(String page) {
		this.page = page;
	}
	
	public List<String> getTerritories() {
		if (territories == null) {
			territories = new ArrayList<>();
		}
		return territories;
	}
	
	public void setTerritories(List<String> territories) {
		this.territories = territories;
	}
	
	public void addTerritory(String territoryId) {
		if (!territories.contains(territoryId)) {
			territories.add(territoryId);
		}
	}

	public void removeTerritory(String territoryId) {
		territories.remove(territoryId);
	}

	public List<String> getOrganizations() {
		if (organizations == null) {
			organizations = new ArrayList<>();
		}
		return organizations;
	}
	
	public void setOrganizations(List<String> organizations) {
		this.organizations = organizations;
	}
	
	public void addOrganization(String organizationId) {
		if (!organizations.contains(organizationId)) {
			organizations.add(organizationId);
		}
	}

	public void removeOrganization(String organizationId) {
		organizations.remove(organizationId);
	}

}
