package it.nextdevs.Capstone.repository;

import it.nextdevs.Capstone.enums.TipoUtente;
import it.nextdevs.Capstone.model.Evento;
import it.nextdevs.Capstone.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    Optional<Utente> findByNome(String nome);

    Optional<Utente> findByEmail(String email);

    @Query("SELECT u FROM Utente u WHERE u.tipoUtente = :tipoUtente")
    List<Utente> findAllByTipoUtente(TipoUtente tipoUtente);

}
