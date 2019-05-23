package com.example.finalsolution;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class manager extends AppCompatActivity {
    int current_image;
    float min = Float.POSITIVE_INFINITY;
    float max = Float.NEGATIVE_INFINITY;
    float left = Float.POSITIVE_INFINITY;
    float right = Float.NEGATIVE_INFINITY;
    float scaleFactor;
    int[] images = {R.drawable.sample,
            R.drawable.pic1,
            R.drawable.pic2,
            R.drawable.pic3,
            R.drawable.pic4,
            R.drawable.pic5,
            R.drawable.pic6,
    };

    Button height,waist,hip,next;
    ImageView imageView;
    float waistLocation;
    float hipLocation;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        height = findViewById(R.id.height);
        waist = findViewById(R.id.waist);
        hip = findViewById(R.id.hip);
        next = findViewById(R.id.next);
        imageView = findViewById(R.id.imageView);

        height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateHeigt();
                drawHeight();
            }
        });

        waist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                min = Float.POSITIVE_INFINITY;
                max = Float.NEGATIVE_INFINITY;
                left = Float.POSITIVE_INFINITY;
                right = Float.NEGATIVE_INFINITY;

                calculateWaist();
                drawWaist();
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int[] viewCoords = new int[2];
                imageView.getLocationOnScreen(viewCoords);
                float touchX = event.getX();
                float touchY = event.getY();

                System.out.println("x is " + touchX);
                System.out.println("y is " + touchY);
                return true;
            }
        });

        hip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateHip();
                drawHip();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_image++;
                current_image = current_image % images.length;
                imageView.setImageResource(images[current_image]);
                min = Float.POSITIVE_INFINITY;
                max = Float.NEGATIVE_INFINITY;
                left = Float.POSITIVE_INFINITY;
                right = Float.NEGATIVE_INFINITY;
            }
        });

    }


    private void drawWaist(){
        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Bitmap bitmap = originalbitmap.copy(Bitmap.Config.RGB_565,true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(30);

        canvas.drawLine( left , waistLocation ,right ,waistLocation, paint);
        paint.setTextSize(100);
        paint.setColor(Color.GREEN);

        String pixelInfo = right - left + "pixels";

        canvas.drawText(pixelInfo,600, waistLocation, paint);
        imageView.setImageBitmap(bitmap);
    }

    private void calculateWaist(){
        Toast.makeText(manager.this, "calculating waist....", Toast.LENGTH_SHORT).show();
        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat hierarchy = new Mat();

        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat originalMat = new Mat();
        Utils.bitmapToMat(originalbitmap,originalMat);

        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
        Imgproc.cvtColor(originalMat,grayMat,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.erode(grayMat,grayMat,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(5,5)));
        Imgproc.Canny(grayMat,cannyEdges,10,100);
        Imgproc.findContours(cannyEdges,contourList,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

        Mat contours = new Mat();
        contours.create(cannyEdges.rows(),cannyEdges.cols(), CvType.CV_8UC3);


        for (int i = 0; i< contourList.size();i++){
            for(int j = 0; j < contourList.get(i).toList().size();j++){
                if( originalbitmap.getHeight() * 3 / 8.0 <  contourList.get(i).toList().get(j).y && originalbitmap.getHeight() * 4 / 8.0 > contourList.get(i).toList().get(j).y ){
                    if(left > contourList.get(i).toList().get(j).x){
                        left = (float) contourList.get(i).toList().get(j).x;
                    }
                    if(right < contourList.get(i).toList().get(j).x ) {
                        right = (float) contourList.get(i).toList().get(j).x;
                        waistLocation = (float) contourList.get(i).toList().get(j).y;
                    }
                }
            }
        }

        System.out.println("left is " +
                left);
        System.out.println("right is " +
                right);
    }
    private void drawHip(){

        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Bitmap bitmap = originalbitmap.copy(Bitmap.Config.RGB_565,true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(30);

        canvas.drawLine( left , hipLocation ,right ,hipLocation, paint);
        paint.setTextSize(100);
        paint.setColor(Color.GREEN);

        String pixelInfo = right - left + "pixels";

        canvas.drawText(pixelInfo,600, hipLocation, paint);
        imageView.setImageBitmap(bitmap);
    }

    private void calculateHip(){
        Toast.makeText(manager.this, "calculating hip....", Toast.LENGTH_SHORT).show();
        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat hierarchy = new Mat();

        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat originalMat = new Mat();
        Utils.bitmapToMat(originalbitmap,originalMat);

        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
        Imgproc.cvtColor(originalMat,grayMat,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.erode(grayMat,grayMat,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(5,5)));
        Imgproc.Canny(grayMat,cannyEdges,10,100);
        Imgproc.findContours(cannyEdges,contourList,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

        Mat contours = new Mat();
        contours.create(cannyEdges.rows(),cannyEdges.cols(), CvType.CV_8UC3);


        for (int i = 0; i< contourList.size();i++){
            for(int j = 0; j < contourList.get(i).toList().size();j++){
                if( originalbitmap.getHeight() * 4 / 8.0 <  contourList.get(i).toList().get(j).y && originalbitmap.getHeight() * 5 / 8.0 > contourList.get(i).toList().get(j).y ){
                    if(left > contourList.get(i).toList().get(j).x){
                        left = (float) contourList.get(i).toList().get(j).x;
                        hipLocation = (float) contourList.get(i).toList().get(j).y;
                    }
                    if(right < contourList.get(i).toList().get(j).x ) {
                        right = (float) contourList.get(i).toList().get(j).x;
                        hipLocation = (float) contourList.get(i).toList().get(j).y;
                    }
                }
            }
        }

        System.out.println("left is " +
                left);
        System.out.println("right is " +
                right);
        System.out.println("hiplocation is " +
                hipLocation);
    }


    private void calculateHeigt(){
        Toast.makeText(manager.this, "calculating height....", Toast.LENGTH_SHORT).show();
        //using contours
        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat hierarchy = new Mat();

        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat originalMat = new Mat();
        Utils.bitmapToMat(originalbitmap,originalMat);

        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();
        Imgproc.cvtColor(originalMat,grayMat,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.erode(grayMat,grayMat,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(5,5)));
        Imgproc.Canny(grayMat,cannyEdges,100,120);//change the threshold to fit different image
        Imgproc.dilate(cannyEdges,cannyEdges,Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3)));
        Imgproc.erode(cannyEdges,cannyEdges,Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE,new Size(5,5)));
        Imgproc.findContours(cannyEdges,contourList,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

        Mat contours = new Mat();
        contours.create(cannyEdges.rows(),cannyEdges.cols(), CvType.CV_8UC3);

        for (int i = 0; i< contourList.size();i++){
            for(int j = 0; j < contourList.get(i).toList().size();j++){
                if(min > contourList.get(i).toList().get(j).y){
                    min = (float) contourList.get(i).toList().get(j).y;
                }
                if(max <contourList.get(i).toList().get(j).y ){
                    max =(float) contourList.get(i).toList().get(j).y;
                }
            }
        }
        System.out.println("min is " +
                min);
        System.out.println("max is " +
                max);
    }

    private void drawHeight(){
        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Bitmap bitmap = originalbitmap.copy(Bitmap.Config.RGB_565,true);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(30);


        float middle = bitmap.getWidth()/(float) 2.0;
        canvas.drawLine( middle ,min  ,middle ,max, paint);
        paint.setTextSize(100);
        paint.setColor(Color.GREEN);

        String pixelInfo = max - min + "pixels";

        canvas.drawText(pixelInfo,500, 2000, paint);
        imageView.setImageBitmap(bitmap);
    }

}
