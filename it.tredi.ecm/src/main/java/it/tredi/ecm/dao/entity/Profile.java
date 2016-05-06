package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="profile")
@Getter
@Setter
public class Profile extends BaseEntity{
	private String name;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "profile_role",
			joinColumns = {
							@JoinColumn(name = "profile_id")}, 
			inverseJoinColumns = {
							@JoinColumn(name = "role_id")
				}
			)
	private Set<Role> roles = new HashSet<Role>(); 
	
	public void setName(String name){
		this.name = name.toUpperCase();
	}
}
