package org.softwarewolf.gameserver.base.domain.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is just for putting data together to pass up to the front end
 * for display. As such there is no need to populate the parent of each node. 
 */
public class HierarchyJsonBuilder {
	private List<HierarchyJsonBuilder> children;
	private String id;
	private String name;
	private String typeId;
	private String displayName;
	
	public HierarchyJsonBuilder() {
		children = new ArrayList<>();
	}
	public HierarchyJsonBuilder(String id, String name, String typeId, String displayName) {
		this.id = id;
		this.name = name;
		this.typeId = typeId;
		this.displayName = displayName;
		children = new ArrayList<>();
	}
	
	public List<HierarchyJsonBuilder> getChildren() {
		return children;
	}
	
	public void setChildren(List<HierarchyJsonBuilder> children) {
		this.children = children;
	}
	
	public void addChild(HierarchyJsonBuilder child) {
		boolean childDoesNotExist = true;
		for (HierarchyJsonBuilder current : children) {
			if (child.getId().equals(current.getId())) {
				childDoesNotExist = false;
				break;
			}
		}
		if (childDoesNotExist) {
			children.add(child);
		}
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayName() {
		if ("ROOT".equals(name)) { 
			return "Create new top level item";
		}
		return displayName;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getTypeId() {
		return typeId;
	}
	
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	
//	public String getDisplayName() {
//		StringBuilder displayName = new StringBuilder();
//		if ("ROOT".equals(name)) { 
//			displayName.append("Create new top level item");
//		} else {
//			displayName.append(name).append(" (").append(typeName).append(")");
//		}
//		return displayName.toString();
//	}
}
