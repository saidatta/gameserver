package org.softwarewolf.gameserver.base.domain;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
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

	@Override
	public ObjectTag getTag() {
		return new ObjectTag(this.getClass().toString(), id, displayName, campaignId);
	}	
}
