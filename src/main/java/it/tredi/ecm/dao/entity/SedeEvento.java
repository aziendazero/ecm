package it.tredi.ecm.dao.entity;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class SedeEvento implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = -1418164019707534287L;
	private String provincia; //TODO da lista
	private String comune; //TODO da lista
	private String indirizzo;//campo libero
	private String luogo;//campo libero

	public SedeEvento() {

	}

	public SedeEvento(SedeEvento sedeEvento) {
		if(sedeEvento != null) {
			this.setComune(sedeEvento.getComune());
			this.setIndirizzo(sedeEvento.getIndirizzo());
			this.setLuogo(sedeEvento.getLuogo());
			this.setProvincia(sedeEvento.getProvincia());
		}
	}

	public void copiaDati(SedeEvento sedeEvento) {
		if(sedeEvento == null) {
			this.comune = "";
			this.indirizzo = "";
			this.luogo = "";
			this.provincia = "";
		} else {
			this.comune = sedeEvento.comune;
			this.indirizzo = sedeEvento.indirizzo;
			this.luogo = sedeEvento.luogo;
			this.provincia = sedeEvento.provincia;
		}
	}

	public boolean isEmpty() {
		return (comune == null || comune.isEmpty())
		&& (indirizzo == null || indirizzo.isEmpty())
		&& (luogo == null || luogo.isEmpty())
		&& (provincia == null || provincia.isEmpty());
	}

	public static boolean isEmpty(SedeEvento o) {
		if(o == null)
			return true;
		return o.isEmpty();
	}

	public static boolean compare(SedeEvento o1, SedeEvento o2) {
		if (o1 == o2)
			return true;
		//null e empty vengono considerati uguali
		if (isEmpty(o1) && isEmpty(o2))
			return true;
		//a questo punto le sedi != null con l'altra sede null sono sicuramente non empty
		if ((o1 == null && o2 != null) || o2 == null && o1 != null)
			return false;
		//ora sono entrambe non null
		return Objects.equals(o1.comune, o2.comune) &&
		Objects.equals(o1.indirizzo, o2.indirizzo) &&
		Objects.equals(o1.luogo, o2.luogo) &&
		Objects.equals(o1.provincia, o2.provincia);
	}
}
