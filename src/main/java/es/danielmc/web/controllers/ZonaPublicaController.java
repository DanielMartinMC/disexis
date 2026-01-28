package es.danielmc.web.controllers;


import es.danielmc.rest.dispositivos.dto.DispositivoResponseDto;
import es.danielmc.rest.dispositivos.services.DispositivosService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@Controller
// Fijamos ya de por si una ruta por defecto para escuchar en este controlador
@RequestMapping("/public")
public class ZonaPublicaController {
    private final DispositivosService dispositivosService;


    @GetMapping({"", "/", "/index"})
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "4") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<DispositivoResponseDto>  dispositivosPage = dispositivosService.findAll(
                Optional.empty(), Optional.empty(), Optional.empty(), pageable);

        model.addAttribute("page", dispositivosPage);
        return "index";
    }

}