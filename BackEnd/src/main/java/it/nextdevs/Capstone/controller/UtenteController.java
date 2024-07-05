package it.nextdevs.Capstone.controller;

import it.nextdevs.Capstone.DTO.UtenteDataDto;
import it.nextdevs.Capstone.DTO.UtenteDto;
import it.nextdevs.Capstone.exception.BadRequestException;
import it.nextdevs.Capstone.exception.NotFoundException;
import it.nextdevs.Capstone.model.Brano;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class UtenteController {
    @Autowired
    private UtenteService utenteService;


    @GetMapping("/utenti")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Page<Utente> getAllUtenti(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam (defaultValue = "15") int size,
                                   @RequestParam (defaultValue = "id") String sortBy) {
        return utenteService.getAllUtenti(page, size, sortBy);
    }

    @GetMapping("/artisti")
    public List<Utente> getAllArtisti() {
        return utenteService.getAllArtisti();
    }

    @GetMapping("/utenti/{id}")
    public Utente getUtenteById(@PathVariable int id) throws NotFoundException {
        Optional<Utente> userOptional = utenteService.getUserById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new NotFoundException("User con id: "+id+" non trovata");
        }
    }

    @GetMapping("/utenti/nome/{nome}")
    public Utente getUtenteByNome(@PathVariable String nome) {
        return utenteService.getUtenteByNome(nome);
    }

    @PatchMapping("/utenti/2/{id}")
    public Utente register2(@PathVariable int id, @RequestBody UtenteDto utenteDto) {
        return utenteService.updateDescrizione(id, utenteDto);
    }

    @PutMapping("/utenti/{id}")
    @PreAuthorize("hasAuthority('ADMIN)")
    public Utente updateUtente(@PathVariable int id, @RequestBody @Validated UtenteDto utenteDto, BindingResult bindingResult) throws NotFoundException {
        if(bindingResult.hasErrors()){
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage()).reduce("",((s1,s2) -> s1+s2)));
        }
        return utenteService.updateUtente(id, utenteDto);
    }

    @PatchMapping("/utenti/{id}")
    public UtenteDataDto patchUtente(@PathVariable int id, @RequestBody UtenteDto utenteDto) {
        return utenteService.patchUser(id, utenteDto);
    }

    @PatchMapping(value = "/utenti/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UtenteDataDto patchAvatarUtente(@PathVariable int id, @RequestParam("file") MultipartFile file) throws IOException {
        return utenteService.patchAvatarUtente(id, file);
    }

    @DeleteMapping("/utenti/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteUtente(@PathVariable int id) throws NotFoundException {
        return utenteService.deleteUtente(id);
    }

}
