package com.softlib.imatch.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.softlib.imatch.enums.UserLevel;

@Entity
@Table(name="USERS")
public class User {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_id")
	protected int id = 0;
	
	@Column(name="username")
	private String username;
	
	@Transient
	private String displayName;
	
	@Column(name="password")
	private String password;
	
	@Column(name="extra")
	private String extra;
	@Column(name="pin_code")
	private String pinCode;
	
	@Transient
	private boolean enabled;
	@Transient
	private UserLevel userLevel = UserLevel.REGULAR;
	@Transient
	private boolean disabled = true;
	
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	public void setId(int id) {
		this.id = id;
	}
	public int getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setUsername(String username, boolean guest) {
		this.username = username;
	}
	
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
		if (password.length() > 0)
			this.disabled = false;
		else
			this.disabled = true;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getExtra() {
		return extra;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getPinCode() {
		return pinCode;
	}

	
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void setUserLevel(UserLevel userLevel) {
		this.userLevel = userLevel;
	}
	public UserLevel getUserLevel() {
		return userLevel;
	}
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	
}
