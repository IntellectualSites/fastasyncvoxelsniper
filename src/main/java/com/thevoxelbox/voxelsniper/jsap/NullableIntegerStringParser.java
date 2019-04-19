package com.thevoxelbox.voxelsniper.jsap;

import com.martiansoftware.jsap.ParseException;
import com.martiansoftware.jsap.StringParser;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link com.martiansoftware.jsap.StringParser} for parsing Integers. The parse() method delegates the actual parsing to Integer.decode(String).
 *
 * @author <a href="http://www.martiansoftware.com/contact.html">Marty Lamb</a>
 * @see com.martiansoftware.jsap.StringParser
 * @see Integer
 */
public class NullableIntegerStringParser extends StringParser {

	@SuppressWarnings("unused")
	private static final NullableIntegerStringParser INSTANCE = new NullableIntegerStringParser();

	/**
	 * Convenient access to the only instance returned by this method is available through {@link com.martiansoftware.jsap.JSAP#INTEGER_PARSER}.
	 */
	public static NullableIntegerStringParser getParser() {
		return new NullableIntegerStringParser();
	}

	/**
	 * Parses the specified argument into an Integer. This method delegates the parsing to {@code Integer.decode(arg)}. If {@code Integer.decode()}
	 * throws a NumberFormatException, it is encapsulated into a ParseException and re-thrown.
	 *
	 * @param arg the argument to parse
	 * @return an Integer object with the value contained in the specified argument.
	 * @throws com.martiansoftware.jsap.ParseException if {@code Integer.decode(arg)} throws a NumberFormatException.
	 * @see Integer
	 */
	@Nullable
	@Override
	public final Object parse(String arg) throws ParseException {
		if (arg == null) {
			return null;
		}
		Integer result;
		try {
			result = Integer.decode(arg);
		} catch (NumberFormatException nfe) {
			throw (new ParseException("Unable to convert '" + arg + "' to an Integer.", nfe));
		}
		return (result);
	}
}
