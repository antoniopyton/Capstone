package it.nextdevs.Capstone.controller;

import it.nextdevs.Capstone.DTO.BranoDto;
import it.nextdevs.Capstone.DTO.EventoDto;
import it.nextdevs.Capstone.DTO.UtenteDataDto;
import it.nextdevs.Capstone.exception.BadRequestException;
import it.nextdevs.Capstone.model.Brano;
import it.nextdevs.Capstone.model.Evento;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.service.BranoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class BranoController {

    @Autowired
    private BranoService branoService;

    @PostMapping("/brani")
    @PreAuthorize("hasAnyAuthority('ARTISTA', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Integer salvaBrano(@RequestBody @Validated BranoDto branoDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage()).reduce("", (s, s2) -> s + s2));
        }
        return branoService.saveBrano(branoDto);
    }

    @GetMapping("/brani")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<Brano> getBrani(@RequestParam(defaultValue = "0") int page,
                                     @RequestParam (defaultValue = "15") int size,
                                     @RequestParam (defaultValue = "id") String sortBy) {
        return branoService.getBrani(page, size, sortBy);
    }

    @DeleteMapping("/brani/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String deleteBrano(@PathVariable int id) {
        return branoService.deleteBrano(id);
    }

    @PatchMapping(value = "/brani/{id}/copertina", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Brano patchCopertinaBrano(@PathVariable int id, @RequestParam("file") MultipartFile avatar) throws IOException {
        return branoService.patchCopertinaBrano(id, avatar);
    }

    @GetMapping("/brani/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Brano getBranoById(@PathVariable int id) {
        return branoService.getBranoById(id);
    }

    @PatchMapping(value = "/brani/{id}/brano", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Brano patchBrano(@PathVariable int id, @RequestParam("file") MultipartFile brano) throws IOException {
        return branoService.patchBrano(id, brano);
    }


}
