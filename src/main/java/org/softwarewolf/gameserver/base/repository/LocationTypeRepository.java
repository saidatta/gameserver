package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.LocationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface LocationTypeRepository extends MongoRepository<LocationType, String> {

	public LocationType findOneByName(String arg0);
	
    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<LocationType> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<LocationType> findAllByKeyValues(String key, Object[] value);
}
