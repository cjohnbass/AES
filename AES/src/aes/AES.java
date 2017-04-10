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
        travereDirectory().forEach((file) -> {
            try {
                System.out.println(file.getName() + " "+ getFileBytes(file).length);
            } catch (IOException ex) {
                Logger.getLogger(AES.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    private static void runAES() {
    }

    private static byte[] getFileBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    private static List<File> travereDirectory() {
        List<File> files = new ArrayList();
        final File file = new File("Pics");
        for (final File child : file.listFiles()) {
            files.add(child);
        }
        return files;
    }

}
