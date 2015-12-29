package org.softwarewolf.gameserver.base.domain;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Location extends GameDataImpl {
	private static final long serialVersionUID = 1L;
	
	public Location(String name, String campaignId) {
		super(name, campaignId);
	}
	
	public Location() {
		super();
	}

	@Override
	public ObjectTag getTag() {
		return new ObjectTag(this.getClass().getSimpleName(), id, displayName, campaignId,
				gameDataTypeId, gameDataTypeName, null);
	}	
}
