package com.markman.core.organization;

import com.markman.core.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "organization")
public class Organization extends AbstractEntity {

    @Column(nullable = false, unique = true, length = 32)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrganizationStatus status;

    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @Column(nullable = false, length = 50)
    private String timezone;

    protected Organization() {
        // JPA
    }

    public Organization(String code, String name) {
        this.code = code;
        this.name = name;
        this.status = OrganizationStatus.ACTIVE;
        this.currencyCode = "PHP";
        this.timezone = "Asia/Manila";
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public OrganizationStatus getStatus() {
        return status;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getTimezone() {
        return timezone;
    }

    public boolean isActive() {
        return status == OrganizationStatus.ACTIVE;
    }

    public void suspend() {
        this.status = OrganizationStatus.SUSPENDED;
    }

    public void reactivate() {
        this.status = OrganizationStatus.ACTIVE;
    }
}
