package es.danielmc.web.controllers;

import es.danielmc.rest.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.rest.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.rest.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.rest.dispositivos.services.DispositivosService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("dispositivos")
public class DispositivosController {
    private final DispositivosService dispositivosService;

    @GetMapping("/{id}")
    public String getById(@PathVariable Long id, Model model) {
        DispositivoResponseDto dispositivo = dispositivosService.findById(id);
        model.addAttribute("dispositivos", dispositivo);
        return "dispositivos/tipo";
    }

    @GetMapping({"", "/", "/lista"})
    public String lista(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<DispositivoResponseDto> dispositivosPage = dispositivosService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", dispositivosPage);
        return "dispositivos/lista";
    }

    // Creamos una nueva tarjeta
    @GetMapping("/new")
    public String nuevoDispositivoForm(Model model) {
        // Lo añadimos al model
        model.addAttribute("dispositivos", DispositivoCreateDto.builder().build());
        model.addAttribute("modoEditar", false );
        return "/dispositivos/form";
    }

    @PostMapping("/new")
    public String nuevaDispositivoSubmit(@Valid @ModelAttribute("dispositivos") DispositivoCreateDto dispositivo,
                                     BindingResult bindingResult) {

        log.info("Datos recibidos del formulario: {}", dispositivo);
        // Si no tiene errores...
        if (bindingResult.hasErrors()) {
            log.info("hay errores en la validación");
            return "/dispositivos/form";
        } else {
            //insertamos
            dispositivosService.save(dispositivo);
            return "redirect:/dispositivos/lista";
        }
    }

    @GetMapping("/{id}/edit")
    public String editarDispositivoForm(@PathVariable Long id, Model model) {
        DispositivoResponseDto dispositivoEncontrado = dispositivosService.findById(id);
        if (dispositivoEncontrado == null) {
            return "redirect:/dispositivos/new";
        } else {
            DispositivoUpdateDto dispositivo = DispositivoUpdateDto.builder()
                    .marca(dispositivoEncontrado.getMarca())
                    .modelo(dispositivoEncontrado.getModelo())
                    .fabricante(dispositivoEncontrado.getFabricante())
                    .tipo(dispositivoEncontrado.getTipo())
                    .numeroSerie(dispositivoEncontrado.getNumeroSerie())
                    .build();
            model.addAttribute("dispositivo", dispositivo);
            model.addAttribute("dispositivoId", id );
            model.addAttribute("modoEditar", true);
            return "dispositivos/form";
        }
    }

    @PostMapping("/{id}/edit")
    public String editarDispositivoSubmit(@PathVariable("id") Long id,
                                      @Valid @ModelAttribute("dispositivo") DispositivoUpdateDto dispositivo,
                                      BindingResult result,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error",
                    "Ha ocurrido un error al actualizar el dispositivo.");
            model.addAttribute("dispositivoId", id );
            model.addAttribute("modoEditar", true);
            return "/dispositivos/form";
        }

        dispositivosService.update(id, dispositivo);
        redirectAttributes.addFlashAttribute("message",
                "Dispositivo actualizado correctamente.");
        return "redirect:/dispositivos/{id}";
    }

    @GetMapping("/{id}/delete")
    public String borrarDispositivo(@PathVariable Long id) {

        // TO DO Borrar con confirmación mediante ventana modal

        dispositivosService.deleteById(id);
        return "redirect:/dispositivos/lista";
    }

}