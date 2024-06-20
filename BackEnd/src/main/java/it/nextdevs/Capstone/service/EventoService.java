package it.nextdevs.Capstone.service;

import com.cloudinary.Cloudinary;
import it.nextdevs.Capstone.DTO.EventoDto;
import it.nextdevs.Capstone.exception.NotFoundException;
import it.nextdevs.Capstone.model.Evento;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.repository.EventoRepository;
import it.nextdevs.Capstone.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class EventoService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Autowired
    private Cloudinary cloudinary;

    public Integer salvaEvento(EventoDto eventoDto) {

        if (eventoDto.getData().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La data dell'evento non può essere nel passato.");
        }

        Evento evento = new Evento();
        evento.setNome(eventoDto.getNome());
        evento.setLuogo(eventoDto.getLuogo());
        evento.setData(eventoDto.getData());
        evento.setTipoEvento(eventoDto.getTipoEvento());
        evento.setPostiDisponibili(eventoDto.getPostiDisponibili());
        evento.setDescrizione(eventoDto.getDescrizione());
        evento.setCapienzaMax(eventoDto.getCapienzaMax());
        evento.setImmagine(eventoDto.getImmagine());

        eventoRepository.save(evento);

        return evento.getId();
    }

    public Optional<Evento> getEventoById(int id) {
        return eventoRepository.findById(id);
    }

    public List<Evento> getEventi() {
        return eventoRepository.findAll();
    }

    public Evento updateEvento(int id, EventoDto eventoDto) {
        Optional<Evento> eventoOptional = eventoRepository.findById(id);

        if (eventoOptional.isPresent()) {
            Evento evento = eventoOptional.get();
            evento.setNome(eventoDto.getNome());
            evento.setLuogo(eventoDto.getLuogo());
            evento.setData(eventoDto.getData());
            evento.setDescrizione(eventoDto.getDescrizione());
            evento.setCapienzaMax(eventoDto.getCapienzaMax());
            evento.setImmagine(eventoDto.getImmagine());

            eventoRepository.save(evento);
            return eventoRepository.save(evento);
        } else {
            throw new NotFoundException("Cliente con id " + id + " non esiste");
        }
    }

    public String deleteEvento(int id) {
        Optional<Evento> eventoOptional = eventoRepository.findById(id);

        if (eventoOptional.isPresent()) {
            Evento evento = eventoOptional.get();
            eventoRepository.delete(evento);
            return "Evento con id: " + id + " correttamente eliminato";
        } else {
            throw new NotFoundException("Evento " + id + " non esiste");
        }
    }

    public List<Evento> getEventoByDataInserimento(LocalDate dataInserimento) {
        return eventoRepository.findAllByDataInserimento(dataInserimento);
    }

    public List<Evento> getEventiOrdinatiPerNome(String order) {
        if (order.equals("ASC")) {
            return eventoRepository.findAllByOrderByNomeAsc();
        } else if (order.equals("DESC")) {
            return eventoRepository.findAllByOrderByNomeDesc();
        } else {
            throw new IllegalArgumentException("Ordine non valido. Deve essere 'ASC' o 'DESC'");
        }
    }

    public Evento patchImmagineEvento(Integer id, MultipartFile avatar) throws IOException {
        Optional<Evento> clienteOptional = getEventoById(id);

        if (clienteOptional.isPresent()) {
            String url = (String) cloudinary.uploader().upload(avatar.getBytes(), Collections.emptyMap()).get("url");
            Evento evento = clienteOptional.get();
            evento.setImmagine(url);
            eventoRepository.save(evento);
            return evento;
        } else {
            throw new NotFoundException("Evento con id " + id + " non trovato");
        }
    }

    public Evento getEventoByNome(String nome) {
        Optional<Evento> eventoOptional = eventoRepository.findByNome(nome);

        if (eventoOptional.isPresent()) {
            return eventoOptional.get();
        } else {
            throw new NotFoundException("Cliente con nome " + nome + " non esiste");
        }
    }

    public String nuovaPrenotazione(int eventoId, int utenteId) {
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(() -> new NotFoundException("Evento non trovato"));
        Utente utente = utenteRepository.findById(utenteId).orElseThrow(() -> new NotFoundException("Utente non trovato"));

        if (evento.getPostiDisponibili() > 0) {
            List<Evento> eventiPrenotati = utente.getEventiPrenotati();
            eventiPrenotati.add(evento);
            utente.setEventiPrenotati(eventiPrenotati);
            evento.setPostiDisponibili(evento.getPostiDisponibili() - 1);
            eventoRepository.save(evento);
            sendMailEvento(utente.getEmail());
            return "Prenotazione effettuata con successo!";
        } else {
            throw new IllegalStateException("Non ci sono posti disponibili.");
        }
    }

    public String eliminaPrenotazione(int eventoId, int utenteId) {
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(() -> new NotFoundException("Evento non trovato"));
        Utente utente = utenteRepository.findById(utenteId).orElseThrow(() -> new NotFoundException("Utente non trovato"));

        if (evento.getUtentiPrenotati().contains(utente)) {
            evento.getUtentiPrenotati().remove(utente);
            evento.setPostiDisponibili(evento.getPostiDisponibili() + 1);
            eventoRepository.save(evento);
            sendMailDisdetta(utente.getEmail());
            return "Prenotazione rimossa con successo!";
        } else {
            throw new IllegalStateException("L'utente non ha alcuna prenotazione.");
        }
    }

    private void sendMailEvento(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Prenotazione Evento");
        message.setText("Grazie per esserti prenotato ad uno dei nostri eventi Unici. Possiamo confermarti che la prenotazione all'evento è avvenuta con successo" +
                "Potrai disdirla in qualsiasi momento!");

        javaMailSender.send(message);
    }

    private void sendMailDisdetta(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Prenotazione Cancellata con successo");
        message.setText("Ci dispiace sapere che hai annullato la prenotazione!" +
                "Saremo lieti di ospitarti in tutti i nostri altri eventi. Ti aspettiamo.");

        javaMailSender.send(message);
    }

