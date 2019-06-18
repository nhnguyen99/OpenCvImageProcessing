package com.example.finalsolution;

import android.annotation.SuppressLint;
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
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
            R.drawable.pic7,
            R.drawable.pic8,
            R.drawable.pic9,
            R.drawable.pic10,
            R.drawable.pic11,
            R.drawable.pic12,
            R.drawable.pic13,
            R.drawable.pic14,
            R.drawable.pic15,
            R.drawable.pic16,
            R.drawable.pic17,
            R.drawable.pic18,
            R.drawable.pic19,
            R.drawable.pic20,
            R.drawable.pic21,
            R.drawable.pic22,
            R.drawable.pic23,
            R.drawable.pic24,
            R.drawable.pic25,
            R.drawable.pic26,
            R.drawable.pic27,
            R.drawable.pic28,
            R.drawable.pic29,
            R.drawable.pic30,
            R.drawable.pic31

    };

    Button height,waist,hip,next;
    ImageView imageView;
    float waistLocation = 0;
    float hipLocation = 0;


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
                //drawWaist();
            }
        });

//        imageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int[] viewCoords = new int[2];
//                imageView.getLocationOnScreen(viewCoords);
//                float touchX = event.getX();
//                float touchY = event.getY();
//
//                System.out.println("x is " + touchX);
//                System.out.println("y is " + touchY);
//                return true;
//            }
//        });

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


//    private void drawWaist(){
//        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
//        Bitmap bitmap = originalbitmap.copy(Bitmap.Config.RGB_565,true);
//
//        Canvas canvas = new Canvas(bitmap);
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        paint.setStrokeWidth(30);
//
//        canvas.drawLine( left , waistLocation ,right ,waistLocation, paint);
//        paint.setTextSize(100);
//        paint.setColor(Color.GREEN);
//
//        String pixelInfo = right - left + "pixels";
//
//        canvas.drawText(pixelInfo,600, waistLocation, paint);
//        imageView.setImageBitmap(bitmap);
//    }

    private void calculateWaist(){
        Toast.makeText(manager.this, "calculating waist....", Toast.LENGTH_SHORT).show();
        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat image_original = new Mat();
        Utils.bitmapToMat(originalbitmap,image_original);

        int heightStartPoint = (int) (originalbitmap.getHeight() * 2.5/8.0);
        Rect rectCrop = new Rect(0,heightStartPoint , originalbitmap.getWidth(),500);

        //apply k-mean
        Mat rgba = image_original.submat(rectCrop);
        Mat mHSV = new Mat();

        //must convert to 3 channel image
        Imgproc.cvtColor(rgba, mHSV, Imgproc.COLOR_RGBA2RGB,3);
        Imgproc.cvtColor(rgba, mHSV, Imgproc.COLOR_RGB2HSV,3);
        Mat clusters = cluster(mHSV, 3).get(0);


        Bitmap outputBitmap = Bitmap.createBitmap(rectCrop.width,rectCrop.height, Bitmap.Config.RGB_565);
        Utils.matToBitmap(clusters,outputBitmap);


        imageView.setImageBitmap(outputBitmap);
        

    }
    //help function clustering
    public static List<Mat> cluster(Mat cutout, int k) {
        Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        //criteria means the maximum loop
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 100, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 6, Core.KMEANS_PP_CENTERS, centers);//set attempts 2 make sure cluster correct

        return showClusters(cutout, labels, centers);
    }

    //help function show clusters
    private static List<Mat> showClusters (Mat cutout, Mat labels, Mat centers) {
        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(3);

        System.out.println(labels + "labels");
        List<Mat> clusters = new ArrayList<Mat>();
        for(int i = 0; i < centers.rows(); i++) {
            clusters.add(Mat.zeros(cutout.size(), cutout.type()));
        }

        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
        for(int i = 0; i < centers.rows(); i++) counts.put(i, 0);
        int min = cutout.cols();
        int max = 0;
        int rows = 0;
        System.out.println("left " + cutout.rows());
        System.out.println("right " + cutout.cols());

        for(int y = 0; y < cutout.rows(); y++) {
            for(int x = 0; x < cutout.cols(); x++) {
                //find label change point
                int label = (int)labels.get(rows, 0)[0];
                counts.put(label, counts.get(label) +1 );
                clusters.get(label).put(y, x, 255, 255, 255);
                rows++;
                if(y==cutout.rows()/2 && x==cutout.cols()/2){
                    System.out.println("label in mid is " + label);
                    System.out.println("min is "+ min);
                    System.out.println("max is " + max);
                }
            }
        }
        System.out.println("label col"+labels.cols());
        System.out.println("label rows"+labels.rows());
        System.out.println("cutout cols" +cutout.cols());
        System.out.println("cutout rows" +cutout.rows());
        int left = 0;
        int right = cutout.cols();

        int labelInMid = (int) labels.get(labels.rows()/2,0)[0];
        System.out.println("label in mid is " + labelInMid);
        //loop to right
        for(int y = 0; y <cutout.rows(); y++){
            //find the lowest point on the right
            for(int x = cutout.cols()/2 ; x < cutout.cols() ; x++){
                if((int) labels.get(y*cutout.cols() + x,0)[0] == labelInMid && right > x  ){
                    right = x;
                    System.out.println("x is " + x);
                    System.out.println("y is "+ y);
                    System.out.println("convert is " + (y*cutout.cols() + x));
                    System.out.println((int) labels.get(y*cutout.rows() + x,0)[0]);
                }
            }
            //find the lowest point on the left
            for(int x = cutout.cols()/2;x>=0;x--){
                if((int) labels.get(y*cutout.cols() + x,0)[0] == labelInMid && left < x ){
                    left = x;
                     System.out.println((int) labels.get(y*cutout.rows() + x,0)[0]);
                }
            }
        }
        System.out.println("left is " + left);
        System.out.println("right is " + right);
        //print out how many pixels for each
        System.out.println(counts);
        Point pt1 = new Point(left, clusters.get(0).height()/2);
        Point pt2 = new Point(right, clusters.get(0).height()/2);
        Imgproc.line( clusters.get(0), pt1, pt2, new Scalar(0,255,0), 3);
        return clusters;
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
                if( originalbitmap.getHeight() * 4.0 / 8.0 <  contourList.get(i).toList().get(j).y && originalbitmap.getHeight() * 5.0 / 8.0 > contourList.get(i).toList().get(j).y ){
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
