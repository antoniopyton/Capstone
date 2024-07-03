package it.nextdevs.Capstone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.nextdevs.Capstone.enums.TipoArtista;
import it.nextdevs.Capstone.enums.TipoUtente;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Data
@Entity
public class Utente implements UserDetails {

    @Id
    @GeneratedValue
    private int id;

    private String nome;

    private String cognome;

    private String email;

    private String password;

    private String provider;

    private String avatar;

    @Enumerated(EnumType.STRING)
    private TipoArtista tipoArtista;

    private String nomeArtista;

    private String sfondoArtista;

    private String descrizioneArtista;

    @Enumerated(EnumType.STRING)
    private TipoUtente tipoUtente;

    @ManyToMany
    @JoinTable(
            name = "eventi_prenotati_utente",
            joinColumns = @JoinColumn(name = "utente_id"),
            inverseJoinColumns = @JoinColumn(name = "evento_id")
    )
    @JsonIgnore
    private List<Evento> eventiPrenotati = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "artista", fetch = FetchType.EAGER)
    private List<Brano> brani;

    @JsonIgnore
    @OneToMany(mappedBy = "utente")
    private List<Valutazione> valutazioni;

    @JsonIgnore
    @OneToMany(mappedBy = "organizzatore")
    private List<Evento> eventi;

    @JsonIgnore
    @ManyToMany(mappedBy = "artistiCandidati")
    private List<Evento> candidature = new ArrayList<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "preferiti",
            joinColumns = @JoinColumn(name = "utente_id"),
            inverseJoinColumns = @JoinColumn(name = "brano_id")
    )
    private Set<Brano> braniPreferiti;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(tipoUtente.name()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
