package hk.ust.cse.castle.toolkit.jvm.posix

import java.nio.file.Path
import kotlin.io.path.*

/**
 * Wrapper class for creating and managing `/dev/shm` entries.
 *
 * @param name The name of the ramdisk to create.
 * @param subpath The subdirectory under `/dev/shm` to create the directory in. Must be a location under `/dev/shm` or
 * a relative path.
 */
class LinuxShm(name: String, subpath: Path? = null) : AutoCloseable {

    init {
        check(isAvailable) { "/dev/shm is not present in the system" }

        require(subpath == null || !subpath.isAbsolute || subpath.startsWith(SHM_ROOT))
    }

    /**
     * The directory created in `/dev/shm`.
     */
    val directory: Path = SHM_ROOT
        .let { root ->
            subpath?.let { root.resolve(it) } ?: root
        }
        .resolve(name)

    init {
        if (directory.exists()) {
            throw RuntimeException("Target directory already exists")
        }

        directory.createDirectories()
        directory.setPosixFilePermissions(MODE_770)
    }

    private val symlinks = mutableSetOf<Path>()

    /**
     * Creates a symlink located in pointing to this shared memory.
     *
     * @param location The path to create the symlink in.
     * @param subpath The subpath within this shared memory directory to point the symlink to.
     * @return [location]
     */
    fun createSymlink(location: Path, subpath: Path = Path("")): Path {
        require(location.notExists())
        require(directory.resolve(subpath).startsWith(directory))

        val symlinkPath = location.createSymbolicLinkPointingTo(directory.resolve(subpath))
        symlinks.add(location)
        return symlinkPath
    }

    /**
     * Removes a symlink pointing to this shared memory.
     *
     * @param location The path to the symlink for deletion.
     * @return `true` if the symlink is deleted, or `false` if the symlink cannot be found or deleted.
     */
    fun removeSymlink(location: Path): Boolean {
        if (location.notExists() || !location.isSymbolicLink()) {
            symlinks.remove(location)
            return false
        }

        val isDeleted = location.deleteIfExists()
        if (isDeleted) {
            symlinks.remove(location)
        }
        return isDeleted
    }

    override fun equals(other: Any?): Boolean = directory == (other as? LinuxShm)?.directory
    override fun hashCode(): Int = directory.hashCode()

    /**
     * Closes this resource.
     *
     * Removes all symbolic links pointing at the `shm` directory created by this instance, and deletes the directory.
     *
     * WARNING: If this method throws a [RuntimeException], it is not advisable to catch the exception and keep running.
     * This is because there will be no mechanism to clear the contents of the [directory], and since the directory
     * resides in RAM, it may eventually lead to memory exhaustion!
     *
     * @see AutoCloseable.close
     * @throws RuntimeException if the directory cannot be deleted.
     */
    override fun close() {
        symlinks.forEach { it.deleteIfExists() }
        if (!directory.toFile().deleteRecursively()) {
            throw RuntimeException("Unable to delete $directory - This may cause a memory leak!")
        }
    }

    companion object {

        /**
         * Root directory of Linux's `shm`.
         */
        val SHM_ROOT = Path("/dev/shm")

        private val MODE_770 = PosixFilePermissions(7, 7, 0)

        /**
         * Whether `/dev/shm` is present on the system.
         */
        val isAvailable: Boolean by lazy { SHM_ROOT.exists() }
    }
}