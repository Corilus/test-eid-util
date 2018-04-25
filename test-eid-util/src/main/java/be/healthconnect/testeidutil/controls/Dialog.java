/*
 * (C) 2012-2015 HealthConnect NV. All rights reserved.
 */
package be.healthconnect.testeidutil.controls;

import be.healthconnect.testeidutil.util.OSUtility;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 * Abstract dialog window.
 * 
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 * @author <a href="mailto:debasmita.sahoo@e-zest.in">Debasmita Sahoo</a>
 * @author <a href="mailto:peter.mylemans@healthconnect.be">Peter Mylemans</a>
 */
public abstract class Dialog extends Popup {

	private static final double DIALOG_OWNER_OFFSET_X = 100.0;
	private static final double DIALOG_OWNER_OFFSET_Y = 100.0;

	/**
	 * Dialog event type.
	 * 
	 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
	 */
	public static class DialogEvent extends Event {

		private static final long serialVersionUID = 987155314774986770L;

		public DialogEvent(final EventType<? extends DialogEvent> eventType) {
			super(eventType);
		}

	}

	/** Dialog close event. */
	public static final EventType<DialogEvent> CLOSE = new EventType<>("close");

	protected final Button closeButton;
	protected final Label titleLabel;
	protected final StackPane titleLabelContainer;
	protected final BorderPane top;
	protected final BorderPane body;
	protected final Window owner;

	private final SimpleObjectProperty<EventHandler<DialogEvent>> onDialogClosed = new SimpleObjectProperty<>(this, "onDialogClosed");

	private double startDragX;
	private double startDragY;
	private boolean hideOnClose;
	private InvalidationListener xListener;
	private InvalidationListener yListener;
	private InvalidationListener windowFocusedListener;

	/**
	 * Creates a new {@link Dialog}.
	 * 
	 * @param owner
	 *            the owning window
	 */
	public Dialog(final Window owner) {
		super();
		this.owner = owner;
		if (owner == null) {
			throw new IllegalArgumentException("Owner cannot be null");
		}
		setHideOnEscape(false);

		closeButton = new Button();
		closeButton.getStyleClass().add("dialog-close-button");
		closeButton.setVisible(false);
		closeButton.setFocusTraversable(false);
		closeButton.setOnAction(event -> {
			if (isHideOnClose()) {
				hide();
			}
			final EventHandler<DialogEvent> onDialogClosed = getOnDialogClosed();
			if (onDialogClosed != null) {
				onDialogClosed.handle(new DialogEvent(CLOSE));
			}
		});
		titleLabel = new Label();
		titleLabel.getStyleClass().add("dialog-header-title");
		titleLabel.setVisible(false);
		
		titleLabelContainer = new ClipContainer(titleLabel);
		
		top = new BorderPane();
		top.getStyleClass().add("dialog-header");
		top.setCenter(titleLabelContainer);
		top.setRight(closeButton);

		body = new BorderPane();
		body.getStyleClass().add("dialog-body");
		body.maxWidthProperty().bind(owner.widthProperty().subtract(DIALOG_OWNER_OFFSET_X));
		body.maxHeightProperty().bind(owner.heightProperty().subtract(DIALOG_OWNER_OFFSET_Y));

		getContent().add(body);

		final InvalidationListener visibleListener = property -> {
			if (isTitleVisible() || isCloseVisible()) {
				body.setTop(top);
				addDragListeners(top);
			} else {
				removeDragListeners(top);
				body.setTop(null);
			}
		};
		titleVisibleProperty().addListener(visibleListener);
		closeVisibleProperty().addListener(visibleListener);
		getStylesheets().add("be/healthconnect/javafx/controls/controls.css");
		getStyleClass().add("dialog");

	}

	/**
	 * Updates the horizontal position from {@link #owner}.
	 */
	protected void updateX() {
		setX(owner.getX() + owner.getWidth() / 2.0 - getScene().getWidth() / 2.0);
	}

	/**
	 * Updates the vertical position from {@link #owner}.
	 */
	protected void updateY() {
		setY(owner.getY() + owner.getHeight() / 2.0 - getScene().getHeight() / 2.0);
	}

