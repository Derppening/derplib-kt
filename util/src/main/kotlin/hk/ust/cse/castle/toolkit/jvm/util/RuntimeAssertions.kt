package hk.ust.cse.castle.toolkit.jvm.util

import kotlin.jvm.Throws

/**
 * Utilities methods for enforcing and/or asserting conditions at runtime.
 */
object RuntimeAssertions {

    /**
     * Throws an [UnreachableStatementError] indicating that an unreachable statement has been reached.
     */
    @JvmStatic
    @Throws(UnreachableStatementError::class)
    fun unreachable(): Nothing = throw UnreachableStatementError()

    /**
     * Throws an [UnreachableStatementError] indicating that an unreachable statement has been reached.
     *
     * @param context Additional context of the unreachable statement, which may include why the statement is
     * unreachable, internal state of the method, etc.
     */
    @JvmStatic
    @Throws(UnreachableStatementError::class)
    fun unreachable(context: String): Nothing = throw UnreachableStatementError(context)
}