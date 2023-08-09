@file:JvmName("PosixFilePermissionUtils")

package hk.ust.cse.castle.toolkit.jvm.posix

import java.nio.file.attribute.PosixFilePermission

/**
 * The bitmask for this bit of [PosixFilePermission].
 */
val PosixFilePermission.bitmask: Int
    get() = when (this) {
        PosixFilePermission.OWNER_READ -> 8
        PosixFilePermission.OWNER_WRITE -> 7
        PosixFilePermission.OWNER_EXECUTE -> 6
        PosixFilePermission.GROUP_READ -> 5
        PosixFilePermission.GROUP_WRITE -> 4
        PosixFilePermission.GROUP_EXECUTE -> 3
        PosixFilePermission.OTHERS_READ -> 2
        PosixFilePermission.OTHERS_WRITE -> 1
        PosixFilePermission.OTHERS_EXECUTE -> 0
    }.let { 1 shl it }

/**
 * Creates a mode set of [PosixFilePermission] using an octal mode.
 */
@JvmName("fromMode")
@Suppress("FunctionName")
fun PosixFilePermissions(mode: Int): Set<PosixFilePermission> {
    return PosixFilePermission.values()
        .fold(setOf()) { acc, it ->
            if ((mode and it.bitmask) > 0) {
                acc + it
            } else {
                acc
            }
        }
}

/**
 * Creates a mode set of [PosixFilePermission] using the modes of owner, group and others.
 */
@JvmName("fromModes")
@Suppress("FunctionName")
fun PosixFilePermissions(ownerMode: Int, groupMode: Int, othersMode: Int): Set<PosixFilePermission> {
    return PosixFilePermissionBits(ownerMode, groupMode, othersMode).toPosixFilePermissions()
}