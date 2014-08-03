package field.extras.plugins.processing;

import field.core.execution.IExecutesPromise;
import field.core.ui.text.PythonTextEditor.EditorExecutionInterface;
import field.launch.IUpdateable;

public interface iProcessingLoader extends IUpdateable {

	public void close();
	public EditorExecutionInterface getEditorExecutionInterface(EditorExecutionInterface delegateTo);
	public
    IExecutesPromise getExecutesPromise(IExecutesPromise delegateTo);

	public void init();
	public void injectIntoGlobalNamespace();
	public void setOntop(Boolean s);
}
