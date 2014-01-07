package org.apache.peer.security;

/**
 * @author Jian Fang (John.Jian.Fang@gmail.com)
 *
 * Date: Jun 26, 2009
 */
public class HOTPException extends SecurityException {
	private static final long serialVersionUID = -1142969500466612077L;

	public final static char ERROR_SEPARATOR = '\n';

	HOTPCode code;

	public final HOTPCode getHOTPCode() {
		return code;
	}

	public HOTPException(HOTPCode code, String msg, Throwable t) {
		super(code.toString() + ERROR_SEPARATOR + msg, t);
		this.code = code;
	}

	public HOTPException(HOTPCode code, String msg) {
		super(code.toString() + ERROR_SEPARATOR + msg);
		this.code = code;
	}

}
