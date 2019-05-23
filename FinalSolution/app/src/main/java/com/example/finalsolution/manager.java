package com.example.finalsolution;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.LoaderCallbackInterface;
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
                Toast.makeText(manager.this, "calculating height....", Toast.LENGTH_SHORT).show();
                calculateHeigt();
                drawHeight();
            }
        });

        waist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                calculateWaist();
                waistLocation = 430*scaleFactor;
                drawWaist();
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int[] viewCoords = new int[2];
                imageView.getLocationOnScreen(viewCoords);
                float touchX = (int)event.getX();
                float touchY = (int)event.getY();
                float x = touchX - viewCoords[0]; // viewCoords[0] is the X coordinate
                float y = touchY - viewCoords[1];
                waistLocation = 430;
                System.out.println(x +"and "+ y);
                return true;
            }
        });

        hip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.RGB_565);
        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        scaleFactor = (float) imageView.getWidth() / (float) originalbitmap.getWidth();
        float scaleFactor2 = (float) imageView.getHeight() / (float) originalbitmap.getHeight();
        System.out.println("imageview x" + imageView.getWidth());
        System.out.println("original x " + originalbitmap.getWidth());
        Canvas canvas = new Canvas(bitmap);
        imageView.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        float middle = 275 / scaleFactor;

        Paint paint1 = new Paint();
        paint1.setColor(Color.GREEN);
        paint1.setTextSize(40);
        String pixelInfo = (int) ((right - left)*scaleFactor) + "pixels" + scaleFactor;
        canvas.drawText(pixelInfo,((right + left)*scaleFactor/(float) 2.0),middle,paint1);
        canvas.drawLine( 444, middle,  731,middle, paint);
        System.out.println("scaled left" +
                (float) left * scaleFactor);
        System.out.println("scaled right" +
                (float) right * scaleFactor);
        imageView.setImageBitmap(bitmap);
    }

    private void calculateWaist(){
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
                if(300 < contourList.get(i).toList().get(j).y && contourList.get(i).toList().get(j).y < 900){
                    if(left > contourList.get(i).toList().get(j).x){
                        left = (float) contourList.get(i).toList().get(j).x;
                    }
                    if(right <contourList.get(i).toList().get(j).x ) {
                        right = (float) contourList.get(i).toList().get(j).x;
                    }
                }
            }
        }

        System.out.println("left is " +
                left);
        System.out.println("right is " +
                right);
        System.out.println("wasitlocation is " +
                waistLocation);

    }


    private void calculateHeigt(){
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
        Imgproc.Canny(grayMat,cannyEdges,10,100);
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
        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.RGB_565);
        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        float scaleFactor = (float) imageView.getHeight() / (float) originalbitmap.getHeight();
        Canvas canvas = new Canvas(bitmap);
        imageView.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        float middle = imageView.getWidth() / 2;

        Paint paint1 = new Paint();
        paint1.setColor(Color.GREEN);
        paint1.setTextSize(40);
        String pixelInfo = (int) ((max - min)*scaleFactor) + "pixels";
        canvas.drawText(pixelInfo,middle,((max + min)*scaleFactor/(float) 2.0),paint1);
        canvas.drawLine(middle, (float) min * scaleFactor, middle, (float) max * scaleFactor, paint);
        imageView.setImageBitmap(bitmap);
    }

}
