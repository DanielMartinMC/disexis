package es.danielmc.graphql.controllers;

import es.danielmc.rest.dispositivos.models.Dispositivo;
import es.danielmc.rest.dispositivos.repositories.DispositivosRepository;
import es.danielmc.rest.titulares.models.Titular;
import es.danielmc.rest.titulares.repositories.TitularesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
// @PreAuthorize("hasAnyRole('USER')") // Protección a nivel de clase
public class DispositivoTitularGraphQLController {
    private final DispositivosRepository dispositivosRepository;
    private final TitularesRepository titularesRepository;


    // --- QUERIES ---

    @QueryMapping
    public List<Dispositivo> dispositivos() {
        // Devuelve todas las tarjetas como entidades (ojo: no paginado)
        return dispositivosRepository.findAll();
    }

    @QueryMapping
    public Dispositivo dispositivoById(@Argument Long id) {
        // Devuelve una tarjeta por su id
        Optional<Dispositivo> dispositivoOpt = dispositivosRepository.findById(id);
        return dispositivoOpt.orElse(null);
    }

    @QueryMapping
    public List<Titular> titulares() {
        // Devuelve todos los titulares como entidades
        return titularesRepository.findAll();
    }

    @QueryMapping
    public Titular titularById(@Argument Long id) {
        // Devuelve un titular por id
        return titularesRepository.findById(id).orElse(null);
    }

    // titularesByNombre(nombre: String!): [Titular!]!
    @QueryMapping
    public List<Titular> titularesByNombre(@Argument String nombre) {
        // Devuelve los titulares que coinciden con el nombre (case insensitive)
        return titularesRepository.findByNombreContainingIgnoreCase(nombre);
        // En caso de que no encuentre ninguna, devuelve una lista vacía
    }

    // --- RESOLVERS RELACIONES ---

    @SchemaMapping(typeName = "Dispositivo", field = "titular")
    public Titular titular(Dispositivo dispositivo) {
        // Devuelve el titular de la tarjeta (ya viene cargada en la entidad)
        return dispositivo.getTitular();
    }

    @SchemaMapping(typeName = "Dispositivo", field = "tarjetas")
    public List<Dispositivo> dispositivos(Titular titular) {
        // Devuelve las tarjetas de un titular
        return dispositivosRepository.findByTitular(titular);
    }
}

