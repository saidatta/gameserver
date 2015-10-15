package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Campaign implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String name;
	private String description;
	private String ownerId;
	private List<String> gameMasterList;
	
	public Campaign() {}
	
	public Campaign(String ownerId) {
		this.ownerId = ownerId;
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
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
		
	public String getOwnerId() {
		return ownerId;
	}
	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	
	public List<String> getGameMasterList() {
		return gameMasterList;
	}
	
	public void setGameMasterList(List<String> gameMasterList) {
		this.gameMasterList = gameMasterList;
	}
}
