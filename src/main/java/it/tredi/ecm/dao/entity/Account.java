package it.tredi.ecm.dao.entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import it.tredi.ecm.dao.enumlist.ProfileEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="account")
//@Table(name="account",
//indexes={ @Index(name="testIndex", columnList="username"),
//		@Index(columnList="id,password")}
//		)
@Getter
@Setter
public class Account extends BaseEntity{
	private String username;

	@JsonIgnore
	private String password = "";
	@JsonView(JsonViewModel.ComunicazioniDestinatari.class)
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
	@ManyToMany(cascade= CascadeType.REMOVE)
	@JoinTable(name="account_domande_non_valutate",
				joinColumns={@JoinColumn(name="account_id")},
				inverseJoinColumns={@JoinColumn(name="accreditamento_id")}
	)
	private Set<Accreditamento> domandeNonValutate = new HashSet<Accreditamento>();

	private String note;

	@Column(name = "username_workflow")
	private String usernameWorkflow;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "account_profile",
	joinColumns = {
			@JoinColumn(name = "account_id")},
	inverseJoinColumns = {
			@JoinColumn(name = "profile_id")
	}
			)
	private Set<Profile> profiles = new HashSet<Profile>();

	@ManyToOne
	@JoinColumn(name = "provider_id")
	private Provider provider;

	private boolean fakeAccountComunicazioni = false;

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

	@JsonView(JsonViewModel.ComunicazioniDestinatari.class)
	public String getFullName(){
		String toRet = "";
		if(nome != null)
			toRet = nome;

		if(cognome != null) {
			if(!toRet.isEmpty())
				toRet += " ";
			toRet += cognome;
		}

		if(provider != null) {
			if(!toRet.isEmpty())
				toRet += " ";
			toRet += "(" + provider.getDenominazioneLegale() + ")";
		}

		if(isSegreteria()) {
			toRet += " ";
			toRet += "(Segreteria ECM)";
		}

		if(isReferee()) {
			toRet += " ";
			toRet += "(Referee ECM)";
		}

		if(isCommissioneEcm()) {
			toRet += " ";
			toRet += "(Commissario ECM)";
		}

		if(isComponenteOsservatorioEcm()) {
			toRet += " ";
			toRet += "(Componente Osservatorio ECM)";
		}

		if(isReferenteInformatico()) {
			toRet += " ";
			toRet += "(Referente Informatico)";
		}

		return toRet;
	}

	public String getFullNameBase(){
		String toRet = "";
		if(nome != null)
			toRet = nome;

		if(cognome != null) {
			if(!toRet.isEmpty())
				toRet += " ";
			toRet += cognome;
		}

//		if(provider != null) {
//			if(!toRet.isEmpty())
//				toRet += " ";
//			toRet += "(" + provider.getId() + ")";
//		}

		return toRet;
	}

	public boolean isResponsabileSegreteriaEcm() {
		for (Profile p : profiles){
			if(p.getProfileEnum().equals(ProfileEnum.RESPONSABILE_SEGRETERIA_ECM)){
				return true;
			}
		}
		return false;
	}

	public boolean isSegreteria() {
		if (this.isResponsabileSegreteriaEcm())
			return true;

		for (Profile p : profiles){
			if(p.getProfileEnum().equals(ProfileEnum.SEGRETERIA)
					|| p.getProfileEnum().equals(ProfileEnum.RESPONSABILE_SEGRETERIA_ECM)){
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

	public boolean isCommissioneEcm() {
		for (Profile p : profiles){
			if(p.getProfileEnum().equals(ProfileEnum.COMMISSIONE)){
				return true;
			}
		}
		return false;
	}

	public boolean isComponenteOsservatorioEcm() {
		for (Profile p : profiles){
			if(p.getProfileEnum().equals(ProfileEnum.COMPONENTE_OSSERVATORIO)){
				return true;
			}
		}
		return false;
	}

	public boolean isReferenteInformatico() {
		for (Profile p : profiles){
			if(p.getProfileEnum().equals(ProfileEnum.REFERENTE_INFORMATICO)){
				return true;
			}
		}
		return false;
	}

	public boolean isProviderUserAdmin() {
		if(provider != null) {
			for (Profile p : profiles){
				if(p.getProfileEnum().equals(ProfileEnum.PROVIDERUSERADMIN)){
					return true;
				}
			}
		}
		return false;
	}

	public boolean isProvider() {
		if(this.isProviderUserAdmin())
			return true;

		if(provider != null) {
			for (Profile p : profiles){
				if(p.getProfileEnum().equals(ProfileEnum.PROVIDER)){
					return true;
				}
			}
		}
		return false;
	}

	public boolean isProviderVisualizzatore() {
		if(this.isProviderUserAdmin())
			return true;
		if(this.isProvider())
			return true;

		if(provider != null) {
			for (Profile p : profiles){
				if(p.getProfileEnum().equals(ProfileEnum.PROVIDER_VISUALIZZATORE)){
					return true;
				}
			}
		}
		return false;
	}

	public boolean isProviderAccountComunicazioni() {

		if(provider != null && fakeAccountComunicazioni) {
			for (Profile p : profiles) {
				if(p.getProfileEnum().equals(ProfileEnum.PROVIDER_ACCOUNT_COMUNICAZIONI)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean isSegreteriaAccountComunicazioni() {

		if(fakeAccountComunicazioni) {
			for (Profile p : profiles) {
				if(p.getProfileEnum().equals(ProfileEnum.SEGRETERIA_ACCOUNT_COMUNICAZIONI)) {
					return true;
				}
			}
		}
		return false;
	}
}
