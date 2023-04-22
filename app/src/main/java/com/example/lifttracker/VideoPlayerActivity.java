package com.example.lifttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity {
    private VideoView videoView;
    private ArrayList<RectF> boundingBoxList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        boundingBoxList = new ArrayList<>();

        videoView = findViewById(R.id.videoView);
        Intent intent = getIntent();
        Uri videoFilePath = intent.getData();
        videoView.setVideoURI(videoFilePath);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);

        videoView.start();

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getApplicationContext(), videoFilePath);

        /*String widthString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
        String heightString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
        videoWidth = Integer.parseInt(widthString);
        videoHeight = Integer.parseInt(heightString);*/

        // Set up a Paint object to draw the bounding box
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5.0f);

        // Set up a Handler to run the object detection model on a separate thread
        HandlerThread handlerThread = new HandlerThread("ObjectDetectionThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            // Loop over each frame of the video and draw the bounding box
            long videoDuration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            int frameInterval = 375;
            for (int i = 0; i < videoDuration; i += frameInterval) {
                // Get the current video frame as a Bitmap
                Bitmap frameBitmap = retriever.getFrameAtTime(i * 1000L);
                frameBitmap = frameBitmap.copy(Bitmap.Config.ARGB_8888, true);
                try {
                    runObjectDetection(frameBitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            String coordinates = "";
            for(int i = 0; i < boundingBoxList.size(); i++) {
                RectF box = boundingBoxList.get(i);
                float x = box.centerX();
                float y = box.centerY();

                String cords = "(" + x + ", " + y + ")";
                coordinates += ", ";
                coordinates += cords;
            }

            String finalCoordinates = coordinates;
            runOnUiThread(() -> System.out.println(finalCoordinates));

        });
    }

    private void runObjectDetection(Bitmap bitmap) throws IOException {
        // Initialization
        ObjectDetector.ObjectDetectorOptions options =
                ObjectDetector.ObjectDetectorOptions.builder()
                        .setBaseOptions(BaseOptions.builder().build())
                        .setMaxResults(1)
                        .build();
        ObjectDetector objectDetector =
                ObjectDetector.createFromFileAndOptions(
                        getApplicationContext(), "model3meta.tflite", options);

        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        tensorImage.load(bitmap);

        Detection detectionResult = objectDetector.detect(tensorImage).get(0);

        RectF boundingBox = detectionResult.getBoundingBox();
        boundingBoxList.add(boundingBox);

        Bitmap imgWithResult = drawDetectionResult(bitmap, detectionResult);
        runOnUiThread(() -> {
            Drawable d = new BitmapDrawable(getResources(), imgWithResult);
            videoView.setForeground(d);
        });
    }

    private Bitmap drawDetectionResult(Bitmap bitmap, Detection result) {
        Bitmap outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outputBitmap);
        Paint pen = new Paint();
        pen.setTextAlign(Paint.Align.LEFT);

        // draw bounding box
        pen.setColor(Color.RED);
        pen.setStrokeWidth(8F);
        pen.setStyle(Paint.Style.STROKE);
        RectF box = result.getBoundingBox();
        canvas.drawRect(box, pen);

        Rect tagSize = new Rect();

        // calculate the right font size
        pen.setStyle(Paint.Style.FILL_AND_STROKE);
        pen.setColor(Color.YELLOW);
        pen.setStrokeWidth(2F);

        pen.setTextSize(96F);
        String text = String.valueOf(result.getCategories().get(0).getScore());
        pen.getTextBounds(text, 0, text.length(), tagSize);
        float fontSize = pen.getTextSize() * box.width() / tagSize.width();

        // adjust the font size so texts are inside the bounding box
        if (fontSize < pen.getTextSize()) pen.setTextSize(fontSize);

        float margin = (box.width() - tagSize.width()) / 2.0F;
        if (margin < 0F) margin = 0F;
        canvas.drawText(
                text, box.left + margin,
                box.top + tagSize.height() * 1F, pen
        );
        return outputBitmap;
    }

    /*private MappedByteBuffer loadModelFile() {
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = getApplicationContext().getAssets().openFd("model3meta.tflite");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        try {
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/

    /*
    private void createImageFolder() {
        File imageFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        imageFolder = new File(imageFile, "LiftTrackerImages");
        if(!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
    }

    private File createImageFileName() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prepend = "IMAGE_" + timestamp + "_";

        createImageFolder();
        File imageFile = File.createTempFile(prepend, ".jpg", imageFolder);
        //mImageFileName = imageFile.getAbsolutePath();
        return new File(imageFile.getAbsolutePath());
    }

    public void convert(Uri videoFilePath, int numPics) {
        //numpics is how many total images you want

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(String.valueOf(videoFilePath));

        /*String fileName = new File(videoFilePath).getName();
        String outputPath = outputFolder + File.separator + fileName + "_frame_";
        File folder = new File(outputFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }*//*

        long duration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        long interval = duration / numPics; // extract 10 frames evenly spaced over the video
        for (long i = 0; i < duration && i < interval * numPics; i += interval) {
            Bitmap bitmap = retriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            if (bitmap != null) {
                File frameFileName = null;
                try {
                    frameFileName = createImageFileName();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                saveBitmap(bitmap, String.valueOf(frameFileName));
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
            throw new RuntimeException(e);
        }
    }
    */
}