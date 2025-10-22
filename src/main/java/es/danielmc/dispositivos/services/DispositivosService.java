package es.danielmc.dispositivos.services;


import es.danielmc.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.models.Dispositivo;
import org.springframework.cache.annotation.CachePut;

import java.util.List;
import java.util.Optional;

public interface DispositivosService {
    List<DispositivoResponseDto> findAll(String Marca, String Modelo);

    DispositivoResponseDto findById(Long id);

    DispositivoResponseDto findbyUuid(String uuid);

    @CachePut
    DispositivoResponseDto save(DispositivoCreateDto dispositivoCreateDto);

    @CachePut
    DispositivoResponseDto update(Long id, DispositivoUpdateDto dispositivoUpdateDto);

    void deleteById(Long id);
}