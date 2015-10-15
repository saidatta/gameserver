package org.softwarewolf.gameserver.base.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface SimpleGrantedAuthorityRepository extends MongoRepository<SimpleGrantedAuthority, String> {
	public SimpleGrantedAuthority findByRole(String role); 
}
