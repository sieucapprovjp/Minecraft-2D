package com.main.game.audio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AudioFileSupportTest {

    @Test
    public void wavFormatCodeReadsPcmAndAdpcmHeaders() {
        assertEquals(Integer.valueOf(1), AudioFileSupport.wavFormatCode(wavHeader(1)));
        assertEquals(Integer.valueOf(17), AudioFileSupport.wavFormatCode(wavHeader(17)));
    }

    @Test
    public void wavFormatCodeRejectsInvalidHeaders() {
        assertNull(AudioFileSupport.wavFormatCode(new byte[] {'R', 'I', 'F', 'F'}));
        assertNull(AudioFileSupport.wavFormatCode(new byte[] {'N', 'O', 'P', 'E', 0, 0, 0, 0, 'W', 'A', 'V', 'E'}));
    }

    private static byte[] wavHeader(int format) {
        byte[] bytes = new byte[36];
        bytes[0] = 'R';
        bytes[1] = 'I';
        bytes[2] = 'F';
        bytes[3] = 'F';
        bytes[8] = 'W';
        bytes[9] = 'A';
        bytes[10] = 'V';
        bytes[11] = 'E';
        bytes[12] = 'f';
        bytes[13] = 'm';
        bytes[14] = 't';
        bytes[15] = ' ';
        bytes[16] = 16;
        bytes[20] = (byte) (format & 0xff);
        bytes[21] = (byte) ((format >> 8) & 0xff);
        return bytes;
    }
}
