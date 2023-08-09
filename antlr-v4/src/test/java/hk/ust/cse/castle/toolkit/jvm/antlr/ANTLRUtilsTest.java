package hk.ust.cse.castle.toolkit.jvm.antlr;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.tree.TerminalNodeImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ANTLRUtilsTest {

    /**
     * Helper method which adds a child {@link ParseTree} to a {@link ParserRuleContext} parent.
     *
     * <p>
     *     This method does two things: It adds the child to the parent, and sets the child's parent to the parent.
     * </p>
     *
     * @param parent The parent node.
     * @param child The child node.
     */
    private void addChildToParent(@NotNull final ParserRuleContext parent, @NotNull final ParseTree child) {
        parent.addAnyChild(child);
        child.setParent(parent);
    }

    /**
     * Creates a tree for testing retrieving ancestors.
     *
     * <p>
     *     The created tree is a single-descendent tree with the given number of levels as its height.
     * </p>
     *
     * @param expectedAncestors A {@link List} to store the sequence of expected ancestors, direct-parent first.
     *                          The list must be mutable.
     * @param height The height of the ancestor test tree.
     * @return The singular leaf node of the tree.
     */
    @NotNull
    private ParseTree createAncestorTestTree(@NotNull final List<ParseTree> expectedAncestors, int height) {
        expectedAncestors.clear();

        @NotNull ParserRuleContext currentNode = new ParserRuleContext();
        expectedAncestors.add(currentNode);

        for (int i = 0; i < height - 1; ++i) {
            final ParserRuleContext newNode = new ParserRuleContext();
            addChildToParent(currentNode, newNode);
            currentNode = newNode;

            expectedAncestors.add(newNode);
        }
        final TerminalNode leafNode = new TerminalNodeImpl(new CommonToken(Token.INVALID_TYPE));
        addChildToParent(currentNode, leafNode);

        // Reverse the expectedAncestors list, since this list is root-first whereas getAncestors returns direct
        // parent-first
        Collections.reverse(expectedAncestors);

        return leafNode;
    }

    /**
     * Creates a {@link ParseTree} which looks like this:
     * <code>
     *     *
     *    / \
     *   *   *
     *  / \
     * *   *
     * </code>
     *
     * @return The parse tree.
     */
    @NotNull
    private ParseTree createMockTree() {
        final ParserRuleContext root = new ParserRuleContext();
        final ParserRuleContext child1 = new ParserRuleContext();
        child1.addChild(new TerminalNodeImpl(new CommonToken(Token.INVALID_TYPE)));
        child1.addChild(new TerminalNodeImpl(new CommonToken(Token.INVALID_TYPE)));

        root.addChild(child1);
        root.addChild(new TerminalNodeImpl(new CommonToken(Token.INVALID_TYPE)));

        return root;
    }

    @Test
    void testGetChildrenEmpty() {
        final ParserRuleContext root = new ParserRuleContext();

        @NotNull List<ParseTree> children = ANTLRUtils.getChildren(root);
        assertTrue(children.isEmpty());
    }

    @Test
    void testGetChildrenNonEmpty() {
        final ParserRuleContext root = new ParserRuleContext();
        for (int i = 0; i < 10; ++i) {
            root.addChild(new TerminalNodeImpl(new CommonToken(Token.INVALID_TYPE)));
        }

        @NotNull List<ParseTree> children = ANTLRUtils.getChildren(root);
        assertEquals(root.getChildCount(), children.size());
        for (int i = 0; i < 10; ++i) {
            assertSame(root.getChild(i), children.get(i));
        }
    }

    @Test
    void testGetAncestorsAsStream() {
        @NotNull final List<ParseTree> expectedAncestors = new ArrayList<>();
        @NotNull final ParseTree leafNode = createAncestorTestTree(expectedAncestors, 10);

        assertEquals(10, ANTLRUtils.getAncestorsAsStream(leafNode).count());
        @NotNull Stream<ParseTree> ancestors = ANTLRUtils.getAncestorsAsStream(leafNode);
        final AtomicInteger idx = new AtomicInteger(0);
        ancestors.forEachOrdered(ancestor -> assertSame(expectedAncestors.get(idx.getAndIncrement()), ancestor));
    }

    @Test
    void testGetAncestorsAsList() {
        @NotNull final List<ParseTree> expectedAncestors = new ArrayList<>();
        @NotNull final ParseTree leafNode = createAncestorTestTree(expectedAncestors, 10);

        @NotNull final List<ParseTree> actualAncestors = ANTLRUtils.getAncestors(leafNode);
        assertEquals(expectedAncestors.size(), actualAncestors.size());
        for (int i = 0; i < expectedAncestors.size(); ++i) {
            assertSame(expectedAncestors.get(i), actualAncestors.get(i));
        }
    }

    @Test
    void testGetRoot() {
        @NotNull final List<ParseTree> expectedAncestors = new ArrayList<>();
        @NotNull final ParseTree leafNode = createAncestorTestTree(expectedAncestors, 10);

        assertEquals(expectedAncestors.get(expectedAncestors.size() - 1), ANTLRUtils.getRoot(leafNode));
    }

    @Test
    void testGetRootTrivial() {
        @NotNull final List<ParseTree> expectedAncestors = new ArrayList<>();
        createAncestorTestTree(expectedAncestors, 10);

        final ParseTree rootNode = expectedAncestors.get(expectedAncestors.size() - 1);

        assertEquals(rootNode, ANTLRUtils.getRoot(rootNode));
    }
}
