package hk.ust.cse.castle.toolkit.jvm

import org.jetbrains.annotations.MustBeInvokedByOverriders
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

/**
 * A thread factory with custom name prefixes.
 *
 * All threads created from this thread factory with have their name as `<prefix>-<poolNum>-thread-<threadNum>`, where:
 *
 * - `<prefix>` is the name prefix of the thread factory supplied to the constructor.
 * - `<poolNum>` is the numeric index of this factory, starting from 1.
 * - `<threadNum>` is the numeric index of the thread created by this factory, starting from 1.
 *
 * @param threadPoolNamePrefix The name prefix of this thread factory.
 */
open class NamedThreadFactory(
    val threadPoolNamePrefix: String
) : ThreadFactory {

    init {
        globalPoolCount.putIfAbsent(threadPoolNamePrefix, AtomicInteger())
    }

    private val globalPoolIdx = checkNotNull(globalPoolCount[threadPoolNamePrefix]).incrementAndGet()
    private val localThreadCount = AtomicInteger()

    @MustBeInvokedByOverriders
    override fun newThread(runnable: Runnable): Thread {
        val localThreadIdx = localThreadCount.incrementAndGet()
        val threadName = "$threadPoolNamePrefix-$globalPoolIdx-thread-$localThreadIdx"

        return thread(
            start = false,
            name = threadName,
            block = runnable::run
        )
    }

    companion object {

        private val globalPoolCount: ConcurrentMap<String, AtomicInteger> = ConcurrentHashMap()
    }
}