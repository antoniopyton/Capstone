package it.nextdevs.Capstone.service;

import com.cloudinary.Cloudinary;
import com.google.cloud.storage.*;
import it.nextdevs.Capstone.DTO.BranoDto;

import it.nextdevs.Capstone.exception.BadRequestException;
import it.nextdevs.Capstone.exception.NotFoundException;
import it.nextdevs.Capstone.model.Ascolto;
import it.nextdevs.Capstone.model.Brano;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.repository.AscoltoRepository;
import it.nextdevs.Capstone.repository.BranoRepository;
import it.nextdevs.Capstone.repository.UtenteRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BranoService {

    @Autowired
    private BranoRepository branoRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private AscoltoRepository ascoltoRepository;

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    public Integer saveBrano(BranoDto branoDto) {

        Optional<Utente> optionalUtente = utenteRepository.findById(branoDto.getArtista());
        if (optionalUtente.isPresent()) {
            Utente utente = optionalUtente.get();
            Brano brano = new Brano();
            brano.setTitolo(branoDto.getTitolo());
            brano.setDurata(branoDto.getDurata());
            brano.setCopertina(branoDto.getCopertina());
            brano.setGenere(branoDto.getGenere());
            brano.setDataInserimento(LocalDate.now());
            brano.setFileUrl(branoDto.getFileUrl());
            brano.setCopertina(branoDto.getCopertina());
            brano.setArtista(utente);

            branoRepository.save(brano);
            sendMailBrano(utente.getEmail(), utente.getNomeArtista(), brano.getTitolo());

            return brano.getId();
        } else {
            throw new NotFoundException("Artista non trovato per l'ID artista: " + branoDto.getArtista());
        }


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

    public List<Brano> getBraniByUtenteId(int utenteId) {
        return branoRepository.findBraniByUtenteId(utenteId);
    }

//    public Brano getBranoByNome(String nome) {
//        Optional<Brano> branoOptional = branoRepository.findByNome(nome);
//
//        if (branoOptional.isPresent()) {
//            return branoOptional.get();
//        } else {
//            throw new NotFoundException("Cliente con nome " + nome + " non esiste");
//        }
//    }

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
            throw new NotFoundException("Brano con id " + id + " non trovato");
        }
    }

    public Brano patchBrano(int id, MultipartFile brano) throws IOException {
        // Caricamento del file su Firebase
        String fileName = UUID.randomUUID().toString() + "-" + brano.getOriginalFilename();
        String bucketName = "fir-muzikfest.appspot.com";  // Nome del bucket
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(brano.getContentType()).build();
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.create(blobInfo, brano.getBytes());

        String url = "https://storage.googleapis.com/" + blobId.getBucket() + "/" + blobId.getName();

        // Aggiornamento del database con l'URL del file
        Brano branoEntity = branoRepository.findById(id).orElseThrow(() -> new RuntimeException("Brano non trovato"));
        branoEntity.setFileUrl(url);
        return branoRepository.save(branoEntity);
    }

    private void sendMailBrano(String email, String nomeArtista, String titolo) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("RE: Richiesta inserimento Brano");

            String htmlMsg = String.format("""
                    <html>
                               <body>
                                   <p>Ciao %s,</p>
                                   <p>Grazie per aver inviato il tuo brano <strong>%s</strong> alla nostra piattaforma!</p>
                                   <p>La tua richiesta di inserimento del brano è stata presa in carico e sarà valutata dal nostro team. Riceverai un responso entro le prossime 48 ore.</p>
                                   <p>Nel frattempo, ecco alcuni suggerimenti su cosa puoi fare:</p>
                                   <ul>
                                       <li>Controlla il tuo profilo: Assicurati che la tua descrizione, le tue foto e tutti i dettagli siano aggiornati per presentarti al meglio alla nostra community.</li>
                                       <li>Esplora la piattaforma: Scopri le varie funzionalità e opportunità offerte per promuovere il tuo lavoro artistico.</li>
                                       <li>Connettiti con altri artisti: Inizia a seguire altri artisti e partecipa alle discussioni per costruire il tuo network professionale.</li>
                                   </ul>
                                   <p>Se hai domande o hai bisogno di assistenza, il nostro team di supporto è qui per aiutarti. Puoi contattarci via email a <a href="mailto:muzikfest@supporto.com">muzikfest@supporto.com</a> o visitare il nostro <a href="http://example.com/centro-assistenza">centro assistenza</a>.</p>
                                   <p>Siamo felici di avere il tuo contributo sulla nostra piattaforma e non vediamo l'ora di ascoltare il tuo brano!</p>
                                   <p>Un cordiale saluto,</p>
                                   <p>Il team di MuzikFest</p>
                               </body>
                           </html>
                """, nomeArtista, titolo);
            helper.setText(htmlMsg, true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new BadRequestException("Non è stato possibile inviare la mail");
        }
    }

    public void incrementaAscolti(int branoId) {
        Brano brano = branoRepository.findById(branoId).orElseThrow(() -> new NotFoundException("Brano con id: " + branoId + " non trovato"));
        brano.setAscolti(brano.getAscolti() + 1);
        branoRepository.save(brano);

        Ascolto ascolto = new Ascolto();
        ascolto.setBrano(brano);
        ascolto.setTimestamp(LocalDateTime.now());
        ascoltoRepository.save(ascolto);
    }

    public Integer getAscoltiByArtistaBetweenDates(LocalDateTime startDate, LocalDateTime endDate, int artistaId) {
        return ascoltoRepository.countAscoltiByArtistaBetweenDates(startDate, endDate, artistaId);
    }

    public List<Brano> getTop5BraniByAscolti() {
        return branoRepository.findTop5ByAscolti();
    }

    public Integer getAscoltiByBranoId(int branoId) {
        return ascoltoRepository.countAscoltiByBranoId(branoId);
    }

}
