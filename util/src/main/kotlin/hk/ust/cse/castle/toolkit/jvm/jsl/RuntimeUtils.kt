package hk.ust.cse.castle.toolkit.jvm.jsl

import hk.ust.cse.castle.toolkit.jvm.ByteUnit
import hk.ust.cse.castle.toolkit.jvm.util.JavaApi
import java.text.MessageFormat
import java.util.function.Consumer
import kotlin.concurrent.thread

/**
 * Utility methods for [Runtime].
 */
object RuntimeUtils {

    /**
     * Prints the current memory usage.
     *
     * This method exists for compatibility with Java API users. Kotlin users should prefer to use the
     * [extension method][Runtime.printMemoryUsage] instead.
     *
     * @param outputter The method to use when outputting memory information. The method should be responsible for
     * inserting newlines (`\n`) between each invocation.
     * @see Runtime.printMemoryUsage
     */
    @JavaApi(ReplaceWith("Runtime.printMemoryUsage(outputter)"))
    @JvmStatic
    @JvmOverloads
    fun printMemoryUsage(outputter: Consumer<String> = Consumer { println(it) }) {
        jvmRuntime.printMemoryUsage(outputter::accept)
    }
}

/**
 * Retrieves the current JVM runtime as if by calling [Runtime.getRuntime].
 */
val jvmRuntime: Runtime get() = Runtime.getRuntime()

/**
 * Adds a shutdown hook to the [Runtime], using the given thread creation properties.
 *
 * @see Runtime.addShutdownHook
 */
fun Runtime.addShutdownHook(
    isDaemon: Boolean = false,
    contextClassLoader: ClassLoader? = null,
    name: String? = null,
    priority: Int = -1,
    block: () -> Unit
) = addShutdownHook(thread(start = false, isDaemon, contextClassLoader, name, priority, block))

/**
 * Memory usage information.
 *
 * @property used The memory used by the JVM in MiB.
 * @property total The total memory available to the JVM in MiB.
 * @property allocated The memory allocated by JVM in MiB.
 */
data class MemoryUsageInfo(
    val used: Long,
    val total: Long,
    val allocated: Long
) {

    /**
     * The percent of memory used by the JVM over the available memory.
     */
    val usedPercent = (used.toDouble() / total.toDouble() * 100.0)

    /**
     * The percent of memory allocated by the JVM over the total available memory.
     */
    val allocatedPercent = (total.toDouble() / allocated.toDouble() * 100.0)
}

/**
 * Collects the current memory usage of the JVM.
 */
fun memoryUsage(): MemoryUsageInfo {
    val runtime = jvmRuntime
    val free = runtime.freeMemory()
    val total = runtime.totalMemory()
    val allocated = runtime.maxMemory()

    return MemoryUsageInfo(total - free, total, allocated)
}

/**
 * Prints the current memory usage
 *
 * @param outputter The method to use when outputting memory information. The method should be responsible for
 * inserting newlines (`\n`) between each invocation.
 */
fun Runtime.printMemoryUsage(outputter: (String) -> Unit = ::println) {
    val freeMemory = ByteUnit.MEBIBYTE.convertIntegral(this.freeMemory())
    val totalMemory = ByteUnit.MEBIBYTE.convertIntegral(this.totalMemory())
    val maxMemory = ByteUnit.MEBIBYTE.convertIntegral(this.maxMemory())
    val usedMemory = totalMemory - freeMemory

    val usedRatio = usedMemory * 100 / maxMemory
    val allocatedRatio = totalMemory * 100 / maxMemory

    outputter("JVM Memory Usage:")
    outputter(MessageFormat.format("    Used: {0}/{1} MiB ({2}%)", usedMemory, maxMemory, usedRatio))
    outputter(MessageFormat.format("    Allocated: {0} MiB ({1} %)", totalMemory, allocatedRatio))
}
