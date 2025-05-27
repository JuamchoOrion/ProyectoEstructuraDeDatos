package com.redsocial.red_social;

import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.estructuras.ABBContenidos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ABBContenidosTest {
    private ABBContenidos arbol;
    private Contenido contenido1;
    private Contenido contenido2;

    @BeforeEach
    void setUp() {
        arbol = new ABBContenidos();

        Estudiante autor1 = new Estudiante("autor1", "Autor Uno", "autor1@example.com");
        Estudiante autor2 = new Estudiante("autor2", "Autor Dos", "autor2@example.com");

        contenido1 = new Contenido();
        contenido1.setAutor(autor1);

        contenido2 = new Contenido();
        contenido2.setAutor(autor2);
    }

    @Test
    void testInsertarYBuscarPorAutor() {
        arbol.insertar(contenido1);
        arbol.insertar(contenido2);

        assertTrue(arbol.buscarPorAutor("autor1"));
        assertTrue(arbol.buscarPorAutor("AUTOR2")); // Prueba case insensitive
        assertFalse(arbol.buscarPorAutor("noexiste"));
    }

    @Test
    void testArbolVacio() {
        assertFalse(arbol.buscarPorAutor("autor1"));
    }

    @Test
    void testInsertarDuplicados() {
        arbol.insertar(contenido1);
        arbol.insertar(contenido1); // Insertar duplicado

        // No debería haber problemas, pero la implementación actual no maneja duplicados
        assertTrue(arbol.buscarPorAutor("autor1"));
    }
}