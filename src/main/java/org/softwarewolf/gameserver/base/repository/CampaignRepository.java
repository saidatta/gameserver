package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Campaign;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CampaignRepository extends MongoRepository<Campaign, String> {

	Campaign findOneByName(String arg0);

	@Query("{'ownerId' : ?1}")
	List<Campaign> findByOwnerId(String arg0);
	
    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<Campaign> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<Campaign> findAllByKeyValues(String key, Object[] value);
}
