package org.cresplanex.api.state.webgateway.auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.core.convert.converter.Converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class RoleConverter implements Converter<Jwt, Collection<GrantedAuthority>>  {
    /**
     * @param source the source object to convert, which must be an instance of {@code S} (never {@code null})
     * @return the converted object, which must be an instance of {@code T} (potentially {@code null})
     */
    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt source) {
        Object roles = source.getClaims().get("roles");
        if (!(roles instanceof ArrayList)) {
            return new ArrayList<>();
        }
        ArrayList<String> rolesArray = (ArrayList<String>) roles;
        if (rolesArray.isEmpty()) {
            return new ArrayList<>();
        }
        return rolesArray.stream().map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
