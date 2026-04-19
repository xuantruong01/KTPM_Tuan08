package com.movie.userservice.service;

import com.movie.userservice.event.EventConstants;
import com.movie.userservice.event.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserRegistered(UserRegisteredEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                EventConstants.MOVIE_EXCHANGE,
                EventConstants.USER_REGISTERED_ROUTING_KEY,
                event
            );
            log.info("Published USER_REGISTERED event for userId={}, email={}", event.userId(), event.email());
        } catch (AmqpException ex) {
            log.warn(
                "User registered but failed to publish USER_REGISTERED event for userId={}, email={}: {}",
                event.userId(),
                event.email(),
                ex.getMessage()
            );
        }
    }
}
