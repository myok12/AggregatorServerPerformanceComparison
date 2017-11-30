package com.groupon.expression_tree;

import com.groupon.expression_tree.ExpressionTree.Tree;

import static com.groupon.expression_tree.ExpressionTree.*;

public class ExpressionTreeParser {
    /**
     * Parses a string in the format 1+(2+3)+4 into an AST.
     */
    public static Tree parseExpressionTree(String exp) {
        Tree root = new Tree();

        StringBuilder currNumber = new StringBuilder();
        Tree currTree = root;

        Item lastSeen = Item.OPEN_PAREN; // This marks the start -- a new expression.

        exp = exp + "+"; // Allows the last item to be ingested.

        for (Character c : exp.toCharArray()) {
            switch(c) {
                case '(':
                    assert lastSeen == Item.OPERATOR | lastSeen == Item.OPEN_PAREN : "open paren must be " +
                            "preceded by a plus or open paren";
                    Tree newTree = new Tree();
                    newTree.parent = currTree;
                    currTree.nodes.add(newTree);
                    currTree = newTree;
                    lastSeen = Item.OPEN_PAREN;
                    break;
                case ')':
                    assert lastSeen == Item.DIGIT || lastSeen == Item.CLOSE_PAREN: "close paren must be " +
                            "preceded by a number or a previous close paren";
                    // Inside parens there should be an atom.
                    if (lastSeen == Item.DIGIT) {
                        currTree.nodes.add(parseNumber(currNumber.toString()));
                    }
                    currTree = currTree.parent;
                    lastSeen = Item.CLOSE_PAREN;
                    break;
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case '0':
                    assert lastSeen == Item.DIGIT || lastSeen == Item.OPEN_PAREN || lastSeen ==
                            Item.OPERATOR : "digits cannot be preceded by a closed paren";
                    if (lastSeen != Item.DIGIT) {
                        currNumber = new StringBuilder();
                    }
                    currNumber.append(c);
                    lastSeen = Item.DIGIT;
                    break;
                case '+':
                    assert lastSeen == Item.DIGIT || lastSeen == Item.CLOSE_PAREN : "plus must " +
                            "come after a number or a closed parens.";
                    if (lastSeen == Item.DIGIT) {
                        currTree.nodes.add(parseNumber(currNumber.toString()));
                    }
                    lastSeen = Item.OPERATOR;
                    break;
            }
        }
        return root;
    }

    private static Atom parseNumber(String numString) {
        Integer number = Integer.valueOf(numString);
        Atom atom = new Atom();
        atom.number = number;
        return atom;
    }

    enum Item {
        OPERATOR,
        DIGIT,
        OPEN_PAREN,
        CLOSE_PAREN
    }
}
