package it.nextdevs.Capstone.DTO;

import lombok.Data;

@Data
public class AuthDataDto {
    private String accessToken;
    private UtenteDataDto user;
}
