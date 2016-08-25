package it.tredi.ecm.service.bean;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.enumlist.RoleEnum;
import lombok.Getter;
import it.tredi.ecm.dao.entity.Account;

@Getter
public class CurrentUser extends org.springframework.security.core.userdetails.User {
	private static final long serialVersionUID = 1L;
	private Account account;

	public CurrentUser(Account account, Collection<SimpleGrantedAuthority> auth) {
		super(account.getUsername(),account.getPassword(),account.isEnabled(),true,true,!account.isLocked(),auth);
		this.account = account;
	}

	public String getProfile(){
		String result = "[";

		for(Profile profile : account.getProfiles())
			result += profile.getName() + ", ";

		int x = result.lastIndexOf(", ");
		if(x > 0)
			result = result.substring(0,x);

		result += "]";
		if(result.length() == 2)
			result = "nessun profilo";

		return result;
	}

	public boolean hasProfile(ProfileEnum profileEnum){
		for(Profile profile : account.getProfiles()){
			if(profile.getProfileEnum() == profileEnum)
				return true;
		}
		return false;
	}

	public boolean hasRole(RoleEnum role){
		for(GrantedAuthority auth : getAuthorities()){
			if(auth.getAuthority().equals(role.name()))
				return true;
		}
		return false;
	}
	
}
