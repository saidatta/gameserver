package org.softwarewolf.gameserver.base.domain.helper;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.softwarewolf.gameserver.base.domain.User;

public class UserData implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<UserListItem> userList;
	private String selectedUserId;
	private User selectedUser;
//	public RoleLists roleLists;
	private String password;
	private String verifyPassword;
	private String errorMessage;
	public Map<String, String> allRoles;
	public Map<String, String> selectedRoles;	

	public UserData() {}
	
	public List<UserListItem> getUserList() {
		return userList;
	}

	public void setUserList(List<UserListItem> userList) {
		this.userList = userList;
	}
	
	public String getSelectedUserId() {
		return selectedUserId;
	}
	
	public void setSelectedUserId(String selectedUserId) {
		 this.selectedUserId = selectedUserId;
	}
	
	public User getSelectedUser() {
		return selectedUser;
	}
	
	public void setSelectedUser(User selectedUser) {
		 this.selectedUser = selectedUser;
	}
//	
//	public RoleLists getRoleLists() {
//		return roleLists;
//	}
//	
//	public void setRoleLists(RoleLists roleLists) {
//		this.roleLists = roleLists;
//	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	public String getVerifyPassword() {
		return verifyPassword;
	}
	
	public void setVerifyPassword(String verifyPassword) {
		this.verifyPassword = verifyPassword;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public Map<String, String> getAllRoles() {
		return allRoles;
	}

	public void setAllRoles(Map<String, String> allRoles) {
		this.allRoles = allRoles;
	}

	public Map<String, String> getSelectedRoles() {
		return selectedRoles;
	}

	public void setSelectedRoles(Map<String, String> selectedRoles) {
		this.selectedRoles = selectedRoles;
	}
}
