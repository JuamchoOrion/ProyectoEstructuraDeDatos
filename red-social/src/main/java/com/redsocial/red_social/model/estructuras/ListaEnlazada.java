package com.redsocial.red_social.model.estructuras;


import java.util.List;

public class ListaEnlazada<T>  {
    private Nodo<T> cabeza;
    private Nodo<T> cola;

    public ListaEnlazada() {
        cabeza = null;
    }

    public void agregar(T dato) {
        if (cabeza == null) {
            cola = new Nodo<>(dato);
            cabeza = cola;
        }else{
            Nodo<T> nuevo = new Nodo<>(dato);
            nuevo.siguiente = cabeza;
            cabeza = nuevo;
            cola.siguiente = cabeza;
        }}


    public void mostrar() {
        Nodo<T> actual = cabeza;
        while (actual != null) {
            System.out.print(actual.dato + " -> ");
            actual = actual.siguiente;
        }
        System.out.println("null");
    }

    public void eliminar(T dato) {
        if (cabeza == null) return;
        if (cabeza== cola&&cabeza.dato.equals(dato)) {
            cola = null;
            cabeza = null;
            return;
        }
        if (cabeza.dato.equals(dato)) {
            cabeza = cabeza.siguiente;
            cola.siguiente = cabeza;
            return;
        }

        Nodo<T> actual = cabeza;
        while (!actual.siguiente.dato.equals(dato)) {
            actual = actual.siguiente;
        }
        if (actual.siguiente == cola) {
            cola= actual;
            actual.siguiente = cabeza;
        }
        else{
            actual.siguiente = actual.siguiente.siguiente;
        }
    }

    public void agregarDerechaDeIndice(int indice, T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null && indice == 0) {
            cabeza = nuevo;
            return;
        }

        Nodo<T> actual = cabeza;
        int contador = 0;
        while (actual != null && contador < indice) {
            actual = actual.siguiente;
            contador++;
        }

        if (actual != null) {
            nuevo.siguiente = actual.siguiente;
            actual.siguiente = nuevo;
        } else {
            System.out.println("Índice fuera de rango.");
        }
    }

    public void agregarIzquierdaDeIndice(int indice, T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);

        if (indice == 0) {
            nuevo.siguiente = cabeza;
            cabeza = nuevo;
            return;
        }

        Nodo<T> actual = cabeza;
        int contador = 0;

        while (actual != null && contador < indice - 1) {
            actual = actual.siguiente;
            contador++;
        }

        if (actual != null) {
            nuevo.siguiente = actual.siguiente;
            actual.siguiente = nuevo;
        } else {
            System.out.println("Índice fuera de rango.");
        }
    }
}
