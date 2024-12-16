package com.company.customeremulation.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@JmixEntity
@Table(name = "CST_PARAMS")
@Entity(name = "cst_Params")
public class Params {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "MAX_ITEMS")
    private Integer maxItems;

    @Column(name = "MIN_DELAY")
    private Integer minDelay;

    @Column(name = "MAX_DELAY")
    private Integer maxDelay;

    @Column(name = "FAKE_ADDRESS_PROBABILITY")
    private Integer fakeAddressProbability;

    public Integer getFakeAddressProbability() {
        return fakeAddressProbability;
    }

    public void setFakeAddressProbability(Integer fakeAddressProbability) {
        this.fakeAddressProbability = fakeAddressProbability;
    }

    public Integer getMinDelay() {
        return minDelay;
    }

    public void setMinDelay(Integer minDelay) {
        this.minDelay = minDelay;
    }

    public Integer getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(Integer maxDelay) {
        this.maxDelay = maxDelay;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}