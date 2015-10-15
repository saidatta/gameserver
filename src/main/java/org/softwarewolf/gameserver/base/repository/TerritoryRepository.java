package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Territory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TerritoryRepository extends MongoRepository<Territory, String> {

	Territory findOneByName(String arg0);
	
	Territory findOneByNameAndCampaignId(String arg0, String arg1);
	
	List<Territory> findByCampaignId(String arg0);

    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<Territory> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<Territory> findAllByKeyValues(String key, Object[] value);	
}
