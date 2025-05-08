package com.redsocial.red_social.model.estructuras;

import com.redsocial.red_social.model.Contenido;

public class ABBContenidos {
    NodoABB raiz;

    public void insertar(Contenido contenido) {
        raiz = insertarRec(raiz, contenido);
    }

    private NodoABB insertarRec(NodoABB nodo, Contenido contenido) {
        if (nodo == null) {
            return new NodoABB(contenido);
        }

        int comparacion = contenido.getAutor().getUsername().compareToIgnoreCase(nodo.contenido.getAutor().getUsername());
        if (comparacion < 0) {
            nodo.izquierdo = insertarRec(nodo.izquierdo, contenido);
        } else if (comparacion > 0) {
            nodo.derecho = insertarRec(nodo.derecho, contenido);
        }
        return nodo;
    }

    public void recorridoInorden() {
        inorden(raiz);
    }

    private void inorden(NodoABB nodo) {
        if (nodo != null) {
            inorden(nodo.izquierdo);
            System.out.println(nodo.contenido);
            inorden(nodo.derecho);
        }
    }

    public boolean buscarPorAutor(String autor) {
        return buscarRec(raiz, autor);
    }

    private boolean buscarRec(NodoABB nodo, String autor) {
        if (nodo == null) return false;
        int cmp = autor.compareToIgnoreCase(nodo.contenido.getAutor().getUsername());
        if (cmp == 0) return true;
        return cmp < 0 ? buscarRec(nodo.izquierdo, autor) : buscarRec(nodo.derecho, autor);
    }
    }