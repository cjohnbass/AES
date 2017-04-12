/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.events.StartElement;

/**
 *
 * @author Christopher Bass
 */
public class AES {

    /**
     * @param args the command line arguments
     */
    private static final String[][] sBox = {
        {"63", "7c", "77", "7b", "f2", "6b", "6f", "c5", "30", "01", "67", "2b", "fe", "d7", "ab", "76"},
        {"ca", "82", "c9", "7d", "fa", "59", "47", "f0", "ad", "d4", "a2", "af", "9c", "a4", "72", "c0"},
        {"b7", "fd", "93", "26", "36", "3f", "f7", "cc", "34", "a5", "e5", "f1", "71", "d8", "31", "15"},
        {"04", "c7", "23", "c3", "18", "96", "05", "9a", "07", "12", "80", "e2", "eb", "27", "b2", "75"},
        {"09", "83", "2c", "1a", "1b", "6e", "5a", "a0", "52", "3b", "d6", "b3", "29", "e3", "2f", "84"},
        {"53", "d1", "00", "ed", "20", "fc", "b1", "5b", "6a", "cb", "be", "39", "4a", "4c", "58", "cf"},
        {"d0", "ef", "aa", "fb", "43", "4d", "33", "85", "45", "f9", "02", "7f", "50", "3c", "9f", "a8"},
        {"51", "a3", "40", "8f", "92", "9d", "38", "f5", "bc", "b6", "da", "21", "10", "ff", "f3", "d2"},
        {"cd", "0c", "13", "ec", "5f", "97", "44", "17", "c4", "a7", "7e", "3d", "64", "5d", "19", "73"},
        {"60", "81", "4f", "dc", "22", "2a", "90", "88", "46", "ee", "b8", "14", "de", "5e", "0b", "db"},
        {"e0", "32", "3a", "0a", "49", "06", "24", "5c", "c2", "d3", "ac", "62", "91", "95", "e4", "79"},
        {"e7", "c8", "37", "6d", "8d", "d5", "4e", "a9", "6c", "56", "f4", "ea", "65", "7a", "ae", "08"},
        {"ba", "78", "25", "2e", "1c", "a6", "b4", "c6", "e8", "dd", "74", "1f", "4b", "bd", "8b", "8a"},
        {"70", "3e", "b5", "66", "48", "03", "f6", "0e", "61", "35", "57", "b9", "86", "c1", "1d", "9e"},
        {"e1", "f8", "98", "11", "69", "d9", "8e", "94", "9b", "1e", "87", "e9", "ce", "55", "28", "df"},
        {"8c", "a1", "89", "0d", "bf", "e6", "42", "68", "41", "99", "2d", "0f", "b0", "54", "bb", "16"}

    };

    public static void main(String[] args) {
        // TODO code application logic here
        byte[] by = {1, 2, 3, 4, 5};
        pad(by, 10);
        byte[] test = {0, 1, 2, 3};
        iterateList(Arrays.asList(test).subList(0, 1), 12);
        getFiles("Pics").forEach((file) -> {
//            try {
//                iterateList(getFileBytes(file), 128);
//            } catch (IOException ex) {
//                Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
//            }
        });

    }

    private static void runAES(byte[] content) {

        byte[] pad = pad(content, 128);
        byte[] copy = new byte[16];

        for (int i = 0; i < pad.length; i += 16) {
            //sBox block
            for (int j = i; j < i + 16; j++) {
                pad[j] = sBox(pad[j]);
            }
            //shift rows
            System.arraycopy(pad, i, copy, 0, copy.length);
            shiftRows(pad, copy, i);
        }
    }

    public static void shiftRows(byte[] original, byte[] copy, int start) {

        original[start + 0] = copy[0];
        original[start + 1] = copy[5];
        original[start + 2] = copy[10];
        original[start + 3] = copy[15];

        original[start + 4] = copy[4];
        original[start + 5] = copy[9];
        original[start + 6] = copy[14];
        original[start + 7] = copy[13];

        original[start + 8] = copy[8];
        original[start + 9] = copy[13];
        original[start + 10] = copy[2];
        original[start + 11] = copy[7];

        original[start + 12] = copy[12];
        original[start + 13] = copy[1];
        original[start + 14] = copy[6];
        original[start + 15] = copy[11];
    }

    public static byte sBox(byte b) {
        return hexStringToByte(sBox[highBits(b)][lowBits(b)]);
    }

    public static int highBits(byte b) {
        //System.out.format("%x%n", b);
        return (b & 0xf0) >> 4;
    }

    public static int lowBits(byte b) {
        //System.out.format("%x%n", b);
        return b & 0xf;
    }

    // could try this one instead new BigInteger("00A0BF", 16).toByteArray();
    public static byte hexStringToByte(String s) {
        return (byte) ((Character.digit(s.charAt(0), 16) << 4)
                + Character.digit(s.charAt(1), 16));
    }

    private static void iterateList(List<byte[]> contents, int blockSize) {
        contents.get(0)[0] = 1;
        //Arrays.asList(contents).subList(blockSize, blockSize);
    }

    /**
     * Calculates how much to pad array to equal desired block length and pads
     * array.
     *
     * @return byte array
     */
    private static byte[] pad(byte[] content, int blockSize) {
        //calculate how much to pad array
        int padding = content.length / blockSize;
        padding = content.length - (blockSize * padding);
        padding = blockSize - padding;

        //pad array
        byte[] pad = new byte[padding];
        System.arraycopy(content, 0, pad, 0, content.length);
        return pad;
    }

    /**
     * Returns a byte array that makes up the file.
     *
     * @return byte array
     */
    private static byte[] getFileBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * Returns a list of files given in the directory passed to the parameter.
     *
     * @return List of files
     * @see Image
     */
    private static List<File> getFiles(String directory) {
        List<File> files = new ArrayList();
        final File dir = new File(directory);
        for (final File child : dir.listFiles()) {
            files.add(child);
        }
        return files;
    }

}
