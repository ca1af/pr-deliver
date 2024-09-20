package com.example.githubprconsumer.messenger;

import com.example.githubprconsumer.messenger.domain.Messenger;
import com.example.githubprconsumer.messenger.domain.MessengerType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MessengerTest {
    private static final String LOGIN = "login";
    @Test
    void constructorTest() {
        Messenger messenger = new Messenger(
                999999L,
                MessengerType.DISCORD,
                "random-webhook-url",
                LOGIN
        );

        Assertions.assertThat(messenger).isNotNull();
        Assertions.assertThat(messenger.isActive()).isFalse();
    }

    @Test
    void activateTest() {
        Messenger messenger = new Messenger(
                999999L,
                MessengerType.DISCORD,
                "random-webhook-url",
                LOGIN
        );
        messenger.activate();
        Assertions.assertThat(messenger.isActive()).isTrue();
    }
}