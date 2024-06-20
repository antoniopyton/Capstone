package it.nextdevs.Capstone.model;

import it.nextdevs.Capstone.enums.TipoEvento;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Evento {

    @Id
    @GeneratedValue
    private int id;

    private String nome;

    private String descrizione;

    private LocalDate data;

    private LocalDate dataInserimento;

    private String luogo;

    private String immagine;

    private int capienzaMax;

    private int postiDisponibili;

    private TipoEvento tipoEvento;

    @ManyToMany(mappedBy = "eventiPrenotati")
    private List<Utente> utentiPrenotati = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "organizzatore_id")
    private Utente organizzatore;

}
