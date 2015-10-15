package org.softwarewolf.gameserver.base.repository;

import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.base.domain.Campaign;

public interface CampaignRepositoryCustom {
    List<Campaign> findByKeyMapValueList(Map<String, List<?>> filter);
    List<Campaign> findByKeyMapValueList(Map<String, List<?>> includeFilter, Map<String, List<?>> excludeFilter);
    List<Campaign> findByKeyMapValueList(Map<String, List<?>> includeFilter, Map<String, List<?>> excludeFilter, List<String> fields);
    List<Campaign> list(Map<String, List<?>> filter);
}
