package com.redsocial.red_social.model.estructuras;

import com.redsocial.red_social.model.Contenido;

class NodoABB {
    Contenido contenido;
    NodoABB izquierdo, derecho;

    public NodoABB(Contenido contenido) {
        this.contenido = contenido;
        izquierdo = derecho = null;
    }
}