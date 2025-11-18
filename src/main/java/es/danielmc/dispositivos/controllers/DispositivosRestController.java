package es.danielmc.dispositivos.controllers;

import es.danielmc.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.dispositivos.models.Dispositivo;
import es.danielmc.dispositivos.services.DispositivosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RequestMapping("api/${api.version}/dispositivos")
@RequiredArgsConstructor
@RestController
public class DispositivosRestController {

    // Repositorio de dispositivos
    private final DispositivosService dispositivosService;
    
    @GetMapping()
    public ResponseEntity<List<DispositivoResponseDto>> getAlldispositivos(@RequestParam(required = false) String marca,
                                                                           @RequestParam(required = false) String titular) {
        log.info("Buscando dispositivos por numero={}, titular={}", marca, titular);
        // El servicio aún no implementa findAll con parámetros; devolvemos una lista vacía para compilar
        return ResponseEntity.ok(dispositivosService.findAll(marca, titular));
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