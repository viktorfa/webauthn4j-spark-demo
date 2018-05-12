package net.sharplab.springframework.security.webauthn.sample.app.config;

import com.webauthn4j.validator.WebAuthnAuthenticationContextValidator;
import com.webauthn4j.validator.WebAuthnRegistrationContextValidator;
import com.webauthn4j.validator.attestation.AttestationStatementValidator;
import com.webauthn4j.validator.attestation.NoneAttestationStatementValidator;
import net.sharplab.springframework.security.webauthn.WebAuthnAuthenticationProvider;
import net.sharplab.springframework.security.webauthn.WebAuthnRegistrationRequestValidator;
import net.sharplab.springframework.security.webauthn.authenticator.WebAuthnAuthenticatorService;
import net.sharplab.springframework.security.webauthn.challenge.ChallengeRepository;
import net.sharplab.springframework.security.webauthn.challenge.HttpSessionChallengeRepository;
import net.sharplab.springframework.security.webauthn.context.provider.RelyingPartyProvider;
import net.sharplab.springframework.security.webauthn.context.provider.RelyingPartyProviderImpl;
import net.sharplab.springframework.security.webauthn.context.provider.WebAuthnAuthenticationContextProvider;
import net.sharplab.springframework.security.webauthn.context.provider.WebAuthnAuthenticationContextProviderImpl;
import net.sharplab.springframework.security.webauthn.metadata.MetadataProvider;
import net.sharplab.springframework.security.webauthn.metadata.MetadataProviderImpl;
import net.sharplab.springframework.security.webauthn.sample.domain.component.AuthenticatorManager;
import net.sharplab.springframework.security.webauthn.userdetails.WebAuthnUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.DelegatingAccessDeniedHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;
import org.springframework.security.web.session.InvalidSessionAccessDeniedHandler;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.session.SimpleRedirectInvalidSessionStrategy;

import java.util.LinkedHashMap;
import java.util.List;

@Configuration
public class WebSecurityBeanConfig {

    @Bean
    public WebAuthnAuthenticationContextProvider webAuthnAuthenticationContextProvider(RelyingPartyProvider relyingPartyProvider) {
        return new WebAuthnAuthenticationContextProviderImpl(relyingPartyProvider);
    }

    @Bean
    public WebAuthnAuthenticationContextValidator webAuthnAuthenticationContextValidator() {
        return new WebAuthnAuthenticationContextValidator();
    }

    @Bean
    public WebAuthnRegistrationRequestValidator webAuthnRegistrationRequestValidator(WebAuthnRegistrationContextValidator registrationContextValidator, RelyingPartyProvider relyingPartyProvider) {
        return new WebAuthnRegistrationRequestValidator(registrationContextValidator, relyingPartyProvider);
    }

    @Bean
    public RelyingPartyProvider relyingPartyProvider(ChallengeRepository challengeRepository) {
        return new RelyingPartyProviderImpl(challengeRepository);
    }

    @Bean
    public ChallengeRepository challengeRepository() {
        return new HttpSessionChallengeRepository();
    }

    @Bean
    public WebAuthnRegistrationContextValidator webAuthnRegistrationContextValidator() {
        return WebAuthnRegistrationContextValidator.createNullAttestationStatementValidator();
    }

    @Bean
    public MetadataProvider metadataProvider(WebAuthnUserDetailsService userDetailsService) {
        return new MetadataProviderImpl(userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }


    @Bean
    public WebAuthnAuthenticationProvider webAuthnAuthenticationProvider(
            AuthenticatorManager authenticatorManager,
            WebAuthnUserDetailsService webAuthnUserDetailsService,
            WebAuthnAuthenticatorService webAuthnAuthenticatorService,
            WebAuthnAuthenticationContextValidator authenticationContextValidator) {

        WebAuthnAuthenticationProvider webAuthnAuthenticationProvider =
                new WebAuthnAuthenticationProvider(webAuthnUserDetailsService, webAuthnAuthenticatorService, authenticationContextValidator);
        webAuthnAuthenticationProvider.setAuthenticatorService(authenticatorManager);
        return webAuthnAuthenticationProvider;
    }

    @Bean
    public HttpSessionSecurityContextRepository httpSessionSecurityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public InvalidSessionStrategy invalidSessionStrategy() {
        return new SimpleRedirectInvalidSessionStrategy("/login?expired");
    }

    @Bean
    public InvalidSessionAccessDeniedHandler invalidSessionAccessDeniedHandler(InvalidSessionStrategy invalidSessionStrategy) {
        return new InvalidSessionAccessDeniedHandler(invalidSessionStrategy);
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        LinkedHashMap<Class<? extends AccessDeniedException>, AccessDeniedHandler> errorHandlers = new LinkedHashMap<>();

        // invalid csrf authenticator error handler
        AccessDeniedHandlerImpl invalidCsrfTokenErrorHandler = new AccessDeniedHandlerImpl();
        invalidCsrfTokenErrorHandler.setErrorPage("/error/invalidCsrfTokenError");
        errorHandlers.put(InvalidCsrfTokenException.class, invalidCsrfTokenErrorHandler);

        // invalid csrf authenticator error handler
        AccessDeniedHandlerImpl missingCsrfTokenErrorHandler = new AccessDeniedHandlerImpl();
        missingCsrfTokenErrorHandler.setErrorPage("/error/invalidCsrfTokenError");
        errorHandlers.put(MissingCsrfTokenException.class, missingCsrfTokenErrorHandler);

        // default error handler
        AccessDeniedHandlerImpl defaultErrorHandler = new AccessDeniedHandlerImpl();
        defaultErrorHandler.setErrorPage("/error/accessDeniedError");

        return new DelegatingAccessDeniedHandler(errorHandlers, defaultErrorHandler);
    }

}
