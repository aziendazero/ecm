package it.tredi.ecm.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import it.tredi.ecm.audit.AuditLabelInfo;

@Service
public class AuditServiceImpl implements AuditService {
	private static Logger LOGGER = LoggerFactory.getLogger(AuditServiceImpl.class);

	@Autowired
	private MessageSource messageSource;

	@Value("#{PropertyMapper.startWith('auditlabelmap', '', false)}")
	private Map<String, String> labelForAuditLabel;

	@Override
	public 	String getLabelForAuditProperty(List<AuditLabelInfo> auditLabelInfos, String propertyLabel) {
		String label = "";
		String pathLabel;

		for(AuditLabelInfo ali : auditLabelInfos) {
			pathLabel = getLabel(ali.getPropertyLabel());
			if(pathLabel != null) {
				if(ali.getObjectIdentifier() != null && !ali.getObjectIdentifier().isEmpty())
					pathLabel += " " + ali.getObjectIdentifier();
				if(!label.isEmpty())
					label += " ";
				label += pathLabel;
			}
		}
		if(!label.isEmpty())
			label += " - ";
		label += getLabel(propertyLabel);

		return label;
	}

	//Restituisce la label
	// se non in labelForAuditLabel restituisce "??key " + labelAudit + " not found??"
	// se presente in labelForAuditLabel
	//		se valorizzata il valore
	//		se non valorizzata null
	private String getLabel(String labelAudit) {
		String label = null;
		if(labelForAuditLabel.containsKey(labelAudit)) {
			label = labelForAuditLabel.get(labelAudit);
			if(label != null) {
				if(label.isEmpty())
					label = null;
				else
					label = messageSource.getMessage(label, null, Locale.getDefault());
					//messageSource.getMessage("label.dati_verbale", values, Locale.getDefault())
			}
		} else {
			label = "??key " + labelAudit + " not found??";
		}
		return label;
	}
}
