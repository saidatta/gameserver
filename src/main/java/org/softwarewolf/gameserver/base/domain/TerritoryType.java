package org.softwarewolf.gameserver.base.domain;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class TerritoryType extends GameDataTypeImpl {
	private static final long serialVersionUID = 1L;

	public TerritoryType() {
		super();
	}
}
