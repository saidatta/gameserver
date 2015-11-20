package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface PageRepository extends MongoRepository<Page, String> {

	public Page findOneByCampaignIdAndTitle(String arg0, String arg1);

    /// Find by key value pair
    @Query("{?0 : ?1}")
    public List<Page> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    public List<Page> findAllByKeyValues(String key, Object[] value);
    
}
