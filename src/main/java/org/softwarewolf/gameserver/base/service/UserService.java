package org.softwarewolf.gameserver.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.base.domain.DeleteableRole;
import org.softwarewolf.gameserver.base.domain.User;
import org.softwarewolf.gameserver.base.domain.helper.RoleLists;
import org.softwarewolf.gameserver.base.domain.helper.RolesData;
import org.softwarewolf.gameserver.base.domain.helper.UserListItem;
import org.softwarewolf.gameserver.base.repository.DeleteableRoleRepository;
import org.softwarewolf.gameserver.base.repository.SimpleGrantedAuthorityRepository;
import org.softwarewolf.gameserver.base.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	@Autowired 
	protected SimpleGrantedAuthorityRepository authRepository;
	
	@Autowired
	protected DeleteableRoleRepository deletableRoleRepository;
	
	@Autowired
	protected UserRepository userRepository;
	
	@Autowired
	protected SimpleGrantedAuthorityRepository simpleGrantedAuthorityRepository;
	
	public RolesData getRolesData() {
		RolesData rolesData = new RolesData();
		rolesData.setAllRoles(getAllRoles());
		return rolesData;
	}
	
	public void setRolesData(RolesData rolesData) {
		Map<String, String> allRoles = getAllRoles(); 
		rolesData.setAllRoles(allRoles);
	}

	public List<String> getRoles() {
		List<SimpleGrantedAuthority> authorities = authRepository.findAll();
		List<String> roles = new ArrayList<>();
		for (SimpleGrantedAuthority auth : authorities) {
			String authority = auth.getAuthority().split("_")[1].toLowerCase();
			roles.add(authority);
		}
		return roles;
	}

	public void createRoles(RolesData rolesData) {
		String simpleRole;
		String role;
		simpleRole = rolesData.getCreateRole();
		role = "ROLE_" + simpleRole.toUpperCase();
		SimpleGrantedAuthority auth = new SimpleGrantedAuthority(role);
		authRepository.save(auth);
	}


	public void deleteRoles(RolesData rolesData) {
		for (String role : rolesData.getSelectedRoles()) {
			DeleteableRole deleteableRole = deletableRoleRepository.findOneByRole(role);
			if (deleteableRole != null) {
				deletableRoleRepository.delete(deleteableRole);
			}
		}
	}	
	
	public RoleLists getRoleLists(String userId) {
		RoleLists roleLists = new RoleLists();
		Map<String, String> allRoles = getAllRoles();

		if (userId != null) {
			User user = userRepository.findOne(userId);
			List<String> userRoles = user.getRoles();
			Map<String, String> selectedRoles = new HashMap<>();
			for (String userRole : userRoles) {
				String roleString = roleToString(userRole);
				selectedRoles.put(userRole, roleString);
			}
			roleLists.setSelectedRoles(selectedRoles);
		} else {
			roleLists.setSelectedRoles(new HashMap<>());
		}
		roleLists.setAllRoles(allRoles);
		
		return roleLists;
	}
	
	public List<UserListItem> getUserList() {
		List<UserListItem> userList = new ArrayList<>();
		List<User> uList = userRepository.findAll();
		for (User user : uList) {
			UserListItem userListItem = new UserListItem(user.getId(), user.getUsername());
			userList.add(userListItem);
		}
		return userList;
	}
	
	public User getUser(String userID) {
		return userRepository.findOne(userID);
	}

	public String roleToString(String role) {
		int underscore = role.indexOf('_') + 1;
		return role.substring(underscore).toLowerCase();
	}
	
	public String stringToRole(String role) {
		return "ROLE_" + (role.toUpperCase());
	}
	
	public Map<String, String> makeIdUsernameMap(String userId) {
		User user = userRepository.findOne(userId);
		Map<String, String> userMap = new HashMap<String, String>();
		userMap.put(user.getId(), user.getUsername());
		
		return userMap;
	}
	
	public List<User> getUsersByRole(String role) {
		List<SimpleGrantedAuthority> sgaList = new ArrayList<>();
		SimpleGrantedAuthority sga = simpleGrantedAuthorityRepository.findByRole(role);
		sgaList.add(sga);
		return userRepository.findByAuthorities(sgaList);
	}
	
	public Map<String, String> getAllRoles() {
		Map<String, String> allRolesMap = new HashMap<>();
		List<SimpleGrantedAuthority> sgaList = simpleGrantedAuthorityRepository.findAll();
		for (SimpleGrantedAuthority sga : sgaList) {
			String roleString = roleToString(sga.getAuthority());
			allRolesMap.put(roleString, sga.getAuthority());
		}
		return allRolesMap;
	}
	
	public String getUserIdFromUsername(String username) {
		String userId = null;
		User user = userRepository.findOneByUsername(username);
		if (user != null) {
			userId = user.getId();
		}
		return userId;
	}
}
