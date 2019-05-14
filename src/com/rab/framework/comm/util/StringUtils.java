package com.rab.framework.comm.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * 
 * <P>Title: StringUtils</P>
 * <P>Description: </P>
 * <P>程序说明：字串处理工具包</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public class StringUtils {
	private StringUtils() {
	};

	/**
	 * 将给定的字符串格式化为定长字符串, 原始字符串长度超过给定长度的,按照给定长度从左到右截取 如果原始字符串小于给定长??,
	 * 则按照给定字符在左端补足空位
	 * 
	 * @param src
	 *            原始字符??
	 * @param s2
	 *            补充用字??,
	 * @param length
	 *            格式化后长度
	 * @return 格式化后字符??
	 */
	public static String formatString(String src, char s2, int length) {
		String retValue = src;
		if (src == null || length <= 0) {
			return null;
		}

		if (src.length() > length) {
			retValue = src.substring(0, length);
		}

		for (int i = 0; i < length - src.length(); i++) {
			retValue = s2 + retValue;
		}

		return retValue;
	}

	/**
	 * 替换原字符串中指定起止位置的内容
	 * 
	 * @param src
	 *            String 源字符串
	 * @param start
	 *            int 起始位置
	 * @param end
	 *            int 结束位置
	 * @param dst
	 *            String 要替换成的字符串
	 * @return String 替换后的字符??
	 * @author 于英??
	 * @since 2004-04-21
	 */
	public static String replaceByPos(String src, int start, int end, String dst) {
		if ((src == null) || (dst == null) || (end < start)) {
			return null;
		} else {
			return src.substring(0, start) + dst
					+ src.substring(end + 1, src.length());
		}
	}

	/**
	 * Count the occurrences of the substring in string s.
	 * 
	 * @param s
	 *            string to search in. Return 0 if this is null.
	 * @param sub
	 *            string to search for. Return 0 if this is null.
	 */
	public static int countOccurrencesOf(String s, String sub) {
		if (s == null || sub == null || "".equals(sub)) {
			return 0;
		}
		int count = 0, pos = 0, idx = 0;
		while ((idx = s.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
		}
		return count;
	}

	/**
	 * Replace all occurences of a substring within a string with another
	 * string.
	 * 
	 * @param inString
	 *            String to examine
	 * @param oldPattern
	 *            String to replace
	 * @param newPattern
	 *            String to insert
	 * @return a String with the replacements
	 */
	public static String replace(String inString, String oldPattern,
			String newPattern) {
		if (inString == null) {
			return null;
		}
		if (oldPattern == null || newPattern == null) {
			return inString;
		}

		StringBuffer sbuf = new StringBuffer();
		// output StringBuffer we'll build up
		int pos = 0; // Our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sbuf.append(inString.substring(pos, index));
			sbuf.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sbuf.append(inString.substring(pos));

		// remember to append any characters to the right of a match
		return sbuf.toString();
	}

	/**
	 * Delete all occurrences of the given substring.
	 * 
	 * @param pattern
	 *            the pattern to delete all occurrences of
	 */
	public static String delete(String inString, String pattern) {
		return replace(inString, pattern, "");
	}

	/**
	 * Delete any character in a given string.
	 * 
	 * @param chars
	 *            characters to delete. E.g. az\n will delete as, zs and new
	 *            lines.
	 */
	public static String deleteAny(String inString, String chars) {
		if (inString == null || chars == null) {
			return inString;
		}
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (chars.indexOf(c) == -1) {
				out.append(c);
			}
		}
		return out.toString();
	}

	/**
	 * Tokenize the given String into a String array via a StringTokenizer.
	 * 
	 * @param s
	 *            the String to tokenize
	 * @param delimiters
	 *            the delimiter characters, assembled as String
	 * @param trimTokens
	 *            trim the tokens via String.trim
	 * @param ignoreEmptyTokens
	 *            omit empty tokens from the result array
	 * @return an array of the tokens
	 * @see java.util.StringTokenizer
	 * @see java.lang.String#trim
	 */
	public static String[] tokenizeToStringArray(String s, String delimiters,
			boolean trimTokens, boolean ignoreEmptyTokens) {
		StringTokenizer st = new StringTokenizer(s, delimiters);
		List<String> tokens = new ArrayList<String>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!(ignoreEmptyTokens && token.length() == 0)) {
				tokens.add(token);
			}
		}
		return (String[]) tokens.toArray(new String[tokens.size()]);
	}

	/**
	 * Take a String which is a delimited list and convert it to a String array.
	 * 
	 * @param s
	 *            String
	 * @param delim
	 *            delim (this will not be returned)
	 * @return an array of the tokens in the list
	 */
	public static String[] delimitedListToStringArray(String s, String delim) {
		if (s == null) {
			return new String[0];
		}
		if (delim == null) {
			return new String[] { s };
		}

		List<String> l = new LinkedList<String>();
		int pos = 0;
		int delPos = 0;
		while ((delPos = s.indexOf(delim, pos)) != -1) {
			l.add(s.substring(pos, delPos));
			pos = delPos + delim.length();
		}
		if (pos <= s.length()) {
			// add rest of String
			l.add(s.substring(pos));
		}

		return (String[]) l.toArray(new String[l.size()]);
	}

	/**
	 * Convert a CSV list into an array of Strings.
	 * 
	 * @param s
	 *            CSV list
	 * @return an array of Strings, or the empty array if s is null
	 */
	public static String[] commaDelimitedListToStringArray(String s) {
		return delimitedListToStringArray(s, ",");
	}

	/**
	 * Convenience method to convert a CSV string list to a set. Note that this
	 * will suppress duplicates.
	 * 
	 * @param s
	 *            CSV String
	 * @return a Set of String entries in the list
	 */
	public static Set<String> commaDelimitedListToSet(String s) {
		Set<String> set = new TreeSet<String>();
		String[] tokens = commaDelimitedListToStringArray(s);
		for (int i = 0; i < tokens.length; i++) {
			set.add(tokens[i]);
		}
		return set;
	}

	/**
	 * Convenience method to return a String array as a delimited (e.g. CSV)
	 * String. E.g. useful for toString() implementations.
	 * 
	 * @param arr
	 *            array to display. Elements may be of any type (toString will
	 *            be called on each element).
	 * @param delim
	 *            delimiter to use (probably a ,)
	 */
	public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (arr == null) {
			return "null";
		} else {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < arr.length; i++) {
				if (i > 0) {
					sb.append(delim);
				}
				sb.append(arr[i]);
			}
			return sb.toString();
		}
	}

	/**
	 * Convenience method to return a Collection as a delimited (e.g. CSV)
	 * String. E.g. useful for toString() implementations.
	 * 
	 * @param c
	 *            Collection to display
	 * @param delim
	 *            delimiter to use (probably a ",")
	 */
	public static String collectionToDelimitedString(Collection<String> c, String delim) {
		if (c == null) {
			return "null";
		}
		StringBuffer sb = new StringBuffer();
		Iterator<String> itr = c.iterator();
		int i = 0;
		while (itr.hasNext()) {
			if (i++ > 0) {
				sb.append(delim);
			}
			sb.append(itr.next());
		}
		return sb.toString();
	}

	/**
	 * Convenience method to return a String array as a CSV String. E.g. useful
	 * for toString() implementations.
	 * 
	 * @param arr
	 *            array to display. Elements may be of any type (toString will
	 *            be called on each element).
	 */
	public static String arrayToCommaDelimitedString(Object[] arr) {
		return arrayToDelimitedString(arr, ",");
	}

	/**
	 * Convenience method to return a Collection as a CSV String. E.g. useful
	 * for toString() implementations.
	 * 
	 * @param c
	 *            Collection to display
	 */
	public static String collectionToCommaDelimitedString(Collection<String> c) {
		return collectionToDelimitedString(c, ",");
	}

	/**
	 * Append the given String to the given String array, returning a new array
	 * consisting of the input array contents plus the given String.
	 * 
	 * @param arr
	 *            the array to append to
	 * @param s
	 *            the String to append
	 * @return the new array
	 */
	public static String[] addStringToArray(String[] arr, String s) {
		String[] newArr = new String[arr.length + 1];
		System.arraycopy(arr, 0, newArr, 0, arr.length);
		newArr[arr.length] = s;
		return newArr;
	}

	/**
	 * Checks if a String has length.
	 * <p>
	 * 
	 * <pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength(&quot;&quot;) = false
	 * StringUtils.hasLength(&quot; &quot;) = true
	 * StringUtils.hasLength(&quot;Hello&quot;) = true
	 * </pre>
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @return <code>true</code> if the String is has length and is not null
	 */
	public static boolean hasLength(String str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Checks if a String has text. More specifically, returns <code>true</code>
	 * if the string not <code>null<code>, it's <code>length is > 0</code>, and it has at least one
	 * non-whitespace character.
	 * <p>
	 * 
	 * <pre>
	 * StringUtils.hasText(null) = false
	 * StringUtils.hasText(&quot;&quot;) = false
	 * StringUtils.hasText(&quot; &quot;) = false
	 * StringUtils.hasText(&quot;12345&quot;) = true
	 * StringUtils.hasText(&quot; 12345 &quot;) = true
	 * </pre>
	 * 
	 * @param str
	 *            the String to check, may be null
	 * @return <code>true</code> if the String is not null, length > 0, and not
	 *         whitespace only
	 */
	public static boolean hasText(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return false;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Unqualifies a string qualified by a '.' dot character. For example,
	 * "this.name.is.qualified", returns "qualified".
	 * 
	 * @param qualifiedName
	 *            the qualified name
	 */
	public static String unqualify(String qualifiedName) {
		return unqualify(qualifiedName, ".");
	}

	/**
	 * Unqualifies a string qualified by a separator character. For example,
	 * "this:name:is:qualified" returns "qualified" if using a ':' separator.
	 * 
	 * @param qualifiedName
	 *            the qualified name
	 * @param separator
	 *            the separator
	 */
	public static String unqualify(String qualifiedName, String separator) {
		return qualifiedName
				.substring(qualifiedName.lastIndexOf(separator) + 1);
	}

	/**
	 * Uncapitalizes a <code>String</code>, changing the first letter to lower
	 * case as per {@link Character#toLowerCase(char)}. No other letters are
	 * changed.
	 * 
	 * @param str
	 *            the String to uncapitalize, may be null
	 * @return the uncapitalized String, <code>null</code> if null String input
	 */
	public static String uncapitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		StringBuffer buf = new StringBuffer(strLen);
		buf.append(Character.toLowerCase(str.charAt(0)));
		buf.append(str.substring(1));
		return buf.toString();
	}

	public static final boolean isEmptyString(String s) {
		if (s == null) {
			return true;
		}
		if ("".equals(s.trim())) {
			return true;
		}
		return false;
	}

	public static final String[] split(String string, String delim) {
		StringTokenizer token = new StringTokenizer(string, delim);
		String[] result = new String[token.countTokens()];
		List<String> tmp = new ArrayList<String>();
		while (token.hasMoreTokens()) {
			tmp.add(token.nextToken());
		}
		tmp.toArray(result);
		return result;
	}

	/**
	 * 返回值定为String[2]
	 */
	public static final String[] splitStringForOracle(String s) {
		if (s == null) {
			return NullStringArray2;
		}
		int length = s.length();
		if (length <= 650) {
			return new String[] { s, null };
		}
		String a = s.substring(0, 650);
		String b = s.substring(650, length);
		return new String[] { a, b };
	}

	private static final String[] NullStringArray2 = new String[2];

	public static String charSetConvert(String src, String fromCharSet,
			String toCharSet) {
		if (src == null) {
			return src;
		}
		try {
			return new String(src.getBytes(fromCharSet), toCharSet);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将iso8859的字符集转换成UTF-8字符集
	 * 
	 * @param src
	 *            iso8859字符串
	 * @return 转化后的字符串,失败返回null
	 */
	public static String isoToUTF8(String src) {
		return charSetConvert(src, "iso-8859-1", "UTF-8");
	}

	/**
	 * 将iso8859的字符集转换成GBK字符集
	 * 
	 * @param src
	 *            iso8859字符串
	 * @return 转化后的字符串,失败返回null
	 */
	public static String isoToGBK(String src) {
		return charSetConvert(src, "iso-8859-1", "GBK");
	}

	/**
	 * 将GBK的字符集转换成iso8859字符集
	 * 
	 * @param src
	 *            GBK字符串
	 * @return 转化后的字符串,失败返回null
	 */
	public static String gbkToISO(String src) {
		return charSetConvert(src, "GBK", "iso-8859-1");
	}

	public static String gbkToUTF8(String src) {
		return charSetConvert(src, "GBK", "UTF-8");
	}

	public static String utftoGBK(String src) {
		return charSetConvert(src, "UTF-8", "GBK");
	}

	public static String utftoISO(String src) {
		return charSetConvert(src, "UTF-8", "iso-8859-1");
	}

	public static String gb2312ToISO(String src) {
		return charSetConvert(src, "GB2312", "iso-8859-1");
	}

	public static String gb2312ToUTF8(String src) {
		return charSetConvert(src, "GB2312", "UTF-8");
	}

	/**
	 * 判断不为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean notNull(String str) {
		boolean result = false;
		if (str != null && !"".equals(str.trim())) {
			result = true;
		}
		return result;
	}

	public static ArrayList<String> splitStr2AL(String input, String delim) {
		if (input == null) {
			return null;
		}
		StringTokenizer myst = null;

		if (delim == null) {
			myst = new StringTokenizer(input);
		} else {
			myst = new StringTokenizer(input, delim);
			// 首先对 1,4,30,yy01,$1,4,30,yy05,$ 拆分成1,4,30,yy01,和1,4,30,yy05,

		}
		ArrayList<String> myAL = new ArrayList<String>();

		while (myst.hasMoreTokens()) {
			myAL.add(myst.nextToken()); //
		}
		return myAL;
	}

	/**
	 * 将null转换为空字符串
	 * 
	 * @param str
	 * @return
	 * @author zhangbin
	 */
	public static String trimToEmpty(String str) {
		return str != null ? str : "";
	}
}
