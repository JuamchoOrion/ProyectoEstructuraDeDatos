package com.redsocial.red_social;

import com.redsocial.red_social.dto.GrafoDTO;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.estructuras.GrafoEstudiantes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class grafoEstudiantesTest {

    private GrafoEstudiantes grafo;
    private Estudiante juan;
    private Estudiante maria;
    private Estudiante pedro;
    private Estudiante ana;

    @BeforeEach
    void setUp() {
        grafo = new GrafoEstudiantes();

        juan = new Estudiante("juan", "Juan Pérez", "juan@example.com");
        juan.setId(1L);

        maria = new Estudiante("maria", "María García", "maria@example.com");
        maria.setId(2L);

        pedro = new Estudiante("pedro", "Pedro López", "pedro@example.com");
        pedro.setId(3L);

        ana = new Estudiante("ana", "Ana Martínez", "ana@example.com");
        ana.setId(4L);

        grafo.agregarEstudiante(juan);
        grafo.agregarEstudiante(maria);
        grafo.agregarEstudiante(pedro);
        grafo.agregarEstudiante(ana);
    }

    @Test
    void testAgregarEstudiante() {
        Estudiante luis = new Estudiante("luis", "Luis Rodríguez", "luis@example.com");
        luis.setId(5L);
        grafo.agregarEstudiante(luis);

        GrafoDTO grafoDTO = grafo.visualizar();
        assertTrue(grafoDTO.getNodos().stream()
                .anyMatch(n -> n.getUsername().equals("luis") && n.getId().equals(5L)));
    }

    @Test
    void testConectarEstudiantes() {
        grafo.conectarEstudiantes(juan.getId(), maria.getId(), 5);

        GrafoDTO grafoDTO = grafo.visualizar();
        assertTrue(grafoDTO.getAristas().stream()
                .anyMatch(a -> a.getOrigen().equals(juan.getId()) &&
                        a.getDestino().equals(maria.getId()) &&
                        a.getPeso() == 5));
    }

    @Test
    void testObtenerRecomendaciones() {
        // Estructura: juan-maria-pedro y juan-ana
        grafo.conectarEstudiantes(juan.getId(), maria.getId(), 3);
        grafo.conectarEstudiantes(maria.getId(), pedro.getId(), 4);
        grafo.conectarEstudiantes(juan.getId(), ana.getId(), 2);

        List<Estudiante> recomendaciones = grafo.obtenerRecomendaciones(juan.getId());

        // Debería recomendar a pedro (amigo de su amigo maria)
        assertEquals(1, recomendaciones.size());
        assertEquals(pedro.getId(), recomendaciones.get(0).getId());
    }

    @Test
    void testEncontrarCaminoMasCorto() {
        // Estructura: juan-maria-pedro-ana
        grafo.conectarEstudiantes(juan.getId(), maria.getId(), 1);
        grafo.conectarEstudiantes(maria.getId(), pedro.getId(), 1);
        grafo.conectarEstudiantes(pedro.getId(), ana.getId(), 1);

        List<Estudiante> camino = grafo.encontrarCaminoMasCorto(juan.getId(), ana.getId());

        assertEquals(4, camino.size());
        assertEquals(juan.getId(), camino.get(0).getId());
        assertEquals(ana.getId(), camino.get(3).getId());
    }

    @Test
    void testDetectarComunidades() {
        // Dos comunidades desconectadas: juan-maria y pedro-ana
        grafo.conectarEstudiantes(juan.getId(), maria.getId(), 1);
        grafo.conectarEstudiantes(pedro.getId(), ana.getId(), 1);

        List<List<Estudiante>> comunidades = grafo.detectarComunidades();

        // Verifica que hay 2 comunidades y que cada una tiene 2 estudiantes
        assertEquals(2, comunidades.size());
        assertTrue(comunidades.stream().allMatch(c -> c.size() == 2));
    }

    @Test
    void testCalcularAfinidadConEstudiante() {
        grafo.conectarEstudiantes(juan.getId(), maria.getId(), 5);
        grafo.conectarEstudiantes(juan.getId(), maria.getId(), 3); // Conexión múltiple

        int afinidad = grafo.calcularAfinidadConEstudiante(juan.getId(), maria.getId());

        assertEquals(8, afinidad); // 5 + 3
    }

    @Test
    void testVisualizarGrafo() {
        grafo.conectarEstudiantes(juan.getId(), maria.getId(), 5);
        grafo.conectarEstudiantes(pedro.getId(), ana.getId(), 3);

        GrafoDTO grafoDTO = grafo.visualizar();

        assertEquals(4, grafoDTO.getNodos().size());
        assertEquals(2, grafoDTO.getAristas().size());
        assertTrue(grafoDTO.getAristas().stream().anyMatch(a -> a.getPeso() == 5));
        assertTrue(grafoDTO.getAristas().stream().anyMatch(a -> a.getPeso() == 3));
    }
}