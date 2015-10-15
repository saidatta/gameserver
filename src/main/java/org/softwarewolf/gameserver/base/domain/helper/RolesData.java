package org.softwarewolf.gameserver.base.domain.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RolesData {
	public Map<String, String> allRoles;
	public List<String> selectedRoles;
	private String createRole;

	public RolesData() {
		selectedRoles = new ArrayList<>();
	}

	public Map<String, String> getAllRoles() {
		return allRoles;
	}
	
	public void setAllRoles(Map<String, String> allRoles) {
		this.allRoles = allRoles;
	}
	
	public String getCreateRole() {
		return createRole;
	}
	
	public void setCreateRole(String createRole) {
		this.createRole = createRole;
	} 
	
	public List<String> getSelectedRoles() {
		return selectedRoles;
	}
	
	public void setSelectedRoles(List<String> selectedRoles) {
		this.selectedRoles = selectedRoles;
	} 
}
