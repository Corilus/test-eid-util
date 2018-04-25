/*
 * (C) 2012-2013 HealthConnect NV. All rights reserved.
 */
package be.healthconnect.testeidutil.controls;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * JavaFX container that clips its clippedChildren from the scene graph as soon as it becomes invisible, and puts it back when it becomes
 * visible again. Use this class to remove the screen space taken up by invisible {@link Node}s (same effect as resizing a {@link Node} to
 * zero width/height).
 * 
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 */
public class ClipContainer extends StackPane {

	protected final Set<Node> clippedChildren = new LinkedHashSet<>();

	/**
	 * Creates a new {@link ClipContainer}.
	 * 
	 * @param children
	 *            the contained children
	 */
	public ClipContainer(final Node... children) {
		this.clippedChildren.addAll(Arrays.asList(children));
		clippedChildren.forEach(this::addChildListener);
	}

	/**
	 * Creates a new {@link ClipContainer}.
	 * 
	 * @param children
	 *            the contained children
	 */
	public ClipContainer(Pos pos, final Node... children) {
		setAlignment(pos);
		this.clippedChildren.addAll(Arrays.asList(children));
		clippedChildren.forEach(this::addChildListener);
	}

	/**
	 * Adds visibility listener to the given child node.
	 * 
	 * @param child
	 *            the child node
	 */
	private void addChildListener(final Node child) {
		final ChangeListener<Boolean> visibleChangeListener = (observable, oldVal, newVal) -> {
			final List<Node> children = getChildren();
			if (newVal) {
				if (!children.contains(child)) {
					children.add(child);
				}
			} else {
				if (children.contains(child)) {
					children.remove(child);
				}
			}
		};
		child.visibleProperty().addListener(visibleChangeListener);
		visibleChangeListener.changed(child.visibleProperty(), false, child.isVisible());
	}

}
