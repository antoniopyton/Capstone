package it.nextdevs.Capstone.DTO;

import it.nextdevs.Capstone.model.Utente;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BranoDto {

    private String titolo;

    @NotNull(message = "L'artista non può essere nullo")
    private Utente artista;

    @NotNull(message = "Il brano deve avere una durata")
    private double durata;

    private String genere;

    private int ascolti;

    private String copertina;

}
