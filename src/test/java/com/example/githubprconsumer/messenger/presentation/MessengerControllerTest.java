package com.example.githubprconsumer.messenger.presentation;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class MessengerControllerTest {

    @Test
    void name() {
        String origin = "ldH6/+87mz6Ihr13oUm5JCyf9DC3hr+WmAQ45ppZawc44y+vLBgHhflzSNC1R1vRFYSYV6x3ITVoJrFNDU/opsPYuygoKqSn+0l4icSwJYf9refLH4seTCuLxFez03XMlT/hNRk+0P5OoOGi3KtE+pmzdpR1pSHOUDgmsRL640g=";
        String converted = "ldH6/ 87mz6Ihr13oUm5JCyf9DC3hr WmAQ45ppZawc44y vLBgHhflzSNC1R1vRFYSYV6x3ITVoJrFNDU/opsPYuygoKqSn 0l4icSwJYf9refLH4seTCuLxFez03XMlT/hNRk 0P5OoOGi3KtE pmzdpR1pSHOUDgmsRL640g=";

        String replaced = converted.replace(" ", "+");
        Assertions.assertThat(replaced).isEqualTo(origin);
    }
}