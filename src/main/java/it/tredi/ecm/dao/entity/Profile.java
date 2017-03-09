package it.tredi.ecm.dao.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import it.tredi.ecm.dao.enumlist.ProfileEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="profile")
@Getter
@Setter
public class Profile extends BaseEntity{
	@Enumerated(EnumType.STRING)
	private ProfileEnum profileEnum;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "profile_role",
			joinColumns = {
							@JoinColumn(name = "profile_id")},
			inverseJoinColumns = {
							@JoinColumn(name = "role_id")
				}
			)
	private Set<Role> roles = new HashSet<Role>();

	public String getName(){
		return profileEnum.name();
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Profile entitapiatta = (Profile) o;
        return Objects.equals(id, entitapiatta.id);
    }

	//metodo che esclude i profili non utilizzabili nell'applicazione,
	//come ad esempio i profili fake per le comunicazioni
	public boolean isUsable() {
		if(this.getProfileEnum() == ProfileEnum.PROVIDER_ACCOUNT_COMUNICAZIONI)
			return false;
		if(this.getProfileEnum() == ProfileEnum.SEGRETERIA_ACCOUNT_COMUNICAZIONI)
			return false;
		return true;
	}
}
