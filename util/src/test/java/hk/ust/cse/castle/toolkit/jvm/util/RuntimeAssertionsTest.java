package hk.ust.cse.castle.toolkit.jvm.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RuntimeAssertionsTest {

    @Test
    void testUnreachable() {
        assertThrows(UnreachableStatementError.class, RuntimeAssertions::unreachable);
        assertThrows(UnreachableStatementError.class, () -> RuntimeAssertions.unreachable("testUnreachable"));
    }
}
