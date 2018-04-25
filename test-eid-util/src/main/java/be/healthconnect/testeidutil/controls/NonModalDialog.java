/*
 * (C) 2012 HealthConnect CVBA. All rights reserved.
 */
package be.healthconnect.testeidutil.controls;

import javafx.geometry.Pos;
import javafx.stage.Window;

/**
 * Abstract non modal dialog window.
 * 
 * @author <a href="mailto:debasmita.sahoo@e-zest.in">Debasmita Sahoo</a>
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 */
public class NonModalDialog extends Dialog {

	/**
	 * Creates a new {@link NonModalDialog}.
	 * 
	 * @param owner
	 *            the owning window
	 */
	public NonModalDialog(Window owner) {
		super(owner);
		setTitleAlignment(Pos.CENTER_LEFT);
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

}
