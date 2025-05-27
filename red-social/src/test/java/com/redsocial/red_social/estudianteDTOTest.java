package com.redsocial.red_social;

import com.redsocial.red_social.dto.EstudianteDTO;
import com.redsocial.red_social.model.Intereses;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashSet;
import java.util.Set;

class estudianteDTOTest {

    @Test
    void testConstructorVacio() {
        EstudianteDTO dto = new EstudianteDTO();

        assertNull(dto.getId());
        assertNull(dto.getUsername());
        assertNull(dto.getEmail());
        assertNull(dto.getIntereses());
    }

    @Test
    void testConstructorConTresParametros() {
        EstudianteDTO dto = new EstudianteDTO(1L, "juan123", "juan@example.com");

        assertEquals(1L, dto.getId());
        assertEquals("juan123", dto.getUsername());
        assertEquals("juan@example.com", dto.getEmail());
        assertNotNull(dto.getIntereses());
        assertTrue(dto.getIntereses().isEmpty());
    }

    @Test
    void testConstructorCompleto() {
        Set<Intereses> intereses = new HashSet<>();
        intereses.add(Intereses.MATEMATICAS);
        intereses.add(Intereses.BIOLOGIA);

        EstudianteDTO dto = new EstudianteDTO(1L, "juan123", "juan@example.com", intereses);

        assertEquals(1L, dto.getId());
        assertEquals("juan123", dto.getUsername());
        assertEquals("juan@example.com", dto.getEmail());
        assertEquals(2, dto.getIntereses().size());
        assertTrue(dto.getIntereses().contains(Intereses.MATEMATICAS));
        assertTrue(dto.getIntereses().contains(Intereses.BIOLOGIA));
    }

    @Test
    void testBuilder() {
        EstudianteDTO dto = EstudianteDTO.builder()
                .id(1L)
                .username("juan123")
                .email("juan@example.com")
                .intereses(Set.of(Intereses.MATEMATICAS))
                .build();

        assertEquals(1L, dto.getId());
        assertEquals("juan123", dto.getUsername());
        assertEquals("juan@example.com", dto.getEmail());
        assertEquals(1, dto.getIntereses().size());
        assertTrue(dto.getIntereses().contains(Intereses.MATEMATICAS));
    }

    @Test
    void testSettersAndGetters() {
        EstudianteDTO dto = new EstudianteDTO();

        dto.setId(1L);
        dto.setUsername("juan123");
        dto.setEmail("juan@example.com");

        Set<Intereses> intereses = new HashSet<>();
        intereses.add(Intereses.MATEMATICAS);
        dto.setIntereses(intereses);

        assertEquals(1L, dto.getId());
        assertEquals("juan123", dto.getUsername());
        assertEquals("juan@example.com", dto.getEmail());
        assertEquals(1, dto.getIntereses().size());
        assertTrue(dto.getIntereses().contains(Intereses.MATEMATICAS));
    }

    @Test
    void testEqualsAndHashCode() {
        EstudianteDTO dto1 = new EstudianteDTO(1L, "juan123", "juan@example.com");
        EstudianteDTO dto2 = new EstudianteDTO(1L, "juan123", "juan@example.com");

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }

    @Test
    void testToString() {
        EstudianteDTO dto = new EstudianteDTO(1L, "juan123", "juan@example.com");
        String toStringResult = dto.toString();

        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("username=juan123"));
        assertTrue(toStringResult.contains("email=juan@example.com"));
    }

    @Test
    void testAddInteres() {
        EstudianteDTO dto = new EstudianteDTO();
        dto.setIntereses(new HashSet<>());

        dto.getIntereses().add(Intereses.MATEMATICAS);
        dto.getIntereses().add(Intereses.BIOLOGIA);

        assertEquals(2, dto.getIntereses().size());
        assertTrue(dto.getIntereses().contains(Intereses.MATEMATICAS));
        assertTrue(dto.getIntereses().contains(Intereses.BIOLOGIA));

        // Test para evitar duplicados
        dto.getIntereses().add(Intereses.MATEMATICAS);
        assertEquals(2, dto.getIntereses().size());
    }
}