package org.softwarewolf.gameserver.base.repository;

import org.softwarewolf.gameserver.base.domain.CampaignPlayer;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CampaignPlayerRepository extends MongoRepository<CampaignPlayer, String> {

}
