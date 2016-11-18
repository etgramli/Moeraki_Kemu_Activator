package services;

public class AjaxHelper {

	private static final String DQUOTES = "\"";
	public static final String JSONARRAYDELIMITER = ", ";
	
	/**
	 * Takes a parameter (from AJAX call) and extracts the x and y coordinate.
	 *
	 * @param param String that must match the pattern [0-9]+/[0-9]*
	 * @return An array of int with the length of 2.
	 */
	public static final int[] splitXY(final String param) {
	    final int idx = param.indexOf("-");
	    final int i = Integer.valueOf(param.substring(0, idx));
	    final int j = Integer.valueOf(param.substring(idx + 1));
	    return new int[]{i, j};
	}
	
	public static final String JsonEscapeValue(final String value) {
		return DQUOTES + value + DQUOTES;
	}
	
}
