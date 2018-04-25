/*
 * (C) 2012-2016 HealthConnect NV. All rights reserved.
 */
package be.healthconnect.testeidutil.controls;

import com.sun.javafx.tk.Toolkit;

import be.healthconnect.testeidutil.util.OSUtility;
import javafx.beans.value.ChangeListener;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.BlendMode;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;

/**
 * Abstract modal dialog window.
 * 
 * @author <a href="mailto:debasmita.sahoo@e-zest.in">Debasmita Sahoo</a>
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 */
@SuppressWarnings("restriction")
public abstract class ModalDialog extends Dialog {

	private static final double OVERLAY_OPACITY = 0.5;
	protected final Group group = new Group();
	protected final Rectangle overlay = new Rectangle();

	private boolean inNestedEventLoop = false;

	private final ChangeListener<Boolean> focusListener = (observable, oldValue, newValue) -> {
		if (newValue) {
			open(true);
		} else {
			hide(true);
		}
	};

	/**
	 * Creates a new {@link ModalDialog}.
	 * 
	 * @param owner
	 *            the owning window
	 */
	public ModalDialog(final Window owner) {
		super(owner);
		group.setBlendMode(BlendMode.DARKEN);
		overlay.setOpacity(OVERLAY_OPACITY);
		overlay.widthProperty().bind(owner.widthProperty());
		overlay.heightProperty().bind(owner.heightProperty());
	}

	@Override
	public void hide() {
		hide(false);
	}

	/**
	 * Opens (shows) the dialog.
	 * 
	 * @param owner
	 *            the owner window
	 */
	@Override
	public void open() {
		open(false);
	}

	/**
	 * Opens (shows) the dialog and waits for the result.
	 *
	 * @param owner
	 *            the owner window
	 */
	public void openAndWait() {
		open();
		setOnHiding(windowEvent -> {
			if (isInNestedEventLoop()) {
				setInNestedEventLoop(false);
				Toolkit.getToolkit().exitNestedEventLoop(this, null);
			}
		});
		setInNestedEventLoop(true);
		Toolkit.getToolkit().enterNestedEventLoop(this);
	}

	/**
	 * Hides the popup.
	 * 
	 * @param fromListener
	 *            boolean indicating if called from focused listener
	 */
	private void hide(final boolean fromListener) {
		// if dealing with a root which is a stackpane it is much faster to add the overlay directly to it instead of replacing the root
		final Parent sceneRoot = owner.getScene().getRoot();
		if (sceneRoot != null && sceneRoot instanceof StackPane) {
			if (!fromListener) {
				final StackPane rootStackPane = (StackPane) sceneRoot;
				rootStackPane.getChildren().remove(overlay);
			}
		} else if (!group.getChildren().isEmpty()) {
			assert group.getChildren().size() == 2;
			final Parent root = (Parent) group.getChildren().get(0);
			group.getChildren().remove(root);
			if (!fromListener) {
				group.getChildren().remove(overlay);
			}
			assert group.getChildren().isEmpty();
			owner.getScene().setRoot(root);
			root.setDisable(false);
		}

		if (OSUtility.isMac() && !fromListener) {
			owner.focusedProperty().removeListener(focusListener);
		}

		super.hide();
	}

	/**
	 * Opens the popup.
	 * 
	 * @param fromListener
	 *            boolean indicating if called from focused listener
	 */
	private void open(final boolean fromListener) {
		super.open();
		final Parent root = owner.getScene().getRoot();
		if (root != null) {
			// if dealing with a root which is a stackpane it is much faster to add the overlay directly to it instead of replacing the root
			if (root instanceof StackPane) {
				if (!fromListener) {
					final StackPane rootStackPane = (StackPane) root;
					if (!rootStackPane.getChildren().contains(overlay)) {
						rootStackPane.getChildren().add(overlay);
					}
				}
			} else if (group.getChildren().isEmpty()) {
				group.getChildren().add(0, root);
				if (!fromListener) {
					group.getChildren().add(overlay);
				}
				final Scene scene = owner.getScene();
				scene.setRoot(group);
				// Bind root Region size to Scene size
				if (root instanceof Region) {
					((Region) root).prefWidthProperty().bind(scene.widthProperty());
					((Region) root).prefHeightProperty().bind(scene.heightProperty());
				}
			}
		}

		if (OSUtility.isMac() && !fromListener) {
			owner.focusedProperty().addListener(focusListener);
		}
	}

	/**
	 * Returns the inNestedEventLoop
	 *
	 * @return the inNestedEventLoop
	 */
	protected boolean isInNestedEventLoop() {
		return inNestedEventLoop;
	}

	/**
	 * Sets the inNestedEventLoop
	 *
	 * @param inNestedEventLoop
	 *            the inNestedEventLoop
	 */
	protected void setInNestedEventLoop(final boolean inNestedEventLoop) {
		this.inNestedEventLoop = inNestedEventLoop;
	}
}