package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.LocationType;

public interface LocationTypeRepositoryCustom {
	List<LocationType> findByCampaignId(String campaignId);
}
