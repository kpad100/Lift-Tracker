package com.example.lifttracker;



import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class videobreaker {

    public static void convert(String videoFilePath, String outputFolder, int numPics) {
        //numpics is how many total images you want

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoFilePath);
        String fileName = new File(videoFilePath).getName();
        String outputPath = outputFolder + File.separator + fileName + "_frame_";
        File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        long interval = duration / numPics; // extract 10 frames evenly spaced over the video
        for (long i = 0; i < duration && i < interval * numPics; i += interval) {
            Bitmap bitmap = retriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (bitmap != null) {
                String frameFileName = outputPath + i + ".jpg";
                saveBitmap(bitmap, frameFileName);
            }
        }
    }

    private static void saveBitmap(Bitmap bitmap, String fileName) {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        convert("C:\\Capstone\\Lift-Tracker\\app\\assets\\clip.mp4", "C:\\Capstone\\Lift-Tracker\\app\\assets", 10);
    }




}
