package field.core.ui.text.embedded;

import field.core.dispatch.IVisualElement;
import field.core.dispatch.override.IVisualElementOverrides;
import field.core.dispatch.VisualElementProperty;
import field.core.dispatch.override.Ref;
import field.math.graph.TopologyViewOfGraphNodes;
import field.math.graph.visitors.hint.StandardTraversalHint;
import field.math.graph.visitors.hint.TraversalHint;
import field.math.graph.visitors.TopologyVisitor_breadthFirst;

import java.util.ArrayList;
import java.util.List;


public
interface iReferenceAlgorithm {

    public abstract static
    class BaseReferenceAlgorithm implements iReferenceAlgorithm {
        public
        List<IVisualElement> evaluate(IVisualElement root,
                                      String uniqueReferenceID,
                                      String algorithmName,
                                      IVisualElement forElement) {
            VisualElementProperty pr = new VisualElementProperty(uniqueReferenceID);
            List<IVisualElement> prop = (List<IVisualElement>) forElement.getProperty(pr);
            List<IVisualElement> newProp = doEvaluation(root, prop, forElement);


            //forElement.setProperty(	pr, newProp)

            String name = algorithmName;
            forElement.setProperty(new VisualElementProperty(uniqueReferenceID + "-source"), name);
            IVisualElementOverrides.MakeDispatchProxy.getBackwardsOverrideProxyFor(forElement)
                                                           .setProperty(forElement, pr, new Ref(newProp));
            IVisualElementOverrides.MakeDispatchProxy.getOverrideProxyFor(forElement)
                                                           .setProperty(forElement, pr, new Ref(newProp));

            assert newProp != null;
            return newProp;
        }


        protected
        List<IVisualElement> allVisualElements(IVisualElement root) {


            final List<IVisualElement> ret = new ArrayList<IVisualElement>();
            new TopologyVisitor_breadthFirst<IVisualElement>(true) {
                @Override
                protected
                TraversalHint visit(IVisualElement n) {
                    String name = n.getProperty(IVisualElement.name);
                    //System.out.println(" adding <"+n+" called <"+name+">");
                    ret.add(n);
                    return StandardTraversalHint.CONTINUE;
                }

            }.apply(new TopologyViewOfGraphNodes<IVisualElement>(false).setEverything(true), root);
            return ret;
        }

        protected abstract
        List<IVisualElement> doEvaluation(IVisualElement root, List<IVisualElement> old, IVisualElement forElement);

    }


    /**
     * in addition to computing and returning, this must set a property uniqueReferenceID on forElement
     * <p/>
     * we can scrub these, upon deletion, when changes are posted to the text, and __minimalReferences dissappear from the python source
     * <p/>
     * (we'll have an additional plugin here for that)
     *
     * @param algorithmName TODO
     */
    public
    List<IVisualElement> evaluate(IVisualElement root,
                                  String uniqueReferenceID,
                                  String algorithmName,
                                  IVisualElement forElement);
}
