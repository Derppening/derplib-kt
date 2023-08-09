package hk.ust.cse.castle.toolkit.jvm.posix

import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions

/**
 * Class representing the octal/textual value of file permissions.
 *
 * @param value The value of the file permission bits; Must be between 0 and `(1 << 9) - 1`.
 */
class PosixFilePermissionBits(val value: Int) {

    /**
     * Class representing the triad of bits for each group of permission targets.
     *
     * @param value The value of the triad; Must be between 0 and 7.
     */
    class Triad(val value: Int) {

        init {
            require(value in 0..7)
        }

        /**
         * @see read
         */
        val r get() = read

        /**
         * @see write
         */
        val w get() = write

        /**
         * @see execute
         */
        val x get() = execute

        /**
         * Whether the read bit is set.
         */
        val read: Boolean get() = (value and (1 shl 2)) != 0
        /**
         * Whether the write bit is set.
         */
        val write: Boolean get() = (value and (1 shl 1)) != 0
        /**
         * Whether the execute bit is set.
         */
        val execute: Boolean get() = (value and (1 shl 0)) != 0

        /**
         * Converts the value of this instance into an octal string.
         */
        fun toOctalString(): String = Integer.toOctalString(value).padStart(1, '0')

        /**
         * Converts the value of this instance into a textual string.
         */
        fun toTextString(): String = buildString(3) {
            append(if (r) 'r' else '-')
            append(if (w) 'w' else '-')
            append(if (x) 'x' else '-')
        }

        companion object {

            /**
             * Bitmask for a single triad.
             */
            const val BITMASK = ((1 shl 3) - 1)
        }
    }

    /**
     * Creates a [PosixFilePermissionBits] instance from user, group and other bits.
     */
    constructor(u: Int, g: Int, o: Int) : this(
        ((u and Triad.BITMASK) shl OWNER_MODE_SHIFT) or
                ((g and Triad.BITMASK) shl GROUP_MODE_SHIFT) or
                ((o and Triad.BITMASK) shl OTHERS_MODE_SHIFT)
    )

    /**
     * Creates a [PosixFilePermissionBits] instance from user, group and other [Triad].
     */
    constructor(u: Triad, g: Triad, o: Triad) : this(u.value, g.value, o.value)

    init {
        require(value in 0 until (1 shl 9))
    }

    /**
     * The bit triad representing the owner permissions.
     */
    val ownerMode get() = Triad((value shr OWNER_MODE_SHIFT) and Triad.BITMASK)
    /**
     * The bit triad representing the group permissions.
     */
    val groupMode get() = Triad((value shr GROUP_MODE_SHIFT) and Triad.BITMASK)
    /**
     * The bit triad representing other permissions.
     */
    val othersMode get() = Triad((value shr OTHERS_MODE_SHIFT) and Triad.BITMASK)

    /**
     * @return Whether the bit representing the [permission] is set.
     */
    fun hasPermission(permission: PosixFilePermission): Boolean = (value and permission.bitmask) != 0

    /**
     * Converts the value of this instance into an octal string.
     */
    fun toOctalString(): String = arrayOf(ownerMode, groupMode, othersMode).joinToString("") { it.toOctalString() }

    /**
     * Converts the value of this instance into a textual string.
     */
    fun toTextString(): String = PosixFilePermissions.toString(toPosixFilePermissions())

    /**
     * Converts the value of this instance into a [Set] of [PosixFilePermission].
     */
    fun toPosixFilePermissions(): Set<PosixFilePermission> = PosixFilePermissions(value)

    companion object {

        private const val OWNER_MODE_SHIFT = 6
        private const val GROUP_MODE_SHIFT = 3
        private const val OTHERS_MODE_SHIFT = 0
    }
}
