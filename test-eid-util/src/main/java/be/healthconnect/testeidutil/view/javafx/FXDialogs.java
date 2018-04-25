/**
 * (C) 2012-2015 HealthConnect NV. All rights reserved.
 */
package be.healthconnect.testeidutil.view.javafx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import be.healthconnect.testeidutil.concurrent.CallResponse;
import be.healthconnect.testeidutil.controls.MessageDialog;
import be.healthconnect.testeidutil.util.Reversed;
import be.healthconnect.testeidutil.view.Dialogs;
import javafx.event.EventType;
import javafx.stage.Stage;

/**
 * JavaFX implementation for {@link Dialogs}.
 * 
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 */
public class FXDialogs implements Dialogs {
	protected final Stage root;
	protected final List<MessageDialog> currentMessageDialogs = Collections.synchronizedList(new ArrayList<>());

	/**
	 * Creates a new {@link FXDialogs} instance.
	 * 
	 * @param root
	 *            the root window
	 */
	public FXDialogs(final Stage root) {
		super();
		this.root = root;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void error(final Throwable exception, final Runnable onClose) {
		final String title = "Error";
		String msg = exception.getLocalizedMessage();
		if (msg == null || msg.isEmpty()) {
			msg = exception.getClass().getName();
		}
		final MessageDialog messageDialog;
			messageDialog = new MessageDialog(root);
			messageDialog.setMessage(msg);
			messageDialog.setTitle(title);
			messageDialog.setMaxContentWidth(500);
			messageDialog.setTitleVisible(true);
			messageDialog.setCloseVisible(true);
			messageDialog.setCancelVisible(false);
			messageDialog.setHideOnClose(true);

		messageDialog.setOnDialogClosed(dialogEvent -> {
			currentMessageDialogs.remove(messageDialog);
			// [DMW] CAEMR-6820: fix memory leak
			messageDialog.setOnDialogClosed(null);
			if (onClose != null) {
				// [GBA] CAEMR-9596: call run() to execute task in same thread
				onClose.run();
			}
		});
		messageDialog.open();
		currentMessageDialogs.add(messageDialog);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void message(final String message, final String title, final boolean cancelVisible, final CallResponse<DialogResult> onClose) {
		message(message, title, cancelVisible, onClose, "OK", "Cancel");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void message(final String message, final String title, final boolean cancelVisible, final CallResponse<DialogResult> onClose,
			final String okBtnText, final String cancelBtnText) {
		final boolean topVisible = (title != null && !title.isEmpty());
		final MessageDialog messageDialog = new MessageDialog(root);
		messageDialog.setMessage(message);
		messageDialog.setTitle(title);
		messageDialog.setMaxContentWidth(350);
		messageDialog.setCloseVisible(topVisible);
		messageDialog.setTitleVisible(topVisible);
		messageDialog.setCancelVisible(cancelVisible);
		messageDialog.setOkText(okBtnText);
		messageDialog.setCancelText(cancelBtnText);
		messageDialog.setHideOnClose(true);
		messageDialog.setOnDialogClosed(dialogEvent -> {
			currentMessageDialogs.remove(messageDialog);
			// [DMW] CAEMR-6820: fix memory leak
			messageDialog.setOnDialogClosed(null);
			final EventType<?> eventType = dialogEvent.getEventType();
			if (onClose != null) {
				onClose.response(eventType == MessageDialog.OK ? DialogResult.OK : DialogResult.CANCEL);
			}
		});
		messageDialog.open();
		currentMessageDialogs.add(messageDialog);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hideCurrentDialogs() {
		for (final MessageDialog messageDialog : new Reversed<>(currentMessageDialogs)) {
			messageDialog.hide();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reopenCurrentDialogs() {
		currentMessageDialogs.forEach(MessageDialog::open);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isActive() {
		return !currentMessageDialogs.isEmpty();
	}

}
