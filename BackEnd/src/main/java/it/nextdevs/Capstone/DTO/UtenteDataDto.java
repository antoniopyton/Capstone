package it.nextdevs.Capstone.DTO;

import it.nextdevs.Capstone.enums.TipoArtista;
import it.nextdevs.Capstone.enums.TipoUtente;
import it.nextdevs.Capstone.model.Brano;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.util.List;

@Data
public class UtenteDataDto {

    private int id;
    private String email;
    private String nome;
    private String cognome;
    private TipoUtente tipoUtente;
    private String avatar;

    private String nomeArtista;

    @Enumerated(EnumType.STRING)
    private TipoArtista tipoArtista;

    private String sfondoArtista;

    private String descrizioneArtista;

    private List<Brano> brani;

}
