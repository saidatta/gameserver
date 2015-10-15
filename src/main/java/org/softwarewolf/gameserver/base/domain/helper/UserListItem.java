package org.softwarewolf.gameserver.base.domain.helper;

public class UserListItem {
	String id;
	String displayName;
	public UserListItem(String id, String displayName) {
		this.id = id;
		this.displayName = displayName;
	}
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getDisplayName() { return displayName; }
	public void setDisplayName(String displayName) { this.displayName = displayName; }
}
