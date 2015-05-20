package com.fredtm.resources.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AccountDTO extends BaseDTO {
	private String name;
	private String email;
	private String password;

	public AccountDTO uuid(String uuid) {
		setUuid(uuid);
		return this;
	}

	public AccountDTO name(String name) {
		this.name = name;
		return this;
	}

	public AccountDTO email(String email) {
		this.email = email;
		return this;
	}

	public AccountDTO password(String pass) {
		this.password = pass;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPasswordHash(String password) {
		this.password = password;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(email).append(password)
				.append(name).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountDTO other = (AccountDTO) obj;
		return new EqualsBuilder().append(getEmail(), other.getEmail())
				.append(getName(), other.getName())
				.append(getPassword(), other.getPassword()).isEquals();

	}

	@Override
	public String toString() {
		return "AccountResource [name=" + name + ", email=" + email
				+ ", password=" + password + ", getUuid()=";
	}

}