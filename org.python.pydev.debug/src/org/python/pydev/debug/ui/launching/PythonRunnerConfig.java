/*
 * Author: atotic
 * Created on Mar 18, 2004
 * License: Common Public License v1.0
 */
package org.python.pydev.debug.ui.launching;

import java.io.File;
import java.net.URL;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.externaltools.internal.launchConfigurations.ExternalToolsUtil;
import org.eclipse.ui.externaltools.internal.variable.ExpandVariableContext;
import org.python.pydev.debug.core.Constants;
import org.python.pydev.debug.core.PydevDebugPlugin;
import org.python.pydev.debug.ui.InterpreterEditor;

/**
 * Holds configuration for PythonRunner.
 * 
 * It knows how to extract proper launching arguments from disparate sources. 
 * Has many launch utility functions (getCommandLine & friends).
 */
public class PythonRunnerConfig {

	public IPath file;
	public String interpreter;
	public String[] arguments;
	public File workingDirectory;
	// debugging
	public boolean isDebug;
	private int debugPort = 0;  // use getDebugPort
	public String debugScript;
	public int acceptTimeout = 5000; // miliseconds

	/**
	 * Sets defaults.
	 */

	public PythonRunnerConfig(ILaunchConfiguration conf, String mode, ExpandVariableContext resourceContext) throws CoreException {
		isDebug = mode.equals(ILaunchManager.DEBUG_MODE);
		file = ExternalToolsUtil.getLocation(conf, resourceContext);
		interpreter = conf.getAttribute(Constants.ATTR_INTERPRETER, "python");
		arguments = ExternalToolsUtil.getArguments(conf, resourceContext);
		IPath workingPath = ExternalToolsUtil.getWorkingDirectory(conf, resourceContext);
		workingDirectory = workingPath == null ? null : workingPath.toFile();
		if (isDebug) {
			debugScript = getDebugScript();
			// TODO debug socket port?
		}
		// E3		String[] envp = DebugPlugin.getDefault().getLaunchManager().getEnvironment(conf);

	}
	
	public int getDebugPort() throws CoreException {
		if (debugPort == 0) {
			debugPort= SocketUtil.findUnusedLocalPort("", 5000, 15000); //$NON-NLS-1$
			if (debugPort == -1)
				throw new CoreException(new Status(IStatus.ERROR, PydevDebugPlugin.getPluginID(), 0, "Could not find a free socket for debugger", null));
		}
		return debugPort;		
	}


	public String getRunningName() {
		return file.lastSegment();
	}

	/**
	 * @throws CoreException if arguments are inconsistent
	 */
	public void verify() throws CoreException {
		if (file == null
			|| interpreter == null)
		throw new CoreException(new Status(IStatus.ERROR, PydevDebugPlugin.getPluginID(), 0, "Invalid PythonRunnerConfig",null));
		if (isDebug &&
			( acceptTimeout < 0
			|| debugPort < 0
			|| debugScript == null))
		throw new CoreException(new Status(IStatus.ERROR, PydevDebugPlugin.getPluginID(), 0, "Invalid PythonRunnerConfig",null));
	}

	/** 
	 * gets location of jpydaemon.py
	 */
	public static String getDebugScript() throws CoreException {
		IPath relative = new Path("pysrc").addTrailingSeparator().append("jpydaemon.py");
//		IPath relative = new Path("pysrc").addTrailingSeparator().append("rpdb.py");
		URL location = org.python.pydev.debug.core.PydevDebugPlugin.getDefault().find(relative);
		if (location == null)
			throw new CoreException(new Status(IStatus.ERROR, PydevDebugPlugin.getPluginID(), 0, "Internal pydev error: Cannot find jpydaemon.py", null));
		return location.getPath();
	}

	/**
	 * Create a command line for launching.
	 * @return command line ready to be exec'd
	 */
	public String[] getCommandLine() {
		Vector cmdArgs = new Vector(10);
		cmdArgs.add(interpreter);
		// Next option is for unbuffered stdout, otherwise Eclipse will not see any output until done
		cmdArgs.add(InterpreterEditor.isJython(interpreter) ? "-i" : "-u");
		if (isDebug) {
//	rpdb		cmdArgs.add(debugScript);
//			cmdArgs.add("-c");
//			cmdArgs.add("-p"+Integer.toString(debugPort));		
			cmdArgs.add(debugScript);
			cmdArgs.add("localhost");
			cmdArgs.add(Integer.toString(debugPort));
		}
		cmdArgs.add(file.toOSString());
		for (int i=0; arguments != null && i<arguments.length; i++)
			cmdArgs.add(arguments[i]);
		String[] retVal = new String[cmdArgs.size()];
		cmdArgs.toArray(retVal);
		return retVal;
	}
	
	public String getCommandLineAsString() {
		String[] args = getCommandLine();
		StringBuffer s = new StringBuffer();
		for (int i=0; i< args.length; i++) {
			s.append(args[i]);
			s.append(" ");
		}
		return s.toString();
	}
}
