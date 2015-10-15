package org.softwarewolf.gameserver.base.repository;

import java.util.Collection;
import java.util.List;

import org.softwarewolf.gameserver.base.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

	User findOneByUsername(String arg0);
	
	List<User> findByAuthorities(Collection arg0);

}
