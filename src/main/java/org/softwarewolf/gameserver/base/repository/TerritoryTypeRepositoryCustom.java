package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.TerritoryType;

public interface TerritoryTypeRepositoryCustom {
	List<TerritoryType> findByCampaignId(String campaignId);
}
