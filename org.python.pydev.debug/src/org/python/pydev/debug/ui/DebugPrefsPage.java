/*
 * Author: atotic
 * Created: Jun 23, 2003
 * License: Common Public License v1.0
 */
package org.python.pydev.debug.ui;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.python.pydev.debug.core.Constants;
import org.python.pydev.debug.core.PydevDebugPlugin;

/**
 * Debug preferences.
 * 
 * <p>Simple 1 page debug preferences page.
 * <p>Prefeernce constants are defined in Constants.java
 */
public class DebugPrefsPage extends FieldEditorPreferencePage 
	implements IWorkbenchPreferencePage{

	/**
	 * Initializer sets the preference store
	 */
	public DebugPrefsPage() {
		super(GRID);
		setPreferenceStore(PydevDebugPlugin.getDefault().getPreferenceStore());
	}

	public void init(IWorkbench workbench) {
		String interpreterPath = getPreferenceStore().getString(Constants.PREF_INTERPRETER_PATH);
		// If the interpreter path is empty, always try to come up with something
		if (interpreterPath == null || interpreterPath.length() == 0) {
			getPreferenceStore().setDefault(Constants.PREF_INTERPRETER_PATH, getDefaultInterpreterPath());
			getPreferenceStore().setToDefault(Constants.PREF_INTERPRETER_PATH);
		}
	}
	
	/**
	 * Creates the editors
	 */
	protected void createFieldEditors() {
		Composite p = getFieldEditorParent();
		InterpreterEditor pathEditor = new InterpreterEditor (
		Constants.PREF_INTERPRETER_PATH, "Python interpreters (for example python.exe)", p);
		addField(pathEditor);
	}

	/**
	 * Return the default python executable
	 * I tried making this smarter, but you can't do much without getenv(PATH)
	 */
	private String getDefaultInterpreterPath() {
		String executable = "python";
		return executable;
// ideally, I'd search the system path here, but getenv has been disabled
// some code on finding the binary
//		java.util.Properties p = System.getProperties();
//		java.util.Enumeration keys = p.keys();
//		while( keys.hasMoreElements() ) {
//			System.out.println( keys.nextElement() );
//		}
//		StringBuffer retVal = new StringBuffer();
//		String sysPath = System.getProperty("sys.path");
//		if (sysPath == null) 
//			sysPath = System.getenv("PATH");
//		if (sysPath != null) {
//			StringTokenizer st = new StringTokenizer(sysPath, File.pathSeparator + "\n\r");
//			while (st.hasMoreElements()) {
//				String path =st.nextToken();
//				System.out.println(path);
//			}
//		}
//		return retVal.toString();
	}
	

	/**
	 * Sets default preference values
	 */
	protected void initializeDefaultPreferences(Preferences prefs) {
		prefs.setDefault(Constants.PREF_INTERPRETER_PATH, getDefaultInterpreterPath());
	}
}
