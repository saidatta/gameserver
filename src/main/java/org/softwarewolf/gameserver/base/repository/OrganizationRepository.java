package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface OrganizationRepository extends MongoRepository<Organization, String> {

	Organization findOneByName(String arg0);
	
	Organization findOneByNameAndCampaignId(String arg0, String arg1);
	
	List<Organization> findByCampaignId(String arg0);

    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<Organization> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<Organization> findAllByKeyValues(String key, Object[] value);	
}
