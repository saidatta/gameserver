package org.softwarewolf.gameserver.base.domain;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
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
	
	@Override
	public ObjectTag getTag() {
		return new ObjectTag(this.getClass().getSimpleName(), id, displayName, campaignId,
				gameDataTypeId, gameDataTypeName);
	}
}
