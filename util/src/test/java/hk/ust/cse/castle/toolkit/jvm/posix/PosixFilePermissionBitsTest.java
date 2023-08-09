package hk.ust.cse.castle.toolkit.jvm.posix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PosixFilePermissionBitsTest {

    @Test
    void testCreateInstanceFromTripleInts() {
        final PosixFilePermissionBits permissions = new PosixFilePermissionBits(7, 7, 5);

        assertEquals(7, permissions.getOwnerMode().getValue());
        assertEquals(7, permissions.getGroupMode().getValue());
        assertEquals(5, permissions.getOthersMode().getValue());
    }

    // TODO: Add more test cases
}
