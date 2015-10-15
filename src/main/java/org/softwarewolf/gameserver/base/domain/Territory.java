package org.softwarewolf.gameserver.base.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Territory extends GameDataImpl {
	private static final long serialVersionUID = 1L;
	
	public Territory(String name, String campaignId) {
		super(name, campaignId);
	}
	
	public Territory() {
		super();
	}
	
}
