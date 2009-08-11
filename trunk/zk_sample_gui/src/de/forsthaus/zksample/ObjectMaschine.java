package de.forsthaus.zksample;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.zkoss.zk.ui.Sessions;

/**
 * This class creates randomly demodata out of several textfiles. <br>
 * The textfiles are seperated to the different needed columns. <br>
 * 
 * @author bj
 * @changes sge
 * 
 */
public final class ObjectMaschine implements Serializable {

	private static final long serialVersionUID = 1L;

	final transient private static Random RANDOM;
	final transient private static String[] BLOB;
	final transient private static String[] EMAIL;
	final transient private static String[] HOMEPAGE;
	final transient private static String[] M;
	final transient private static String[] NACH;
	final transient private static String[] ORT;
	final transient private static String[] PLZ;
	final transient private static String[] STRASSE;
	final transient private static String[] TEL;
	final transient private static String[] W;

	public static String getRandomBlob() {
		return BLOB[RANDOM.nextInt(BLOB.length)];
	}

	public static String getRandomEmail() {
		return EMAIL[RANDOM.nextInt(EMAIL.length)];
	}

	public static String getRandomHomepage() {
		return HOMEPAGE[RANDOM.nextInt(HOMEPAGE.length)];
	}

	public static String getRandomM() {
		return M[RANDOM.nextInt(M.length)];
	}

	public static String getRandomNach() {
		return NACH[RANDOM.nextInt(NACH.length)];
	}

	public static String getRandomOrt() {
		return ORT[RANDOM.nextInt(ORT.length)];
	}

	public static String getRandomPLZ() {
		return PLZ[RANDOM.nextInt(PLZ.length)];
	}

	public static String getRandomStrasse() {
		return STRASSE[RANDOM.nextInt(STRASSE.length)];
	}

	public static String getRandomTel() {
		return TEL[RANDOM.nextInt(TEL.length)];
	}

	public static String getRandomW() {
		return W[RANDOM.nextInt(W.length)];
	}

	private static String[] createStringArray(String name) {
		try {
			return new MyReader(name).result;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	static {
		RANDOM = new Random((long) Math.random());
		BLOB = createStringArray("blob.txt");
		EMAIL = createStringArray("email.txt");
		HOMEPAGE = createStringArray("homepage.txt");
		M = createStringArray("m.txt");
		NACH = createStringArray("nach.txt");
		ORT = createStringArray("ort.txt");
		PLZ = createStringArray("plz.txt");
		STRASSE = createStringArray("strasse.txt");
		TEL = createStringArray("tel.txt");
		W = createStringArray("w.txt");
	}

	final static class LineReader {
		public LineReader(InputStream inStream) {
			this.inStream = inStream;
		}

		byte[] inBuf = new byte[8192];

		char[] lineBuf = new char[1024];

		int inLimit = 0;

		int inOff = 0;

		InputStream inStream;

		int readLine() throws IOException {
			int len = 0;
			char c = 0;

			boolean skipWhiteSpace = true;
			boolean isNewLine = true;
			boolean appendedLineBegin = false;
			boolean precedingBackslash = false;
			boolean skipLF = false;

			while (true) {
				if (inOff >= inLimit) {
					inLimit = inStream.read(inBuf);
					inOff = 0;
					if (inLimit <= 0) {
						if (len == 0) {
							return -1;
						}
						return len;
					}
				}
				// The line below is equivalent to calling a
				// ISO8859-1 decoder.
				c = (char) (0xff & inBuf[inOff++]);
				if (skipLF) {
					skipLF = false;
					if (c == '\n') {
						continue;
					}
				}
				if (skipWhiteSpace) {
					if (c == ' ' || c == '\t' || c == '\f') {
						continue;
					}
					if (!appendedLineBegin && (c == '\r' || c == '\n')) {
						continue;
					}
					skipWhiteSpace = false;
					appendedLineBegin = false;
				}
				if (isNewLine) {
					isNewLine = false;
					if (c == '#' || c == '!') {
						continue;
					}
				}

				if (c != '\n' && c != '\r') {
					lineBuf[len++] = c;
					if (len == lineBuf.length) {
						int newLength = lineBuf.length * 2;
						if (newLength < 0) {
							newLength = Integer.MAX_VALUE;
						}
						char[] buf = new char[newLength];
						System.arraycopy(lineBuf, 0, buf, 0, lineBuf.length);
						lineBuf = buf;
					}
					// flip the preceding backslash flag
					if (c == '\\') {
						precedingBackslash = !precedingBackslash;
					} else {
						precedingBackslash = false;
					}
				} else {
					// reached EOL
					if (len == 0) {
						isNewLine = true;
						skipWhiteSpace = true;
						len = 0;
						continue;
					}
					if (inOff >= inLimit) {
						inLimit = inStream.read(inBuf);
						inOff = 0;
						if (inLimit <= 0) {
							return len;
						}
					}
					if (precedingBackslash) {
						len -= 1;
						// skip the leading whitespace characters in following
						// line
						skipWhiteSpace = true;
						appendedLineBegin = true;
						precedingBackslash = false;
						if (c == '\r') {
							skipLF = true;
						}
					} else {
						return len;
					}
				}
			}
		}
	}

	private static class MyReader {
		final String[] result;

		MyReader(String name) throws IOException {
			super();
			String path = Sessions.getCurrent().getWebApp().getRealPath("/res") + "/";
			File file = new File(path + name);
			LineReader lr = new LineReader(new BufferedInputStream(new FileInputStream(file)));

			int limit;

			List<String> r = new ArrayList<String>();

			while ((limit = lr.readLine()) >= 0) {
				String value = new String(lr.lineBuf, 0, limit);
				r.add(value);
			}

			result = (String[]) r.toArray(new String[r.size()]);
		}
	}

	private ObjectMaschine() {
	}
}
