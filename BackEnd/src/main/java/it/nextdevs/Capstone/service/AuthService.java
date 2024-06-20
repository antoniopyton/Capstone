package it.nextdevs.Capstone.service;


import it.nextdevs.Capstone.DTO.AuthDataDto;
import it.nextdevs.Capstone.DTO.UtenteDataDto;
import it.nextdevs.Capstone.DTO.UtenteLoginDto;
import it.nextdevs.Capstone.exception.UnauthorizedException;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.security.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UtenteService utenteService;
    @Autowired
    private JwtTool jwtTool;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthDataDto authenticateUserAndCreateToken(UtenteLoginDto utenteLoginDto) {
        Optional<Utente> utenteOptional = utenteService.getUtenteByEmail(utenteLoginDto.getEmail());
        if (utenteOptional.isEmpty()) {
            throw new UnauthorizedException("Error in authorization, relogin!");
        } else {
            Utente user = utenteOptional.get();
            if (passwordEncoder.matches(utenteLoginDto.getPassword(), user.getPassword())) {
                AuthDataDto authDataDto = new AuthDataDto();
                authDataDto.setAccessToken(jwtTool.createToken(user));
                UtenteDataDto utenteDataDto = new UtenteDataDto();
                utenteDataDto.setNome(user.getNome());
                utenteDataDto.setCognome(user.getCognome());
                utenteDataDto.setAvatar(user.getAvatar());
                utenteDataDto.setEmail(user.getEmail());
                utenteDataDto.setId(user.getId());
                utenteDataDto.setTipoUtente(user.getTipoUtente());
                authDataDto.setUser(utenteDataDto);
                return authDataDto;
            } else {
                throw new UnauthorizedException("Error in authorization, relogin!");
            }
        }
    }
}
