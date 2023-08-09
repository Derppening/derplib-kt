@file:JvmName("ANTLRUtils")

package hk.ust.cse.castle.toolkit.jvm.antlr

import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.ParseTreeListener
import org.antlr.v4.runtime.tree.ParseTreeWalker
import java.util.*
import java.util.stream.Stream
import kotlin.streams.toList

/**
 * Gets all children of a [ParseTree] as a [List].
 *
 * @receiver The input parse tree.
 * @return List containing the children of the input parse tree.
 */
val ParseTree.children: List<ParseTree>
    get() = Collections.unmodifiableList(List(childCount) { getChild(it) })

/**
 * Gets all ancestors of a [ParseTree] as a [Stream].
 *
 * @receiver The input parse tree.
 * @return [Stream] containing all ancestor [ParseTree]. The first element will be the direct ancestor of the input.
 */
fun ParseTree.getAncestorsAsStream(): Stream<ParseTree> {
    val builder = Stream.builder<ParseTree>()

    var parent = this
    while (parent.parent != null) {
        parent = parent.parent
        builder.accept(parent)
    }

    return builder.build()
}

/**
 * Gets all ancestors of a [ParseTree] as a [List].
 *
 * @receiver The input parse tree.
 * @return [List] containing all ancestor [ParseTree]. The first element will be the direct ancestor of the input.
 */
fun ParseTree.getAncestors(): List<ParseTree> = Collections.unmodifiableList(getAncestorsAsStream().toList())

/**
 * Gets the root node of a [ParseTree].
 *
 * @receiver The input parse tree.
 * @return The root node of the input parse tree.
 */
val ParseTree.root: ParseTree
    get() = getAncestors().lastOrNull() ?: this

/**
 * Walks over a [ParseTree] with a given [ParseTreeListener].
 *
 * @receiver The parse tree to walk over.
 * @param listener The listener object to execute on every node.
 * @return [listener].
 */
fun <T : ParseTreeListener> ParseTree.walkWithListener(listener: T): T {
    ParseTreeWalker.DEFAULT.walk(listener, this)
    return listener
}

/**
 * Walks over a [ParseTree] with a given [ParseTreeListener].
 *
 * @receiver The parse tree to walk over.
 * @param listenerCreator A lambda returning an instance of [ParseTreeListener].
 * @return The [ParseTreeListener] created by [listenerCreator].
 */
fun <T : ParseTreeListener> ParseTree.walkWithListener(listenerCreator: () -> T): T =
    walkWithListener(listenerCreator())
