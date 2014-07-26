package field.core.ui.text.embedded;

//import javax.swing.text.DefaultStyledDocument;

import field.core.dispatch.iVisualElement;
import field.util.collect.tuple.Pair;
import org.eclipse.swt.custom.StyledText;


public
interface iCustomInsertSystem {
    /*
     * given some styled text, create the textual description of it
     */
    public
    String convertUserTextToExecutableText(String text);

    /*
     * converts text to pure text and a serializable property (that presumably contains some kind of range list)
     */
    public
    Pair<String, Object> swapOutText(String text);

    /**
     * this will be followed by a call to updateAllStyles
     */
    public
    String swapInText(Pair<String, Object> text);

    public
    void updateAllStyles(StyledText document, iVisualElement inside);

}
