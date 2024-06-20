package it.nextdevs.Capstone.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.nextdevs.Capstone.DTO.BranoDto;
import it.nextdevs.Capstone.DTO.UtenteDataDto;
import it.nextdevs.Capstone.DTO.UtenteDto;
import it.nextdevs.Capstone.enums.TipoUtente;
import it.nextdevs.Capstone.exception.BadRequestException;
import it.nextdevs.Capstone.exception.NotFoundException;
import it.nextdevs.Capstone.model.Brano;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.repository.BranoRepository;
import it.nextdevs.Capstone.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

@Service
public class BranoService {

    @Autowired
    private BranoRepository branoRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    public Integer saveBrano(BranoDto branoDto) {
        Brano brano = new Brano();
        brano.setTitolo(branoDto.getTitolo());
        brano.setArtista(branoDto.getArtista());
        brano.setDurata(branoDto.getDurata());
        brano.setCopertina(branoDto.getCopertina());
        brano.setGenere(branoDto.getGenere());
        brano.setDataInserimento(LocalDate.now());

        branoRepository.save(brano);
        Optional<Utente> optionalUtente = utenteRepository.findById(brano.getArtista().getId());
        if (optionalUtente.isPresent()) {
            Utente utente = optionalUtente.get();
            sendMailBrano(utente.getEmail());
        } else {
            throw new NotFoundException("Utente non trovato per l'ID artista: " + brano.getArtista().getId());
        }

        return brano.getId();
    }

    public Page<Brano> getBrani(int page, int size , String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return branoRepository.findAll(pageable);
    }

    public Brano getBranoById(int id) {
        Optional<Brano> branoOptional = branoRepository.findById(id);

        if (branoOptional.isPresent()) {
            return branoOptional.get();
        } else {
            throw new NotFoundException("Brano con id: " + id + " non trovato");
        }
    }

    public String deleteBrano(int id) {
        Optional<Brano> branoOptional = branoRepository.findById(id);

        if (branoOptional.isPresent()) {
            branoRepository.delete(branoOptional.get());
            return "Brano con id: " + id + " correttamente eliminato.";
        } else {
            throw new NotFoundException("Brano con id: " + id + " non trovato");
        }
    }

    public Brano patchCopertinaBrano(Integer id, MultipartFile avatar) throws IOException {
        Optional<Brano> utenteOptional = branoRepository.findById(id);

        if (utenteOptional.isPresent()) {
            String url = (String) cloudinary.uploader().upload(avatar.getBytes(), Collections.emptyMap()).get("url");
            Brano brano = utenteOptional.get();
            brano.setCopertina(url);

            return brano;
        } else {
            throw new NotFoundException("Brano con id "+id+" non trovato");
        }
    }

    public Brano patchBrano(Integer id, MultipartFile file) throws IOException {
        Optional<Brano> branoOptional = branoRepository.findById(id);

        if (branoOptional.isPresent()) {
            // Caricamento del file su Cloudinary con specifica del tipo di risorsa
            String url = (String) cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto")).get("url");
            Brano brano = branoOptional.get();
            brano.setFileUrl(url);

            // Salva il brano aggiornato nel repository
            branoRepository.save(brano);

            return brano;
        } else {
            throw new NotFoundException("Brano con id " + id + " non trovato");
        }
    }

    private void sendMailBrano(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Registrazione Brano");
        message.setText("Registrazione Brano avvenuta con successo." +
                "Riceverai una nuova email che ti darà l'esito della tua richiesta");

        javaMailSender.send(message);
    }


}
