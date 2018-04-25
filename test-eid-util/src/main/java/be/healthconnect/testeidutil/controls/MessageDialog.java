/*
 * (C) 2012-2015 HealthConnect NV. All rights reserved.
 */
package be.healthconnect.testeidutil.controls;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Window;

/**
 * Message dialog window.
 * 
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 */
public class MessageDialog extends ModalDialog {

	/** Dialog OK selected event. */
	public static EventType<DialogEvent> OK = new EventType<>(EventType.ROOT, "MessageDialog.OK");
	/** Dialog Cancel selected event. */
	public static EventType<DialogEvent> CANCEL = new EventType<>(EventType.ROOT, "MessageDialog.CANCEL");

	protected final TextFlow textFlow;
	protected final Text text;
	protected final Button okButton;
	protected final Button cancelButton;
	protected final StackPane centerPane;

	private final BooleanProperty cancelVisible = new SimpleBooleanProperty(this, "cancelVisible");

	private StringProperty message;
	private String messageNodesText;

	/**
	 * Creates a new {@link MessageDialog}.
	 * 
	 * @param owner
	 *            the owning window
	 */
	public MessageDialog(final Window owner) {
		super(owner);

		final Text text = new Text();
		text.getStyleClass().add("label");
		this.text = text;

		final TextFlow textFlow = new TextFlow(text);
		this.textFlow = textFlow;

		final Button okButton = new Button();
		okButton.setText("OK");
		okButton.getStyleClass().add("ok-cancel-button");
		this.okButton = okButton;

		final Button cancelButton = new Button();
		cancelButton.setText("Cancel");
		cancelButton.getStyleClass().add("ok-cancel-button");
		this.cancelButton = cancelButton;

		final StackPane centerPane = new StackPane();
		centerPane.getChildren().add(textFlow);
		centerPane.setPadding(new Insets(15));
		this.centerPane = centerPane;

		getStyleClass().add("message-dialog");
		setContents(buildContents());
		setHideOnClose(true);
	}

	/**
	 * Builds the dialog contents.
	 * 
	 * @return the dialog content pane
	 */
	protected Parent buildContents() {
		text.textProperty().bind(messageProperty());

		okButton.setOnAction(event -> {
			if (isHideOnClose()) {
				hide();
			}
			final EventHandler<DialogEvent> handler = getOnDialogClosed();
			if (handler != null) {
				handler.handle(new DialogEvent(OK));
			}
		});

		cancelButton.setOnAction(event -> {
			if (isHideOnClose()) {
				hide();
			}
			final EventHandler<DialogEvent> handler = getOnDialogClosed();
			if (handler != null) {
				handler.handle(new DialogEvent(CANCEL));
			}
		});

		final HBox buttonBar = new HBox();
		buttonBar.setAlignment(Pos.CENTER);
		buttonBar.getStyleClass().add("message-dialog-buttons");
		buttonBar.getChildren().add(okButton);

		cancelVisibleProperty().addListener((InvalidationListener) property -> {
			if (cancelButton.isVisible()) {
				buttonBar.getChildren().add(cancelButton);
			} else {
				buttonBar.getChildren().remove(cancelButton);
			}
		});

		final BorderPane contents = new BorderPane();
		contents.setMinWidth(200.0);
		contents.getStyleClass().add("message-dialog-body");
		contents.setCenter(centerPane);
		contents.setBottom(buttonBar);
		contents.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ESCAPE)) {
				cancelButton.fire();
			}
		});
		return contents;
	}

	@Override
	public void show() {
		super.show();
		okButton.setDefaultButton(true);
		cancelButton.setCancelButton(true);
	}

	@Override
	public void hide() {
		okButton.setDefaultButton(false);
		cancelButton.setCancelButton(false);
		super.hide();
	}

	/**
	 * Returns the dialog message.
	 * 
	 * @return the dialog message
	 */
	public String getMessage() {
		return messageProperty().getValue();
	}

	/**
	 * Sets the dialog message
	 * 
	 * @param message
	 *            the dialog message to set
	 */
	public void setMessage(final String message) {
		messageProperty().setValue(message);
	}

	/**
	 * Returns the dialog message observable property.
	 * 
	 * @return the dialog message observable property
	 */
	public StringProperty messageProperty() {
		if (message == null) {
			message = new SimpleStringProperty();
		}
		return message;
	}

	/**
	 * Returns whether the cancel button should be visible.
	 * 
	 * @return whether the cancel button should be visible
	 */
	public boolean getCancelVisible() {
		return cancelVisible.get();
	}

	/**
	 * Sets whether the cancel button should be visible.
	 * 
	 * @param cancelVisible
	 *            whether the cancel button should be visible
	 */
	public void setCancelVisible(final boolean cancelVisible) {
		this.cancelVisible.set(cancelVisible);
	}

	/**
	 * Returns the cancel button visibility observable property.
	 * 
	 * @return the cancel button visibility observable property
	 */
	public BooleanProperty cancelVisibleProperty() {
		return cancelVisible;
	}

	/**
	 * Sets Maximum width for message dialog label.
	 * 
	 * @param width
	 */
	public void setMaxContentWidth(final double width) {
		textFlow.setMaxWidth(width);
	}

	public String getOkText() {
		return okButton.getText();
	}

	public void setOkButtonHandler(final EventHandler<ActionEvent> eventHandler) {
		okButton.setOnAction(eventHandler);
	}

	public void setOkText(final String text) {
		okButton.setText(text);
	}

	public StringProperty okTextProperty() {
		return okButton.textProperty();
	}

	public String getCancelText() {
		return cancelButton.getText();
	}

	public void setCancelText(final String text) {
		cancelButton.setText(text);
	}

	public StringProperty cancelTextProperty() {
		return cancelButton.textProperty();
	}

	/**
	 * Adds the given {@link Node} to the dialog.
	 * 
	 * @param messageNode
	 *            the node
	 */
	public void addMessageNode(final Node messageNode) {
		centerPane.getChildren().add(messageNode);
	}

	/**
	 * Returns the messageNodesText.
	 *
	 * @return the messageNodesText
	 */
	public String getMessageNodesText() {
		return messageNodesText;
	}

	/**
	 * Sets the messageNodesText.
	 * 
	 * @param messageNodesText
	 *            the messageNodesText
	 */
	public void setMessageNodesText(final String messageNodesText) {
		this.messageNodesText = messageNodesText;
	}
}
