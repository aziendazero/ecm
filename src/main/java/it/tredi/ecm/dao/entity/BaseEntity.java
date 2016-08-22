package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonView;

@MappedSuperclass
public class BaseEntity implements Cloneable{
	@JsonView(JsonViewModel.Integrazione.class)
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
    protected Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public boolean isNew() {
        return (this.id == null);
    }
    
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
    
    @Override
	public Object clone() throws CloneNotSupportedException {
    	return super.clone();
    }
}
