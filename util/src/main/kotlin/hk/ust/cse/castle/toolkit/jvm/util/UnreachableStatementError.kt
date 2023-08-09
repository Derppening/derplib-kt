package hk.ust.cse.castle.toolkit.jvm.util

import java.lang.AssertionError

/**
 * An [AssertionError] indicating that an unreachable statement has been reached.
 *
 * @param context Contextual information to understand the error.
 */
class UnreachableStatementError @JvmOverloads constructor(
    context: String = "Control flow reached an unreachable statement"
) : AssertionError(context)