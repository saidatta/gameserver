package org.softwarewolf.gameserver.base.repository;

import org.softwarewolf.gameserver.base.domain.DeleteableRole;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeleteableRoleRepository extends MongoRepository<DeleteableRole, String> {

	DeleteableRole findOneByRole(String arg0);

}
