package com.example.lifttracker;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import android.content.ContextWrapper;


public class object_detector{

    private Context context;
    private Interpreter tflite;
    public object_detector(Context context) throws Exception {
        this.context = context;
        // Load the TFLite model from the assets folder
        this.tflite = new Interpreter(loadModelFile());
    }


    private MappedByteBuffer loadModelFile() throws Exception {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd("detection_model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    public Interpreter getModel() {
        return tflite;
    }


    public static void main(String args[]) throws IOException {

        ContextWrapper context = new ContextWrapper(null);

        try {
            // Create an instance of object_detector class with a null context
            object_detector detector = new object_detector(context);

            // Get the TFLite model from the detector object
            Interpreter model = detector.getModel();

            // Use the model to do inference on some input data
            // ...

        } catch (Exception e) {
            e.printStackTrace();
        }



//        Interpreter model;

        System.out.println("hello");


    }
}
