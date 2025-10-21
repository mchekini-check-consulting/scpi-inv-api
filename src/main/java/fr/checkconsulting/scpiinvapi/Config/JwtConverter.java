package fr.checkconsulting.scpiinvapi.Config;

import com.nimbusds.jwt.JWTClaimNames;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    private final JwtConverterProperties jwtConverterProperties;


    public JwtConverter(JwtConverterProperties jwtConverterProperties) {
        this.jwtConverterProperties = jwtConverterProperties;
    }


    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Conversion des rôles (authorities)
        Collection<GrantedAuthority> authorities = Stream.concat(jwtGrantedAuthoritiesConverter.convert(jwt).stream(), extractRessourceRoles(jwt).stream()).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    private Collection<? extends GrantedAuthority> extractRessourceRoles(Jwt jwt) {
        Set<GrantedAuthority> authorities = new HashSet<>();
//Role de realm
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(jwtConverterProperties.getRessourceId());
            if (resource != null) {
                Collection<String> resourceRoles = (Collection<String>) resource.get("roles");
                if (resourceRoles != null) {
                    authorities.addAll(resourceRoles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())).collect(Collectors.toSet()));
                }
            }
        }

        //Role de client
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null) {
            Collection<String> realmRoles = (Collection<String>) realmAccess.get("roles");
            if (realmRoles != null) {
                authorities.addAll(realmRoles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())).collect(Collectors.toSet()));
            }
        }

        return authorities;
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JWTClaimNames.SUBJECT;

        if (jwtConverterProperties.getPrincipalAttribute() != null) {
            claimName = jwtConverterProperties.getPrincipalAttribute();
        }
        return jwt.getClaim(claimName);
    }
}
