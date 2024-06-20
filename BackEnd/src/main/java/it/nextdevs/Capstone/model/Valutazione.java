package it.nextdevs.Capstone.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Valutazione {

    @Id
    @GeneratedValue
    private int id;

    @ManyToOne
    @JoinColumn(name = "brano_id")
    private Brano brano;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente utente;

    private int punteggio;

}
