package it.nextdevs.Capstone.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import it.nextdevs.Capstone.DTO.AuthDataDto;
import it.nextdevs.Capstone.DTO.UtenteDataDto;
import it.nextdevs.Capstone.DTO.UtenteDto;
import it.nextdevs.Capstone.exception.BadRequestException;
import it.nextdevs.Capstone.exception.NotFoundException;
import it.nextdevs.Capstone.model.Utente;
import it.nextdevs.Capstone.repository.UtenteRepository;
import it.nextdevs.Capstone.security.JwtTool;
import it.nextdevs.Capstone.service.PasswordGenerator;
import it.nextdevs.Capstone.service.UtenteService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
public class OAuth2Controller {
    private final OAuth2AuthorizedClientService clientService;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    @Autowired
    private UtenteRepository utenteRepository;
    @Autowired
    private UtenteService utenteService;
    @Autowired
    private JwtTool jwtTool;

    @Autowired
    public OAuth2Controller(OAuth2AuthorizedClientService clientService) {
        this.clientService = clientService;
    }

//    @GetMapping("/auth/login/oauth2/code/{provider}")
//    public Integer loginSuccess(@PathVariable String provider,@AuthenticationPrincipal OAuth2AuthenticationToken authenticationToken) {
//
//        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
//                authenticationToken.getAuthorizedClientRegistrationId(),
//                authenticationToken.getName()
//        );
//
//        Map<String, Object> attributes = authenticationToken.getPrincipal().getAttributes();
//        String userEmail = (String) attributes.get("email");
//
//        Optional<User> existingUser = userRepository.findByEmail(userEmail);
//        if (existingUser.isEmpty()) {
//            UserDto userDto = new UserDto();
//            userDto.setEmail(userEmail);
//            userDto.setPassword(PasswordGenerator.generatePassword(32));
//            userDto.setUsername(userEmail);
//            userDto.setProvider(provider);
//            if (Objects.equals(provider, "Google")) {
//                userDto.setNome((String) attributes.get("given_name"));
//                userDto.setCognome((String) attributes.get("family_name"));
//            } else if (Objects.equals(provider, "Facebook")) {
//                userDto.setNome((String) attributes.get("first_name"));
//                userDto.setCognome((String) attributes.get("last_name"));
//            }
//            userDto.setNewsletter(false);
//            return userService.saveUser(userDto);
//        } else {
//            return existingUser.get().getIdUtente();
//        }
//    }

    @PostMapping("/auth/login/oauth2/code/{provider}")
    public AuthDataDto loginSuccess(@PathVariable String provider, @RequestBody Map<String, String> request) throws GeneralSecurityException, IOException {
        String idTokenString = request.get("token");
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleClientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken!= null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String userId = payload.getSubject();
            String email = payload.getEmail();
            boolean emailVerified = payload.getEmailVerified();
            String name = (String) payload.get("given_name");
            String surname = (String) payload.get("family_name");
            String pictureUrl = (String) payload.get("picture");

            Optional <Utente> utenteOptional = utenteService.getUtenteByEmail(email);

            if (utenteOptional.isEmpty()) {
                UtenteDto utenteDto = new UtenteDto();
                utenteDto.setEmail(email);
                utenteDto.setPassword(PasswordGenerator.generatePassword(32));
//                utenteDto.setUsername(email);
                utenteDto.setProvider(provider);
                utenteDto.setAvatar(pictureUrl);
                utenteDto.setNome(name);
                utenteDto.setCognome(surname);
//                utenteDto.setNewsletter(false);
                AuthDataDto authDataDto = new AuthDataDto();
                Integer id = utenteService.saveUtente(utenteDto);
                utenteOptional = utenteService.getUserById(id);
                if (utenteOptional.isPresent()) {
                    UtenteDataDto utenteDataDto = new UtenteDataDto();
                    Utente utente = utenteOptional.get();
                    utenteDataDto.setAvatar(utente.getAvatar());
                    utenteDataDto.setNome(utente.getNome());
//                    utenteDataDto.setNewsletter(utente.isNewsletter());
                    utenteDataDto.setEmail(utente.getEmail());
                    utenteDataDto.setCognome(utente.getCognome());
                    utenteDataDto.setId(utente.getId());
                    utenteDataDto.setTipoUtente(utente.getTipoUtente());
//                    utenteDataDto.setUsername(utente.getUsername());
                    authDataDto.setUser(utenteDataDto);
                    authDataDto.setAccessToken(jwtTool.createToken(utente));
                    return authDataDto;
                } else {
                    throw new NotFoundException("Utente non trovato");
                }
            } else {
                AuthDataDto authDataDto = new AuthDataDto();
                UtenteDataDto utenteDataDto = new UtenteDataDto();
                Utente utente = utenteOptional.get();
                utenteDataDto.setAvatar(utente.getAvatar());
                utenteDataDto.setNome(utente.getNome());
//                utenteDataDto.setNewsletter(utente.isNewsletter());
                utenteDataDto.setEmail(utente.getEmail());
                utenteDataDto.setCognome(utente.getCognome());
                utenteDataDto.setId(utente.getId());
                utenteDataDto.setTipoUtente(utente.getTipoUtente());
//                utenteDataDto.setUsername(utente.getUsername());
                authDataDto.setUser(utenteDataDto);
                authDataDto.setAccessToken(jwtTool.createToken(utente));
                return authDataDto;
            }
        } else {
            throw new BadRequestException("Token Google non valido");
        }
//        System.out.println(authenticationToken);
//        if (authenticationToken == null) {
//            // Handle the case where authenticationToken is null
//            throw new IllegalStateException("Authentication token is missing.");
//        }

//        OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
//                authenticationToken.getAuthorizedClientRegistrationId(),
//                authenticationToken.getName()
//        );
//
//        Map<String, Object> attributes = authenticationToken.getPrincipal().getAttributes();
//        String userEmail = (String) attributes.get("email");
//
//        Optional<User> existingUser = userRepository.findByEmail(userEmail);
//        if (existingUser.isEmpty()) {
//            UserDto userDto = new UserDto();
//            userDto.setEmail(userEmail);
//            userDto.setPassword(PasswordGenerator.generatePassword(32));
//            userDto.setUsername(userEmail);
//            userDto.setProvider(provider);
//            if (Objects.equals(provider, "Google")) {
//                userDto.setNome((String) attributes.get("given_name"));
//                userDto.setCognome((String) attributes.get("family_name"));
//            } else if (Objects.equals(provider, "Facebook")) {
//                userDto.setNome((String) attributes.get("first_name"));
//                userDto.setCognome((String) attributes.get("last_name"));
//            }
//            userDto.setNewsletter(false);
//            return userService.saveUser(userDto);
//        } else {
//            return existingUser.get().getIdUtente();
//        }
    }


}
