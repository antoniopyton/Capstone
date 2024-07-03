package it.nextdevs.Capstone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.nextdevs.Capstone.enums.Genere;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Brano {

    @Id
    @GeneratedValue
    private int id;

    private String titolo;

    @ManyToOne
    @JoinColumn(name = "artista_id")
    private Utente artista;

    private String fileUrl;

    private double durata;

    @Enumerated(EnumType.STRING)
    private Genere genere;

    private double valutazioneMedia;

    private int ascolti;

    private LocalDate dataInserimento;

    private String copertina;

    @JsonIgnore
    @OneToMany(mappedBy = "brano", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Valutazione> valutazioni;

    @JsonIgnore
    @ManyToMany(mappedBy = "braniPreferiti")
    private List<Utente> utentiPreferiti;


}
