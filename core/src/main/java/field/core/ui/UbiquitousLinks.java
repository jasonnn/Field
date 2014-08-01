package field.core.ui;

import field.core.Constants;
import field.core.StandardFluidSheet;
import field.core.dispatch.IVisualElement;
import field.core.dispatch.Rect;
import field.core.execution.PythonInterface;
import field.core.plugins.selection.SelectionSetDriver.TravelTo;
import field.core.ui.text.util.OpenInEclipse;
import field.core.util.AppleScript;
import field.core.util.ExecuteCommand;
import field.core.windowing.GLComponentWindow;
import field.core.windowing.components.DraggableComponent;
import field.core.windowing.components.SelectionGroup;
import field.core.windowing.components.iComponent;
import field.launch.Launcher;
import field.namespace.generic.ReflectionTools;

import javax.accessibility.AccessibleComponent;
import javax.accessibility.AccessibleContext;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.HTML;
import javax.swing.text.html.StyleSheet;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public
class UbiquitousLinks {

    public static final UbiquitousLinks links = new UbiquitousLinks();
    public static final List<StandardFluidSheet> sheets = new ArrayList<StandardFluidSheet>();
    public static final HashMap<String, Object> shortTermObjectStore = new HashMap<String, Object>();

    UbiquitousLinks() {
//		StyleSheet styleSheet = new HTMLEditorKit().getStyleSheet();
//		injectCSS(styleSheet);

    }

    public static
    void injectCSS(StyleSheet styleSheet) {
        styleSheet.addRule("body { font-family: \"" + Constants.defaultFont + "\"; color:#444444}");
        styleSheet.addRule(".wellspaced { padding-top: 7px; padding-bottom:7px; margin-top:0px; margin-bottom:0px;}");
        styleSheet.addRule(".wellspacedBlack { color:#000000; padding-top: 7px; padding-bottom:7px; margin-top:0px; margin-bottom:0px;}");
        styleSheet.addRule(".wellspacedIndented { color:#000000;  font: '"
                           + Constants.defaultFont
                           + "' margin-left:20px; padding-top: 7px; padding-bottom:7px; margin-top:0px; margin-bottom:0px;}");
        styleSheet.addRule(".wellspacedWhite { color:#fff; padding-top: 7px; padding-bottom:7px; margin-top:0px; margin-bottom:0px;}");
        styleSheet.addRule(".wellspacedIndentedWhite { color: #fff; padding-left:20px; padding-top: 7px; padding-bottom:7px; margin-top:0px; margin-bottom:0px;}");
        styleSheet.addRule("h3 { color:#000; padding-top: 17px; margin-top: 30px; padding-bottom:0px; }");

        styleSheet.addRule(".invertedheading {padding-top: 7px; padding-bottom:4px; font-size:medium; font-weight:bold; background: #888888; color:#222222}");
        styleSheet.addRule("h2 { padding-top: 14px; padding-bottom:7px }");
        styleSheet.addRule("h1 { padding-top: 14px; padding-bottom:7px }");

        styleSheet.addRule("source{ padding-top: 0px; padding-bottom:7px; color:#000000; font-family:\""
                           + Constants.defaultFont
                           + "\"}");
        styleSheet.addRule("pre{ font-family:\"" + Constants.defaultFont + "\"}");
    }

    public static
    String code_copyTextToClipboard(String string) {
        String code = "_l.popupMenu( {'copy to clipboard': lambda : _l.copyTextToClipboard('"
                      + string
                      + "')}, _component, _event.getPoint())";
        return code;
    }

    public static
    String code_revealInFinder(String path) {
        String code = "_l.popupMenu( {'reveal in finder': lambda : _l.showPathInFinder('"
                      + path
                      + "')}, _component, _event.getPoint())";
        return code;
    }

    public static
    String code_revealInSafari(String path) {
        String code = "_l.popupMenu( {'open in new Web Browser': lambda : _l.showPathInSafari('"
                      + path
                      + "')}, _component, _event.getPoint())";
        return code;
    }

