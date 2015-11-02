package org.softwarewolf.gameserver.base.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OrganizationRank extends GameDataImpl {
	private static final long serialVersionUID = 1L;
	private String organizationId;
	
	public OrganizationRank(String name, String campaignId, String organizationId) {
		super(name, campaignId);
		this.organizationId = organizationId;
	}
	
	public OrganizationRank() {
		super();
	}
	
	public String getOrganizationId() {
		return organizationId;
	}
	
	public void setOrganizationId(String organizationId) {
		this.organizationId = organizationId;
	}
	
}
