package hk.ust.cse.castle.toolkit.jvm.jsl

import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.function.Predicate
import kotlin.io.path.isHidden

/**
 * Utility class which collects all files within a directory matching a specific predicate.
 *
 * @param root The root directory of this collector.
 * @param skipHidden Whether to skip hidden directories or files.
 */
open class PredicatedFileCollector @JvmOverloads constructor(
    root: Path,
    private val skipHidden: Boolean = true
) {

    /**
     * [SimpleFileVisitor] implementation for this collector.
     *
     * @param predicate Predicate used for filtering entries.
     */
    protected inner class Visitor(
        private val predicate: Predicate<Path>
    ) : SimpleFileVisitor<Path>() {

        /**
         * [List] of all [Path] matching the given predicate.
         */
        private val _matchedPaths = mutableListOf<Path>()
        val matchedPaths: List<Path> get() = _matchedPaths.toList()

        override fun preVisitDirectory(path: Path, basicFileAttributes: BasicFileAttributes): FileVisitResult {
            val superclassResult = super.preVisitDirectory(path, basicFileAttributes)
            return visitPath(path, superclassResult)
        }

        override fun visitFile(path: Path, basicFileAttributes: BasicFileAttributes): FileVisitResult {
            val superclassResult = super.visitFile(path, basicFileAttributes)
            return visitPath(path, superclassResult)
        }

        /**
         * Visits a path, regardless of whether it is a directory or file.
         *
         * This method is invoked on every visited directory and file, in order to determine whether the path should
         * be 1. skipped (or in the case of a directory, the path and all its children should be skipped), and 2.
         * added to the [_matchedPaths] list.
         *
         * Note that if [PredicatedFileCollector.skipHidden] is set to `true`, this method will always return
         * [FileVisitResult.SKIP_SUBTREE] for hidden paths, even if this is ignored for files. This is to allow derived
         * implementations to handle hidden paths differently than non-hidden paths.
         *
         * @param path The path being visited.
         * @param defaultResult The default [FileVisitResult] to return if the path is visited with no errors.
         * @return [FileVisitResult.SKIP_SUBTREE] if [PredicatedFileCollector.skipHidden] is specified and the path is
         * hidden; Otherwise returns [defaultResult].
         * @throws IOException If an I/O exception occurred while visiting the path.
         */
        @Throws(IOException::class)
        private fun visitPath(path: Path, defaultResult: FileVisitResult): FileVisitResult {
            if (skipHidden && path.isHidden()) {
                return FileVisitResult.SKIP_SUBTREE
            }

            if (predicate.test(path)) {
                _matchedPaths.add(path)
            }

            return defaultResult
        }
    }

    /**
     * The root directory of this collector.
     */
    private val root = root.toAbsolutePath().normalize()

    /**
     * Collects a [List] of all subdirectories and files within the root directory which matches the given
     * predicate.
     *
     * Whether hidden files are collected depends on whether [skipHidden] is specified when constructing this class
     * instance.
     *
     * @param predicate The predicate to execute on every directory and file within the root directory.
     * @return List of all files within the root directory matching the given predicate.
     * @throws IOException If an I/O error is encountered while executing walking the directory tree.
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun collect(predicate: (Path) -> Boolean = { true }): List<Path> {
        val visitor = Visitor(predicate)
        Files.walkFileTree(root, visitor)
        return Collections.unmodifiableList(visitor.matchedPaths)
    }
}