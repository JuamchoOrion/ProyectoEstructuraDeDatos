package com.redsocial.red_social.model.estructuras;
import com.redsocial.red_social.model.Contenido;

import java.util.*;
public class ArbolABB {
    private NodoABB raiz;

    // Clase interna para los nodos del árbol
    private static class NodoABB {
        Contenido contenido;
        NodoABB izquierdo;
        NodoABB derecho;

        public NodoABB(Contenido contenido) {
            this.contenido = contenido;
        }
    }

    // Insertar contenido en el árbol (ordenado por nombre del autor)
    public void insertar(Contenido contenido) {
        raiz = insertarRec(raiz, contenido);
    }

    private NodoABB insertarRec(NodoABB nodo, Contenido contenido) {
        if (nodo == null) {
            return new NodoABB(contenido);
        }

        // Comparar por ID para permitir múltiples contenidos del mismo autor
        int comparacion = contenido.getId().compareTo(nodo.contenido.getId());

        if (comparacion < 0) {
            nodo.izquierdo = insertarRec(nodo.izquierdo, contenido);
        } else if (comparacion > 0) {
            nodo.derecho = insertarRec(nodo.derecho, contenido);
        }
        // Si son iguales (mismo ID), no insertar duplicados

        return nodo;
    }

    // En tu ArbolABB
    public List<Contenido> buscarPorAutor(String autor) {
        List<Contenido> resultados = new ArrayList<>();
        buscarPorAutorRec(raiz, autor.toLowerCase(), resultados);
        return resultados;
    }

    private void buscarPorAutorRec(NodoABB nodo, String autor, List<Contenido> resultados) {
        if (nodo == null) return;

        // Primero recorrer izquierda
        buscarPorAutorRec(nodo.izquierdo, autor, resultados);

        // Luego procesar nodo actual
        if (nodo.contenido.getAutor().getUsername().equalsIgnoreCase(autor)) {
            resultados.add(nodo.contenido);
        }

        // Finalmente recorrer derecha
        buscarPorAutorRec(nodo.derecho, autor, resultados);
    }
    public ArbolABB() {
    }

    // Obtener todos los contenidos (recorrido inorden)
    public List<Contenido> obtenerTodosContenidos() {
        List<Contenido> contenidos = new ArrayList<>();
        inorden(raiz, contenidos);
        return contenidos;
    }


    private void inorden(NodoABB nodo, List<Contenido> contenidos) {
        if (nodo != null) {
            inorden(nodo.izquierdo, contenidos);
            contenidos.add(nodo.contenido);
            inorden(nodo.derecho, contenidos);
        }
    }

    // Buscar contenido por ID (necesario para valoraciones)
    public Contenido buscarPorId(Long id) {
        return buscarPorIdRec(raiz, id);
    }

    private Contenido buscarPorIdRec(NodoABB nodo, Long id) {
        if (nodo == null) return null;

        if (nodo.contenido.getId().equals(id)) {
            return nodo.contenido;
        }

        Contenido encontradoIzq = buscarPorIdRec(nodo.izquierdo, id);
        if (encontradoIzq != null) return encontradoIzq;

        return buscarPorIdRec(nodo.derecho, id);
    }
}