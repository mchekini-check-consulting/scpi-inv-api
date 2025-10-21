package fr.checkconsulting.scpiinvapi.Config;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.OAuth2Constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakAdminConfig {


    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.admin.username}")
    private String adminUsername;

    @Value("${keycloak.admin.password}")
    private String adminPassword;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder().serverUrl(authServerUrl).realm(realm).username(adminUsername).password(adminPassword).clientId(clientId).grantType(OAuth2Constants.PASSWORD).build();
    }

    @Bean
    public RealmResource realmResource(Keycloak keycloak) {
        return keycloak.realm(realm);
    }

    @Bean
    public UsersResource usersResource(Keycloak keycloak) {
        return keycloak.realm(realm).users();
    }

}
