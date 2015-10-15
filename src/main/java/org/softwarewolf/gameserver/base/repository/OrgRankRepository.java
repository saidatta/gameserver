package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.OrgRank;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface OrgRankRepository extends MongoRepository<OrgRank, String> {

	OrgRank findOneByName(String arg0);
	
	OrgRank findOneByNameAndCampaignId(String arg0, String arg1);
	
	List<OrgRank> findByCampaignId(String arg0);

    /// Find by key value pair
    @Query("{?0 : ?1}")
    List<OrgRank> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    List<OrgRank> findAllByKeyValues(String key, Object[] value);	
}