	/**
	 * Opens (shows) the dialog.
	 */
	public void open() {
		show(owner);
		updateX();
		updateY();
		if (xListener == null) {
			xListener = property -> {
				if (isShowing()) {
					updateX();
				}
			};
			owner.xProperty().addListener(xListener);
			owner.widthProperty().addListener(xListener);
		}
		if (yListener == null) {
			yListener = property -> {
				if (isShowing()) {
					updateY();
				}
			};
			owner.yProperty().addListener(yListener);
			owner.heightProperty().addListener(yListener);
		}
		if (windowFocusedListener == null) {
			// On windows the popup windows stick on top of other applications, so we hide them manually when the main window loses focus.
			if (OSUtility.isWindows()) {
				windowFocusedListener = property -> {
					if (!owner.isFocused() && isShowing()) {
						Dialog.super.hide();
					} else if (!isShowing()) {
						show(owner);
						updateX();
						updateY();
					}
				};
				owner.focusedProperty().addListener(windowFocusedListener);
			}
		}
	}

	@Override
	public void hide() {
		if (windowFocusedListener != null) {
			owner.focusedProperty().removeListener(windowFocusedListener);
			windowFocusedListener = null;
		}
		if (xListener != null) {
			owner.xProperty().removeListener(xListener);
			owner.widthProperty().removeListener(xListener);
			xListener = null;
		}
		if (yListener != null) {
			owner.yProperty().removeListener(yListener);
			owner.heightProperty().removeListener(yListener);
			yListener = null;
		}
		super.hide();
	}

	/**
	 * Returns the applicable CSS stylesheets.
	 * 
	 * @return the applicable CSS stylesheets
	 */
	public ObservableList<String> getStylesheets() {
		return body.getStylesheets();
	}

	/**
	 * Returns the applicable CSS style class.
	 * 
	 * @return the applicable CSS style class
	 */
	public ObservableList<String> getStyleClass() {
		return body.getStyleClass();
	}

	/**
	 * Returns the dialog contents panel.
	 * 
	 * @return the contents
	 */
	public Node getContents() {
		return body.getCenter();
	}

	/**
	 * Sets the dialog contents panel
	 * 
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(final Node contents) {
		body.setCenter(contents);
	}

	/**
	 * Returns the dialog contents panel observable property.
	 * 
	 * @return the dialog contents panel observable property
	 */
	public ObjectProperty<Node> contentsProperty() {
		return body.centerProperty();
	}

	/**
	 * Returns the dialog closed event handler.
	 * 
	 * @return the dialog closed event handler
	 */
	public EventHandler<DialogEvent> getOnDialogClosed() {
		return onDialogClosed.getValue();
	}

	/**
	 * Sets the dialog closed event handler.
	 * 
	 * @param onDialogClosed
	 *            the dialog closed event handler to set
	 */
	public void setOnDialogClosed(final EventHandler<DialogEvent> onDialogClosed) {
		this.onDialogClosed.setValue(onDialogClosed);
	}

	/**
	 * Returns the dialog closed event handler observable property.
	 * 
	 * @return the dialog closed event handler observable property
	 */
	public SimpleObjectProperty<EventHandler<DialogEvent>> onDialogClosedProperty() {
		return onDialogClosed;
	}

	/**
	 * Returns whether the close button should be visible.
	 * 
	 * @return whether the close button should be visible
	 */
	public boolean isCloseVisible() {
		return closeButton.isVisible();
	}

	/**
	 * Sets whether the close button should be visible.
	 * 
	 * @param closeVisible
	 *            whether the close button should be visible
	 */
	public void setCloseVisible(final boolean closeVisible) {
		closeButton.setVisible(closeVisible);
	}

	/**
	 * Returns the close button visibility observable property.
	 * 
	 * @return the close button visibility observable property
	 */
	public BooleanProperty closeVisibleProperty() {
		return closeButton.visibleProperty();
	}

	public String getTitle() {
		return titleLabel.getText();
	}

	public void setTitle(final String title) {
		titleLabel.setText(title);
	}

	public StringProperty titleProperty() {
		return titleLabel.textProperty();
	}

	public boolean isTitleVisible() {
		return titleLabel.isVisible();
	}

	public void setTitleVisible(final boolean titleVisible) {
		titleLabel.setVisible(titleVisible);
	}

	public BooleanProperty titleVisibleProperty() {
		return titleLabel.visibleProperty();
	}

	/**
	 * Sets the minimum width for dialog body.
	 * 
	 * @param width
	 *            the minimum width to set
	 */
	public void setBodyMinWidth(final double width) {
		body.setMinWidth(width);
	}

	/**
	 * Returns the minimum width for dialog body in pixels.
	 * 
	 * @return the minimum width for dialog body in pixels
	 */
	public double getBodyMinWidth() {
		return body.getMinWidth();
	}

	/**
	 * Returns the observable minWidth property.
	 * 
	 * @return the observable minWidth property
	 */
	public ReadOnlyDoubleProperty bodyMinWidthProperty() {
		return body.minWidthProperty();
	}