    public static
    String code_openInBrowser(String name, Object value) {
        String v = reference(value);
        String code = "_l.popupMenu( {'open in new Browser' : lambda : _l.openInBrowser('"
                      + name
                      + "', _l.dereference('"
                      + v
                      + "'))}, _component, _event.getPoint())";
        return code;
    }

    public static
    String code_selectOrMarkByUID(String uid) {
        String code = "_l.popupMenu( {'select': lambda : _l.selectElementByUID('"
                      + uid
                      + "'), 'mark': lambda : _l.markElementByUID('"
                      + uid
                      + "'), 'go to' : lambda : _l.gotoElementByUID('"
                      + uid
                      + "')}, _component, _event.getPoint())";
        return code;
    }

    public static
    String code_openInEclipse(String name, int line) {
        String code = "_l.popupMenu( {'open in eclipse': lambda : _l.openInEclipse(\'"
                      + name
                      + "\',"
                      + line
                      + ")}, _component, _event.getPoint())";
        return code;
    }

    public static
    boolean copyTextToClipboard(String text) {
        //System.out.println(" copy text to clip <" + text + ">");
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        c.setContents(new StringSelection(text), null);
        return true;
    }

    public static
    String simpleLink(String href) {

        return href;

    }


    public static
    boolean showPathInFinder(String path) {

        String c = "\n"
                   + "tell application \"Finder\"\n"
                   + "	activate\n"
                   + "	reveal (POSIX file \""
                   + path
                   + "\")\n"
                   + "end tell";

        //System.out.println(c);

        new AppleScript(c, false);
        return true;
    }

    public static
    Object dereference(String id) {
        //System.out.println(" get <" + id + "> from <" + shortTermObjectStore + "> <" + System.identityHashCode(this) + ">");
        return shortTermObjectStore.get(id);
    }

    public static
    void install(final JLabel label) {
        label.addMouseListener(new MouseListener() {

            public
            void mouseClicked(MouseEvent e) {

                View cp = (View) label.getClientProperty("html");

                int w1 = label.getWidth();

                int max = label.getAccessibleContext().getAccessibleText().getCharCount();
                float minx = Float.POSITIVE_INFINITY;
                float maxx = Float.NEGATIVE_INFINITY;
                for (int i = 0; i < max; i++) {
                    Rectangle b = label.getAccessibleContext().getAccessibleText().getCharacterBounds(i);
                    if (b.x < minx) minx = b.x;
                    if (b.x + b.width > maxx) maxx = b.x + b.width;
                }

                int index = cp.viewToModel(e.getPoint().x + label.getBounds().x - minx,
                                           e.getPoint().y + label.getBounds().y,
                                           label.getBounds());

                //System.out.println(" aa :" + label.getAccessibleContext().getAccessibleText().getCharacterAttribute(index));

                for (int i = 0; i < max; i++) {
                    AttributeSet attr = (AttributeSet) (label.getAccessibleContext()
                                                             .getAccessibleText()
                                                             .getCharacterAttribute(i)
                                                             .getAttribute(HTML.Tag.A));
                }

                AttributeSet attr = (AttributeSet) (label.getAccessibleContext()
                                                         .getAccessibleText()
                                                         .getCharacterAttribute(index)
                                                         .getAttribute(HTML.Tag.A));
                //System.out.println(" index <" + index + "> <" + attr + ">");
                if (attr != null) {
                    String onclick = (String) attr.getAttribute("onclick");
                    String onrightClick = (String) attr.getAttribute("onrightclick");
                    //System.out.println(" got menu <" + onclick + "> <" + onrightClick + ">");

                    if (onclick != null && e.getButton() == MouseEvent.BUTTON1) {
                        PythonInterface.getPythonInterface().setVariable("_l", links);
                        PythonInterface.getPythonInterface().setVariable("_event", e);
                        PythonInterface.getPythonInterface().setVariable("_component", label);
                        PythonInterface.getPythonInterface().execString(onclick);
                    }
                    else if (onrightClick != null && e.getButton() == MouseEvent.BUTTON2) {
                        PythonInterface.getPythonInterface().setVariable("_l", links);
                        PythonInterface.getPythonInterface().setVariable("_event", e);
                        PythonInterface.getPythonInterface().setVariable("_component", label);
                        PythonInterface.getPythonInterface().execString(onrightClick);
                    }
                }
            }

            public
            void mouseEntered(MouseEvent e) {
                // TODO
                // Auto-generated
                // method stub

            }

            public
            void mouseExited(MouseEvent e) {
                // TODO
                // Auto-generated
                // method stub

            }

            public
            void mousePressed(MouseEvent e) {
                // TODO
                // Auto-generated
                // method stub

            }

            public
            void mouseReleased(MouseEvent e) {
                // TODO
                // Auto-generated
                // method stub

            }
        });
    }

