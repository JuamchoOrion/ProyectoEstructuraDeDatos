package com.redsocial.red_social.controller;
import com.redsocial.red_social.model.Estudiante;
import com.redsocial.red_social.model.RedSocial;
import com.redsocial.red_social.model.Intereses;
import com.redsocial.red_social.model.Moderador;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/grafo")
public class GrafoController {

    private final RedSocial redSocial;

    public GrafoController(RedSocial redSocial) {
        this.redSocial = redSocial;
    }

    @GetMapping("/gustos")
    public Map<String, Set<String>> obtenerGrafoGustos() {

        List<Estudiante> usuarios = new ArrayList<>();
        Estudiante e1 = new Estudiante("e1","qwe","sac");
        Estudiante e2 = new Estudiante("e2","qwe","c");
        Estudiante e3 = new Estudiante("e3","qwe","sc");
        e1.agregarInteres(Intereses.FISICA);
        e2.agregarInteres(Intereses.FISICA);
        e3.agregarInteres(Intereses.BIOLOGIA);
        e2.agregarInteres(Intereses.BIOLOGIA);
        usuarios.add(e1);
        usuarios.add(e2);
        usuarios.add(e3);

        redSocial.getListaUsuarios().addAll(usuarios);

        Moderador moderador = new Moderador("mod1", "123", "mod1@correo.com");
        moderador.setRed_social(redSocial);
        return moderador.construirGrafoPorGustos();
    }
}
