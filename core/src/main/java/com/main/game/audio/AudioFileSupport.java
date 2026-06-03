package com.main.game.audio;

import com.badlogic.gdx.files.FileHandle;

final class AudioFileSupport {

    private static final int WAV_FORMAT_PCM = 1;

    private AudioFileSupport() {
    }

    static String unsupportedReason(FileHandle file) {
        if (file == null || !file.path().toLowerCase().endsWith(".wav")) {
            return null;
        }
        Integer format = wavFormatCode(file.readBytes());
        if (format == null) {
            return "invalid WAV header";
        }
        if (format != WAV_FORMAT_PCM) {
            return "WAV format " + format + " is not PCM";
        }
        return null;
    }

    static Integer wavFormatCode(byte[] bytes) {
        if (bytes == null || bytes.length < 24) {
            return null;
        }
        if (!matches(bytes, 0, 'R', 'I', 'F', 'F') || !matches(bytes, 8, 'W', 'A', 'V', 'E')) {
            return null;
        }

        int offset = 12;
        while (offset + 10 <= bytes.length) {
            int size = littleEndianInt(bytes, offset + 4);
            int dataStart = offset + 8;
            if (size < 0 || dataStart + size > bytes.length) {
                return null;
            }
            if (matches(bytes, offset, 'f', 'm', 't', ' ')) {
                return littleEndianShort(bytes, dataStart);
            }
            offset = dataStart + size + (size & 1);
        }
        return null;
    }

    private static boolean matches(byte[] bytes, int offset, char a, char b, char c, char d) {
        return offset + 4 <= bytes.length
            && bytes[offset] == (byte) a
            && bytes[offset + 1] == (byte) b
            && bytes[offset + 2] == (byte) c
            && bytes[offset + 3] == (byte) d;
    }

    private static int littleEndianShort(byte[] bytes, int offset) {
        return (bytes[offset] & 0xff) | ((bytes[offset + 1] & 0xff) << 8);
    }

    private static int littleEndianInt(byte[] bytes, int offset) {
        return (bytes[offset] & 0xff)
            | ((bytes[offset + 1] & 0xff) << 8)
            | ((bytes[offset + 2] & 0xff) << 16)
            | ((bytes[offset + 3] & 0xff) << 24);
    }
}
