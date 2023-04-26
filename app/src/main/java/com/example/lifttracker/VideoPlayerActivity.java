package com.example.lifttracker;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.ArrayList;

public class VideoPlayerActivity extends AppCompatActivity {
    private VideoView videoView;
    private LinearLayout videoLayout;
    private GraphView graph;
    private ArrayList<RectF> boundingBoxList;
    private ArrayList<Float> timeList = new ArrayList<>();
    private Analyze analyzer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        boundingBoxList = new ArrayList<>();
        graph = findViewById(R.id.graph);
        videoLayout = findViewById(R.id.videoLayout);
        videoView = findViewById(R.id.videoView);

        Intent intent = getIntent();
        Uri videoFilePath = intent.getData();
        videoView.setVideoURI(videoFilePath);

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(getApplicationContext(), videoFilePath);
        long videoDuration = Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));

        Toast.makeText(this, "Processing", Toast.LENGTH_LONG).show();

        // Set up a Handler to run the object detection model on a separate thread
        HandlerThread handlerThread = new HandlerThread("ObjectDetectionThread");
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            // Loop over each frame of the video and draw the bounding box
            int frameInterval = 500;
            for (int i = 0; i < videoDuration; i += frameInterval) {
                // Get the current video frame as a Bitmap
                Bitmap frameBitmap = retriever.getFrameAtTime(i * 1000L);
                frameBitmap = frameBitmap.copy(Bitmap.Config.ARGB_8888, true);
                try {
                    runObjectDetection(frameBitmap, (float) i/1000);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            analyzer = new Analyze(boundingBoxList, timeList, 0.3);
            String coordinates = "";
            String velocities = "";
            String accelerations = "";
            for(int i = 0; i < boundingBoxList.size(); i++) {
                RectF box = boundingBoxList.get(i);
                float x = box.centerX();
                float y = box.centerY();

                String cords = "(" + x + ", " + y + ")";
                coordinates += ", ";
                coordinates += cords;

                velocities += ", ";
                velocities += analyzer.getVelocityArr()[i];

                accelerations += ", ";
                accelerations += analyzer.getAccelArr()[i];
            }

            String finalCoordinates = coordinates;
            System.out.println(finalCoordinates);
            System.out.println(velocities);
            System.out.println(accelerations);

            drawGraph();
            videoView.setForeground(null);
            videoView.start();
        });
    }

    private void drawGraph() {
        float maxVal = 0;
        float minVal = 100;
        float[] velocityArr = analyzer.getVelocityArr();
        float[] accelArr = analyzer.getAccelArr();
        LineGraphSeries<DataPoint> velocitySeries = new LineGraphSeries();
        LineGraphSeries<DataPoint> accelSeries = new LineGraphSeries();

        velocitySeries.setColor(Color.RED);
        velocitySeries.setTitle("Velocity (m/s)");
        velocitySeries.setThickness(5);
        accelSeries.setColor(Color.BLUE);
        accelSeries.setTitle("Δ Acceleration (m/s^2)");
        accelSeries.setThickness(5);
        /*velocitySeries.setDrawDataPoints(true);
        accelSeries.setDrawDataPoints(true);
        velocitySeries.setDataPointsRadius(5);
        accelSeries.setDataPointsRadius(5);*/

        for(int i = 0; i < timeList.size(); i++) {
            float velocity = velocityArr[i];
            if(velocity > maxVal) maxVal = velocity;
            if(velocity < minVal) minVal = velocity;
            velocitySeries.appendData(new DataPoint(timeList.get(i), velocity), false, velocityArr.length);
        }

        for(int i = 1; i < timeList.size(); i++) {
            float acceleration = accelArr[i];
            if(acceleration > maxVal) maxVal = acceleration;
            if(acceleration < minVal) minVal = acceleration;
            accelSeries.appendData(new DataPoint(timeList.get(i), acceleration), false, accelArr.length);
        }

        graph.addSeries(velocitySeries);
        graph.addSeries(accelSeries);

        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time (s)");
        gridLabel.setHorizontalAxisTitleTextSize(40);
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graph.getViewport().setScalable(true);
        //graph.getViewport().setScalableY(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(timeList.get(timeList.size()-1));
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(minVal - 0.5);
        graph.getViewport().setMaxY((int)maxVal + 2);

        velocitySeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getApplicationContext(), "Velocity at " + dataPoint.getX() + "s = " + ((double)dataPoint.getY()) + " m/s", Toast.LENGTH_SHORT).show();
            }
        });

        accelSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(getApplicationContext(), "Acceleration at " + dataPoint.getX() + "s = " + ((double)dataPoint.getY()) + " m/s^2", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void runObjectDetection(Bitmap bitmap, float time) throws IOException {
        // Initialization
        ObjectDetector.ObjectDetectorOptions options =
                ObjectDetector.ObjectDetectorOptions.builder()
                        .setBaseOptions(BaseOptions.builder().build())
                        .setMaxResults(1)
                        .setScoreThreshold(0.5f)
                        .build();
        ObjectDetector objectDetector =
                ObjectDetector.createFromFileAndOptions(
                        getApplicationContext(), "model3meta.tflite", options);

        TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        tensorImage.load(bitmap);

        if(!objectDetector.detect(tensorImage).isEmpty()) {
            Detection detectionResult = objectDetector.detect(tensorImage).get(0);

            RectF boundingBox = detectionResult.getBoundingBox();

        /*if(boundingBoxList.size() > 0) {
            if(!boundingBox.equals(boundingBoxList.get(boundingBoxList.size()-1))) {
                boundingBoxList.add(boundingBox);
                timeList.add(time);
            }
        }*/
            //else {
            boundingBoxList.add(boundingBox);
            timeList.add(time);
            //}

            Bitmap imgWithResult = drawDetectionResult(bitmap, detectionResult);
            runOnUiThread(() -> {
                Drawable d = new BitmapDrawable(getResources(), imgWithResult);
                videoView.setForeground(d);
            });
        }
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