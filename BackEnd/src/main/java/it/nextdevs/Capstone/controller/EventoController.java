package it.nextdevs.Capstone.controller;

import it.nextdevs.Capstone.DTO.EventoDto;
import it.nextdevs.Capstone.exception.BadRequestException;
import it.nextdevs.Capstone.model.Evento;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @PostMapping("/eventi")
    @PreAuthorize("hasAnyAuthority('EVENT_CREATOR', 'ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Integer salvaEvento(@RequestBody @Validated EventoDto eventoDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage()).reduce("", (s, s2) -> s + s2));
        }
        return eventoService.salvaEvento(eventoDto);
    }

    @GetMapping("/eventi")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<Evento> getAllEventi(@RequestParam Map<String, String> allParams) {
        return eventoService.getEventi();
    }

    @GetMapping("/utenti/{id}/prenotazioni")
    public List<Evento> getEventiPrenotati(@PathVariable int id) {
        return eventoService.getEventiPrenotati(id);
    }

    @GetMapping("/prossimiEventi")
    public List<Evento> getUpcomingEvents() {
        return eventoService.getUpcomingEvents();
    }

    @GetMapping("/eventi/{eventoId}/artisti-candidati")
    public List<Utente> getArtistiCandidati(@PathVariable int eventoId) {
        return eventoService.getArtistiCandidati(eventoId);
    }

    @GetMapping("/artista/{id}")
    public List<Evento> getEventiByArtista(@PathVariable int id) {
        return eventoService.getEventiByArtista(id);
    }

    @GetMapping("/eventi/{id}/simili")
    public List<Evento> getEventiSimili(@PathVariable int id) {
        return eventoService.getEventiSimili(id);
    }

    @PutMapping("/eventi/{id}")
    @PreAuthorize("hasAnyAuthority('EVENT_CREATOR','ADMIN')")
    public Evento updateEvento(@PathVariable int id, @RequestBody @Validated EventoDto eventoDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(error -> error.getDefaultMessage()).reduce("", (s, s2) -> s + s2));
        }

        return eventoService.updateEvento(id, eventoDto);
    }

    @DeleteMapping("/eventi/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public String deleteEvento(@PathVariable int id) {
        return eventoService.deleteEvento(id);
    }


    @GetMapping("/eventi/dataInserimento/{dataInserimento}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<Evento> getEventoByDataInserimento(@PathVariable LocalDate dataInserimento) {
        return eventoService.getEventoByDataInserimento(dataInserimento);
    }

    @GetMapping("/eventi/{id}")
    public Optional<Evento> getEventoById(@PathVariable int id) {
        return eventoService.getEventoById(id);
    }

    @PatchMapping(value = "/eventi/{id}/logo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Evento patchImmagineEvento(@PathVariable int id, @RequestParam("file") MultipartFile avatar) throws IOException {
        return eventoService.patchImmagineEvento(id, avatar);
    }

    @GetMapping("/eventi/nome/{nome}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public Evento getEventoByNome(@PathVariable String nome) {
        return eventoService.getEventoByNome(nome);
    }

    @PostMapping("/prenotazioni/{eventoId}/{utenteId}")
    public String nuovaPrenotazione(@PathVariable int eventoId, @PathVariable int utenteId, @RequestBody Map<String, Integer> request) {
        int quantita = request.get("quantita");
        return eventoService.nuovaPrenotazione(eventoId, utenteId, quantita);
    }

    @DeleteMapping("/prenotazioni/{eventoId}/{utenteId}")
    @PreAuthorize("hasAnyAuthority('EVENT_CREATOR','ADMIN', 'UTENTE_BASIC')")
    public String eliminaPrenotazione(@PathVariable int eventoId, @PathVariable int utenteId) {
        return eventoService.eliminaPrenotazione(eventoId, utenteId);
    }

    @PostMapping("/candidature/{eventoId}/{artistaId}")
    @PreAuthorize("hasAnyAuthority('ARTISTA', 'ADMIN')")
    public String nuovaCandidatura(@PathVariable int eventoId, @PathVariable int artistaId) {
        return eventoService.nuovaCandidatura(eventoId, artistaId);
    }



//        @GetMapping("/eventi/email/{email}")
//    @PreAuthorize("hasAnyAuthority('ADMIN','USER')")
//    public Evento getClienteByEmail(@PathVariable String email) {
//        return eventoService.getClienteByEmail(email);
//    }


}
