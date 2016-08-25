package it.tredi.ecm.dao.entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import it.tredi.ecm.dao.enumlist.ProfileEnum;
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
	private boolean enabled = true;
	private boolean changePassword;
	@Column(name = "data_scadenza_password")
	private LocalDate dataScadenzaPassword;

	private String nome;
	private String cognome;
	private String codiceFiscale;
	//in realtà servono solo per gli utenti con profilo REFEREE
	private int valutazioniNonDate = 0;
	private String note;

	@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_profile",
    			joinColumns = {
    							@JoinColumn(name = "account_id")},
    			inverseJoinColumns = {
    							@JoinColumn(name = "profile_id")
    			}
    		)
	private Set<Profile> profiles = new HashSet<Profile>();

	public boolean isPasswordExpired(){
		if(dataScadenzaPassword == null || !dataScadenzaPassword.isAfter(LocalDate.now()))
			return true;
		return false;
	}

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

	public boolean isSegreteria() {
		for (Profile p : profiles){
			if(p.getProfileEnum().equals(ProfileEnum.SEGRETERIA)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isProvider() {
		for (Profile p : profiles){
			if(p.getProfileEnum().equals(ProfileEnum.PROVIDER)){
				return true;
			}
		}
		return false;
	}
	
	public boolean isReferee() {
		for (Profile p : profiles){
			if(p.getProfileEnum().equals(ProfileEnum.REFEREE)){
				return true;
			}
		}
		return false;
	}
}
