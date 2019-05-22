package com.example.finalsolution;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
            }
        });
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
        canvas.drawLine(middle, (float) min * scaleFactor, middle, (float) max * scaleFactor, paint);
        imageView.setImageBitmap(bitmap);
    }

}
