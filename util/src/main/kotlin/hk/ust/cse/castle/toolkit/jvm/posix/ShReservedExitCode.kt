package hk.ust.cse.castle.toolkit.jvm.posix

/**
 * Exit codes reserved by the `sh` family of shells.
 */
enum class ShReservedExitCode(@JvmField val code: Int) {

    /**
     * General errors.
     */
    GENERAL_ERRORS(1),
    /**
     * Misuse of shell builtins.
     */
    MISUSE_SHELL_BUILTINS(2),
    /**
     * Cannot execute the invoked command.
     */
    CANNOT_EXECUTE(126),
    /**
     * Command not found.
     */
    COMMAND_NOT_FOUND(127),
    /**
     * Invalid exit code (usually due to mismatching data types).
     */
    INVALID_EXIT_CODE(128),
    /**
     * Exit code out-of-range.
     */
    EXIT_CODE_OUT_OF_RANGE(255);

    /**
     * Returns a description of this error code.
     *
     * This list is taken from [here](https://tldp.org/LDP/abs/html/exitcodes.html).
     */
    fun toDescriptiveString(): String {
        return when (this) {
            GENERAL_ERRORS -> "Catchall for general errors"
            MISUSE_SHELL_BUILTINS -> "Misuse of shell builtins"
            CANNOT_EXECUTE -> "Command invoked cannot execute"
            COMMAND_NOT_FOUND -> "\"command not found\""
            INVALID_EXIT_CODE -> "Invalid argument to exit"
            EXIT_CODE_OUT_OF_RANGE -> "Exit status out of range"
        }
    }

    companion object {

        /**
         * Resolves the given [code] as a [ShReservedExitCode].
         *
         * @return The [ShReservedExitCode] instance representing this exit code, or `null` if the provided code does
         * not match any reserved exit code.
         */
        @JvmStatic
        fun resolveExitCode(code: Int): ShReservedExitCode? = values().singleOrNull { it.code == code }
    }
}