    public static
    void install(final JButton label) {
        label.addMouseListener(new MouseListener() {

            public
            void mouseClicked(MouseEvent e) {

                View cp = (View) label.getClientProperty("html");

                int w1 = label.getWidth();

                int max = label.getAccessibleContext().getAccessibleText().getCharCount();
                float minx = Float.POSITIVE_INFINITY;
                float maxx = Float.NEGATIVE_INFINITY;
                for (int i = 0; i < max; i++) {
                    Rectangle b = label.getAccessibleContext().getAccessibleText().getCharacterBounds(i);
                    if (b.x < minx) minx = b.x;
                    if (b.x + b.width > maxx) maxx = b.x + b.width;
                }

                int index = cp.viewToModel(e.getPoint().x + label.getBounds().x - minx,
                                           e.getPoint().y + label.getBounds().y,
                                           label.getBounds());

                //System.out.println(" aa :" + label.getAccessibleContext().getAccessibleText().getCharacterAttribute(index));

                for (int i = 0; i < max; i++) {
                    AttributeSet attr = (AttributeSet) (label.getAccessibleContext()
                                                             .getAccessibleText()
                                                             .getCharacterAttribute(i)
                                                             .getAttribute(HTML.Tag.A));
                }

                AttributeSet attr = (AttributeSet) (label.getAccessibleContext()
                                                         .getAccessibleText()
                                                         .getCharacterAttribute(index)
                                                         .getAttribute(HTML.Tag.A));
                //System.out.println(" index <" + index + "> <" + attr + ">");
                if (attr != null) {
                    String onclick = (String) attr.getAttribute("onclick");
                    String onrightClick = (String) attr.getAttribute("onrightclick");
                    //System.out.println(" got menu <" + onclick + "> <" + onrightClick + ">");

                    if (onclick != null && e.getButton() == MouseEvent.BUTTON1) {
                        PythonInterface.getPythonInterface().setVariable("_l", links);
                        PythonInterface.getPythonInterface().setVariable("_event", e);
                        PythonInterface.getPythonInterface().setVariable("_component", label);
                        PythonInterface.getPythonInterface().execString(onclick);
                    }
                    else if (onrightClick != null && e.getButton() == MouseEvent.BUTTON2) {
                        PythonInterface.getPythonInterface().setVariable("_l", links);
                        PythonInterface.getPythonInterface().setVariable("_event", e);
                        PythonInterface.getPythonInterface().setVariable("_component", label);
                        PythonInterface.getPythonInterface().execString(onrightClick);
                    }
                }
            }

            public
            void mouseEntered(MouseEvent e) {
                // TODO
                // Auto-generated
                // method stub

            }

            public
            void mouseExited(MouseEvent e) {
                // TODO
                // Auto-generated
                // method stub

            }

            public
            void mousePressed(MouseEvent e) {
                // TODO
                // Auto-generated
                // method stub

            }

            public
            void mouseReleased(MouseEvent e) {
                // TODO
                // Auto-generated
                // method stub

            }
        });
    }

