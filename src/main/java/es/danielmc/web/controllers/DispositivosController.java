package es.danielmc.web.controllers;

import es.danielmc.rest.dispositivos.dto.DispositivoCreateDto;
import es.danielmc.rest.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.rest.dispositivos.dto.DispositivoUpdateDto;
import es.danielmc.rest.dispositivos.models.Dispositivo;
import es.danielmc.rest.dispositivos.services.DispositivosService;
import es.danielmc.rest.users.models.User;
import es.danielmc.rest.users.services.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/app")
public class DispositivosController {
    private final DispositivosService dispositivosService;
    private final UsersService usersService;

    // Enviamos mis tarjetas a la vista lista
    @GetMapping("/misdispositivos")
    public String misTarjetas(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> usuario = usersService.findByUsername(username);
        List<Dispositivo> dispositivos = List.of();
        if (usuario.isPresent()) {
            dispositivos = dispositivosService.buscarPorUsuarioId(usuario.get().getId());
        }
        model.addAttribute("dispositivos", dispositivos);
        return "app/dispositivos/lista";
    }

    @GetMapping("/misdispositivos/{id}")
    public String getById(@PathVariable Long id, Model model) {
        Dispositivo dispositivo = dispositivosService.buscarPorId(id).orElse(null);
        model.addAttribute("dispositivo", dispositivo);
        return "app/dispositivos/detalle";
    }


}