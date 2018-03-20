package it.tredi.ecm.web.confirm;

import java.util.ArrayList;
import java.util.List;

public class EventoConfirmWrapper {
	List<IConfirm> confirms = new ArrayList<IConfirm>();
	
	public EventoConfirmWrapper() {
		this.clean();
	}
	
	public void clean() {
		confirms.clear();
	}
	
	public void addConfirm(IConfirm confirm) {
		if(confirm.isConfirmRequired())
			this.confirms.add(confirm);
	}
	
	public boolean isConfirmRequired() {
		for(IConfirm confirm : confirms) {
			if(confirm.isConfirmRequired())
				return true;
		}
		return false;
	}

	public List<IConfirm> getConfirms() {
		return confirms;
	}

}
