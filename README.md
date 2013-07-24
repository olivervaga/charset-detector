File Charset Detector
================================

This is a wrapper around a Java implementation of Mozilla's automatic charset detection algorithm.

The original implementation can be found [here](http://jchardet.sourceforge.net/).

Dependencies
--------------------------------

First grab the *charset-detector.jar* file from the project root.

Then get the original Java implementation.
Download [here](http://sourceforge.net/projects/jchardet/files/) or just get it from the *lib* folder in the project.

Usage
--------------------------------

Easy, there are three static methods:
    CharsetDetector.detectCharset(String filename);
    CharsetDetector.detectCharset(File file);
    CharsetDetector.detectCharset(InputStream inStream);
    
Calling any of those will try to guess the charset of the file and if it finds one, it returns the charset's name as a String (e.g. "UTF-8) or if no charset can be guessed, it returns "nomatch".