    public static
    void install(final JTree tree) {
        tree.addMouseListener(new MouseListener() {

            public
            void mouseClicked(MouseEvent e) {
                AccessibleComponent at = ((AccessibleComponent) tree.getAccessibleContext());
                AccessibleComponent boundingAccess = (AccessibleComponent) at.getAccessibleAt(e.getPoint());
                AccessibleComponent parent =
                        (AccessibleComponent) ((AccessibleContext) boundingAccess).getAccessibleParent();

                TreePath p = (TreePath) ReflectionTools.illegalGetObject(boundingAccess, "path");

                JLabel cellRenderer =
                        (JLabel) ReflectionTools.illegalGetObject(((AccessibleContext) boundingAccess).getAccessibleText(),
                                                                  "this$0");
                View clientProperty = (View) cellRenderer.getClientProperty("html");

                Point convertedpoint = new Point(e.getPoint().x - 21 * (p.getPathCount() - 1), 5);

                int index = clientProperty.viewToModel(convertedpoint.x,
                                                       convertedpoint.y,
                                                       new Rectangle(boundingAccess.getBounds().width,
                                                                     boundingAccess.getBounds().height));
                AttributeSet attr = (AttributeSet) ((AccessibleContext) boundingAccess).getAccessibleText()
                                                                                       .getCharacterAttribute(index)
                                                                                       .getAttribute(HTML.Tag.A);

                if (attr != null) {
                    String onclick = (String) attr.getAttribute("onclick");
                    String onrightClick = (String) attr.getAttribute("onrightclick");

                    if (onclick != null && e.getButton() == MouseEvent.BUTTON1) {
                        PythonInterface.getPythonInterface().setVariable("_l", links);
                        PythonInterface.getPythonInterface().setVariable("_event", e);
                        PythonInterface.getPythonInterface().setVariable("_component", tree);
                        PythonInterface.getPythonInterface().execString(onclick);
                    }
                    else if (onrightClick != null && e.getButton() == MouseEvent.BUTTON2) {
                        PythonInterface.getPythonInterface().setVariable("_l", links);
                        PythonInterface.getPythonInterface().setVariable("_event", e);
                        PythonInterface.getPythonInterface().setVariable("_component", tree);
                        PythonInterface.getPythonInterface().execString(onrightClick);
                    }
                }
            }

            public
            void mouseEntered(MouseEvent e) {
            }

            public
            void mouseExited(MouseEvent e) {
            }

            public
            void mousePressed(MouseEvent e) {
            }

            public
            void mouseReleased(MouseEvent e) {
            }

            private
            String enumerate(Enumeration<?> attributeNames) {
                String s = "";
                while (attributeNames.hasMoreElements()) {
                    s += attributeNames.nextElement() + " ";
                }
                return s;
            }
        });
    }


    public static
    void install(final JEditorPane infoText) {
        final MouseEvent[] event = {null};
        infoText.addMouseListener(new MouseAdapter() {
            @Override
            public
            void mousePressed(MouseEvent e) {
                event[0] = e;

            }
        });
        infoText.addHyperlinkListener(new HyperlinkListener() {

            long lastFiredAt = 0;

            public
            void hyperlinkUpdate(HyperlinkEvent e) {
                if (!e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) return;
                if (System.currentTimeMillis() - lastFiredAt < 1000) return;
                lastFiredAt = System.currentTimeMillis();
                Element ee = e.getSourceElement();
                AttributeSet attr = (AttributeSet) infoText.getAccessibleContext()
                                                           .getAccessibleText()
                                                           .getCharacterAttribute(ee.getStartOffset())
                                                           .getAttribute(HTML.Tag.A);

                //System.out.println(" attribute set is <" + attr + "> <" + ee.getParentElement() + "> <" + this + ">");

                String href = (String) attr.getAttribute(HTML.Attribute.HREF);
                Enumeration<?> names = attr.getAttributeNames();
                while (names.hasMoreElements()) {
                    Object element = names.nextElement();
                    //System.out.println(" element :" + element + " " + element.getClass() + " " + HTML.getAttributeKey("HREF"));
                }
                if (href != null && !"'http://".equals(href)) {

                    //System.out.println(" href ? :" + href);
                    showPathInSafari(href);
                }
                else {

                    String onclick = (String) attr.getAttribute("onclick");
                    String onrightClick = (String) attr.getAttribute("onrightclick");
                    //System.out.println(onclick + " " + onrightClick);

                    PythonInterface.getPythonInterface().setVariable("_l", links);
                    PythonInterface.getPythonInterface().setVariable("_event", event[0]);
                    PythonInterface.getPythonInterface().setVariable("_component", infoText);
                    PythonInterface.getPythonInterface().execString(onclick);

                    //System.out.println(" event is <" + event[0] + ">");
                }
            }

        });
    }

