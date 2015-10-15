package org.softwarewolf.gameserver.base.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class OrganizationType extends GameDataTypeImpl {
	private static final long serialVersionUID = 1L;

	public OrganizationType() {
		super();
	}
}
