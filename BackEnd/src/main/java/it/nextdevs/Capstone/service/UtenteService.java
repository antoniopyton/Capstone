package it.nextdevs.Capstone.service;

import com.cloudinary.Cloudinary;
import it.nextdevs.Capstone.DTO.UtenteDataDto;
import it.nextdevs.Capstone.DTO.UtenteDto;
import it.nextdevs.Capstone.enums.TipoUtente;
import it.nextdevs.Capstone.exception.BadRequestException;
import it.nextdevs.Capstone.exception.NotFoundException;
import it.nextdevs.Capstone.model.Brano;
import it.nextdevs.Capstone.model.Evento;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.repository.UtenteRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    public Integer saveUtente(UtenteDto utenteDto) {
        if (getUtenteByEmail(utenteDto.getEmail()).isEmpty()) {
            Utente utente = new Utente();
            utente.setNome(utenteDto.getNome());
            utente.setCognome(utenteDto.getCognome());
            utente.setEmail(utenteDto.getEmail());
            utente.setTipoUtente(TipoUtente.UTENTE_BASIC);
            utente.setPassword(passwordEncoder.encode(utenteDto.getPassword()));

            utenteRepository.save(utente);
            sendMailUtente(utenteDto.getEmail(), utenteDto.getNome());

            return utente.getId();
        } else {
            throw new BadRequestException("L'utente con email " + utenteDto.getEmail() + " già esistente");
        }
    }

    public Integer saveArtista(UtenteDto utenteDto) {
        if (getUtenteByEmail(utenteDto.getEmail()).isEmpty()) {
            Utente artista = new Utente();
            artista.setNome(utenteDto.getNome());
            artista.setCognome(utenteDto.getCognome());
            artista.setEmail(utenteDto.getEmail());
            artista.setTipoUtente(TipoUtente.ARTISTA);
            artista.setTipoArtista(utenteDto.getTipoArtista());
            artista.setNomeArtista(utenteDto.getNomeArtista());
            artista.setSfondoArtista(utenteDto.getSfondoArtista());
            artista.setDescrizioneArtista(utenteDto.getDescrizioneArtista());
            artista.setPassword(passwordEncoder.encode(utenteDto.getPassword()));

            utenteRepository.save(artista);
            sendMailArtista(utenteDto.getEmail(), utenteDto.getNomeArtista());

            return artista.getId();
        } else {
            throw new BadRequestException("L'artista con email " + utenteDto.getEmail() + " già esistente");
        }
    }

    public Page<Utente> getAllUtenti(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return utenteRepository.findAll(pageable);
    }

    public List<Utente> getAllArtisti() {
        return utenteRepository.findAllByTipoUtente(TipoUtente.ARTISTA);
    }

    public Optional<Utente> getUserById(int id) {
        return utenteRepository.findById(id);
    }

    public Utente getUtenteByNome(String nome) {
        Optional<Utente> utenteOptional = utenteRepository.findByNome(nome);

        if (utenteOptional.isPresent()) {
            return utenteOptional.get();
        } else {
            throw new NotFoundException("Cliente con nome " + nome + " non esiste");
        }
    }

    public Utente updateDescrizione(int id, UtenteDto utenteDto) {
        Optional<Utente> utenteOptional = getUserById(id);
        if (utenteOptional.isPresent()) {
            Utente utente = utenteOptional.get();
            utente.setDescrizioneArtista(utenteDto.getDescrizioneArtista());
            return utenteRepository.save(utente);
        } else {
            throw new NotFoundException("User with id: " + id + " not found");
        }
    }

    public Utente updateUtente(int id, UtenteDto utenteDto) {
        Optional<Utente> utenteOptional = getUserById(id);
        if (utenteOptional.isPresent()) {
            Utente utente = utenteOptional.get();
            utente.setNome(utenteDto.getNome());
            utente.setCognome(utenteDto.getCognome());
            utente.setEmail(utenteDto.getEmail());
            utente.setPassword(passwordEncoder.encode(utenteDto.getPassword()));
            return utenteRepository.save(utente);
        } else {
            throw new NotFoundException("User with id:" + id + " not found");
        }
    }

    public String deleteUtente(int id) {
        Optional<Utente> utenteOptional = utenteRepository.findById(id);

        if (utenteOptional.isPresent()) {
            utenteRepository.delete(utenteOptional.get());
            return "Utente con id: " + id + " correttamente eliminato.";
        } else {
            throw new NotFoundException("Utente con id: " + id + " non trovato");
        }
    }

    public Optional<Utente> getUtenteByEmail(String email) {
        return utenteRepository.findByEmail(email);
    }

    public List<Evento> findEventiByEmail(String email) {
        Optional<Utente> utenteOptional = getUtenteByEmail(email);

        if (utenteOptional.isPresent()) {
            return utenteOptional.get().getEventi();
        } else {
            throw new NotFoundException("Non sono stati trovati eventi con un organizzatore avente questa email!");
        }
    }

    public UtenteDataDto patchUser(Integer id, UtenteDto utenteDto) {
        Optional<Utente> utenteOptional = getUserById(id);

        if (utenteOptional.isPresent()) {
            Utente utente = utenteOptional.get();
            if (utenteDto.getPassword() != null) {
                utente.setPassword(passwordEncoder.encode(utenteDto.getPassword()));
            }
            if (utenteDto.getNome() != null) {
                utente.setNome(utenteDto.getNome());
            }
            if (utenteDto.getCognome() != null) {
                utente.setCognome(utenteDto.getCognome());
            }
            if (utenteDto.getEmail() != null) {
                utente.setEmail(utenteDto.getEmail());
            }
            if (utenteDto.getAvatar() != null) {
                utente.setAvatar(utenteDto.getAvatar());
            }
            utenteRepository.save(utente);
            UtenteDataDto utenteDataDto = new UtenteDataDto();
            utenteDataDto.setNome(utente.getNome());
            utenteDataDto.setCognome(utente.getCognome());
            utenteDataDto.setAvatar(utente.getAvatar());
            utenteDataDto.setEmail(utente.getEmail());
            utenteDataDto.setId(utente.getId());
            utenteDataDto.setTipoUtente(utente.getTipoUtente());
            return utenteDataDto;
        } else {
            throw new NotFoundException("Utente con id " + id + " non trovato");
        }
    }

    public UtenteDataDto patchAvatarUtente(Integer id, MultipartFile avatar) throws IOException {
        Optional<Utente> utenteOptional = getUserById(id);

        if (utenteOptional.isPresent()) {
            String url = (String) cloudinary.uploader().upload(avatar.getBytes(), Collections.emptyMap()).get("url");
            Utente utente = utenteOptional.get();
            utente.setAvatar(url);
            utenteRepository.save(utente);
            UtenteDataDto utenteDataDto = new UtenteDataDto();
            utenteDataDto.setNome(utente.getNome());
            utenteDataDto.setCognome(utente.getCognome());
            utenteDataDto.setAvatar(utente.getAvatar());
            utenteDataDto.setEmail(utente.getEmail());
            utenteDataDto.setId(utente.getId());
            utenteDataDto.setTipoUtente(utente.getTipoUtente());
            utenteDataDto.setDescrizioneArtista(utente.getDescrizioneArtista());
            utenteDataDto.setNomeArtista(utente.getNomeArtista());
            utenteDataDto.setTipoArtista(utente.getTipoArtista());
            utenteDataDto.setSfondoArtista(utente.getSfondoArtista());

            return utenteDataDto;
        } else {
            throw new NotFoundException("Utente con id " + id + " non trovato");
        }
    }

    private void sendMailUtente(String email, String nome) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Benvenuto su MuzikFest");

            String htmlMsg = String.format("""
                <html>
                    <body>
                        <p>Ciao %s,</p>
                        <p>Siamo entusiasti di darti il benvenuto su MuzikFest!</p>
                        <p>Grazie per esserti unito alla nostra community. Su MuzikFest, avrai accesso a una vasta gamma di artisti emergenti e potrai partecipare a festival emozionanti. Ecco cosa puoi fare sulla nostra piattaforma:</p>
                        <ul>
                            <li><strong>Ascolta artisti emergenti:</strong> Scopri nuovi talenti e goditi la musica di artisti emergenti provenienti da tutto il mondo.</li>
                            <li><strong>Partecipa a festival:</strong> Unisciti ai nostri festival musicali, partecipa agli eventi live e condividi l'esperienza con altri appassionati di musica.</li>
                            <li><strong>Connettiti con la community:</strong> Interagisci con altri utenti, lascia recensioni e supporta i tuoi artisti preferiti.</li>
                        </ul>
                        <p>Se hai domande o hai bisogno di assistenza, il nostro team di supporto è qui per aiutarti. Puoi contattarci via email a <a href="mailto:supporto@muzikfest.com">supporto@muzikfest.com</a> o visitare il nostro <a href="http://example.com/centro-assistenza">centro assistenza</a>.</p>
                        <p>Siamo felici di averti con noi e non vediamo l'ora di condividere con te questa fantastica esperienza musicale!</p>
                        <p>Un caloroso benvenuto,</p>
                        <p>Il team di MuzikFest</p>
                    </body>
                </html>
                """, nome);
            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BadRequestException("Non è stato possibile inviare la mail");
        }
    }

    private void sendMailArtista(String email, String nomeArtista) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Benvenuto su MuzikFest");

            String htmlMsg = String.format("""
                <html>
                    <body>
                        <p>Ciao %s,</p>
                        <p>Siamo entusiasti di darti il benvenuto sulla nostra piattaforma! Grazie per esserti registrato come artista.</p>
                        <p>Ecco alcuni passaggi per iniziare:</p>
                        <ul>
                            <li>Completa il tuo profilo: Aggiungi una descrizione, foto, e tutti i dettagli che ritieni importanti per presentarti al meglio alla nostra community.</li>
                            <li>Esplora la piattaforma: Scopri le varie funzionalità e opportunità offerte per promuovere il tuo lavoro artistico.</li>
                            <li>Connettiti con altri artisti: Inizia a seguire altri artisti e partecipa alle discussioni per costruire il tuo network professionale.</li>
                        </ul>
                        <p>Se hai domande o hai bisogno di assistenza, il nostro team di supporto è qui per aiutarti. Puoi contattarci via email a <a href="mailto:muzikfest@supporto.com">muzikfest@supporto.com</a> o visitare il nostro <a href="http://example.com/centro-assistenza">centro assistenza</a>.</p>
                        <p>Siamo felici di averti con noi e non vediamo l'ora di vedere come contribuirai alla nostra comunità artistica!</p>
                        <p>Un caloroso benvenuto,</p>
                        <p>Il team di MuzikFest</p>
                    </body>
                </html>
                """, nomeArtista);
            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BadRequestException("Non è stato possibile inviare la mail");
        }
    }

}

