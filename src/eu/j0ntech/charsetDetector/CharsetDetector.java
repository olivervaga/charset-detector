/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is mozilla.org code.
 *
 * The Initial Developer of the Original Code is
 * Netscape Communications Corporation.
 * Portions created by the Initial Developer are Copyright (C) 1998
 * the Initial Developer. All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package eu.j0ntech.charsetDetector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsICharsetDetectionObserver;

public class CharsetDetector {

	private CharsetDetector() {
	}

	private static boolean found = false;
	private static String foundCharset;

	private static final String NO_MATCH = "nomatch";

	/**
	 * Tries to guess the charset of a given file using Mozilla's automatic
	 * charset detection algorithm
	 * 
	 * @param filename
	 *            path of the file to be detected
	 * @return detected charset name (e.g. "UTF-8") or "nomatch" if no known
	 *         charset can be guessed
	 * @throws IOException
	 */
	public static String detectCharset(String filename) throws IOException {
		return detectCharset(new File(filename));
	}

	/**
	 * Tries to guess the charset of a given file using Mozilla's automatic
	 * charset detection algorithm
	 * 
	 * @param file
	 *            the file to be detected
	 * @return detected charset name (e.g. "UTF-8") or "nomatch" if no known
	 *         charset can be guessed
	 * @throws IOException
	 */
	public static String detectCharset(File file) throws IOException {
		return detectCharset(new FileInputStream(file));
	}

	/**
	 * Tries to guess the charset of a given file using Mozilla's automatic
	 * charset detection algorithm
	 * 
	 * @param inStream
	 *            InputStream from the file to be detected
	 * @return detected charset name (e.g. "UTF-8") or "nomatch" if no known
	 *         charset can be guessed
	 * @throws IOException
	 */
	public static String detectCharset(InputStream inStream) throws IOException {

		if (inStream == null) {
			return NO_MATCH;
		}

		nsDetector detector = new nsDetector();

		detector.Init(new nsICharsetDetectionObserver() {

			@Override
			public void Notify(String charset) {
				CharsetDetector.found = true;
				CharsetDetector.foundCharset = charset;
			}
		});

		BufferedInputStream bis = new BufferedInputStream(inStream);
		byte[] buf = new byte[1024];
		int len;
		boolean done = false;
		boolean isAscii = true;
		while ((len = bis.read(buf, 0, buf.length)) != -1) {

			// Check if the stream is only ascii.
			if (isAscii)
				isAscii = detector.isAscii(buf, len);

			// DoIt if non-ascii and not done yet.
			if (!isAscii && !done)
				done = detector.DoIt(buf, len, false);
		}
		detector.DataEnd();

		if (isAscii) {
			found = true;
			return "ascii";
		}

		if (!found) {
			String prob[] = detector.getProbableCharsets();
			foundCharset = prob[0];
		}
		found = false;
		return foundCharset;
	}

}
