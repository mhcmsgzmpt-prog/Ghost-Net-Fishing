package de.finnk.ghostnet.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reporting_persons")
public class ReportingPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phoneNumber;
    private boolean anonymous;

    public ReportingPerson() {
    }

    public ReportingPerson(String name, String phoneNumber, boolean anonymous) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.anonymous = anonymous;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isAnonymous() {
        return anonymous;
    }
public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
}