package it.nextdevs.Capstone.service;

import com.cloudinary.Cloudinary;
import it.nextdevs.Capstone.DTO.UtenteDataDto;
import it.nextdevs.Capstone.DTO.UtenteDto;
import it.nextdevs.Capstone.enums.TipoUtente;
import it.nextdevs.Capstone.exception.BadRequestException;
import it.nextdevs.Capstone.exception.NotFoundException;
import it.nextdevs.Capstone.model.Evento;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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
            sendMailRegistrazione(utenteDto.getEmail());

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
            artista.setNomeArtista(utenteDto.getNomeArtista());
            artista.setSfondoArtista(utenteDto.getSfondoArtista());
            artista.setDescrizioneArtista(utenteDto.getDescrizioneArtista());
            artista.setPassword(passwordEncoder.encode(utenteDto.getPassword()));

            utenteRepository.save(artista);
            sendMailRegistrazione(utenteDto.getEmail());

            return artista.getId();
        } else {
            throw new BadRequestException("L'artista con email " + utenteDto.getEmail() + " già esistente");
        }
    }

    public Page<Utente> getAllUtenti(int page, int size , String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return utenteRepository.findAll(pageable);
    }

    public Optional<Utente> getUserById(int id) {
        return utenteRepository.findById(id);
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
            throw new NotFoundException("Utente con id "+id+" non trovato");
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
            return utenteDataDto;
        } else {
            throw new NotFoundException("Utente con id " + id + " non trovato");
        }
    }

    private void sendMailRegistrazione(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Registrazione Utente");
        message.setText("Registrazione Utente avvenuta con successo");

        javaMailSender.send(message);
    }

}
