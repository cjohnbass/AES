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

/**
 *
 * @author Christopher Bass
 */
public class AES {

    private static final int NUM_ROUNDS = 10;
    private static final int BLOCK_SIZE = 128;
    private static final int NUM_BYTES_IN_BLOCK = 16;

    public static void main(String[] args) {

        getFiles("Files").forEach((file) -> {
            try {
                byte[] original = pad(getFileBytes(file), BLOCK_SIZE);
                byte[] encrypted = runAES(original);
                byte[] decrypted = runInverseAES(encrypted);

                System.out.println("Original equals Decrypted: " + Arrays.equals(original, decrypted));
            } catch (IOException ex) {
                Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    static byte[] runAES(byte[] content) {

        byte[] block = pad(content, BLOCK_SIZE);
        byte[] copy = new byte[NUM_BYTES_IN_BLOCK];
        for (int k = 0; k < NUM_ROUNDS; k++) {
            //iterating a block(128 bits / 16 bytes) at a time
            for (int i = 0; i < block.length; i += NUM_BYTES_IN_BLOCK) {
                //sBox block
                //iterate through bytes in block
                for (int j = i; j < i + NUM_BYTES_IN_BLOCK; j++) {
                    block[j] = sBox(block[j]);
                }
                //shift rows
                System.arraycopy(block, i, copy, 0, copy.length);
                shiftRows(block, copy, i);

                //mix columns
                int[][] temp = mixColumns(block, i);
                byteArray(temp, block, i);
            }
        }
        return block;
    }

    static byte[] runInverseAES(byte[] content) {
        byte[] block = pad(content, BLOCK_SIZE);
        byte[] copy = new byte[NUM_BYTES_IN_BLOCK];
        for (int k = 0; k < NUM_ROUNDS; k++) {
            for (int i = 0; i < block.length; i += NUM_BYTES_IN_BLOCK) {
                //mix columns
                int[][] temp = invMixColumns(block, i);
                byteArray(temp, block, i);

                //shift rows
                System.arraycopy(block, i, copy, 0, copy.length);
                invShiftRows(block, copy, i);

                //sBox block
                for (int j = i; j < i + NUM_BYTES_IN_BLOCK; j++) {
                    block[j] = inverseSBox(block[j]);
                }
            }
        }
        return block;
    }

    static int[][] mixColumns(byte[] block, int index) //method for mixColumns
    {
        int[][] tarr = doubleArray(block, index);
        int[][] arr = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                arr[i][j] = mcHelper(tarr, Tables.galois, i, j);
            }
        }
        return arr;
    }

    static int mcHelper(int[][] arr, int[][] g, int i, int j) {
        int mcsum = 0;
        for (int k = 0; k < 4; k++) {
            int a = g[i][k];
            int b = arr[k][j];
            mcsum ^= mcCalc(a, b);
        }
        return mcsum;
    }

    static int mcCalc(int a, int b) //Helper method for mcHelper
    {
        switch (a) {
            case 1:
                return b;
            case 2:
                return Tables.mc2[b / 16][b % 16];
            case 3:
                return Tables.mc3[b / 16][b % 16];
            default:
                break;
        }
        return 0;
    }

    static int[][] invMixColumns(byte[] block, int index) //method for mixColumns
    {
        int[][] tarr = doubleArray(block, index);
        int[][] arr = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                arr[i][j] = invMcHelper(tarr, Tables.invgalois, i, j);
            }
        }
        return arr;
    }

    static int invMcHelper(int[][] arr, int[][] igalois, int i, int j) //Helper method for invMixColumns
    {
        int mcsum = 0;
        for (int k = 0; k < 4; k++) {
            int a = igalois[i][k];
            int b = arr[k][j];
            mcsum ^= invMcCalc(a, b);
        }
        return mcsum;
    }

