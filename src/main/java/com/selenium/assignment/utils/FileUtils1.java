package com.selenium.assignment.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.apache.commons.io.FileUtils;

public class FileUtils1 {

    /**
     * Downloads and saves an image from the provided URL.
     *
     * @param imageUrl URL of the image
     * @param savePath Local path to save the image
     */
    public static void downloadImage(String imageUrl, String savePath) {
        try {
            URL url = new URL(imageUrl);
            File imageFile = new File(savePath);
            FileUtils.copyURLToFile(url, imageFile);
            System.out.println("✅ Image saved: " + savePath);
        } catch (IOException e) {
            System.err.println("❌ Failed to save image: " + e.getMessage());
        }
    }
}
