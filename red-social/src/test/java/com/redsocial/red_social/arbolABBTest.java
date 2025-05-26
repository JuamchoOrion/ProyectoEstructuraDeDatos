package com.redsocial.red_social;

import com.redsocial.red_social.model.Contenido;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.estructuras.ArbolABB;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class arbolABBTest {
    private ArbolABB arbol;
    private Contenido contenido1;
    private Contenido contenido2;
    private Contenido contenido3;

    @BeforeEach
    void setUp() {
        arbol = new ArbolABB();

        Estudiante autor1 = new Estudiante("autor1", "Autor Uno", "autor1@example.com");
        autor1.setId(1L);
        Estudiante autor2 = new Estudiante("autor2", "Autor Dos", "autor2@example.com");
        autor2.setId(2L);

        contenido1 = new Contenido();
        contenido1.setId(1L);
        contenido1.setAutor(autor1);

        contenido2 = new Contenido();
        contenido2.setId(2L);
        contenido2.setAutor(autor2);

        contenido3 = new Contenido();
        contenido3.setId(3L);
        contenido3.setAutor(autor1); // Mismo autor que contenido1
    }

    @Test
    void testInsertarYBuscarPorId() {
        arbol.insertar(contenido1);
        arbol.insertar(contenido2);

        Contenido encontrado = arbol.buscarPorId(contenido1.getId());
        assertNotNull(encontrado);
        assertEquals(contenido1.getId(), encontrado.getId());

        assertNull(arbol.buscarPorId(99L)); // ID que no existe
    }

    @Test
    void testObtenerTodosContenidos() {
        arbol.insertar(contenido1);
        arbol.insertar(contenido2);
        arbol.insertar(contenido3);

        List<Contenido> contenidos = arbol.obtenerTodosContenidos();
        assertEquals(3, contenidos.size());
        assertTrue(contenidos.contains(contenido1));
        assertTrue(contenidos.contains(contenido2));
        assertTrue(contenidos.contains(contenido3));
    }

    @Test
    void testBuscarPorAutor() {
        arbol.insertar(contenido1);
        arbol.insertar(contenido2);
        arbol.insertar(contenido3);

        List<Contenido> resultados = arbol.buscarPorAutor("autor1");
        assertEquals(2, resultados.size());
        assertTrue(resultados.stream().allMatch(c -> c.getAutor().getUsername().equals("autor1")));

        List<Contenido> resultadosVacios = arbol.buscarPorAutor("noexiste");
        assertTrue(resultadosVacios.isEmpty());
    }

    @Test
    void testArbolVacio() {
        assertTrue(arbol.obtenerTodosContenidos().isEmpty());
        assertNull(arbol.buscarPorId(1L));
        assertTrue(arbol.buscarPorAutor("autor1").isEmpty());
    }
}