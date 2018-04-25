package be.healthconnect.testeidutil.concurrent;

/**
 * A task that responds to a computed value.
 * 
 * @author <a href="mailto:dennis.wagelaar@healthconnect.be">Dennis Wagelaar</a>
 * 
 * @param <V>
 *            the value type
 */
public interface CallResponse<V> {

	/**
	 * Performs a response action based on <code>value</code>.
	 * 
	 * @param value
	 *            the value to respond to
	 */
	void response(V value);

}