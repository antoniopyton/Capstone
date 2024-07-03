package it.nextdevs.Capstone.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Ascolto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "brano_id", nullable = false)
    private Brano brano;

    private LocalDateTime timestamp;

}
