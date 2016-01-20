package org.softwarewolf.gameserver.base.domain.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjectTag implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/** The name of the class, Organization, Location, etc. */
	private String className;
	/** The id of the instance of the class */
	private String objectId;
	/** The name of the instance of the class */
	private String tagName;
	private String campaignId;
	private String gameDataTypeId;
	private String gameDataTypeName;
	private String parentId = null;
	private List<String> children;
	
	public ObjectTag() { 
	}
	
	public ObjectTag(String className, String objectId, String tagName, String campaignId, 
			String gameDataTypeId, String gameDataTypeName, String parentId) {
		this.className = className;
		this.objectId = objectId;
		this.tagName = tagName;
		this.campaignId = campaignId;
		this.gameDataTypeId = gameDataTypeId;
		this.gameDataTypeName = gameDataTypeName;
		this.parentId = parentId;
		children = new ArrayList<>();
	}
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getTagName() {
		return tagName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	public String getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	
	public String getGameDataTypeId() {
		return gameDataTypeId;
	}
	public void setGameDataTypeId(String gameDataTypeId) {
		this.gameDataTypeId = gameDataTypeId;
	}
	
	public String getGameDataTypeName() {
		return gameDataTypeName;
	}
	public void setGameDataTypeName(String gameDataTypeName) {
		this.gameDataTypeName = gameDataTypeName;
	}

	public boolean hasChildren() {
		return (children != null && children.size() > 0);
	}
	public void addChildTag(String childTagId) {
		if (!children.contains(childTagId)) {
			children.add(childTagId);
		}
	}
	public void removeChildTag(String childTagId) {
		children.remove(childTagId);
	}
	
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((campaignId == null) ? 0 : campaignId.hashCode());
		result = prime * result
				+ ((className == null) ? 0 : className.hashCode());
		result = prime * result
				+ ((objectId == null) ? 0 : objectId.hashCode());
		result = prime * result + ((tagName == null) ? 0 : tagName.hashCode());
		result = prime * result + ((gameDataTypeId == null) ? 0 : gameDataTypeId.hashCode());
		result = prime * result + ((gameDataTypeName == null) ? 0 : gameDataTypeName.hashCode());
		result = prime * result + ((parentId == null) ? 0 : parentId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ObjectTag other = (ObjectTag) obj;
		if (campaignId == null) {
			if (other.campaignId != null)
				return false;
		} else if (!campaignId.equals(other.campaignId))
			return false;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (objectId == null) {
			if (other.objectId != null)
				return false;
		} else if (!objectId.equals(other.objectId))
			return false;
		if (tagName == null) {
			if (other.tagName != null)
				return false;
		} else if (!tagName.equals(other.tagName))
			return false;
		if (gameDataTypeId == null) {
			if (other.gameDataTypeId != null)
				return false;
		} else if (!gameDataTypeId.equals(other.gameDataTypeId))
			return false;
		if (gameDataTypeName == null) {
			if (other.gameDataTypeName != null)
				return false;
		} else if (!gameDataTypeName.equals(other.gameDataTypeName))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		return true;
	}
	
	public String toString() {
		StringBuilder string = new StringBuilder("{\n  \"className\":\"").append(className).append("\",\n  \"objectId\": \"")
				.append(objectId).append("\",\n  \"tagName\": \"").append(tagName).append("\",\n  \"campaignId\": \"")
				.append(campaignId).append("\",\n  \"gameDataTypeId\": \"").append(gameDataTypeId)
				.append("\",\n  \"gameDataTypeName\": \"").append(gameDataTypeName).append("\",\n  \"parentId\": \"")
				.append(parentId).append("\",\n  \"children\": [");
		Iterator<String> iter = children.iterator();
		while (iter.hasNext()) {
			String child = iter.next();
			string.append("\n    \"").append(child).append("\"");
			if (iter.hasNext()) {
				string.append(",");
			}
		}
		string.append("\n  ]").append("\n}");
		return string.toString();
	}
	
}
