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
	// Any class whose name ends in Type doesn't have a parentType
	public ObjectTag createTag(String parentId) {
		return new ObjectTag(this.getClass().getSimpleName(), this.getId(), this.getName(), this.getCampaignId(),
				this.getId(), this.getName(), parentId);
	}
}
