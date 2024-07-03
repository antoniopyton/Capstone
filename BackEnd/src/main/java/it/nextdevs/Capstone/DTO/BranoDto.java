package it.nextdevs.Capstone.DTO;

import it.nextdevs.Capstone.enums.Genere;
import it.nextdevs.Capstone.model.Utente;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BranoDto {

    private String titolo;

    @NotNull(message = "L'artista non può essere nullo")
    private int artista;

    @NotNull(message = "Il brano deve avere una durata")
    private double durata;

    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private Genere genere;

    private int ascolti;

    private String copertina;

}
