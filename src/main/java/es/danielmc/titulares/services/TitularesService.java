package es.danielmc.titulares.services;

import es.danielmc.titulares.dto.TitularRequestDto;
import es.danielmc.titulares.models.Titular;

import java.util.List;

public interface TitularesService {
    List<Titular> findAll(String nombre);

    Titular findByNombre(String nombre);

    Titular findById(Long id);

    Titular save(TitularRequestDto titularRequestDto);

    Titular update(Long id, TitularRequestDto titularRequestDto);

    void deleteById(Long id);
}