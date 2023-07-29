package com.lzy.nacosnotify.util;

import java.io.StringWriter;
import java.util.Locale;

public class EscapeUtil {

    public static String escape(String str) {
        if (str == null) {
            return null;
        } else {
            StringWriter writer = new StringWriter(str.length() * 2);
            escapeString(writer, str);
            return writer.toString();
        }
    }

    private static void escapeString(StringWriter out, String str) {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        } else if (str != null) {
            int sz = str.length();

            for (int i = 0; i < sz; ++i) {
                char ch = str.charAt(i);
                if (ch > 4095) {
                    out.write("\\u" + hex(ch));
                } else if (ch > 255) {
                    out.write("\\u0" + hex(ch));
                } else if (ch > 127) {
                    out.write("\\u00" + hex(ch));
                } else if (ch < ' ') {
                    switch (ch) {
                        case '\b':
                            out.write(92);
                            out.write(98);
                            break;
                        case '\t':
                            out.write(92);
                            out.write(116);
                            break;
                        case '\n':
                            out.write(92);
                            out.write(110);
                            break;
                        case '\u000b':
                        default:
                            if (ch > 15) {
                                out.write("\\u00" + hex(ch));
                            } else {
                                out.write("\\u000" + hex(ch));
                            }
                            break;
                        case '\f':
                            out.write(92);
                            out.write(102);
                            break;
                        case '\r':
                            out.write(92);
                            out.write(114);
                    }
                }
                else {
                    out.write(ch);
                }
            }

        }
    }

    private static String hex(char ch) {
        return Integer.toHexString(ch).toUpperCase(Locale.ENGLISH);
    }
}
