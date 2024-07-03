package it.nextdevs.Capstone.repository;

import it.nextdevs.Capstone.model.Ascolto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AscoltoRepository extends JpaRepository <Ascolto, Integer> {

    @Query("SELECT COUNT(a) FROM Ascolto a WHERE a.timestamp BETWEEN :startDate AND :endDate AND a.brano.artista.id = :artistaId")
    Integer countAscoltiByArtistaBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("artistaId") int artistaId);

    @Query("SELECT COUNT(a) FROM Ascolto a WHERE a.brano.id = :branoId")
    Integer countAscoltiByBranoId(@Param("branoId") int branoId);

}
