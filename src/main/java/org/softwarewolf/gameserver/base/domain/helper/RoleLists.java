package org.softwarewolf.gameserver.base.domain.helper;

import java.util.Map;

public class RoleLists {
	public Map<String, String> allRoles;
	public Map<String, String> selectedRoles;
	
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
