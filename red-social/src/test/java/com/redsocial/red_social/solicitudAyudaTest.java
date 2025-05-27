package com.redsocial.red_social;

import com.redsocial.red_social.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Date;

class solicitudAyudaTest {

    private Estudiante estudiante;
    private Date fechaActual;
    private Date fechaPasada;
    private Date fechaFutura;

    @BeforeEach
    void setUp() {
        estudiante = new Estudiante("user123", "Juan Pérez", "juan@example.com");
        estudiante.setId(1L);

        fechaActual = new Date();
        fechaPasada = new Date(System.currentTimeMillis() - 100000);
        fechaFutura = new Date(System.currentTimeMillis() + 100000);
    }

    @Test
    void testConstructorCompleto() {
        String peticion = "Necesito ayuda con matemáticas avanzadas";
        SolicitudAyuda solicitud = new SolicitudAyuda(estudiante, fechaFutura, peticion, Intereses.MATEMATICAS);

        assertEquals(estudiante, solicitud.getEstudiante());
        assertEquals(fechaFutura, solicitud.getFechaNecesidad());
        assertEquals(peticion, solicitud.getPeticion());
        assertEquals(Intereses.MATEMATICAS, solicitud.getInteres());
        assertNotNull(solicitud.getGrupos());
        assertTrue(solicitud.getGrupos().isEmpty());
    }

    @Test
    void testConstructorVacio() {
        SolicitudAyuda solicitud = new SolicitudAyuda();

        assertNull(solicitud.getEstudiante());
        assertNull(solicitud.getFechaNecesidad());
        assertNull(solicitud.getPeticion());
        assertNull(solicitud.getInteres());
        assertNotNull(solicitud.getGrupos());
        assertTrue(solicitud.getGrupos().isEmpty());
    }

    @Test
    void testSettersAndGetters() {
        SolicitudAyuda solicitud = new SolicitudAyuda();

        solicitud.setEstudiante(estudiante);
        solicitud.setFechaNecesidad(fechaFutura);
        solicitud.setPeticion("Ayuda con programación");
        solicitud.setInteres(Intereses.BIOLOGIA);

        assertEquals(estudiante, solicitud.getEstudiante());
        assertEquals(fechaFutura, solicitud.getFechaNecesidad());
        assertEquals("Ayuda con programación", solicitud.getPeticion());
        assertEquals(Intereses.BIOLOGIA, solicitud.getInteres());
    }

    @Test
    void testCompareTo() {
        SolicitudAyuda solicitud1 = new SolicitudAyuda(estudiante, fechaPasada, "Petición pasada", Intereses.MATEMATICAS);
        SolicitudAyuda solicitud2 = new SolicitudAyuda(estudiante, fechaFutura, "Petición futura", Intereses.BIOLOGIA);
        SolicitudAyuda solicitud3 = new SolicitudAyuda(estudiante, fechaActual, "Petición actual", Intereses.FISICA);

        // solicitud1 (pasada) vs solicitud2 (futura)
        assertTrue(solicitud1.compareTo(solicitud2) > 0);

        // solicitud2 (futura) vs solicitud3 (actual)
        assertTrue(solicitud2.compareTo(solicitud3) > 0);

        // Igualdad consigo misma
        assertEquals(0, solicitud1.compareTo(solicitud1));
    }

    @Test
    void testCompareToConFechasNulas() {
        SolicitudAyuda solicitud1 = new SolicitudAyuda();
        SolicitudAyuda solicitud2 = new SolicitudAyuda(estudiante, null, "Sin fecha", Intereses.MATEMATICAS);

        assertEquals(0, solicitud1.compareTo(solicitud2));
        assertEquals(0, solicitud2.compareTo(solicitud1));
    }

    @Test
    void testAgregarGrupoEstudio() {
        SolicitudAyuda solicitud = new SolicitudAyuda(estudiante, fechaFutura, "Petición", Intereses.MATEMATICAS);
        GrupoEstudio grupo = new GrupoEstudio();
        grupo.setId(1L);

        solicitud.getGrupos().add(grupo);

        assertEquals(1, solicitud.getGrupos().size());
        assertEquals(grupo, solicitud.getGrupos().get(0));
    }

    @Test
    void testRedSocialTransient() {
        SolicitudAyuda solicitud = new SolicitudAyuda();
        RedSocial redSocial = new RedSocial();

        solicitud.setRedSocial(redSocial);

        assertEquals(redSocial, solicitud.getRedSocial());
    }
}