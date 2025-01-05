package com.company.customeremulation.entity;

import io.jmix.appsettings.defaults.AppSettingsDefault;
import io.jmix.appsettings.entity.AppSettingsEntity;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

@JmixEntity
@Table(name = "CST_CUSTOMER_SETTING")
@Entity(name = "cst_CustomerSetting")
public class CustomerSetting extends AppSettingsEntity {
    @JmixGeneratedValue
    @Column(name = "UUID")
    private UUID uuid;

    @AppSettingsDefault("orders")
    @Column(name = "QUEUE_NAME")
    private String queueName;

    @AppSettingsDefault("3")
    @Column(name = "MIN_DELAY")
    private Integer minDelay;

    @AppSettingsDefault("20")
    @Column(name = "MAX_DELAY")
    private Integer maxDelay;

    @AppSettingsDefault("15")
    @Column(name = "FAKE_PROBABILITY")
    private Integer fakeProbability;

    @AppSettingsDefault("10")
    @Column(name = "MAX_ITEMS")
    private Integer maxItems;

    @AppSettingsDefault("10")
    @Column(name = "ATTEMPTS")
    private Integer attempts;

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(Integer maxItems) {
        this.maxItems = maxItems;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public Integer getFakeProbability() {
        return fakeProbability;
    }

    public void setFakeProbability(Integer fakeProbability) {
        this.fakeProbability = fakeProbability;
    }

    public Integer getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(Integer maxDelay) {
        this.maxDelay = maxDelay;
    }

    public Integer getMinDelay() {
        return minDelay;
    }

    public void setMinDelay(Integer minDelay) {
        this.minDelay = minDelay;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}