package hk.ust.cse.castle.toolkit.jvm

/**
 * Enumeration of different magnitudes of data.
 */
enum class ByteUnit(@JvmField val scale: Long) {

    /**
     * Byte.
     */
    BYTE(1),
    /**
     * Kilobyte - Equivalent to 1000 bytes.
     */
    KILOBYTE(PrefixScale.SI.pow(1)),
    /**
     * Kibibyte - Equivalent to 1024 bytes.
     */
    KIBIBYTE(PrefixScale.IEC.pow(1)),
    /**
     * Megabyte - Equivalent to 1000^2 bytes.
     */
    MEGABYTE(PrefixScale.SI.pow(2)),
    /**
     * Mebibyte - Equivalent to 1024^2 bytes.
     */
    MEBIBYTE(PrefixScale.IEC.pow(2)),
    /**
     * Gigabyte - Equivalent to 1000^3 bytes.
     */
    GIGABYTE(PrefixScale.SI.pow(3)),
    /**
     * Gibibyte - Equivalent to 1024^3 bytes.
     */
    GIBIBYTE(PrefixScale.IEC.pow(3)),
    /**
     * Terabyte - Equivalent to 1000^4 bytes.
     */
    TERABYTE(PrefixScale.SI.pow(4)),
    /**
     * Tebibyte - Equivalent to 1024^4 bytes.
     */
    TEBIBYTE(PrefixScale.IEC.pow(4));

    /**
     * Enumeration of different prefix systems.
     */
    enum class PrefixScale(@JvmField val value: Long) {

        /**
         * SI prefix scale, which uses decimal (base-10) counting system.
         */
        SI(1000L),
        /**
         * IEC prefix scale, which uses binary (base-2) counting system.
         */
        IEC(1024L);

        /**
         * Performs integral power on the value represented by this prefix scale.
         *
         * @param v The power to raise this value.
         * @return The result of the power.
         * @throws IllegalArgumentException if the given power is negative, or if the result will overflow a long.
         */
        fun pow(v: Int): Long {
            require(v >= 0) { "Power must be a non-negative number" }

            if (v == 0) {
                return 1
            }

            return when (this) {
                SI -> {
                    (1L until v).fold(value) { acc, _ ->
                        try {
                            Math.multiplyExact(acc, value)
                        } catch (e: ArithmeticException) {
                            throw IllegalArgumentException("Encountered overflow while computing power", e)
                        }
                    }
                }
                IEC -> {
                    require(v <= (Long.SIZE_BITS / 10)) { "Result value cannot fit in long type" }

                    1L shl (10 * v)
                }
            }
        }
    }

    /**
     * Converts the input number into an integral value with the current unit, performing truncation if necessary.
     *
     * @param bytes Number of bytes.
     * @return Equivalent integral number represented by this unit.
     * @see convertReal
     */
    fun convertIntegral(bytes: Long): Long = bytes / scale

    /**
     * Converts the input number into a real value with the current unit, performing truncation if necessary.
     *
     * @param bytes Number of bytes.
     * @return Equivalent real number represented by this unit.
     * @see convertIntegral
     */
    fun convertReal(bytes: Long): Double = bytes.toDouble() / scale


    /**
     * Scales the given number by the amount represented by this unit.
     *
     * @param rawNumber The number of scale by.
     * @return The scaled number.
     * @see scaleReal
     */
    fun scaleIntegral(rawNumber: Int): Long = scale * rawNumber

    /**
     * Scales the given number by the amount represented by this unit.
     *
     * @param rawNumber The number of scale by.
     * @return The scaled number.
     * @see scaleIntegral
     */
    fun scaleReal(rawNumber: Double): Long = (scale * rawNumber).toLong()
}