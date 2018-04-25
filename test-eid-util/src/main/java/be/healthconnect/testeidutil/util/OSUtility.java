/*
 * (C) 2014 HealthConnect NV. All rights reserved.
 */
package be.healthconnect.testeidutil.util;

/**
 * Class to hold OS specific utility methods.
 * 
 * @author <a href="mailto:kim.wauters@address">Kim Wauters</a>
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 */
public class OSUtility {

	private static final String OS = System.getProperty("os.name").toLowerCase();
	
	/**
	 * Returns <code>true</code> on Mac.
	 * 
	 * @return <code>true</code> on Mac
	 */
	public static boolean isWindows() {
		return (OS.indexOf("windows") >= 0);
	}

	/**
	 * Returns <code>true</code> on Mac.
	 * 
	 * @return <code>true</code> on Mac
	 */
	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	/**
	 * Returns <code>true</code> on Linux.
	 * 
	 * @return <code>true</code> on Linux
	 */
	public static boolean isLinux() {
		return (OS.indexOf("linux") >= 0);
	}

}
