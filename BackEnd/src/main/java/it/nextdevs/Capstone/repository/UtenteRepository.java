package it.nextdevs.Capstone.repository;

import it.nextdevs.Capstone.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Integer> {

    public Optional<Utente> findByEmail(String email);

}
