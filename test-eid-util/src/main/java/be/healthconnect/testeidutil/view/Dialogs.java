/**
 * (C) 2012 HealthConnect CVBA. All rights reserved.
 */
package be.healthconnect.testeidutil.view;

import be.healthconnect.testeidutil.concurrent.CallResponse;

/**
 * Dialogs utility.
 * 
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 */
public interface Dialogs {

	/**
	 * The result on dialog close.
	 * 
	 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
	 */
	public static enum DialogResult {
		OK, CANCEL
	}

	/**
	 * Notifies the user that an error occurred.
	 * 
	 * @param exception
	 *            the error
	 * @param onClose
	 *            callback method that is invoked when the dialog is closed, if null the dialog is just closed without performing any extra
	 *            actions.
	 */
	void error(Throwable exception, Runnable onClose);

	/**
	 * Displays an OK/Cancel message dialog.
	 * 
	 * @param message
	 *            the message to display
	 * @param cancelVisible
	 *            whether to display a Cancel button
	 * @param onClose
	 *            callback method that is invoked when the dialog is closed
	 */
	void message(String message, String title, boolean cancelVisible, CallResponse<DialogResult> onClose);

	/**
	 * Displays an OK/Cancel message dialog with the specified button texts.
	 * 
	 * @param message
	 *            the message to display
	 * @param cancelVisible
	 *            whether to display a Cancel button
	 * @param onClose
	 *            callback method that is invoked when the dialog is closed
	 * @param okBtnText
	 *            Text on OK button.
	 * @param cancelBtnText
	 *            Text on cancel button.
	 */
	void message(String message, String title, boolean cancelVisible, CallResponse<DialogResult> onClose, String okBtnText,
			String cancelBtnText);

	/**
	 * Hides currently opened dialogs.
	 */
	void hideCurrentDialogs();

	/**
	 * Reopens current dialogs.
	 */
	void reopenCurrentDialogs();

	/**
	 * Checks if the current dialog is active.
	 * 
	 * @return active
	 */
	boolean isActive();

}
