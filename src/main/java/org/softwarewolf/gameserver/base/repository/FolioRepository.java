package org.softwarewolf.gameserver.base.repository;

import java.util.List;

import org.softwarewolf.gameserver.base.domain.Folio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FolioRepository extends MongoRepository<Folio, String> {

	public Folio findOneByCampaignIdAndTitle(String arg0, String arg1);

    /// Find by key value pair
    @Query("{?0 : ?1}")
    public List<Folio> findAllByKeyValue(String key, Object value);

    /// Find by key and array of values
    @Query("{?0 : {$in : ?1}}")
    public List<Folio> findAllByKeyValues(String key, Object[] value);
    
}
