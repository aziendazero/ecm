package it.tredi.ecm.dao.entity;

import java.sql.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="account",
		indexes={ @Index(name="testIndex", columnList="username"),
					@Index(columnList="id,password")}
		)
@Getter
@Setter
public class Account extends BaseEntity{
	private String username;
	private String password = "";
	private String email;
	private Date expiresDate;
	private boolean locked;
	private boolean enabled;
	private boolean changePassword;
	
	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_profile", 
    			joinColumns = {
    							@JoinColumn(name = "account_id")}, 
    			inverseJoinColumns = {
    							@JoinColumn(name = "profile_id")
    			}
    		)	
	private Set<Profile> profiles = new HashSet<Profile>();
	
	public String getProfileAsString(){
		String result = "[";
		for(Profile p : profiles){
			result += p.getName() + ",";
		}
		if(result.length() > 1)
			result = result.substring(0, result.length()-1);
		
		result += "]";
		return result;
	}
	
	@Override
	public String toString(){
		String result = "";
		
		result += username + " | " + password + " | " + enabled + " | "; 
		
		return result;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Account entitapiatta = (Account) o;
        return Objects.equals(id, entitapiatta.id);
    }
}
