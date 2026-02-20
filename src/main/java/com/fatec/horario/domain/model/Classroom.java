package com.fatec.horario.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "classroom")
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private String physicalResources;
    private String softwareResources;
    private Integer capacity;
    private Boolean template;
    private Boolean practical;

    public Classroom(Long id, String name, String location, String physicalResources, String softwareResources,
            Integer capacity, Boolean template, Boolean practical) {
        this.id = id;
        this.name = name;
        this.physicalResources = physicalResources;
        this.softwareResources = softwareResources;
        this.capacity = capacity;
        this.location = location;
        this.template = template;
        this.practical = practical;
    }

    public Classroom() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhysicalResources() {
        return physicalResources;
    }

    public void setPhysicalResources(String physicalResources) {
        this.physicalResources = physicalResources;
    }

    public String getSoftwareResources() {
        return softwareResources;
    }

    public void setSoftwareResources(String softwareResources) {
        this.softwareResources = softwareResources;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Boolean getTemplate() {
        return template;
    }

    public void setTemplate(Boolean template) {
        this.template = template;
    }

    public Boolean getPractical() {
        return practical;
    }

    public void setPractical(Boolean practical) {
        this.practical = practical;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Classroom other = (Classroom) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
