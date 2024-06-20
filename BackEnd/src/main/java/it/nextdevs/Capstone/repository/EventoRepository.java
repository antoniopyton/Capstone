package it.nextdevs.Capstone.repository;

import it.nextdevs.Capstone.model.Evento;
import it.nextdevs.Capstone.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    Optional<Evento> findByNome(String nome);

    List<Evento> findAllByDataInserimento(LocalDate dataInserimento);

    List<Evento> findAllByOrderByNomeAsc();

    List<Evento> findAllByOrderByNomeDesc();

}
