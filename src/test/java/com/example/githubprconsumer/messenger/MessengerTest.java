package com.example.githubprconsumer.messenger;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MessengerTest {
    @Test
    void constructorTest() {
        Messenger messenger = new Messenger(
                999999L,
                MessengerType.DISCORD,
                "random-webhook-url"
        );

        Assertions.assertThat(messenger).isNotNull();
        Assertions.assertThat(messenger.isActive()).isFalse();
    }

    @Test
    void activateTest() {
        Messenger messenger = new Messenger(
                999999L,
                MessengerType.DISCORD,
                "random-webhook-url"
        );
        messenger.activate();
        Assertions.assertThat(messenger.isActive()).isTrue();
    }
}