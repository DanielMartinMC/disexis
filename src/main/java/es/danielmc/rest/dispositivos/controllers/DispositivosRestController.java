package es.danielmc.rest.dispositivos.controllers;

import es.danielmc.rest.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.rest.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.rest.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.rest.dispositivos.services.DispositivosService;
import es.danielmc.utils.pagination.PageResponse;
import es.danielmc.utils.pagination.PaginationLinksUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Dispositivos", description = "Endpoint de Tarjetas de nuestra API")
public class DispositivosRestController {
    // Servicio de tarjetas
    private final DispositivosService dispositivosService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Operation(summary = "Obtiene todas los dispositivos", description = "Obtiene una lista de dispositivos")
    @Parameters({
            @Parameter(name = "marca", description = "Marca del dispositivo", example = ""),
            @Parameter(name = "titular", description = "Titular del dispositivo", example = ""),
            @Parameter(name = "isDeleted", description = "Si está borrada o no", example = "false"),
            @Parameter(name = "page", description = "Número de página", example = "0"),
            @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
            @Parameter(name = "sortBy", description = "Campo de ordenación", example = "id"),
            @Parameter(name = "direction", description = "Dirección de ordenación", example = "asc")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de dispositivos"),
    })
    // Podemos activar CORS en SecurityConfig de manera centralizada
    // o por método de esta manera
    //@CrossOrigin(origins = "http://mifrontend.es")
    @GetMapping()
    public ResponseEntity<PageResponse<DispositivoResponseDto>> getAll(
            @RequestParam(required = false) Optional<String> marca,
            @RequestParam(required = false) Optional<String> titular,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request) {
        log.info("Buscando dispositivos por marca={}, titular={}, isDeleted={}", marca, titular,  isDeleted);
        // Creamos el objeto de ordenación
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        // Creamos cómo va a ser la paginación
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<DispositivoResponseDto> pageResult = dispositivosService.findAll(marca, titular, isDeleted, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
    }

    @Operation(summary = "Obtiene un dispositivo por su id", description = "Obtiene un dispositivo por su id")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del dispositivo", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dispositivo"),
            @ApiResponse(responseCode = "404", description = "Dispositivo no encontrada"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<DispositivoResponseDto> getById(@PathVariable Long id) {
        log.info("Buscando tarjeta por id={}", id);
        return ResponseEntity.ok(dispositivosService.findById(id));
    }

    @Operation(summary = "Crea una tarjeta", description = "Crea una tarjeta")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Tarjeta a crear", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarjeta creada"),
            @ApiResponse(responseCode = "400", description = "Tarjeta no válida"),
    })
    @PostMapping()
    public ResponseEntity<DispositivoResponseDto> create(@Valid @RequestBody DispositivoCreateDto dispositivoCreateDto) {
        log.info("Creando Dispositivo : {}", dispositivoCreateDto);
        var saved = dispositivosService.save(dispositivoCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Actualiza un dispositivo", description = "Actualiza un dispositivo")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del dispositivo", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dispositivo a actualizar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dispositivo actualizado"),
            @ApiResponse(responseCode = "400", description = "Dispositivo no válido"),
            @ApiResponse(responseCode = "404", description = "Dispositivo no encontrado"),
    })
    @PutMapping("/{id}")
    public ResponseEntity<DispositivoResponseDto> update(@PathVariable Long id, @Valid @RequestBody DispositivoUpdateDto dispositivoUpdateDto) {
        log.info("Actualizando dispositivo id={} con dispositivo={}", id, dispositivoUpdateDto);
        return ResponseEntity.ok(dispositivosService.update(id, dispositivoUpdateDto));
    }


    @Operation(summary = "Actualiza parcialmente un dispositivo", description = "Actualiza parcialmente un dispositivo")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del dispositivo", example = "1", required = true)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dispositivo a actualizar", required = true)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dispositivo actualizado"),
            @ApiResponse(responseCode = "400", description = "Dispositivo no válido"),
            @ApiResponse(responseCode = "404", description = "Dispositivo no encontrado"),
    })
    @PatchMapping("/{id}")
    public ResponseEntity<DispositivoResponseDto> updatePartial(@PathVariable Long id, @Valid @RequestBody DispositivoUpdateDto dispositivoUpdateDto) {
        log.info("Actualizando parcialmente dispositivo con id={} con dispositivo={}",id, dispositivoUpdateDto);
        return ResponseEntity.ok(dispositivosService.update(id, dispositivoUpdateDto));
    }

    @Operation(summary = "Borra una dispositivo", description = "Borra una dispositivo")
    @Parameters({
            @Parameter(name = "id", description = "Identificador de la dispositivo", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Dispositivo borrado"),
            @ApiResponse(responseCode = "404", description = "Dispositivo no encontrado"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Borrando producto por id: {}", id);
        dispositivosService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    /**
     * Manejador de excepciones de Validación: 400 Bad Request
     *
     * @param ex excepción
     * @return Mapa de errores de validación con el campo y el mensaje
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

        BindingResult result = ex.getBindingResult();
        problemDetail.setDetail("Falló la validación para el objeto='" + result.getObjectName()
                + "'. " + "Núm. errores: " + result.getErrorCount());

        Map<String, String> errores = new HashMap<>();
        result.getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errores.put(fieldName, errorMessage);
        });

        problemDetail.setProperty("errores", errores);
        return problemDetail;
    }
}