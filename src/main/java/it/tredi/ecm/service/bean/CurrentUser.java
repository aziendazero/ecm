package it.tredi.ecm.service.bean;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.enumlist.RoleEnum;
import lombok.Getter;
import it.tredi.bonita.api.model.UserDataModel;
import it.tredi.ecm.dao.entity.Account;

@Getter
public class CurrentUser extends org.springframework.security.core.userdetails.User {
	private static final long serialVersionUID = 1L;
	private Account account;
	private UserDataModel workflowUserDataModel;

	public CurrentUser(Account account, Collection<SimpleGrantedAuthority> auth, UserDataModel workflowUserDataModel) {
		super(account.getUsername(),account.getPassword(),account.isEnabled(),true,true,!account.isLocked(),auth);
		this.account = account;
		this.workflowUserDataModel = workflowUserDataModel;
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

	public boolean isSegreteria() {
		if(account != null){
			return account.isSegreteria();
		}
		return false;
	}

	public boolean isProvider() {
		if(account != null){
			return account.isProvider();
		}
		return false;
	}

	public boolean isReferee() {
		if(account != null){
			return account.isReferee();
		}
		return false;
	}
	
	public boolean isCommissioneEcm() {
		if(account != null){
			return account.isCommissioneEcm();
		}
		return false;
	}
}
