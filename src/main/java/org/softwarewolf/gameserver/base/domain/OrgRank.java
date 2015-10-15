package org.softwarewolf.gameserver.base.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OrgRank extends GameDataImpl {
	private static final long serialVersionUID = 1L;
	
	public OrgRank(String name, String campaignId) {
		super(name, campaignId);
	}
	
	public OrgRank() {
		super();
	}
}
