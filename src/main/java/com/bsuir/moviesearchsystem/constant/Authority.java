package com.bsuir.moviesearchsystem.constant;

public class Authority {
    public static final String[] USER_AUTHORITIES = { "user:read" };
    public static final String[] SUBSCRIBER_AUTHORITIES = { "user:read", "user:watch" };
    public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:watch","user:create" };
}
