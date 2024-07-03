package it.nextdevs.Capstone.DTO;

import it.nextdevs.Capstone.enums.StatoBiglietti;
import it.nextdevs.Capstone.enums.TipoEvento;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EventoDto {

    @NotNull(message = "Il nome non può essere nullo")
    private String nome;

    private String descrizione;

    @NotNull(message = "Devi scegliere una data")
    private LocalDate data;

    @NotNull(message = "Devi definire un luogo")
    private String luogo;

    private String immagine;

    private int postiDisponibili;

    private TipoEvento tipoEvento;

    private StatoBiglietti statoBiglietti;

    @NotNull(message = "Devi inserire una capienza")
    private int capienzaMax;

}
