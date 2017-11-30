package com.groupon.expression_tree;

import java.util.ArrayList;
import java.util.List;

public class ExpressionTree {

    static abstract class Node { }

    public static class Tree extends Node {
        Tree parent;
        List<Node> nodes = new ArrayList<>();
    }

    /**
     * Represents a number
     */
    static class Atom extends Node {
        int number;
    }
}
