package com.bsuir.neural_network.search_for_similar_photos.enumeration;

import com.bsuir.neural_network.search_for_similar_photos.constant.Authority;

public enum Role {
    ROLE_USER(Authority.USER_AUTHORITIES),
    ROLE_SUBSCRIBER(Authority.SUBSCRIBER_AUTHORITIES),
    ROLE_ADMIN(Authority.ADMIN_AUTHORITIES);

    private String[] authorities;

    Role(String... authorities) {
        this.authorities = authorities;
    }

    public String[] getAuthorities() {
        return authorities;
    }
}
