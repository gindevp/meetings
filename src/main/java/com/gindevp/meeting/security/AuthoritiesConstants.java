package com.gindevp.meeting.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String ROOM_MANAGER = "ROLE_ROOM_MANAGER";

    public static final String UNIT_MANAGER = "ROLE_UNIT_MANAGER";

    public static final String ROLE_SECRETARY = "ROLE_SECRETARY";

    private AuthoritiesConstants() {}
}
