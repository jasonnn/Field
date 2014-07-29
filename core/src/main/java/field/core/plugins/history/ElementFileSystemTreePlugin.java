package field.core.plugins.history;

import field.bytecode.protect.Woven;
import field.bytecode.protect.annotations.NextUpdate;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.IVisualElement.VisualElementProperty;
import field.core.plugins.BaseSimplePlugin;
import field.core.plugins.help.ContextualHelp;
import field.core.plugins.help.HelpBrowser;
import field.core.plugins.history.ElementFileSystemTree.SheetDropSupport;

@Woven
public
class ElementFileSystemTreePlugin extends BaseSimplePlugin {

    public static final VisualElementProperty<ElementFileSystemTreePlugin> fileSystemTree =
            new VisualElementProperty<ElementFileSystemTreePlugin>("fileSystemTree");
    private ElementFileSystemTree efst;

    @Override
    protected
    String getPluginNameImpl() {
        return "efs";
    }

    @Override
    public
    void registeredWith(IVisualElement root) {
        super.registeredWith(root);

        efst = new ElementFileSystemTree();

        new SheetDropSupport(IVisualElement.enclosingFrame.get(root).getCanvas(), root);

        fileSystemTree.set(root, root, this);

        installHelpBrowser(root);
    }

    @NextUpdate(delay = 3)
    private
    void installHelpBrowser(final IVisualElement root) {
        HelpBrowser h = HelpBrowser.helpBrowser.get(root);
        ContextualHelp ch = h.getContextualHelp();
        ch.addContextualHelpForWidget("filesyste",
                                      efst.tree,
                                      ContextualHelp.providerForStaticMarkdownResource("contextual/filesystem.md"),
                                      50);
    }

}
