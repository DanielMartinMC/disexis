package es.danielmc.rest.auth.services.authentication;


import es.danielmc.rest.auth.dto.JwtAuthResponse;
import es.danielmc.rest.auth.dto.UserSignInRequest;
import es.danielmc.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}