//    public List<Evento> getAllEventi(Map<String, String> allParams) {
//        if (allParams.isEmpty()) return clienteRepository.findAll();
//        List<Cliente> clienti = clienteRepository.findAll();
//        for (Map.Entry<String, String> entry : allParams.entrySet()) {
//            if (entry.getKey().equals("fatturatoMin")) {
//                clienti = clienti.stream().filter(cliente -> cliente.getFatturatoAnnuale() >= Double.parseDouble(entry.getValue())).toList();
//            }
//            if (entry.getKey().equals("fatturatoMax")) {
//                clienti = clienti.stream().filter(cliente -> cliente.getFatturatoAnnuale() <= Double.parseDouble(entry.getValue())).toList();
//            }
//            if (entry.getKey().equals("inserimentoMin")) {
//                clienti = clienti.stream().filter(cliente -> cliente.getDataInserimento().isAfter(LocalDate.parse(entry.getValue()))).toList();
//            }
//            if (entry.getKey().equals("inserimentoMax")) {
//                clienti = clienti.stream().filter(cliente -> cliente.getDataInserimento().isBefore(LocalDate.parse(entry.getValue()))).toList();
//            }
//            if (entry.getKey().equals("ultimoContattoMin")) {
//                clienti = clienti.stream().filter(cliente -> cliente.getDataUltimoContatto().isAfter(LocalDate.parse(entry.getValue()))).toList();
//            }
//            if (entry.getKey().equals("ultimoContattoMax")) {
//                clienti = clienti.stream().filter(cliente -> cliente.getDataUltimoContatto().isBefore(LocalDate.parse(entry.getValue()))).toList();
//            }
//            if (entry.getKey().equals("ragioneSociale")) {
//                clienti = clienti.stream().filter(cliente -> cliente.getRagioneSociale().contains(entry.getValue())).toList();
//            }
//            if (entry.getKey().equals("nomeContatto")) {
//                clienti = clienti.stream().filter(cliente -> cliente.getRagioneSociale().contains(entry.getValue())).toList();
//            }
//        }
//        return clienti;
//    }
//
//    private void sendMailRegistrazione(String email) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(email);
//        message.setSubject("Registrazione Cliente");
//        message.setText("Registrazione Cliente avvenuta con successo");
//
//        javaMailSender.send(message);
//    }


}
