package textEditor.ut;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExampleTest {
    @Test
    void shouldBeEqual4() {
        int result = 2 + 2;
        assertThat(result).isEqualTo(4);
    }

    @Test
    void shouldBeEqual6() {
        int result = 2 + 4;
        assertThat(result).isEqualTo(6);
    }
}
