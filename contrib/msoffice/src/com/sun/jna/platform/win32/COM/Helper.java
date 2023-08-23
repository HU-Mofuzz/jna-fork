/*
 * The contents of this file is dual-licensed under 2
 * alternative Open Source/Free licenses: LGPL 2.1 or later and
 * Apache License 2.0. (starting with JNA version 4.0.0).
 *
 * You can freely decide which license you want to apply to
 * the project.
 *
 * You may obtain a copy of the LGPL License at:
 *
 * http://www.gnu.org/licenses/licenses.html
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "LGPL2.1".
 *
 * You may obtain a copy of the Apache License at:
 *
 * http://www.apache.org/licenses/
 *
 * A copy is also included in the downloadable source code package
 * containing JNA, in file "AL2.0".
 */

package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.util.Date;

public class Helper {
    public static final File tempDir = new File(System.getProperty("java.io.tmpdir"));

    /**
     * Sleep for specified seconds.
     *
     * @param seconds
     */
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ex) {
            // Ignore
        }
    }

    public static void waitForEnter() {
        System.out.println("Press enter to continue...");
        try {
            System.in.read();
        } catch (Exception ignored){}
    }

    /**
     * Extract data contained in classpath into a system accessible target file.
     *
     * @param localPath
     * @param target
     * @throws IOException
     */
    public static void extractClasspathFileToReal(String localPath, File target) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = Helper.class.getResourceAsStream(localPath);
            os =  new FileOutputStream(target);

            int read;
            byte[] buffer = new byte[20480];

            while((read = is.read(buffer)) > 0) {
                os.write(buffer, 0, read);
            }

        } finally {
            if(is != null) {
                try {
                    is.close();
                } catch(Exception ex) {}
            }
            if(os != null) {
                try {
                    os.close();
                } catch(Exception ex) {}
            }
        }
    }

    /**
     * Create a temporary file, that does not exist.
     *
     * @param prefix
     * @param suffix
     * @return
     * @throws IOException
     */
    public static File createNotExistingFile(String prefix, String suffix) throws IOException {
        File tempFile = Files.createTempFile(prefix, suffix).toFile();
        tempFile.delete();
        return tempFile;
    }

    public static String errorToString(WinDef.SCODE errorValue) {
        switch (errorValue.intValue()) {
            case -2146826281:
                return "Error: #DIV/0!";
            case -2146826246:
                return "Error: #N/A";
            case -2146826245:
                return "Error: #GETTING_DATA";
            case -2146826259:
                return "Error: #NAME?";
            case -2146826288:
                return "Error: #NULL!";
            case -2146826252:
                return "Error: #NUM!";
            case -2146826265:
                return "Error: #REF!";
            case -2146826273:
                return "Error: #VALUE!";
            default:
                return "Error: Unknown";
        }
    }

    public static String variantToString(Variant.VARIANT variant) {
        Object value = variant.getValue();
        if(value == null) {
            return "";
        }
        if(value instanceof Boolean) {
            return Boolean.toString((Boolean) value);
        } else if(value instanceof Double) {
            return Double.toString((Double) value);
        } else if(value instanceof String) {
            return (String) value;
        } else if (value instanceof Integer) {
            return Integer.toString((Integer) value);
        } else if(value instanceof Date) {
            return DateFormat.getInstance().format((Date) value);
        } else if(value instanceof WTypes.BSTR) {
            return ((WTypes.BSTR)value).getValue();
        } else if(value instanceof OaIdl.VARIANT_BOOL) {
            return Boolean.toString(((OaIdl.VARIANT_BOOL)value).booleanValue());
        } else if(value instanceof WinDef.SCODE) {
            return errorToString((WinDef.SCODE)value);
        }
        return "<UNKNOWN>";
    }
}
