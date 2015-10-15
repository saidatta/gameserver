package org.softwarewolf.gameserver.base.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Document
public class User implements UserDetails, Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	private String id;
	private String username;
	private String firstName;
	private String lastName;
	private String password;
	private String email;
	private List<GrantedAuthority> authorities;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	
	public User() {
		accountNonExpired = false;
		accountNonLocked = false;
		credentialsNonExpired = false;
		enabled = false;
		authorities = new ArrayList<>();
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
		
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}
	
	public void setAuthorities(List<String> roles) {
		if (authorities == null) {
			authorities = new ArrayList<>();
		}
		for (String role : roles) {
			SimpleGrantedAuthority sga = new SimpleGrantedAuthority(role);
			authorities.add(sga);
		}
	}
	
	public void addSimpleGrantedAuthority(SimpleGrantedAuthority sga) {
		if (authorities == null) {
			authorities = new ArrayList<>();
		}
		if (!authorities.contains(sga)) {
			authorities.add(sga);
		}
	}
	
	public void removeSimpleGrantedAuthority(SimpleGrantedAuthority sga) {
		if (authorities != null) {
			authorities.remove(sga);
		}
	}
	
	public List<String> getRoles() {
		List<String> roleList = new ArrayList<String>();
		if (authorities != null) {
			for (GrantedAuthority authority : authorities) {
				roleList.add(authority.getAuthority());
			}
		}
		return roleList;
	}
	
	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}
	
	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}
	
	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public String toString() {
		String out = "{id : " + id + ", "
				+ "username : " + username + ", "
				+ "firstName : " + firstName + ", "
				+ "lastName : " + lastName + ", "
				+ "password : " + password + ", "
				+ "accountNonExpired : " + accountNonExpired + ", "
				+ "accountNonLocked : " + accountNonLocked + ", "
				+ "credentialsNonExpired : " + credentialsNonExpired + ", "
				+ "enabled : " + enabled;
				if (authorities != null) {
					out += ", authorities : [";
					Iterator<GrantedAuthority> sgaIter = authorities.iterator();
					while (sgaIter.hasNext()) {
						out += sgaIter.next().getAuthority();
						if (sgaIter.hasNext()) {
							out += ", ";
						}
					}
					out += "]";
				}
		out += "}";
		return out;
	}
}
