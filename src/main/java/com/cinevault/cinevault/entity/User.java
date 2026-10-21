package com.cinevault.cinevault.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User {

    //indica che l'id è un primary key il cui numero verrà poi generarto in automatico
    //non ha il setter appunto xè è generato in automatico
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    // annotazione che indica che qsto campo è un enum, e che i valori devono essere salvati coem stringhe
    // xè di default vengono se no salvati come numeri
    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean enabled;

    public User() {

    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    

}
