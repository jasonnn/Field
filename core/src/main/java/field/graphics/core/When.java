package field.graphics.core;

import field.core.plugins.PythonOverridden.Callable;
import field.core.util.PythonCallableMap;
import field.graphics.core.pass.StandardPass;
import field.graphics.core.scene.BasicSceneList;
import field.graphics.core.scene.OnePassElement;
import field.launch.IUpdateable;
import field.util.Dict.Prop;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * tools for injecting raw gl commands into various positions in the tree
 *
 * @author marc
 */
public
class When {

    public static Prop<StandardPass> when = new Prop<StandardPass>("when");

    public static HashMap<BasicSceneList, Map<StandardPass, When>> masterMap =
            new LinkedHashMap<BasicSceneList, Map<StandardPass, When>>();

    private final BasicSceneList list;

    public
    When(BasicSceneList list) {
        this.list = list;
    }

    StandardPass pass;

    PythonCallableMap map = new PythonCallableMap() {
        protected
        field.core.plugins.PythonOverridden.Callable newCallable(org.python.core.PyFunction f) {
            Callable c = super.newCallable(f);
            c.getInfo().put(when, pass);
            ensureShim(pass);
            return c;
        }

        protected
        field.core.plugins.PythonOverridden.Callable newCallable(String name, IUpdateable u) {
            Callable c = super.newCallable(name, u);
            c.getInfo().put(when, pass);
            ensureShim(pass);
            return c;
        }
    };

    Map<StandardPass, OnePassElement> shims =
            new EnumMap<StandardPass, OnePassElement>(StandardPass.class);
//new LinkedHashMap<StandardPass, OnePassElement>();

    public
    PythonCallableMap getMap(StandardPass pass) {
        this.pass = pass;
        return map;
    }

    public
    PythonCallableMap getMap(int pass) {
        if (pass < 0) pass = 0;
        if (pass >= StandardPass.values().length)
            pass = StandardPass.values().length - 1;
        this.pass = StandardPass.values()[pass];
        return map;
    }

    protected
    void ensureShim(final StandardPass q) {
        OnePassElement o = shims.get(q);
        if (o == null) {
            shims.put(q, o = new OnePassElement(q) {

                @Override
                public
                void performPass() {
                    for (Map.Entry<String, Callable> c : map.known.entrySet()) {
                        StandardPass pp = c.getValue().getInfo().get(When.when);
                        if (pp != null && pp == q) {
                            map.current = c.getKey();
                            c.getValue().call(null, new Object[]{BasicContextManager.getGl()});
                        }
                    }

                    map.known.keySet().removeAll(map.clear);
                    map.clear.clear();
                }
            });

            list.addChild(o);
        }
    }

}
