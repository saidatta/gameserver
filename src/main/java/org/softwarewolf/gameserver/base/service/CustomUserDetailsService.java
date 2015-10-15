package org.softwarewolf.gameserver.base.service;

import org.softwarewolf.gameserver.base.domain.User;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String arg0)
			throws UsernameNotFoundException {
		
		User user = userRepo.findOneByUsername(arg0);
		
		return user;
	}

}
