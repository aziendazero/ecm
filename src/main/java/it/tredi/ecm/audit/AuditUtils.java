package it.tredi.ecm.audit;

public class AuditUtils {
	//EventoRES/3750#sedeEvento
	//globalId.getTypeName() = it.tredi.ecm.dao.entity.SedeEvento

	//EventoRES/3750
	//

	static String getTypeNameWithoutPackage(String typeName) {
		int pos = typeName.lastIndexOf(".");
		if(pos > 0) {
			return typeName.substring(pos + 1);
		}
		return typeName;
	}
}
