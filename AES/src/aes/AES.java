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

    /**
     * @param args the command line arguments
     */
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

 
        byte[] a = hexStringToByteArray("e6");
        System.out.println(a[0]);
    }

    private static void runAES(byte[] content) {

        byte[] pad = pad(content, 128);

        for (int i = 0; i < content.length; i += 16) {

        }
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
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
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
