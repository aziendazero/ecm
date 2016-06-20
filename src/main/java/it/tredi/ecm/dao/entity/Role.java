package it.tredi.ecm.dao.entity;

import java.util.Objects;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "role")
@Getter
@Setter
public class Role extends BaseEntity{
    private String name;
    private String description;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Role entitapiatta = (Role) o;
        return Objects.equals(id, entitapiatta.id);
    }
}