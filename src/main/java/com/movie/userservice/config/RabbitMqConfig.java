package com.movie.userservice.config;

import com.movie.userservice.event.EventConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public TopicExchange movieExchange() {
        return new TopicExchange(EventConstants.MOVIE_EXCHANGE, true, false);
    }

    @Bean
    public Queue userRegisteredQueue() {
        return new Queue(EventConstants.USER_REGISTERED_QUEUE, true);
    }

    @Bean
    public Binding userRegisteredBinding(TopicExchange movieExchange, Queue userRegisteredQueue) {
        return BindingBuilder
            .bind(userRegisteredQueue)
            .to(movieExchange)
            .with(EventConstants.USER_REGISTERED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
