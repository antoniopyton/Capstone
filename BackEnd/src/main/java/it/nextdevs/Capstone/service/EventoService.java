package it.nextdevs.Capstone.service;

import com.cloudinary.Cloudinary;
import it.nextdevs.Capstone.DTO.EventoDto;
import it.nextdevs.Capstone.enums.StatoBiglietti;
import it.nextdevs.Capstone.enums.TipoUtente;
import it.nextdevs.Capstone.exception.BadRequestException;
import it.nextdevs.Capstone.exception.NotFoundException;
import it.nextdevs.Capstone.model.Evento;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.repository.EventoRepository;
import it.nextdevs.Capstone.repository.UtenteRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
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
import java.util.stream.Collectors;

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
        evento.setDataInserimento(LocalDate.now());
        evento.setTipoEvento(eventoDto.getTipoEvento());
        evento.setStatoBiglietti(StatoBiglietti.DISPONIBILI);
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

    public List<Evento> getEventiPrenotati(int utenteId) {
        return eventoRepository.findEventiPrenotatiByUtenteId(utenteId);
    }

    public List<Evento> getUpcomingEvents() {
        LocalDate now = LocalDate.now();
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
        LocalDate startOfNextMonth = now.plusMonths(1).withDayOfMonth(1);
        LocalDate endOfFirstWeekOfNextMonth = startOfNextMonth.plusDays(6);

        return eventoRepository.findByDataBetween(now, endOfFirstWeekOfNextMonth);
    }

    public List<Evento> getEventiSimili(int id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Evento non trovato"));

        LocalDate startDate = evento.getData().minusDays(10);
        LocalDate endDate = evento.getData().plusDays(10);

        List<Evento> eventiSimili = eventoRepository.findByLuogoAndDataBetween(evento.getLuogo(), startDate, endDate);

        return eventiSimili.stream()
                .filter(e -> e.getId() != evento.getId())
                .collect(Collectors.toList());
    }

    public List<Utente> getArtistiCandidati(int eventoId) {
        Optional<Evento> eventoOptional = eventoRepository.findById(eventoId);
        if (eventoOptional.isPresent()) {
            return eventoOptional.get().getArtistiCandidati();
        } else {
            throw new NotFoundException("Evento non trovato");
        }
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

    public List<Evento> getEventiByArtista(int artistaId) {
        return eventoRepository.findEventiByArtistaId(artistaId);
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
        Optional<Evento> eventoOptional = getEventoById(id);

        if (eventoOptional.isPresent()) {
            String url = (String) cloudinary.uploader().upload(avatar.getBytes(), Collections.emptyMap()).get("url");
            Evento evento = eventoOptional.get();
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

    public String nuovaPrenotazione(int eventoId, int utenteId, int quantita) {
        Evento evento = eventoRepository.findById(eventoId).orElseThrow(() -> new NotFoundException("Evento non trovato"));
        Utente utente = utenteRepository.findById(utenteId).orElseThrow(() -> new NotFoundException("Utente non trovato"));

        if (evento.getPostiDisponibili() >= quantita) {
            List<Evento> eventiPrenotati = utente.getEventiPrenotati();
            for (int i = 0; i < quantita; i++) {
                eventiPrenotati.add(evento);
            }
            utente.setEventiPrenotati(eventiPrenotati);
            evento.setPostiDisponibili(evento.getPostiDisponibili() - quantita);
            evento.aggiornaStato();
            eventoRepository.save(evento);
            sendMailEvento(utente.getEmail(), evento.getNome(), evento.getData(), evento.getLuogo(), quantita);
            return "Prenotazione effettuata con successo!";
        } else {
            throw new IllegalStateException("Non ci sono abbastanza posti disponibili.");
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

    public String nuovaCandidatura(int eventoId, int artistaId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElse(null);
        Utente artista = utenteRepository.findById(artistaId)
                .orElse(null);

        if (evento == null || artista == null) {
            return "Evento o artista non trovato.";
        }

        if (evento.getArtistiCandidati().contains(artista)) {
            return "Hai già fatto domanda per questo evento.";
        }

        evento.getArtistiCandidati().add(artista);
        eventoRepository.save(evento);

        sendMailCandidatura(artista.getEmail(), artista.getNomeArtista(), evento.getNome());

        return "Candidatura effettuata con successo!";
    }

    private void sendMailEvento(String email, String nomeEvento, LocalDate dataEvento, String luogoEvento, int quantita) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Conferma Prenotazione Evento");

            String htmlMsg = String.format("""
        <html>
            <body>
                <p>Ciao,</p>
                <p>Grazie per aver prenotato uno dei nostri eventi unici! Siamo entusiasti di confermare la tua prenotazione.</p>
                <p>Dettagli dell'evento:</p>
                <ul>
                    <li>Nome evento: <strong>%s</strong></li>
                    <li>Data: <strong>%s</strong></li>
                    <li>Luogo: <strong>%s</strong></li>
                    <li>Quantità biglietti: <strong>%d</strong></li>
                </ul>
                <p>Non vediamo l'ora di averti con noi! Se hai bisogno di annullare la tua prenotazione, puoi farlo in qualsiasi momento tramite il nostro sito.</p>
                <p>Se hai domande o necessiti di assistenza, il nostro team di supporto è a tua disposizione. Puoi contattarci via email a <a href="mailto:muzikfest@supporto.com">muzikfest@supporto.com</a> o visitare il nostro <a href="http://example.com/centro-assistenza">centro assistenza</a>.</p>
                <p>A presto!</p>
                <p>Il team di MuzikFest</p>
            </body>
        </html>
        """, nomeEvento, dataEvento, luogoEvento, quantita);
            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BadRequestException("Non è stato possibile inviare la mail");
        }
    }

    private void sendMailCandidatura(String email, String nomeArtista, String nomeEvento) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Candidatura per Evento su MuzikFest");

            String htmlMsg = String.format("""
            <html>
                <body>
                    <p>Ciao %s,</p>
                    <p>Grazie per aver inviato la tua candidatura per l'evento <strong>%s</strong>! Siamo felici che tu voglia esibirti con noi.</p>
                    <p>Il nostro team valuterà la tua candidatura e ti faremo sapere l'esito il prima possibile.</p>
                    <p>Nel frattempo, puoi continuare ad esplorare la nostra piattaforma e scoprire altre opportunità per esibirti e connetterti con la community di MuzikFest.</p>
                    <p>Se hai domande o necessiti di assistenza, il nostro team di supporto è a tua disposizione. Puoi contattarci via email a <a href="mailto:muzikfest@supporto.com">muzikfest@supporto.com</a> o visitare il nostro <a href="http://example.com/centro-assistenza">centro assistenza</a>.</p>
                    <p>Grazie ancora per aver scelto MuzikFest. Non vediamo l'ora di vedere la tua performance!</p>
                    <p>Un caloroso saluto,</p>
                    <p>Il team di MuzikFest</p>
                </body>
            </html>
            """, nomeArtista, nomeEvento);
            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BadRequestException("Non è stato possibile inviare la mail");
        }
    }

    private void sendMailDisdetta(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Prenotazione Cancellata con successo");
        message.setText("Ci dispiace sapere che hai annullato la prenotazione!" +
                "Saremo lieti di ospitarti in tutti i nostri altri eventi. Ti aspettiamo.");

        javaMailSender.send(message);
    }

}
