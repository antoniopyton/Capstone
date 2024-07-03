package it.nextdevs.Capstone.repository;

import it.nextdevs.Capstone.model.Evento;
import it.nextdevs.Capstone.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    Optional<Evento> findByNome(String nome);

    List<Evento> findAllByDataInserimento(LocalDate dataInserimento);

    List<Evento> findAllByOrderByNomeAsc();

    List<Evento> findAllByOrderByNomeDesc();

    @Query("SELECT e FROM Evento e WHERE e.data BETWEEN :startDate AND :endDate ORDER BY e.data ASC")
    List<Evento> findByDataBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


    @Query("SELECT e FROM Evento e WHERE e.luogo = :luogo AND e.data BETWEEN :startDate AND :endDate")
    List<Evento> findByLuogoAndDataBetween(@Param("luogo") String luogo, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
