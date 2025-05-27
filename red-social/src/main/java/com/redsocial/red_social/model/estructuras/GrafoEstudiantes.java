package com.redsocial.red_social.model.estructuras;

import com.redsocial.red_social.dto.AristaDTO;
import com.redsocial.red_social.dto.GrafoDTO;
import com.redsocial.red_social.dto.NodoDTO;
import com.redsocial.red_social.model.Estudiante;

import java.util.*;
import java.util.stream.Collectors;


// Grafo.java
public class GrafoEstudiantes {
    private Map<Long, NodoEstudiante> nodos = new HashMap<>();
    private List<AristaAfinidad> aristas = new ArrayList<>();

    // Métodos para manipular el grafo
    public void agregarEstudiante(Estudiante estudiante) {
        if (!nodos.containsKey(estudiante.getId())) {
            nodos.put(estudiante.getId(), new NodoEstudiante(estudiante));
        }
    }

    public void conectarEstudiantes(Long id1, Long id2, int afinidad) {
        if (nodos.containsKey(id1) && nodos.containsKey(id2)) {
            AristaAfinidad arista = new AristaAfinidad(
                    nodos.get(id1),
                    nodos.get(id2),
                    afinidad
            );
            aristas.add(arista);
            nodos.get(id1).agregarVecino(nodos.get(id2));
            nodos.get(id2).agregarVecino(nodos.get(id1));
        }
    }
    public List<Estudiante> obtenerRecomendaciones(Long idEstudiante) {
        List<Estudiante> recomendaciones = new ArrayList<>();
        NodoEstudiante nodo = nodos.get(idEstudiante);

        if (nodo != null) {
            // Obtener amigos de amigos (nivel 2)
            Set<NodoEstudiante> yaRecomendados = new HashSet<>();

            for (NodoEstudiante vecino : nodo.getVecinos()) {
                for (NodoEstudiante vecinoDeVecino : vecino.getVecinos()) {
                    if (!vecinoDeVecino.equals(nodo) &&
                            !nodo.getVecinos().contains(vecinoDeVecino) &&
                            !yaRecomendados.contains(vecinoDeVecino)) {
                        recomendaciones.add(vecinoDeVecino.getEstudiante());
                        yaRecomendados.add(vecinoDeVecino);
                    }
                }
            }

            // Ordenar por peso de afinidad (simplificado)
            recomendaciones.sort((e1, e2) -> {
                int afinidad1 = calcularAfinidadConEstudiante(idEstudiante, e1.getId());
                int afinidad2 = calcularAfinidadConEstudiante(idEstudiante, e2.getId());
                return Integer.compare(afinidad2, afinidad1);
            });
        }

        return recomendaciones;
    }

    // Algoritmo BFS para encontrar el camino más corto
    public List<Estudiante> encontrarCaminoMasCorto(Long inicio, Long fin) {
        Map<Long, Long> padres = new HashMap<>();
        Queue<Long> cola = new LinkedList<>();
        Set<Long> visitados = new HashSet<>();

        cola.add(inicio);
        visitados.add(inicio);
        padres.put(inicio, null);

        while (!cola.isEmpty()) {
            Long actual = cola.poll();

            if (actual.equals(fin)) {
                return reconstruirCamino(padres, fin);
            }

            for (NodoEstudiante vecino : nodos.get(actual).getVecinos()) {
                Long idVecino = vecino.getEstudiante().getId();
                if (!visitados.contains(idVecino)) {
                    visitados.add(idVecino);
                    padres.put(idVecino, actual);
                    cola.add(idVecino);
                }
            }
        }

        return Collections.emptyList();
    }

    private List<Estudiante> reconstruirCamino(Map<Long, Long> padres, Long fin) {
        List<Estudiante> camino = new LinkedList<>();
        Long actual = fin;

        while (actual != null) {
            camino.add(0, nodos.get(actual).getEstudiante());
            actual = padres.get(actual);
        }

        return camino;
    }

    // Algoritmo para detectar comunidades (simplificado)
    public List<List<Estudiante>> detectarComunidades() {
        List<List<Estudiante>> comunidades = new ArrayList<>();
        Set<Long> visitados = new HashSet<>();

        for (Long id : nodos.keySet()) {
            if (!visitados.contains(id)) {
                List<Estudiante> comunidad = new ArrayList<>();
                Queue<Long> cola = new LinkedList<>();

                cola.add(id);
                visitados.add(id);

                while (!cola.isEmpty()) {
                    Long actual = cola.poll();
                    comunidad.add(nodos.get(actual).getEstudiante());

                    for (NodoEstudiante vecino : nodos.get(actual).getVecinos()) {
                        Long idVecino = vecino.getEstudiante().getId();
                        if (!visitados.contains(idVecino)) {
                            visitados.add(idVecino);
                            cola.add(idVecino);
                        }
                    }
                }

                comunidades.add(comunidad);
            }
        }

        return comunidades;
    }

    public Set<Estudiante> obtenerVecinosEstudiante(Long idEstudiante) {
        NodoEstudiante nodo = nodos.get(idEstudiante);
        if (nodo != null) {
            return nodo.getVecinos().stream()
                    .map(NodoEstudiante::getEstudiante)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }




    // Método para visualizar el grafo
    public GrafoDTO visualizar() {
        List<NodoDTO> nodosDTO = nodos.values().stream()
                .map(n -> NodoDTO.builder()
                        .id(n.getEstudiante().getId())
                        .username(n.getEstudiante().getUsername())
                        .grado(n.getVecinos().size())
                        .build())
                .collect(Collectors.toList());

        List<AristaDTO> aristasDTO = aristas.stream()
                .map(a -> AristaDTO.builder()
                        .origen(a.getEstudiante1().getEstudiante().getId())
                        .destino(a.getEstudiante2().getEstudiante().getId())
                        .peso(a.getPesoAfinidad())
                        .build())
                .collect(Collectors.toList());

        return GrafoDTO.builder()
                .nodos(nodosDTO)
                .aristas(aristasDTO)
                .build();
    }

    public int calcularAfinidadConEstudiante(Long idEstudiante1, Long idEstudiante2) {
        if (!nodos.containsKey(idEstudiante1) || !nodos.containsKey(idEstudiante2)) {
            return 0;
        }

        // Buscar todas las aristas que conecten estos dos estudiantes
        return aristas.stream()
                .filter(a -> (a.getEstudiante1().getEstudiante().getId().equals(idEstudiante1) &&
                        a.getEstudiante2().getEstudiante().getId().equals(idEstudiante2)) ||
                        (a.getEstudiante1().getEstudiante().getId().equals(idEstudiante2) &&
                                a.getEstudiante2().getEstudiante().getId().equals(idEstudiante1)))
                .mapToInt(AristaAfinidad::getPesoAfinidad)
                .sum();
    }
    // Otros métodos como búsqueda de caminos, detección de comunidades, etc.
}