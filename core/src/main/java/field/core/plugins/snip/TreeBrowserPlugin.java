package field.core.plugins.snip;

import field.core.dispatch.iVisualElement;
import field.core.dispatch.iVisualElement.VisualElementProperty;
import field.core.plugins.BaseSimplePlugin;

public
class TreeBrowserPlugin extends BaseSimplePlugin {

    public static final VisualElementProperty<TreeBrowserPlugin> generalTree =
            new VisualElementProperty<TreeBrowserPlugin>("generalTree");
    private TreeBrowser tree;


    @Override
    protected
    String getPluginNameImpl() {
        return "tree";
    }

    @Override
    public
    void registeredWith(iVisualElement root) {
        super.registeredWith(root);

        tree = new TreeBrowser();

        generalTree.set(root, root, this);

        //		installHelpBrowser(root);
    }

    public
    void setRoot(Object r) {
        tree.setRoot(r);
    }

    //	@NextUpdate(delay = 3)
    //	private void installHelpBrowser(final iVisualElement root) {
    //		HelpBrowser h = HelpBrowser.helpBrowser.get(root);
    //		ContextualHelp ch = h.getContextualHelp();
    //		ch.addContextualHelpForWidget("treebrowser", efst.tree, ch.providerForStaticMarkdownResource("contextual/filesystem.md"), 50);
    //	}

}
