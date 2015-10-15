package org.softwarewolf.gameserver.base.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Organization extends GameDataImpl {
	private static final long serialVersionUID = 1L;
	
	public Organization(String name, String campaignId) {
		super(name, campaignId);
	}
	
	public Organization() {
		super();
	}
}
