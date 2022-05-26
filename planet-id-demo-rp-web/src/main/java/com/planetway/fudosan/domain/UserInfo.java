package com.planetway.fudosan.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserInfo extends User {
    private Long id;
    private String planetId;

    public UserInfo(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, String planetId) {
        super(username, password, authorities);
        this.id = id;
        this.planetId = planetId;
    }

    public void setPlanetId(String planetId) {
        this.planetId = planetId;
    }
}
