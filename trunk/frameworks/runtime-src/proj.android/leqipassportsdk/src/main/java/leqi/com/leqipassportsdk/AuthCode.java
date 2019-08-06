package leqi.com.leqipassportsdk;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AuthCode 
{
    public static String CutString(String str, int startIndex, int length) 
    {
        if (startIndex >= 0) {
            if (startIndex > str.length()) {
                return "";
            }
        } else {
            if (length + startIndex > 0) {
                length = length + startIndex;
                startIndex = 0;
            } else {
                return "";
            }
        }

        if (str.length() - startIndex < length) 
        {
            length = str.length() - startIndex;
        }
        return str.substring(startIndex, startIndex + length);
    }

    public static String MD5(String str) 
    {
        StringBuffer sb = new StringBuffer();
        String part = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] md5 = md.digest(str.getBytes());

            for (int i = 0; i < md5.length; i++) {
                part = Integer.toHexString(md5[i] & 0xFF);
                if (part.length() == 1) {
                    part = "0" + part;
                }
                sb.append(part);
            }

        } catch (NoSuchAlgorithmException ex) {
        }
        return sb.toString();
    }

    static private byte[] GetKey(byte[] pass, int kLen) 
    {
        byte[] mBox = new byte[kLen];

        for (int i = 0; i < kLen; i++) {
            mBox[i] = (byte) i;
        }

        int j = 0;
        for (int i = 0; i < kLen; i++) 
        {
            j = (j + (int) ((mBox[i] + 256) % 256) + pass[i % pass.length]) % kLen;
            byte temp = mBox[i];
            mBox[i] = mBox[j];
            mBox[j] = temp;
        }
        return mBox;
    }

    // 随机生成字符串
    public static String RandomString(int lens) 
    {
        char[] CharArray = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' }; 
        int clens = CharArray.length;
        String sCode = "";
        Random random = new Random();
        for (int i = 0; i < lens; i++) {
            sCode += CharArray[Math.abs(random.nextInt(clens))];
        }
        return sCode;
    }

    public static String authcode(String source, String key, int expiry) {
        try {
            if (source == null || key == null) {
                return "";
            }

            String keya, keyb, keyc, cryptkey, result, codestring;

            source =getUnixTimestamp() +";" +source;
            key  = MD5(key);
            keya = MD5(CutString(key, 0, 16));
            keyb = MD5(CutString(key, 16, 16));
            keyc = RandomString(8);

            cryptkey = keya + MD5(keya + keyc);
            source = String.format("%010d", expiry > 0 ? expiry + getUnixTimestamp() : 0) + CutString(MD5(source + keyb), 0, 16) + source;
            byte[] temp = RC4(source.getBytes("GBK"), cryptkey);

            codestring = keyc + Base64.encodeBytes(temp);
            codestring = codestring.replace("/", "_");
            codestring = codestring.replace("+", "|");
            return codestring;
        } catch (Exception e) {
            return "";
        }
    }

    private static byte[] RC4(byte[] input, String pass) 
    {
        if (input == null || pass == null)
            return null;

        byte[] output = new byte[input.length];
        byte[] mBox = GetKey(pass.getBytes(), 256);

        int i = 0;
        int j = 0;

        for (int offset = 0; offset < input.length; offset++) {
            i = (i + 1) % mBox.length;
            j = (j + (int) ((mBox[i] + 256) % 256)) % mBox.length;

            byte temp = mBox[i];
            mBox[i] = mBox[j];
            mBox[j] = temp;
            byte a = input[offset];
            byte b = mBox[(toInt(mBox[i]) + toInt(mBox[j])) % mBox.length];

            output[offset] = (byte) ((int) a ^ (int) toInt(b));
        }

        return output;
    }

    public static int toInt(byte b) {
        return (int) ((b + 256) % 256);
    }

    public static long getUnixTimestamp() 
    {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
        return cal.getTimeInMillis() / 1000;
    }

    public static void main(String[] args) 
    {
        String test = "abc";
        String key = "beenpc2017";
        String afStr = AuthCode.authcode(test, key, 60);
        System.out.println("加密后: " + afStr);
    }
}

