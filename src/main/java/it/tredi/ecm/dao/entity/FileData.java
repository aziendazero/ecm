package it.tredi.ecm.dao.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FileData extends BaseEntity{
    
	private byte[] data;
}
