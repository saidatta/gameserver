package org.softwarewolf.gameserver.base.domain;

import org.softwarewolf.gameserver.base.domain.helper.ObjectTag;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OrganizationType extends GameDataTypeImpl {
	private static final long serialVersionUID = 1L;

	public OrganizationType() {
		super();
	}
	
	@Override
	public ObjectTag createTag(String campaignId) {
		return new ObjectTag(this.getClass().getSimpleName(), this.getId(), this.getName(), campaignId,
				null, null, null);
	}
}
