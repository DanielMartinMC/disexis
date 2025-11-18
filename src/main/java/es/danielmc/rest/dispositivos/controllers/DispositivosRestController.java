package es.danielmc.rest.dispositivos.controllers;

import es.danielmc.rest.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.rest.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.rest.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.rest.dispositivos.services.DispositivosService;
import es.danielmc.utils.pagination.PageResponse;
import es.danielmc.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RequestMapping("api/${api.version}/dispositivos")
@RequiredArgsConstructor
@RestController
public class DispositivosRestController {

    // Repositorio de dispositivos
    private final DispositivosService dispositivosService;
    private final PaginationLinksUtils paginationLinksUtils;

    @GetMapping()
    public ResponseEntity<PageResponse<DispositivoResponseDto>> getAlldispositivos(
            @RequestParam(required = false) Optional<String> marca,
            @RequestParam(required = false) Optional<String> titular,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request){
        log.info("Buscando dispositivos por numero={}, titular={}, ", marca, titular, isDeleted);

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<DispositivoResponseDto> pageResult = dispositivosService.findAll(marca, titular, isDeleted, pageable);

        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }


    @GetMapping("/{id}")
    public ResponseEntity<DispositivoResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando dispositivos por id: " + id);
        return ResponseEntity.ok(dispositivosService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<DispositivoResponseDto> create(@Valid @RequestBody DispositivoCreateDto dispositivoCreateDto) {
        log.info("Creando Dispositivo : {}", dispositivoCreateDto);
        // El servicio aún no implementa save; devolvemos CREATED con el mismo objeto para compilar
        var saved = dispositivosService.save(dispositivoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DispositivoResponseDto> update( @PathVariable Long id,@Valid @RequestBody DispositivoUpdateDto dispositivoUpdateDto) {
        log.info("Actualizando Dispositivo id={} con Dispositivo={}", id, dispositivoUpdateDto);
        return ResponseEntity.ok(dispositivosService.update(id, dispositivoUpdateDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DispositivoResponseDto> updatePartial(@PathVariable Long id,@Valid @RequestBody DispositivoUpdateDto dispositivoUpdateDto) {
        log.info("Actualizando parcialmente Dispositivo con id={} con Dispositivo={}",id, dispositivoUpdateDto);
        return ResponseEntity.ok(dispositivosService.update(id, dispositivoUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Valid @PathVariable Long id) {
        log.info("Borrando Dispositivo por id: " + id);
        dispositivosService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Fallo la validacion para el objeto='" + result.getObjectName() + "'. " + " Num. errores: "
        + result.getErrorCount());

        Map<String, String> errors = new HashMap<>();
        result.getAllErrors().forEach(error -> {
        String fieldName = ((FieldError) error).getField();
        String errorMessage = error.getDefaultMessage();
        errors.put(fieldName, errorMessage);
        });
        problemDetail.setProperty("errors", errors);
        return problemDetail;
    }
}