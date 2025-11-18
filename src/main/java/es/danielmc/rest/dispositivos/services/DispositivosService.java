package es.danielmc.rest.dispositivos.services;


import es.danielmc.rest.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.rest.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.rest.dispositivos.dto.DispositivoUpdateDto;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DispositivosService {
    Page<DispositivoResponseDto> findAll(Optional<String> numero, Optional<String> titular, Optional<Boolean> isDeleted, Pageable pageable);

    DispositivoResponseDto findById(Long id);

    DispositivoResponseDto findByUuid(String uuid);

    Page<DispositivoResponseDto> findByUsuarioId(Long usuarioId, Pageable pageable);
    DispositivoResponseDto findByUsuarioId(Long usuarioId, Long idDispositivo);

    DispositivoResponseDto save(DispositivoCreateDto dispositivoCreateDto);
    DispositivoResponseDto save(DispositivoCreateDto dispositivoCreateDto, Long usuarioId);

    DispositivoResponseDto update(Long id, DispositivoUpdateDto dispositivoUpdateDto);
    DispositivoResponseDto update(Long id, DispositivoUpdateDto dispositivoUpdateDto,  Long usuarioId);

    void deleteById(Long id);
    void deleteById(Long id, Long usuarioId);
}