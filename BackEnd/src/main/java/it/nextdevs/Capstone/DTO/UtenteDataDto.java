package it.nextdevs.Capstone.DTO;

import it.nextdevs.Capstone.enums.TipoUtente;
import lombok.Data;

@Data
public class UtenteDataDto {

    private int id;
    private String email;
    private String nome;
    private String cognome;
    private TipoUtente tipoUtente;
    private String avatar;

}
