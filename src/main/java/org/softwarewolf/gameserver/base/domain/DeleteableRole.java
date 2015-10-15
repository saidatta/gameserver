package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This class exists simply because SimpleGrantedAuthoriy does not 
 * have an id and so no easy way to delete authorities.
 */
@Document(collection="simpleGrantedAuthority")
public class DeleteableRole implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String role;
	
	public DeleteableRole() {
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
    public String getAuthority() {
        return role;
    }

	@Override
	public String toString() {
		return "DeleteableRole [id=" + id + ", role=" + role + "]";
	}
    
}
