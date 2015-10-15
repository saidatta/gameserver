package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.TerritoryType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TerritoryTypeRepository extends MongoRepository<TerritoryType, String> {

	public TerritoryType findOneByName(String arg0);
	
    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<TerritoryType> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<TerritoryType> findAllByKeyValues(String key, Object[] value);
}
