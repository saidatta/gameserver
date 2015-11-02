package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.OrganizationRank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface OrganizationRankRepository extends MongoRepository<OrganizationRank, String> {

	OrganizationRank findOneByNameAndOrganizationId(String arg0, String arg1);
	
	List<OrganizationRank> findByOrganizationId(String arg1);
	
	List<OrganizationRank> findByCampaignId(String arg0);

    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<OrganizationRank> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<OrganizationRank> findAllByKeyValues(String key, Object[] value);	
}