class Base64 
{
    public final static int NO_OPTIONS = 0;
    public final static int ENCODE = 1;
    public final static int DECODE = 0;
    public final static int GZIP = 2;
    public final static int DONT_GUNZIP = 4;
    public final static int DO_BREAK_LINES = 8;
    public final static int URL_SAFE = 16;
    public final static int ORDERED = 32;
    private final static int MAX_LINE_LENGTH = 76;
    private final static byte EQUALS_SIGN = (byte) '=';
    private final static byte NEW_LINE = (byte) '\n';
    private final static String PREFERRED_ENCODING = "US-ASCII";
    private final static byte WHITE_SPACE_ENC = -5;
    private final static byte EQUALS_SIGN_ENC = -1;
    private final static byte[] _STANDARD_ALPHABET = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '+', (byte) '/' };
    private final static byte[] _STANDARD_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9 };
    private final static byte[] _URL_SAFE_ALPHABET = { (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) '-', (byte) '_' };
    private final static byte[] _URL_SAFE_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -9, -9, -9, -9 };
    private final static byte[] _ORDERED_ALPHABET = { (byte) '-', (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9', (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) '_', (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z' };
    private final static byte[] _ORDERED_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 0, -9, -9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -9, -9, -9, -1, -9, -9, -9, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, -9, -9, -9, -9, 37, -9, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, -9, -9, -9, -9 };

    private final static byte[] getAlphabet(int options) 
    {
        if ((options & URL_SAFE) == URL_SAFE) {
            return _URL_SAFE_ALPHABET;
        } else if ((options & ORDERED) == ORDERED) {
            return _ORDERED_ALPHABET;
        } else {
            return _STANDARD_ALPHABET;
        }
    }

    private final static byte[] getDecodabet(int options) 
    {
        if ((options & URL_SAFE) == URL_SAFE) {
            return _URL_SAFE_DECODABET;
        } else if ((options & ORDERED) == ORDERED) {
            return _ORDERED_DECODABET;
        } else {
            return _STANDARD_DECODABET;
        }
    }

    private Base64() 
    {
    }

    private static byte[] encode3to4(byte[] b4, byte[] threeBytes, int numSigBytes, int options) 
    {
        encode3to4(threeBytes, 0, numSigBytes, b4, 0, options);
        return b4;
    }

    private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset, int options) 
    {
        byte[] ALPHABET = getAlphabet(options);
        int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0)
                | (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0)
                | (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

        switch (numSigBytes) {
        case 3:
            destination[destOffset] = ALPHABET[(inBuff >>> 18)];
            destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
            destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
            destination[destOffset + 3] = ALPHABET[(inBuff) & 0x3f];
            return destination;

        case 2:
            destination[destOffset] = ALPHABET[(inBuff >>> 18)];
            destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
            destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
            destination[destOffset + 3] = EQUALS_SIGN;
            return destination;

        case 1:
            destination[destOffset] = ALPHABET[(inBuff >>> 18)];
            destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
            destination[destOffset + 2] = EQUALS_SIGN;
            destination[destOffset + 3] = EQUALS_SIGN;
            return destination;

        default:
            return destination;
        }
    }

    public static void encode(java.nio.ByteBuffer raw, java.nio.ByteBuffer encoded)
    {
        byte[] raw3 = new byte[3];
        byte[] enc4 = new byte[4];

        while (raw.hasRemaining()) {
            int rem = Math.min(3, raw.remaining());
            raw.get(raw3, 0, rem);
            Base64.encode3to4(enc4, raw3, rem, Base64.NO_OPTIONS);
            encoded.put(enc4);
        }
    }

    public static void encode(java.nio.ByteBuffer raw, java.nio.CharBuffer encoded)
    {
        byte[] raw3 = new byte[3];
        byte[] enc4 = new byte[4];

        while (raw.hasRemaining()) {
            int rem = Math.min(3, raw.remaining());
            raw.get(raw3, 0, rem);
            Base64.encode3to4(enc4, raw3, rem, Base64.NO_OPTIONS);
            for (int i = 0; i < 4; i++) {
                encoded.put((char) (enc4[i] & 0xFF));
            }
        }
    }

    public static String encodeBytes(byte[] source) 
    {
        String encoded = null;
        try {
            encoded = encodeBytes(source, 0, source.length, NO_OPTIONS);
        } catch (IOException ex) {
            assert false : ex.getMessage();
        }
        assert encoded != null;
        return encoded;
    }

    public static String encodeBytes(byte[] source, int options) throws IOException
    {
        return encodeBytes(source, 0, source.length, options);
    }

    public static String encodeBytes(byte[] source, int off, int len) 
    {
        String encoded = null;
        try {
            encoded = encodeBytes(source, off, len, NO_OPTIONS);
        } catch (IOException ex) {
            assert false : ex.getMessage();
        }
        assert encoded != null;
        return encoded;
    }

    public static String encodeBytes(byte[] source, int off, int len, int options) throws IOException
    {
        byte[] encoded = encodeBytesToBytes(source, off, len, options);

        try {
            return new String(encoded, PREFERRED_ENCODING);
        }
        catch (UnsupportedEncodingException uue) {
            return new String(encoded);
        }
    }

    public static byte[] encodeBytesToBytes(byte[] source) 
    {
        byte[] encoded = null;
        try {
            encoded = encodeBytesToBytes(source, 0, source.length,
                    Base64.NO_OPTIONS);
        } catch (IOException ex) {
            assert false : "IOExceptions only come from GZipping, which is turned off: "
                    + ex.getMessage();
        }
        return encoded;
    }

    public static byte[] encodeBytesToBytes(byte[] source, int off, int len, int options) throws IOException
    {
        if (source == null) {
            throw new NullPointerException("Cannot serialize a null array.");
        }

        if (off < 0) {
            throw new IllegalArgumentException("Cannot have negative offset: " + off);
        }

        if (len < 0) {
            throw new IllegalArgumentException("Cannot have length offset: " + len);
        }

        if (off + len > source.length) {
            throw new IllegalArgumentException(
                    String.format(
                            "Cannot have offset of %d and length of %d with array of length %d",
                            off, len, source.length));
        }

        if ((options & GZIP) != 0) {
            ByteArrayOutputStream baos = null;
            java.util.zip.GZIPOutputStream gzos = null;
            OutputStream b64os = null;

            try {
                baos = new ByteArrayOutputStream();
                b64os = new OutputStream(baos, ENCODE | options);
                gzos = new java.util.zip.GZIPOutputStream(b64os);

                gzos.write(source, off, len);
                gzos.close();
            }
            catch (IOException e) {
                throw e;
            }
            finally {
                try {
                    gzos.close();
                } catch (Exception e) {
                }
                try {
                    b64os.close();
                } catch (Exception e) {
                }
                try {
                    baos.close();
                } catch (Exception e) {
                }
            }
            return baos.toByteArray();
        }
        else {
            boolean breakLines = (options & DO_BREAK_LINES) > 0;
            int encLen = (len / 3) * 4 + (len % 3 > 0 ? 4 : 0);
            if (breakLines) {
                encLen += encLen / MAX_LINE_LENGTH;
            }
            byte[] outBuff = new byte[encLen];

            int d = 0;
            int e = 0;
            int len2 = len - 2;
            int lineLength = 0;
            for (; d < len2; d += 3, e += 4) {
                encode3to4(source, d + off, 3, outBuff, e, options);

                lineLength += 4;
                if (breakLines && lineLength >= MAX_LINE_LENGTH) {
                    outBuff[e + 4] = NEW_LINE;
                    e++;
                    lineLength = 0;
                }
            }

            if (d < len) {
                encode3to4(source, d + off, len - d, outBuff, e, options);
                e += 4;
            }
            if (e < outBuff.length - 1) {
                byte[] finalOut = new byte[e];
                System.arraycopy(outBuff, 0, finalOut, 0, e);
                return finalOut;
            } else {
                return outBuff;
            }
        }
    }

    private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, int options)
    {
        if (source == null) {
            throw new NullPointerException("Source array was null.");
        }
        if (destination == null) {
            throw new NullPointerException("Destination array was null.");
        }
        if (srcOffset < 0 || srcOffset + 3 >= source.length) {
            throw new IllegalArgumentException(
                    String.format(
                            "Source array with length %d cannot have offset of %d and still process four bytes.",
                            source.length, srcOffset));
        }
        if (destOffset < 0 || destOffset + 2 >= destination.length) {
            throw new IllegalArgumentException(
                    String.format(
                            "Destination array with length %d cannot have offset of %d and still store three bytes.",
                            destination.length, destOffset));
        }
        byte[] DECODABET = getDecodabet(options);
        if (source[srcOffset + 2] == EQUALS_SIGN) {
            int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18)
                    | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12);

            destination[destOffset] = (byte) (outBuff >>> 16);
            return 1;
        }
        else if (source[srcOffset + 3] == EQUALS_SIGN) {
            int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18)
                    | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12)
                    | ((DECODABET[source[srcOffset + 2]] & 0xFF) << 6);

            destination[destOffset] = (byte) (outBuff >>> 16);
            destination[destOffset + 1] = (byte) (outBuff >>> 8);
            return 2;
        }
        else {
            int outBuff = ((DECODABET[source[srcOffset]] & 0xFF) << 18)
                    | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12)
                    | ((DECODABET[source[srcOffset + 2]] & 0xFF) << 6)
                    | ((DECODABET[source[srcOffset + 3]] & 0xFF));

            destination[destOffset] = (byte) (outBuff >> 16);
            destination[destOffset + 1] = (byte) (outBuff >> 8);
            destination[destOffset + 2] = (byte) (outBuff);

            return 3;
        }
    } 
    
    public static class OutputStream extends FilterOutputStream
    {
        private boolean encode;
        private int position;
        private byte[] buffer;
        private int bufferLength;
        private int lineLength;
        private boolean breakLines;
        private byte[] b4;
        private boolean suspendEncoding;
        private int options;
        private byte[] decodabet;

        public OutputStream(java.io.OutputStream out) {
            this(out, ENCODE);
        }

        public OutputStream(java.io.OutputStream out, int options) {
            super(out);
            this.breakLines = (options & DO_BREAK_LINES) != 0;
            this.encode = (options & ENCODE) != 0;
            this.bufferLength = encode ? 3 : 4;
            this.buffer = new byte[bufferLength];
            this.position = 0;
            this.lineLength = 0;
            this.suspendEncoding = false;
            this.b4 = new byte[4];
            this.options = options;
            this.decodabet = getDecodabet(options);
        }

        @Override
        public void write(int theByte) throws IOException {
            if (suspendEncoding) {
                this.out.write(theByte);
                return;
            }

            if (encode) {
                buffer[position++] = (byte) theByte;
                if (position >= bufferLength) {
                    this.out.write(encode3to4(b4, buffer, bufferLength, options));
                    lineLength += 4;
                    if (breakLines && lineLength >= MAX_LINE_LENGTH) {
                        this.out.write(NEW_LINE);
                        lineLength = 0;
                    }
                    position = 0;
                }
            }
            else {
                if (decodabet[theByte & 0x7f] > WHITE_SPACE_ENC) {
                    buffer[position++] = (byte) theByte;
                    if (position >= bufferLength) {

                        int len = Base64.decode4to3(buffer, 0, b4, 0, options);
                        out.write(b4, 0, len);
                        position = 0;
                    }
                }
                else if (decodabet[theByte & 0x7f] != WHITE_SPACE_ENC) {
                    throw new IOException(
                            "Invalid character in Base64 data.");
                }
            }
        } 

        @Override
        public void write(byte[] theBytes, int off, int len) throws IOException {
            if (suspendEncoding) {
                this.out.write(theBytes, off, len);
                return;
            }
            for (int i = 0; i < len; i++) {
                write(theBytes[off + i]);
            }
        }

        public void flushBase64() throws IOException {
            if (position > 0) {
                if (encode) {
                    out.write(encode3to4(b4, buffer, position, options));
                    position = 0;
                }
                else {
                    throw new IOException(
                            "Base64 input not properly padded.");
                }
            }
        }

        @Override
        public void close() throws IOException
        {
            flushBase64();
            super.close();

            buffer = null;
            out = null;
        }

        public void suspendEncoding() throws IOException
        {
            flushBase64();
            this.suspendEncoding = true;
        }

        public void resumeEncoding() 
        {
            this.suspendEncoding = false;
        }
    }
}
