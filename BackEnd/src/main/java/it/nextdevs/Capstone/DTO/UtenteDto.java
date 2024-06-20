package it.nextdevs.Capstone.DTO;

import it.nextdevs.Capstone.enums.TipoUtente;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

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

    private TipoUtente tipoUtente;

    private String avatar;

    private String sfondoArtista;

    private String descrizioneArtista;

}
