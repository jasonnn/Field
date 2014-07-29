package field.core.dispatch;

import field.namespace.context.ContextTopology;
import field.namespace.context.iContextStorage;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;


public
class VisualElementContextTopology extends ContextTopology<IVisualElement, IVisualElementOverrides> {

    private final IVisualElement rootElement;

    public
    VisualElementContextTopology(IVisualElement root) {
        super(IVisualElement.class, IVisualElementOverrides.class);
        this.rootElement = root;
        this.storage = new iContextStorage<IVisualElement, IVisualElementOverrides>() {
            public
            IVisualElementOverrides get(IVisualElement at, Method m) {
                return at.getProperty(IVisualElement.overrides);
            }
        };
    }

    ThreadLocal<Stack<IVisualElement>> atStack = new ThreadLocal<Stack<IVisualElement>>() {
        @Override
        protected
        Stack<IVisualElement> initialValue() {
            return new Stack<IVisualElement>();
        }
    };

    public
    void begin(IVisualElement e) {
        Stack<IVisualElement> stack = atStack.get();
        stack.push(e);
        setAt(e);
    }

    public
    void end(IVisualElement e) {
        Stack<IVisualElement> stack = atStack.get();
        assert stack.peek() == e;
        setAt(stack.pop());
    }

    @Override
    public
    Set<IVisualElement> childrenOf(IVisualElement p) {
        return new LinkedHashSet<IVisualElement>(p.getChildren());
    }

    @Override
    public
    void deleteChild(IVisualElement parent, IVisualElement child) {
    }

    @Override
    public
    Set<IVisualElement> parentsOf(IVisualElement k) {
        return new LinkedHashSet<IVisualElement>((Collection<? extends IVisualElement>) k.getParents());
    }

    @Override
    public
    IVisualElement root() {
        return rootElement;
    }

}
