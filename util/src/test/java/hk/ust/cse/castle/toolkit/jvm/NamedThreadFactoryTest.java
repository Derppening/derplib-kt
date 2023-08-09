package hk.ust.cse.castle.toolkit.jvm;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NamedThreadFactoryTest {

    @NotNull
    private static final String poolLocalTestPrefix = "NamedThreadFactoryTest.PoolLocal";
    @NotNull
    private static final String poolGlobalTestPrefix = "NamedThreadFactoryTest.PoolGlobal";

    private ThreadFactory threadFactory;

    @Test
    void testPoolLocalIndexIncrements() throws InterruptedException {
        threadFactory = new NamedThreadFactory(poolLocalTestPrefix);

        // Loop starts from 1 to align with pool-local thread index
        for (int i = 1; i <= 10; ++i) {
            final Thread thread = threadFactory.newThread(() -> {});
            final String expectedThreadName = poolLocalTestPrefix + "-1-thread-" + i;

            assertEquals(expectedThreadName, thread.getName());

            // We expect this to never happen, but just for safety.
            if (thread.isAlive()) {
                thread.join();
            }
        }
    }

    @Test
    void testPoolGlobalIndexIncrements() throws InterruptedException {
        // Loop starts from 1 to align with global pool index
        for (int i = 1; i <= 10; ++i) {
            threadFactory = new NamedThreadFactory(poolGlobalTestPrefix);

            final Thread thread = threadFactory.newThread(() -> {});
            final String expectedThreadName = poolGlobalTestPrefix + "-" + i + "-thread-1";

            assertEquals(expectedThreadName, thread.getName());

            // We expect this to never happen, but just for safety.
            if (thread.isAlive()) {
                thread.join();
            }
        }
    }

    @AfterEach
    void tearDown() {
        threadFactory = null;
    }
}
