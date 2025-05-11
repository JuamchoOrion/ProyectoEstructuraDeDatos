package com.redsocial.red_social.model.estructuras;

import lombok.Data;

@Data
public class Nodo<T> {
    T dato;
    Nodo<T> siguiente;

    public Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }
}