	/**
	 * Sets the minimum height for dialog body.
	 * 
	 * @param height
	 *            the minimum height to set
	 */
	public void setBodyMinHeight(final double height) {
		body.setMinHeight(height);
	}

	/**
	 * Sets the minimum height for dialog body in pixels.
	 * 
	 * @return the minimum height for dialog body in pixels
	 */
	public double getBodyMinHeight() {
		return body.getMinHeight();
	}

	/**
	 * Returns the observable minHeight property.
	 * 
	 * @return the observable minHeight property
	 */
	public ReadOnlyDoubleProperty bodyMinHeightProperty() {
		return body.minHeightProperty();
	}

	/**
	 * Sets the preferred width for dialog body.
	 * 
	 * @param width
	 *            the preferred width to set
	 */
	public void setBodyPrefWidth(final double width) {
		body.setPrefWidth(width);
	}

	/**
	 * Returns the preferred width for dialog body in pixels.
	 * 
	 * @return the preferred width for dialog body in pixels
	 */
	public double getBodyPrefWidth() {
		return body.getPrefWidth();
	}

	/**
	 * Returns the observable prefWidth property.
	 * 
	 * @return the observable prefWidth property
	 */
	public ReadOnlyDoubleProperty bodyPrefWidthProperty() {
		return body.prefWidthProperty();
	}

	/**
	 * Sets the preferred height for dialog body.
	 * 
	 * @param height
	 *            the preferred height to set
	 */
	public void setBodyPrefHeight(final double height) {
		body.setPrefHeight(height);
	}

	/**
	 * Returns the preferred height for dialog body in pixels.
	 * 
	 * @return the preferred height for dialog body in pixels
	 */
	public double getBodyPrefHeight() {
		return body.getPrefHeight();
	}

	/**
	 * Returns the observable prefHeight property.
	 * 
	 * @return the observable prefHeight property
	 */
	public ReadOnlyDoubleProperty bodyPrefHeightProperty() {
		return body.prefHeightProperty();
	}

	/**
	 * Returns the individual disabled state of this {@link Dialog}'s body.
	 * 
	 * @return the individual disabled state of this {@link Dialog}'s body
	 */
	public boolean isDisable() {
		return body.isDisable();
	}

	/**
	 * Sets the individual disabled state of this {@link Dialog}'s body;
	 * 
	 * @param disable
	 *            the disable state to set
	 */
	public void setDisable(final boolean disable) {
		body.setDisable(disable);
	}

	/**
	 * Returns the individual disabled state observable property of this {@link Dialog}'s body.
	 * 
	 * @return the individual disabled state observable property of this {@link Dialog}'s body
	 */
	public BooleanProperty disableProperty() {
		return body.disableProperty();
	}

	/**
	 * Aligns the title position in the header.
	 * 
	 * @param pos
	 *            the position of the title.
	 */
	public void setTitleAlignment(final Pos pos) {
		titleLabelContainer.setAlignment(pos);
	}

	/**
	 * Adds mouse drag listeners to the given {@link Node} for moving the popup window.
	 * 
	 * @param node
	 *            the {@link Node}
	 */
	private void addDragListeners(final Node node) {
		node.setOnMousePressed(mouseEvent -> {
			if (mouseEvent.getButton() == MouseButton.PRIMARY) {
				startDragX = mouseEvent.getSceneX();
				startDragY = mouseEvent.getSceneY();
			} else {
				startDragX = -1.0;
				startDragY = -1.0;
			}
		});

		node.setOnMouseDragged(mouseEvent -> {
			if (startDragX >= 0.0 && startDragY >= 0.0) {
				Dialog.this.setX(mouseEvent.getScreenX() - startDragX);
				Dialog.this.setY(mouseEvent.getScreenY() - startDragY);
			}
		});
	}

	/**
	 * Removes mouse drag listeners from the given {@link Node} for moving the popup window.
	 * 
	 * @param n
	 *            the {@link Node}
	 */
	private void removeDragListeners(final Node n) {
		n.setOnMousePressed(null);
		n.setOnMouseDragged(null);
	}

	/**
	 * Returns whether the dialog should hide or not when the close button is pressed.
	 * 
	 * @return the hideOnClose true if the dialog should hide, false otherwise
	 */
	public boolean isHideOnClose() {
		return hideOnClose;
	}

	/**
	 * Sets the property whether the dialog should hide or not when the close button is pressed.
	 * 
	 * @param hideOnClose
	 *            true when the dialog should close, false otherwise
	 */
	public void setHideOnClose(final boolean hideOnClose) {
		this.hideOnClose = hideOnClose;
	}

}