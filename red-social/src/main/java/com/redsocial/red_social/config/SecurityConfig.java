package com.redsocial.red_social.config;

import com.redsocial.red_social.filter.JwtRequestFilter;
import com.redsocial.red_social.service.CustomUserDetailService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final CustomUserDetailService userDetailsService;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter, CustomUserDetailService userDetailsService) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos estáticos
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/login.html",
                                "/registro.html",
                                "/perfil.html",
                                "/perfil.js",
                                "/loginModerador.html",
                                "/registroModeradores.html",
                                "/moderador.html",
                                "/grupo-detalle.html",
                                "/grafo.html",
                                "/publicar.html",
                                "/publicarContenido.js",
                                "/solicitudAyuda.html",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/styles.css",
                                "/favicon.ico",
                                "/explorar.html",
                                "/explorar.js",
                                "/moderador.js",
                                "/reportes.html",
                                "/reportes.js",
                                "/crearEstudiante.html",
                                "/crearEstudiante.js",
                                "/solicitudAyuda.js",
                                "/grafoPrueba.html",
                                "/grafo.js",
                                "/grupos.html",
                                "/grupos.js",
                                "/prueba.html",
                                "chat.html",
                                "/pruebaDetalleGrupo.html",
                                "/pruebaPostGrupo.html",
                                "pruebaGrafoFun.html",
                                "pruebaAmigos.html"
                        ).permitAll()

                        // Endpoints públicos de API (POST)
                        .requestMatchers(HttpMethod.POST,
                                "/api/login",
                                "/api/registro",
                                "/api/moderadores/registro",
                                "/api/moderadores/auth/login",
                                "/api/contenido/explorar",
                                "/api/contenido/*/valorar",
                                "/api/solicitudes/urgentes",
                                "/api/solicitudes",
                                "/api/grupos-estudio/*/solicitudes",
                                "/api/grupos-estudio/*/contenidos",
                                "/api/grupos-estudio/generar",
                                "/api/usuario/agregar"
                        ).permitAll()
                        .requestMatchers("/chat-websocket/**").permitAll()  // handshake SockJS
                        .requestMatchers("/tema/**").permitAll()            // destino de mensajes STOMP
                        // Endpoints públicos de API (GET)
                        .requestMatchers(HttpMethod.GET,
                                "/api/verify",
                                "/grafo/**",
                                "/uploads/**",
                                "/api/contenido/explorar",
                                "/api/solicitudes/urgentes",
                                "/chat/*",
                                "/api/usuario/amigos",
                                "/api/solicitudes",
                                "/api/grafo/visualizar",
                                "/api/grafo/comunidades",
                                "/api/grafo/recomendaciones-multinivel",
                                "/api/grafo/recomendaciones",
                                "/api/usuario/listarFiltrados",
                                "/api/grupos-estudio/**",
                                "/api/contenido/mios",
                                "/api/usuario/listar",
                                "/api/grupos-estudio/estudiante/**"
                        ).permitAll()

                        // Endpoints públicos de API (DELETE)
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/usuario/*/eliminar",
                                "/api/usuario/eliminar"
                        ).permitAll()

                        // Endpoints para moderadores
                        .requestMatchers("/api/moderadores/**").hasRole("MODERADOR")

                        // Todos los demás requieren autenticación
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\":\"No autorizado\",\"message\":\"Autenticación requerida\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080", "http://127.0.0.1:8080"));
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With","Accept"));
        configuration.setExposedHeaders(List.of("Authorization","Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
