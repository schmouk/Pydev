/*
 * Author: atotic
 * Created on Mar 22, 2004
 * License: Common Public License v1.0
 */
package org.python.pydev.debug.ui.launching;

import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Random;

/**
 * Utility class to find a port to debug on.
 * 
 * Straight copy of package org.eclipse.jdt.launching.SocketUtil.
 * I just could not figure out how to import that one. 
 * No dependencies kept it on the classpath reliably
 */
public class SocketUtil {
	private static final Random fgRandom= new Random(System.currentTimeMillis());
	
	/**
	 * Returns a free port number on the specified host within the given range,
	 * or -1 if none found.
	 * 
	 * @param host name or IP addres of host on which to find a free port
	 * @param searchFrom the port number from which to start searching 
	 * @param searchTo the port number at which to stop searching
	 * @return a free port in the specified range, or -1 of none found
	 */
	public static int findUnusedLocalPort(String host, int searchFrom, int searchTo) {

		for (int i= 0; i < 10; i++) {
			Socket s= null;
			int port= getRandomPort(searchFrom, searchTo);
			try {
				s= new Socket(host, port);
			} catch (ConnectException e) {
				return port;
			} catch (IOException e) {
			} finally {
				if (s != null) {
					try {
						s.close();
					} catch (IOException ioe) {
					}
				}
			}
		}
		return -1;
	}
	
	private static int getRandomPort(int low, int high) {
		return (int)(fgRandom.nextFloat() * (high-low)) + low;
	}
}
