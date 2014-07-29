package field.core.plugins.history;

import field.math.graph.IMutable;
import field.math.graph.NodeImpl;
import field.math.graph.visitors.GraphNodeSearching;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;


public
class TextSearching {

    public
    interface iProvidesSearchTerms {
        public
        IMutable duplicate();

        public
        void found(int n);

        public
        String[] getTerms();

        public
        void notFound();
    }

    private final Pattern pattern;

    private final String expression;

    public
    TextSearching(String expression) {
        this.expression = expression;
        pattern = Pattern.compile(expression);
    }

    public
    <T extends NodeImpl<T>> T search(T root) {

        final HashSet<Object> keep = new HashSet<Object>();

        new GraphNodeSearching.GraphNodeVisitor_depthFirst<T>(false) {

            @Override
            protected
            TraversalHint visit(T n) {

                if (n instanceof iProvidesSearchTerms) {
                    String[] c = ((iProvidesSearchTerms) n).getTerms();
                    boolean found = false;
                    int q = 0;
                    for (int i = 0; i < c.length; i++) {


                        if (c[i] != null) if (pattern.matcher(c[i]).find()) {
                            q = i;


                            ((iProvidesSearchTerms) n).found(i);
                            found = true;
                        }
                    }
                    if (found) {
                        for (int i = 0; i < stack.size(); i++) {
                            keep.add(stack.get(i));
                        }
                    }
                    else {
                        ((iProvidesSearchTerms) n).notFound();
                    }
                }

                return StandardTraversalHint.CONTINUE;

            }
        }.apply(root);

        final HashMap<T, T> created = new HashMap<T, T>();
        final IMutable[] newroot = {null};

        new GraphNodeSearching.GraphNodeVisitor_depthFirst<T>(false) {

            @Override
            protected
            TraversalHint visit(T n) {

                if (keep.contains(n)) {
                    //System.out.println(" cloning <"+n+">");
                    IMutable cloned = ((iProvidesSearchTerms) n).duplicate();
                    created.put(n, (T) cloned);

                    if (!n.getParents().isEmpty()) {
                        T p = created.get(n.getParents().get(0));
                        ((IMutable) p).addChild(cloned);
                    }
                    else {
                        assert newroot[0] == null;
                        newroot[0] = cloned;
                    }
                }

                return StandardTraversalHint.CONTINUE;

            }
        }.apply(root);

        return (T) newroot[0];
    }

}
