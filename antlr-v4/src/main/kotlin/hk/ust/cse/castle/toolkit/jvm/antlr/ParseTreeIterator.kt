package hk.ust.cse.castle.toolkit.jvm.antlr

import org.antlr.v4.runtime.tree.ParseTree
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * An [Iterator] which iterates nodes within a [ParseTree].
 */
class ParseTreeIterator(
    root: ParseTree,
    @JvmField val order: Order = Order.DEFAULT
) : Iterator<ParseTree> {

    private val deque = ArrayDeque(listOf(root))

    /**
     * The order in which to traverse the [ParseTree].
     */
    enum class Order {

        /**
         * Depth-first traversal.
         */
        DEPTH_FIRST,

        /**
         * Breadth-first traversal.
         */
        BREADTH_FIRST;

        companion object {

            @JvmField
            val DEFAULT = DEPTH_FIRST
        }
    }

    override fun hasNext(): Boolean = deque.isNotEmpty()

    override fun next(): ParseTree {
        val elem: ParseTree

        when (order) {
            Order.DEPTH_FIRST -> {
                elem = deque.removeLast()

                deque.addAll(elem.children.asReversed())
            }
            Order.BREADTH_FIRST -> {
                elem = deque.removeFirst()

                deque.addAll(elem.children)
            }
        }

        return elem
    }

    companion object {

        /**
         * Creates a [ParseTreeIterator].
         *
         * @receiver The root node to start traversing from.
         * @param order The order to traverse the tree in.
         * @return The created iterator.
         */
        @JvmStatic
        @JvmOverloads
        @JvmName("create")
        fun ParseTree.iterator(order: Order = Order.DEFAULT): ParseTreeIterator =
            ParseTreeIterator(root, order)

        /**
         * Creates an [Iterable] which wraps around the [ParseTreeIterator].
         *
         * @receiver The root node to start traversing from.
         * @param order The order to traverse the tree in.
         * @return The created iterable.
         */
        @JvmStatic
        @JvmOverloads
        @JvmName("createIterable")
        fun ParseTree.iterable(order: Order = Order.DEFAULT): Iterable<ParseTree> =
            Iterable { iterator(order) }

        /**
         * Creates a [Stream] of nodes using a [ParseTreeIterator].
         *
         * @receiver The root node to start traversing from.
         * @param order The order to traverse the tree in.
         * @return The created stream.
         */
        @JvmStatic
        @JvmOverloads
        fun ParseTree.asStream(order: Order = Order.DEFAULT): Stream<ParseTree> =
            StreamSupport.stream(iterable(order).spliterator(), false)

        /**
         * Creates a [Sequence] of nodes using a [ParseTreeIterator].
         *
         * @receiver The root node to start traversing from.
         * @param order The order to traverse the tree in.
         * @return The created sequence.
         */
        @JvmStatic
        @JvmOverloads
        fun ParseTree.asSequence(order: Order = Order.DEFAULT): Sequence<ParseTree> =
            iterable(order).asSequence()
    }
}