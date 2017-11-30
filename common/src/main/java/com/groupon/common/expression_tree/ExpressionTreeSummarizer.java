package com.groupon.common.expression_tree;

import com.groupon.common.expression_tree.ExpressionTree.Atom;
import rx.Single;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.groupon.common.expression_tree.ExpressionTree.Node;
import static com.groupon.common.expression_tree.ExpressionTree.Tree;

public class ExpressionTreeSummarizer {
    private Function<List<Integer>, Single<Integer>> summarizer;

    public ExpressionTreeSummarizer(Function<List<Integer>, Single<Integer>> summarizer) {
        this.summarizer = summarizer;
    }

    public Single<Integer> sum(Node node) {
        if (node == null) {
            throw new RuntimeException("Empty node");
        }
        if (node instanceof Atom) {
            Atom atom = (Atom) node;
            return Single.just(atom.number);
        }
        if (node instanceof Tree) {
            Tree subTree = (Tree) node;
            List<Single<Integer>> singles = subTree.nodes.stream().map(this::sum).collect(Collectors
                    .toList());
            return Single.zip(singles, args -> Arrays.copyOf(args, args.length, Integer[].class))
                    .flatMap(numbers -> summarizer.apply(Arrays.asList(numbers)));
        }
        throw new RuntimeException("Illegal node");
    }
}
