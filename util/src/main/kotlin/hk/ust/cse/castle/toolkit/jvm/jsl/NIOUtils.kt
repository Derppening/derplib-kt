@file:JvmName("NIOUtils")

package hk.ust.cse.castle.toolkit.jvm.jsl

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

/**
 * Counts the number of lines of the file represented by its [Path].
 *
 * @receiver The path to the input file.
 * @return The number of lines present in the file.
 * @throws IOException If an I/O error occurred while reading the file.
 */
@JvmName("getNumOfLines")
@Throws(IOException::class)
fun Path.numLines(): Long = Files.lines(this).use(Stream<*>::count)