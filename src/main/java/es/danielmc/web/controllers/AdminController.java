package es.danielmc.web.controllers;

import es.danielmc.rest.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.rest.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.rest.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.rest.dispositivos.models.Dispositivo;
import es.danielmc.rest.dispositivos.services.DispositivosService;
import es.danielmc.web.services.I18nService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final DispositivosService dispositivosService;
    private final I18nService i18nService;


    @GetMapping("/dispositivos")
    public String dispositivos(Model model,
                           @RequestParam(name = "page", defaultValue = "0") int page,
                           @RequestParam(name = "size", defaultValue = "4") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<DispositivoResponseDto> dispositivosPage = dispositivosService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", dispositivosPage);
        return "admin/dispositivos/lista";
    }

    @GetMapping("/dispositivos/filter")
    public String dispositivosFiltrar(Model model,
                                  @RequestParam(required = false) Optional<String> numero,
                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "size", defaultValue = "4") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<DispositivoResponseDto> dispositivosPage = dispositivosService.findAll(
                numero, Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", dispositivosPage);
        return "fragments/listaDispositivos";
    }

    @GetMapping("/dispositivos/{id}")
    public String getById(@PathVariable Long id, Model model) {
        Dispositivo dispositivo = dispositivosService.buscarPorId(id).orElse(null);
        model.addAttribute("dispositivo", dispositivo);
        return "admin/dispositivos/detalle";
    }

    // Creamos una nueva tarjeta
    @GetMapping("/dispositivos/new")
    public String nuevaDispositivoForm(Model model) {
        // Lo añadimos al model
        model.addAttribute("dispositivo", DispositivoCreateDto.builder().build());
        model.addAttribute("modoEditar", false );
        // Ensure the file exists at src/main/resources/templates/admin/dispositivos/form.html
        return "admin/dispositivos/form";
    }


    @PostMapping("/dispositivos/new")
    public String nuevaDispositivoSubmit(@Valid @ModelAttribute("dispositivo") DispositivoCreateDto dispositivo,
                                     BindingResult bindingResult) {

        log.info("Datos recibidos del formulario: {}", dispositivo);
        // Si no tiene errores...
        if (bindingResult.hasErrors()) {
            log.info("hay errores en la validación");
            return "admin/dispositivos/form";
        } else {
            //insertamos
            dispositivosService.save(dispositivo);
            return "redirect:/admin/dispositivos";
        }
    }

    // ... existing code ...

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
            return "admin/dispositivos/form";
        }

        dispositivosService.update(id, dispositivo);
        redirectAttributes.addFlashAttribute("message",
                "Dispositivo actualizado correctamente.");
        return "redirect:/admin/dispositivos/" + id;
    }
    public String borrarDispositivo(@PathVariable Long id,
                                @RequestParam("deleteToken") String deleteToken,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        String sessionKey = "deleteToken_" + id;
        String tokenInSession = (String) session.getAttribute(sessionKey);

        if (tokenInSession == null || !tokenInSession.equals(deleteToken)) {
            redirectAttributes.addFlashAttribute("error", "Confirmación inválida o caducada.");
            return "redirect:/admin/dispositivos";
        }

        // invalidar token y proceder al borrado
        session.removeAttribute(sessionKey);
        dispositivosService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Dispositivo borrado correctamente.");
        return "redirect:/admin/dispositivos";
    }

    @GetMapping("/dispositivos/{id}/delete/confirm")
    public String showModalBorrar(@PathVariable("id") Long id, Model model, HttpSession session) {
        Optional<Dispositivo> dispositivo = dispositivosService.buscarPorId(id);
        String deleteMessage;
        if (dispositivo.isPresent()) {
            deleteMessage = i18nService.getMessage("dispositivos.borrar.mensaje",
                    new Object[]{dispositivo.get().getMarca()} );
        } else {
            return "redirect:/dispositivos/?error=true";
        }

        // generar token de un solo uso y guardarlo en sesión
        String token = UUID.randomUUID().toString();
        String sessionKey = "deleteToken_" + id;
        session.setAttribute(sessionKey, token);

        model.addAttribute("deleteUrl", "/admin/dispositivos/" + id + "/delete");
        model.addAttribute("deleteToken", token);
        model.addAttribute("deleteTitle",
                i18nService.getMessage("dispositivos.borrar.titulo")
        );
        model.addAttribute("deleteMessage", deleteMessage);
        return "fragments/deleteModal";
    }

}