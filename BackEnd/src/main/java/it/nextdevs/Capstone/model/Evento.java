package it.nextdevs.Capstone.model;

import it.nextdevs.Capstone.enums.StatoBiglietti;
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

    @Enumerated(EnumType.STRING)
    private TipoEvento tipoEvento;

    @Enumerated(EnumType.STRING)
    private StatoBiglietti statoBiglietti;

    @ManyToMany(mappedBy = "eventiPrenotati")
    private List<Utente> utentiPrenotati = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "organizzatore_id")
    private Utente organizzatore;

    @ManyToMany
    @JoinTable(
            name = "candidature_evento",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "artista_id")
    )
    private List<Utente> artistiCandidati = new ArrayList<>();

    public void aggiornaStato() {
        if (postiDisponibili == 0) {
            this.statoBiglietti = StatoBiglietti.ESAURITI;
        } else if (postiDisponibili <= capienzaMax * 0.2) {
            this.statoBiglietti = StatoBiglietti.IN_ESAURIMENTO;
        } else {
            this.statoBiglietti = StatoBiglietti.DISPONIBILI;
        }
    }

}
