package com.movie.userservice.event;

public final class EventConstants {

    public static final String MOVIE_EXCHANGE = "movie.exchange";
    public static final String USER_REGISTERED_QUEUE = "user.registered.queue";
    public static final String USER_REGISTERED_ROUTING_KEY = "user.registered";
    public static final String USER_REGISTERED_EVENT_TYPE = "USER_REGISTERED";

    private EventConstants() {
    }
}
