package hk.ust.cse.castle.toolkit.jvm.posix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ShReservedExitCodeTest {

    @Test
    void testInteropGetCode() {
        assertEquals(ShReservedExitCode.GENERAL_ERRORS.code, 1);
    }

    @Test
    void testInteropResolve() {
        assertEquals(ShReservedExitCode.GENERAL_ERRORS, ShReservedExitCode.resolveExitCode(1));
    }

    @Test
    void testResolveUnknownExitCode() {
        assertNull(ShReservedExitCode.resolveExitCode(0));
    }
}
