package com.redsocial.red_social.model.estructuras;

import lombok.Data;
import java.util.Iterator;
import java.util.NoSuchElementException;

@Data
public class ListaEnlazada<T> implements Iterable<T> {
    private Nodo<T> cabeza;
    private Nodo<T> cola;
    private int modCount = 0; // Para control de modificaciones concurrentes

    // Clase Nodo interna
    private static class Nodo<T> {
        T dato;
        Nodo<T> siguiente;

        Nodo(T dato) {
            this.dato = dato;
        }
    }

    // Implementación del Iterator
    private class IteradorListaEnlazada implements Iterator<T> {
        private Nodo<T> actual;
        private Nodo<T> ultimoRetornado;
        private int expectedModCount = modCount;

        IteradorListaEnlazada() {
            this.actual = cabeza;
        }

        @Override
        public boolean hasNext() {
            return actual != null;
        }

        @Override
        public T next() {
            checkForComodification();
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            ultimoRetornado = actual;
            actual = actual.siguiente;
            // Manejo de la circularidad
            if (actual == cabeza) {
                actual = null;
            }
            return ultimoRetornado.dato;
        }

        @Override
        public void remove() {
            checkForComodification();
            if (ultimoRetornado == null) {
                throw new IllegalStateException();
            }

            ListaEnlazada.this.eliminar(ultimoRetornado.dato);
            expectedModCount = modCount;
            ultimoRetornado = null;
        }

        final void checkForComodification() {
            if (modCount != expectedModCount) {
                throw new java.util.ConcurrentModificationException();
            }
        }
    }

    /* ------------------------- */
    /*  Métodos de la lista       */
    /* ------------------------- */

    public ListaEnlazada() {
        cabeza = null;
        cola = null;
    }

    public boolean agregar(T dato) {
        if (cabeza == null) {
            cola = new Nodo<>(dato);
            cabeza = cola;
            cola.siguiente = cabeza; // Circularidad
        } else {
            Nodo<T> nuevo = new Nodo<>(dato);
            nuevo.siguiente = cabeza;
            cabeza = nuevo;
            cola.siguiente = cabeza; // Mantener circularidad
        }
        modCount++;
        return true;
    }

    public void mostrar() {
        if (cabeza == null) {
            System.out.println("null");
            return;
        }

        Nodo<T> actual = cabeza;
        do {
            System.out.print(actual.dato + " -> ");
            actual = actual.siguiente;
        } while (actual != null && actual != cabeza);

        System.out.println(actual == cabeza ? "(circular)" : "null");
    }

    public boolean eliminar(T dato) {
        if (cabeza == null) return false;

        // Caso 1: Lista con un solo nodo
        if (cabeza == cola && cabeza.dato.equals(dato)) {
            cabeza = null;
            cola = null;
            modCount++;
            return true;
        }

        // Caso 2: Eliminar cabeza
        if (cabeza.dato.equals(dato)) {
            cabeza = cabeza.siguiente;
            cola.siguiente = cabeza;
            modCount++;
            return true;
        }

        // Caso 3: Buscar nodo a eliminar
        Nodo<T> actual = cabeza;
        while (actual.siguiente != cabeza && !actual.siguiente.dato.equals(dato)) {
            actual = actual.siguiente;
        }

        if (actual.siguiente.dato.equals(dato)) {
            if (actual.siguiente == cola) {
                cola = actual;
            }
            actual.siguiente = actual.siguiente.siguiente;
            modCount++;
            return true;
        }

        return false;
    }

    public void agregarDerechaDeIndice(int indice, T dato) {
        if (indice < 0) throw new IndexOutOfBoundsException("Índice negativo: " + indice);

        Nodo<T> nuevo = new Nodo<>(dato);

        if (cabeza == null) {
            if (indice == 0) {
                cabeza = nuevo;
                cola = nuevo;
                cola.siguiente = cabeza;
                modCount++;
            } else {
                throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
            }
            return;
        }

        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
            if (actual == cabeza) {
                throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
            }
        }

        nuevo.siguiente = actual.siguiente;
        actual.siguiente = nuevo;

        if (actual == cola) {
            cola = nuevo;
        }

        modCount++;
    }

    public void agregarIzquierdaDeIndice(int indice, T dato) {
        if (indice < 0) throw new IndexOutOfBoundsException("Índice negativo: " + indice);

        if (indice == 0) {
            agregar(dato); // Reutilizamos agregar que pone al inicio
            return;
        }

        agregarDerechaDeIndice(indice - 1, dato);
    }

    public boolean contiene(T dato) {
        if (cabeza == null) return false;

        Nodo<T> actual = cabeza;
        do {
            if (actual.dato.equals(dato)) {
                return true;
            }
            actual = actual.siguiente;
        } while (actual != cabeza);

        return false;
    }

    public int tamaño() {
        if (cabeza == null) return 0;

        int contador = 0;
        Nodo<T> actual = cabeza;
        do {
            contador++;
            actual = actual.siguiente;
        } while (actual != cabeza);

        return contador;
    }

    public void vaciar() {
        cabeza = null;
        cola = null;
        modCount++;
    }

    /* ------------------------- */
    /*  Implementación Iterable   */
    /* ------------------------- */

    @Override
    public Iterator<T> iterator() {
        return new IteradorListaEnlazada();
    }

    /* ------------------------- */
    /*  Métodos adicionales       */
    /* ------------------------- */

    public T obtenerPrimero() {
        if (cabeza == null) {
            throw new NoSuchElementException("La lista está vacía");
        }
        return cabeza.dato;
    }

    public T obtenerUltimo() {
        if (cola == null) {
            throw new NoSuchElementException("La lista está vacía");
        }
        return cola.dato;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }
}