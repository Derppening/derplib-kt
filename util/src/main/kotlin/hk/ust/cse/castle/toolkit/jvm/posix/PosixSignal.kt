package hk.ust.cse.castle.toolkit.jvm.posix

/**
 * Signals as defined in the POSIX standard.
 *
 * **IMPORTANT:** Do NOT use the ordinals of the enumeration to determine/lookup the signal number of the signal. Use
 * [PosixSignal.getNumber] and [PosixSignal.getBySignalNumber] instead.
 *
 * @property common Signal number for most common architectures; Corresponds to [Architecture.COMMON].
 * @property alpha Signal number for Alpha; Corresponds to [Architecture.ALPHA].
 * @property sparc Signal number for SPARC; Corresponds to [Architecture.SPARC].
 * @property mips Signal number for MIPS; Corresponds to [Architecture.MIPS].
 * @property parisc Signal number for PARISC; Corresponds to [Architecture.PARISC].
 */
enum class PosixSignal(
    private val common: Int,
    private val alpha: Int,
    private val sparc: Int,
    private val mips: Int,
    private val parisc: Int,
) {

    SIGHUP(1),
    SIGINT(2),
    SIGQUIT(3),
    SIGILL(4),
    SIGTRAP(5),
    SIGABRT(6),
    SIGIOT(6),
    SIGBUS(7, 10, 10, 10),
    SIGEMT(Constants.ABSENT, 7, 7, Constants.ABSENT),
    SIGFPE(8),
    SIGKILL(9),
    SIGUSR1(10, 30, 16, 16),
    SIGSEGV(11),
    SIGUSR2(12, 31, 17, 17),
    SIGPIPE(13),
    SIGALRM(14),
    SIGTERM(15),
    SIGSTKFLT(16, Constants.ABSENT, Constants.ABSENT, 7),
    SIGCHLD(17, 20, 18, 18),
    SIGCLD(Constants.ABSENT, Constants.ABSENT, 18, Constants.ABSENT),
    SIGCONT(18, 19, 25, 26),
    SIGSTOP(19, 17, 23, 24),
    SIGTSTP(20, 18, 24, 25),
    SIGTTIN(21, 21, 26, 27),
    SIGTTOU(22, 22, 27, 28),
    SIGURG(23, 16, 21, 29),
    SIGXCPU(24, 24, 30, 12),
    SIGXFSZ(25, 25, 31, 30),
    SIGVTALRM(26, 26, 28, 20),
    SIGPROF(27, 27, 29, 21),
    SIGWINCH(28, 28, 20, 23),
    SIGIO(29, 23, 22, 22),
    SIGPOLL(SIGIO),
    SIGPWR(30, 29, Constants.ABSENT, 19, 19),
    SIGINFO(Constants.ABSENT, 29, Constants.ABSENT, Constants.ABSENT, Constants.ABSENT),
    SIGLOST(Constants.ABSENT, Constants.ABSENT, 29, Constants.ABSENT, Constants.ABSENT),
    SIGSYS(31, 12, 12, 31),
    SIGUNUSED(31, Constants.ABSENT, Constants.ABSENT, 31);

    /**
     * A family of system architectures.
     *
     * Each enumeration value corresponds to a family of architectures with a different signal numbering system.
     */
    enum class Architecture {

        /**
         * x86, ARM, and most other architectures.
         */
        COMMON,

        /**
         * Alpha architecture.
         */
        ALPHA,

        /**
         * SPARC architecture.
         */
        SPARC,

        /**
         * MIPS architecture.
         */
        MIPS,

        /**
         * PARISC architecture.
         */
        PARISC;

        companion object {

            /**
             * Mapping of `os.arch` property values (from JVM) to the [Architecture] instance.
             */
            private val ARCH_PLATFORM_MAPPING = mapOf(
                "arm" to COMMON,
                "amd64" to COMMON
            )

            /**
             * @return The [Architecture] of the current machine, or `null` if the architecture is not known.
             * @see current
             */
            @JvmStatic
            fun currentOrNull(): Architecture? = ARCH_PLATFORM_MAPPING[System.getProperty("os.arch")]

            /**
             * @return The [Architecture] of the current machine.
             * @throws RuntimeException if the architecture is not known.
             * @see currentOrNull
             */
            @JvmStatic
            fun current(): Architecture {
                return currentOrNull() ?: run {
                    val arch = System.getProperty("os.arch")
                    throw RuntimeException("Unknown platform \"$arch\" - Please file a bug report and use PosixSignal.forPlatform(...)")
                }
            }
        }
    }

    object Constants {

        /**
         * Number representing that the associated signal is absent from the architecture.
         */
        const val ABSENT = -1
    }

    constructor(alias: PosixSignal) : this(
        common = alias.common,
        alpha = alias.alpha,
        sparc = alias.sparc,
        mips = alias.mips,
        parisc = alias.parisc
    )

    constructor(common: Int, alphaAndSparc: Int, mips: Int, parisc: Int) : this(
        common = common,
        alpha = alphaAndSparc,
        sparc = alphaAndSparc,
        mips = mips,
        parisc = parisc
    )

    constructor(commonValue: Int) : this(
        common = commonValue,
        alphaAndSparc = commonValue,
        mips = commonValue,
        parisc = commonValue
    )

    /**
     * @param architecture The architecture which to return the signal number for. Defaults to the
     * [current architecture][Architecture.current].
     * @return The signal number of this signal for the provided [architecture], or [Constants.ABSENT] if the signal is
     * absent from the architecture.
     * @see getNumberOrNull
     */
    @JvmOverloads
    fun getNumber(architecture: Architecture = Architecture.current()): Int {
        return when (architecture) {
            Architecture.COMMON -> common
            Architecture.ALPHA -> alpha
            Architecture.SPARC -> sparc
            Architecture.MIPS -> mips
            Architecture.PARISC -> parisc
        }
    }

    /**
     * @param architecture The architecture which to return the signal number for. Defaults to the
     * [current architecture][Architecture.current].
     * @return The signal number of this signal for the provided [architecture], or `null` if the signal is
     * absent from the architecture.
     * @see getNumber
     */
    @JvmOverloads
    fun getNumberOrNull(architecture: Architecture = Architecture.current()): Int? =
        getNumber(architecture).takeIf { it != Constants.ABSENT }

    /**
     * Returns a description of this signal.
     *
     * Taken from `signal(7)` manpage on Linux.
     */
    fun toDescriptiveString(): String {
        return when (this) {
            SIGHUP -> "Hangup detected on controlling terminal or death of controlling process"
            SIGINT -> "Interrupt from keyboard"
            SIGQUIT -> "Quit from keyboard"
            SIGILL -> "Illegal Instruction"
            SIGTRAP -> "Trace/breakpoint trap"
            SIGABRT -> "Abort signal from abort"
            SIGIOT -> "IOT trap. A synonym for SIGABRT"
            SIGBUS -> "Bus error (bad memory access)"
            SIGEMT -> "Emulator trap"
            SIGFPE -> "Floating-point exception"
            SIGKILL -> "Kill signal"
            SIGUSR1 -> "User-defined signal 1"
            SIGSEGV -> "Invalid memory reference"
            SIGUSR2 -> "User-defined signal 2"
            SIGPIPE -> "Broken pipe: write to pipe with no readers"
            SIGALRM -> "Timer signal from alarm"
            SIGTERM -> "Termination signal"
            SIGSTKFLT -> "Stack fault on coprocessor"
            SIGCHLD, SIGCLD -> "Child stopped or terminated"
            SIGCONT -> "Continue if stopped"
            SIGSTOP -> "Stop process"
            SIGTSTP -> "Stop typed at terminal"
            SIGTTIN -> "Terminal input for background process"
            SIGTTOU -> "Terminal output for background process"
            SIGURG -> "Urgent condition on socket"
            SIGXCPU -> "CPU time limit exceeded"
            SIGXFSZ -> "File size limit exceeded"
            SIGVTALRM -> "Virtual alarm clock"
            SIGPROF -> "Profiling timer expired"
            SIGWINCH -> "Window resize signal"
            SIGIO -> "I/O now possible"
            SIGPOLL -> "Pollable event"
            SIGPWR, SIGINFO -> "Power failure"
            SIGLOST -> "File lock lost"
            SIGSYS, SIGUNUSED -> "Bad system call"
        }
    }

    companion object {

        /**
         * Generates a signal table for the given architecture as an array.
         *
         * The returned array can be used to directly retrieve the corresponding [PosixSignal] by indexing using the
         * signal number, for example `PosixSignal.forPlatform()[1]` will return [SIGHUP].
         *
         * Note that since some signals are aliases to others, the signal returned by this method will be the more
         * common alias of the signal.
         *
         * @param architecture The architecture which to generate the signal table for. Defaults to the
         * [current architecture][Architecture.current].
         * @return An array of all [PosixSignal] for the [architecture], indexed by their signal number.
         */
        @JvmStatic
        @JvmOverloads
        fun forPlatform(architecture: Architecture = Architecture.current()): Array<PosixSignal?> =
            Array(32) { signalNo -> getBySignalNumber(signalNo, architecture) }

        /**
         * Returns the [PosixSignal] represented by [signalNo] for the [architecture], or `null` if the signal number
         * has no corresponding signal for the architecture.
         *
         * Note that since some signals are aliases to others, the signal returned by this method will be the more
         * common alias of the signal.
         */
        @JvmStatic
        @JvmOverloads
        fun getBySignalNumber(signalNo: Int, architecture: Architecture = Architecture.current()): PosixSignal? =
            values().firstOrNull { it.getNumber(architecture) == signalNo }
    }
}