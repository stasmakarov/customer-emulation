package com.company.customeremulation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jmix.core.MetadataTools;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.DependsOnProperties;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

import java.util.UUID;

@JmixEntity
@Table(name = "CST_ITEM_PERSISTED")
@Entity(name = "cst_ItemPersisted")
public class ItemPersisted {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    @JsonIgnore
    private UUID id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    @Lob
    @JsonIgnore
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @InstanceName
    @DependsOnProperties({"name"})
    @JsonIgnore
    public String getInstanceName(MetadataTools metadataTools) {
        return metadataTools.format(name);
    }
}