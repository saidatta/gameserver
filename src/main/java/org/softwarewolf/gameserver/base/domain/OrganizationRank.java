package org.softwarewolf.gameserver.base.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OrganizationRank extends GameDataImpl {
	private static final long serialVersionUID = 1L;
	
	public OrganizationRank(String name, String campaignId) {
		super(name, campaignId);
	}
	
	public OrganizationRank() {
		super();
	}
	
}
