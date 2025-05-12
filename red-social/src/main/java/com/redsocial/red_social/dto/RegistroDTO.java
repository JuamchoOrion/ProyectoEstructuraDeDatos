package com.redsocial.red_social.dto;

import com.redsocial.red_social.model.Intereses;

import java.util.Set;

public class RegistroDTO {
    private String nombre;
    private String email;
    private String password;
    private String confirmPassword;
    private Set<Intereses> intereses;

    public Set<Intereses> getIntereses() {
        return intereses;
    }

    public void setIntereses(Set<Intereses> intereses) {
        this.intereses = intereses;
    }

    public RegistroDTO() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
