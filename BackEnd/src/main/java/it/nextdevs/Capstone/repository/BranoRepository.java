package it.nextdevs.Capstone.repository;

import it.nextdevs.Capstone.model.Brano;
import it.nextdevs.Capstone.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;

public interface BranoRepository extends JpaRepository<Brano, Integer> {

    @Query("SELECT b FROM Brano b WHERE b.artista.id = :utenteId")
    List<Brano> findBraniByUtenteId(int utenteId);

    @Query(value = "SELECT * FROM brano ORDER BY ascolti DESC LIMIT 5", nativeQuery = true)
    List<Brano> findTop5ByAscolti();



}
