package hk.ust.cse.castle.toolkit.jvm.posix;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PosixSignalTest {

    @Test
    void testEnumValuesInRange() {
        for (@NotNull final PosixSignal signal : PosixSignal.values()) {
            for (@NotNull final PosixSignal.Architecture arch : PosixSignal.Architecture.values()) {
                final int signalNumber = signal.getNumber(arch);

                assertTrue((signalNumber > 0 && signalNumber < 32) || signalNumber == PosixSignal.Constants.ABSENT);
            }
        }
    }

    @Test
    void testForPlatformCommon() {
        final PosixSignal[] platformSignals = PosixSignal.forPlatform(PosixSignal.Architecture.COMMON);

        for (int i = 0; i < platformSignals.length; ++i) {
            final PosixSignal signal = platformSignals[i];
            assertTrue(signal == null || signal.getNumber(PosixSignal.Architecture.COMMON) == i);
        }
    }

    @Test
    void testGetCodeCommon() {
        assertEquals(9, PosixSignal.SIGKILL.getNumber(PosixSignal.Architecture.COMMON));
        assertEquals(PosixSignal.Constants.ABSENT, PosixSignal.SIGINFO.getNumber(PosixSignal.Architecture.COMMON));
    }

    @Test
    void testGetCodeOrNullCommon() {
        assertEquals(9, PosixSignal.SIGKILL.getNumberOrNull(PosixSignal.Architecture.COMMON));
        assertNull(PosixSignal.SIGINFO.getNumberOrNull(PosixSignal.Architecture.COMMON));
    }
}
