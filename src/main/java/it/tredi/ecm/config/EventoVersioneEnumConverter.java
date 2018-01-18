package it.tredi.ecm.config;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import it.tredi.ecm.dao.enumlist.EventoVersioneEnum;

@Converter(autoApply = true)
public class EventoVersioneEnumConverter implements AttributeConverter<EventoVersioneEnum, Integer> {

    @Override
    public Integer convertToDatabaseColumn(EventoVersioneEnum attribute) {
        return attribute == null ? null : attribute.getNumeroVersione();
    }

    @Override
    public EventoVersioneEnum convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : EventoVersioneEnum.getByNumeroVersione(dbData);
    }
}