    static int invMcCalc(int a, int b) //Helper method for invMcHelper
    {
        switch (a) {
            case 9:
                return Tables.mc9[b / 16][b % 16];
            case 0xb:
                return Tables.mc11[b / 16][b % 16];
            case 0xd:
                return Tables.mc13[b / 16][b % 16];
            case 0xe:
                return Tables.mc14[b / 16][b % 16];
            default:
                break;
        }
        return 0;
    }

    static void shiftRows(byte[] original, byte[] copy, int start) {

        original[start + 0] = copy[0];
        original[start + 1] = copy[5];
        original[start + 2] = copy[10];
        original[start + 3] = copy[15];

        original[start + 4] = copy[4];
        original[start + 5] = copy[9];
        original[start + 6] = copy[14];
        original[start + 7] = copy[3];

        original[start + 8] = copy[8];
        original[start + 9] = copy[13];
        original[start + 10] = copy[2];
        original[start + 11] = copy[7];

        original[start + 12] = copy[12];
        original[start + 13] = copy[1];
        original[start + 14] = copy[6];
        original[start + 15] = copy[11];
    }

    static void invShiftRows(byte[] original, byte[] copy, int start) {

        original[start + 0] = copy[0];
        original[start + 1] = copy[13];
        original[start + 2] = copy[10];
        original[start + 3] = copy[7];

        original[start + 4] = copy[4];
        original[start + 5] = copy[1];
        original[start + 6] = copy[14];
        original[start + 7] = copy[11];

        original[start + 8] = copy[8];
        original[start + 9] = copy[5];
        original[start + 10] = copy[2];
        original[start + 11] = copy[15];

        original[start + 12] = copy[12];
        original[start + 13] = copy[9];
        original[start + 14] = copy[6];
        original[start + 15] = copy[3];
    }

    static byte sBox(byte b) {
        return hexStringToByte(Tables.sBox[highBits(b)][lowBits(b)]);
    }

    static byte inverseSBox(byte b) {
        return hexStringToByte(Tables.inverseSBox[highBits(b)][lowBits(b)]);
    }

    static int highBits(byte b) {
        //System.out.format("%x%n", b);
        return (b & 0xf0) >> 4;
    }

    static int lowBits(byte b) {
        //System.out.format("%x%n", b);
        return b & 0xf;
    }

    // could try this one instead new BigInteger("00A0BF", 16).toByteArray();
    static byte hexStringToByte(String s) {
        return (byte) ((Character.digit(s.charAt(0), 16) << 4)
                + Character.digit(s.charAt(1), 16));
    }

    /**
     * Calculates how much to pad array to equal desired block length and pads
     * array.
     *
     * @return byte array
     */
    static byte[] pad(byte[] content, int blockSize) {
        //calculate how much to pad array
        int padding = content.length % blockSize;
        if (padding == 0) {
            return content;
        }
        //padding = content.length - (blockSize * padding);
        padding = content.length + (blockSize - padding);

        //pad array
        byte[] pad = new byte[padding];
        System.arraycopy(content, 0, pad, 0, content.length);
        return pad;
    }

    static int[][] doubleArray(byte[] block, int index) {
        int[][] temp = new int[4][4];
        int count;
        for (int i = 0; i < 4; i++) {
            count = i;
            for (int j = 0; j < 4; j++) {
                temp[i][j] = (int) (block[index + count] & 0xff);
                count = count + 4;
            }
        }
        return temp;
    }

    static void byteArray(int[][] block, byte[] orig, int index) {
        byte[] temp = new byte[16];
        int count;
        for (int i = 0; i < 4; i++) {
            count = i;
            for (int j = 0; j < 4; j++) {
                orig[index + count] = (byte) block[i][j];
                count = count + 4;
            }
        }
    }

    /**
     * Returns a byte array that makes up the file.
     *
     * @return byte array
     */
    static byte[] getFileBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * Returns a list of files given in the directory passed to the parameter.
     *
     * @return List of files
     * @see Image
     */
    static List<File> getFiles(String directory) {
        List<File> files = new ArrayList();
        final File dir = new File(directory);
        for (final File child : dir.listFiles()) {
            files.add(child);
        }
        return files;
    }

}
