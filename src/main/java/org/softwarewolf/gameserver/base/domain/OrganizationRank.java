package org.softwarewolf.gameserver.base.domain;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OrganizationRank extends GameDataImpl {
	private static final long serialVersionUID = 1L;
	private String organizationId;
	
	public OrganizationRank(String name, String campaignId, String organizationId) {
		super(name, campaignId);
		this.organizationId = organizationId;
		setDisplayName(name);
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
	
	@Override
	public String getDisplayName() {
		return getName();
	}

	@Override
	public ObjectTag getTag() {
		return new ObjectTag(this.getClass().getSimpleName(), id, displayName, campaignId);
	}
}
