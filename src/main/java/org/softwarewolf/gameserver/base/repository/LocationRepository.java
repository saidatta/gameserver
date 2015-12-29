package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface LocationRepository extends MongoRepository<Location, String> {

	Location findOneByName(String arg0);
	
	Location findOneByNameAndCampaignId(String arg0, String arg1);
	
	List<Location> findByCampaignId(String arg0);

    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<Location> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<Location> findAllByKeyValues(String key, Object[] value);	
}
