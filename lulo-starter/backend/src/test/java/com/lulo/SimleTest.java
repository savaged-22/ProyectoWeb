//primer test sonar
package com.lulo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleTest {

    @Test
    void pruebaBasica() {
        int resultado = 2 + 2;
        assertEquals(4, resultado);
    }
}