    public static
    void showPathInSafari(String href) {
        new ExecuteCommand(".", new String[]{"/usr/bin/open", href}, false);
    }

    public static
    String link(String linkText, String onclick, String onrightclick) {
        return "<a href='http://' color='#"
               + Constants.defaultTreeColor
               + "' onclick=\""
               + (onclick == null
                  ? ""
                  : onclick)
               + "\" onrightclick=\""
               + (onrightclick == null ? "" : onrightclick)
               + "\"><i>"
               + linkText
               + "</i></a>";
    }

    public static
    boolean markElementByUID(String uid) {
        for (StandardFluidSheet s : sheets) {
            SelectionGroup<iComponent> selectionGroup = IVisualElement.markingGroup.get(s.getRoot());
            selectionGroup.deselectAll();
            IVisualElement found = StandardFluidSheet.findVisualElement(s.getRoot(), uid);
            if (found != null) {
                selectionGroup.addToSelection(IVisualElement.localView.get(found));
                ((DraggableComponent) IVisualElement.localView.get(found)).setMarked(true);
                IVisualElement.dirty.set(found, found, true);
            }
        }
        return true;
    }

    public static
    boolean gotoElementByUID(String uid) {

        Rect bound = null;
        GLComponentWindow window = null;

        for (StandardFluidSheet s : sheets) {
            SelectionGroup<iComponent> selectionGroup = IVisualElement.markingGroup.get(s.getRoot());
            selectionGroup.deselectAll();
            IVisualElement found = StandardFluidSheet.findVisualElement(s.getRoot(), uid);
            if (found != null) {
                selectionGroup.addToSelection(IVisualElement.localView.get(found));
                ((DraggableComponent) IVisualElement.localView.get(found)).setMarked(true);
                IVisualElement.dirty.set(found, found, true);
                bound = Rect.union(bound, found.getFrame(null));
                window = IVisualElement.enclosingFrame.get(found);
            }
        }
        if (bound != null) {
            Launcher.getLauncher().registerUpdateable(new TravelTo(window, bound.midpoint2()));
        }
        return true;
    }

    public
    void openInBrowser(String root, Object o) {
//		BrowserTools.browse(o, null, root);
    }


    public static
    String reference(Object o) {
        String uid = new UID().toString();
        shortTermObjectStore.put(uid, o);
        return uid;
    }

    public static
    boolean selectElementByUID(String uid) {
        for (StandardFluidSheet s : sheets) {
            SelectionGroup<iComponent> selectionGroup = IVisualElement.selectionGroup.get(s.getRoot());
            selectionGroup.deselectAll();
            IVisualElement found = StandardFluidSheet.findVisualElement(s.getRoot(), uid);
            if (found != null) {
                selectionGroup.addToSelection(IVisualElement.localView.get(found));
                IVisualElement.localView.get(found).setSelected(true);
                IVisualElement.dirty.set(found, found, true);
            }
        }
        return true;
    }

    public static
    void openInEclipse(String name, int line) {
        new OpenInEclipse(name, line);
    }

    public static
    String link(String linkText, String perform) {
        return link(linkText, perform, perform);
    }

}
