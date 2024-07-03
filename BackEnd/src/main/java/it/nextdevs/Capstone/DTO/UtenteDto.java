package it.nextdevs.Capstone.DTO;

import it.nextdevs.Capstone.enums.TipoArtista;
import it.nextdevs.Capstone.enums.TipoUtente;
import it.nextdevs.Capstone.model.Brano;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UtenteDto {

    @NotNull(message = "Il nome non può essere nullo")
    private String nome;

    @NotNull(message = "Il cognome non può essere nullo")
    private String cognome;

    @NotNull(message = "L'email non può essere nulla")
    private String email;

    @NotNull(message = "La password non può essere nulla")
    private String password;

    private String nomeArtista;

    @Enumerated(EnumType.STRING)
    private TipoArtista tipoArtista;

    private String provider;

    private TipoUtente tipoUtente;

    private String avatar;

    private String sfondoArtista;

    private String descrizioneArtista;

    private List<Brano> brani;

}
