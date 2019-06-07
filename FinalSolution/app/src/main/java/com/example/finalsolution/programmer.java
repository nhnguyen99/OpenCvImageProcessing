package com.example.finalsolution;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import android.widget.Toast;


import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class programmer extends AppCompatActivity {

    Button apply;
    private int method = 0;
    String[] imageProcessingMethod;
    ImageView imageView;
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
    int current_image;
    Button next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmer);

        Spinner spinner = findViewById(R.id.spinner1);
        apply = findViewById(R.id.apply);
        imageView = findViewById(R.id.imageView);
        next = findViewById(R.id.next);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imageProcessingMethod = getResources().getStringArray(R.array.imageProcessingMethod);
                Toast.makeText(programmer.this, "you choose :"+ imageProcessingMethod[position], Toast.LENGTH_SHORT).show();
                method = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_image++;
                current_image = current_image % images.length;
                imageView.setImageResource(images[current_image]);
            }
        });


//        imageView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                current_image++;
//                current_image = current_image % images.length;
//                imageView.setImageResource(images[current_image]);
//                return true;
//            }
//        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(programmer.this, "apply :"+imageProcessingMethod[method] + "...", Toast.LENGTH_SHORT).show();
                switch (method){
                    case 0:
                        setOriginal();
                        break;
                    case 1:
                        ostuThreshold();

                        break;
                    case 2:
                        imageGradient();

                        break;
                    case 3:
                        cannyContours();

                        break;
                    case 4:
                        ostuThresholdSlow();
                        break;
                    case 5:
                        blur();
                        break;

                    case 6:
                        medianBlur();
                        break;
                    case 7:
                        dilate();
                        break;
                    case 8:
                        gradientThreshold();
                        break;
                    case 9:
                        cannyContoursThreshold();
                        break;
                    case 10:
                        k_Mean();
                        break;
                    case 11:
                        gradientThresholdContours();
                        break;
                        default:
                            setOriginal();
                }

            }
        });

    }

    private void setOriginal(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        imageView.setImageBitmap(bitmap);
    }

    private void ostuThreshold(){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat Rgba = new Mat();
        Mat ostuMat = new Mat();
        Utils.bitmapToMat(bitmap,Rgba);
        Imgproc.cvtColor(Rgba,ostuMat,Imgproc.COLOR_RGBA2GRAY,0);
        Imgproc.threshold(ostuMat,ostuMat,0,255,Imgproc.THRESH_OTSU);
        Bitmap outputhBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(ostuMat,outputhBitmap);
        imageView.setImageBitmap(outputhBitmap);
    }

    private void imageGradient(){
        Mat grayMat = new Mat();
        Mat sobel = new Mat();
        Mat grad_x = new Mat();
        Mat abs_grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_y = new Mat();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat originalMat = new Mat();
        Utils.bitmapToMat(bitmap,originalMat);
        Imgproc.cvtColor(originalMat, grayMat,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Sobel(grayMat,grad_x,CvType.CV_16S,1,0,3,1,0);
        Imgproc.Sobel(grayMat,grad_y,CvType.CV_16S,0,1,3,1,0);
        Core.convertScaleAbs(grad_x,abs_grad_x);
        Core.convertScaleAbs(grad_y,abs_grad_y);
        Core.addWeighted(abs_grad_x,0.5,abs_grad_y,0.5,1,sobel);
        Bitmap currentBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(sobel,currentBitmap);
        imageView.setImageBitmap(currentBitmap);
    }

    private void cannyContours(){
        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat originalMat = new Mat();
        Utils.bitmapToMat(originalbitmap,originalMat);

        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat hierarchy = new Mat();

        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();

        Imgproc.cvtColor(originalMat,grayMat,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Canny(grayMat,cannyEdges,10,100);
        Imgproc.findContours(cannyEdges,contourList,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

        Mat contours = new Mat();
        contours.create(cannyEdges.rows(),cannyEdges.cols(),CvType.CV_8UC3);
        Random r = new Random();

        for (int i = 0; i< contourList.size();i++){
            Imgproc.drawContours(contours,contourList,i,new Scalar(r.nextInt(255),r.nextInt(255),r.nextInt(255)),-1);
        }

        Bitmap currentBitmap = Bitmap.createBitmap(originalbitmap.getWidth(),originalbitmap.getHeight(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(contours,currentBitmap);
        imageView.setImageBitmap(currentBitmap);
    }

    private void blur(){
        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat mat = new Mat();
        Utils.bitmapToMat(originalbitmap,mat);
        Imgproc.blur(mat,mat,new Size(10,10));
        Utils.matToBitmap(mat,originalbitmap);
        imageView.setImageBitmap(originalbitmap);
    }

    private void medianBlur(){
        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat mat = new Mat();
        Utils.bitmapToMat(originalbitmap,mat);
        Imgproc.medianBlur(mat,mat,3);
        Utils.matToBitmap(mat,originalbitmap);
        imageView.setImageBitmap(originalbitmap);
    }

    private void dilate(){
        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat mat = new Mat();
        Utils.bitmapToMat(originalbitmap,mat);
        Imgproc.dilate(mat,mat,Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(3,3)));
        Utils.matToBitmap(mat,originalbitmap);
        imageView.setImageBitmap(originalbitmap);
    }
    private void ostuThresholdSlow(){
        Bitmap original = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Bitmap BWimg = Bitmap.createBitmap(original.getWidth(), original.getHeight(), original.getConfig());

        int width = original.getWidth();
        int height = original.getHeight();
        int A, R, G, B, colorPixel;

        double Wcv = 0, th = 0;
        int[] tPXL = new int[256];
        int[][] pxl = new int[width][height];
        double Bw, Bm, Bv, Fw, Fm, Fv;
        int np, ImgPix = 0, fth = 0;

        // pixel check for histogram //
        for (int x = 0; x < width ; x++) {
            for (int y = 0; y < height; y++) {

                colorPixel = original.getPixel(x, y);

                A = Color.alpha(colorPixel);
                R = Color.red(colorPixel);
                G = Color.green(colorPixel);
                B = Color.blue(colorPixel);

                int gray = (int) ((0.2126 * R) + (0.7152 * G) + (0.0722 * B)); // (int) ( (0.299 * R) + (0.587 * G) + (0.114 * B) );
                pxl[x][y] = gray;
                tPXL[gray] = tPXL[gray] + 1;
                ImgPix = ImgPix + 1;
            }
        }

        // ----- histo-variance ----- //
        for (int t = 0; t < 256; t++) {
            Bw = 0;
            Bm = 0;
            Bv = 0;
            Fw = 0;
            Fm = 0;
            Fv = 0;
            np = 0;

            if (t == 0) { // all white/foreground as t0 ----- //
                Fw = 1;

                for (int d = 0; d < 256; d++) { //mean
                    Fm = Fm + (d * tPXL[d]);
                }
                Fm = Fm / ImgPix;

                for (int e = 0; e < 256; e++) { //variance
                    Fv = Fv + (Math.pow((e - Fm), 2) * tPXL[e]);
                }
                Fv = Fv / ImgPix;

            } else { // main thresholding
                for (int d = 0; d < (t - 1); d++) { // BG weight & mean + BG pixel
                    Bw = Bw + tPXL[d];
                    Bm = Bm + (d * tPXL[d]);
                    np = np + tPXL[d];
                }
                Bw = Bw / ImgPix;
                Bm = Bm / np;

                for (int e = 0; e < (t - 1); e++) { //BG variance
                    Bv = Bv + (Math.pow((e - Bm), 2) * tPXL[e]);
                }
                Bv = Bv / np;

                for (int j = t; j < 256; j++) { // FG weight & mean + BG pixel
                    Fw = Fw + tPXL[j];
                    Fm = Fm + (j * tPXL[j]);
                    np = ImgPix - np;
                }
                Fw = Fw / ImgPix;
                Fm = Fm / np;

                for (int k = t; k < 256; k++) { //FG variance
                    Fv = Fv + (Math.pow((k - Fm), 2) * tPXL[k]);
                }
                Fv = Fv / np;

            }

            // within class variance
            Wcv = (Bw * Bv) + (Fw * Fv);

            if (t == 0) {
                th = Wcv;
            } else if (Wcv < th) {
                th = Wcv;
                fth = t;
            }
        }

        // set binarize pixel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                int fnpx = pxl[x][y];
                colorPixel = original.getPixel(x, y);

                A = Color.alpha(colorPixel);

                if (fnpx > fth) { //R > fth
                    fnpx = 255;
                    BWimg.setPixel(x, y, Color.argb(A, fnpx, fnpx, fnpx));
                } else {
                    fnpx = 0;
                    BWimg.setPixel(x, y, Color.argb(A, fnpx, fnpx, fnpx));
                }
            }
        }

        imageView.setImageBitmap(BWimg);
//        return BWimg;
    }

    private void gradientThreshold(){
        Mat grayMat = new Mat();
        Mat sobel = new Mat();
        Mat grad_x = new Mat();
        Mat abs_grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_y = new Mat();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat originalMat = new Mat();
        Utils.bitmapToMat(bitmap,originalMat);
        Imgproc.cvtColor(originalMat, grayMat,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Sobel(grayMat,grad_x,CvType.CV_16S,1,0,3,1,0);
        Imgproc.Sobel(grayMat,grad_y,CvType.CV_16S,0,1,3,1,0);
        Core.convertScaleAbs(grad_x,abs_grad_x);
        Core.convertScaleAbs(grad_y,abs_grad_y);
        Core.addWeighted(abs_grad_x,0.5,abs_grad_y,0.5,1,sobel);
        Imgproc.threshold(sobel,sobel,0,255,Imgproc.THRESH_OTSU);
        Bitmap currentBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(sobel,currentBitmap);
        imageView.setImageBitmap(currentBitmap);
    }

    private void cannyContoursThreshold(){
        Bitmap originalbitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Mat originalMat = new Mat();
        Utils.bitmapToMat(originalbitmap,originalMat);

        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat hierarchy = new Mat();

        List<MatOfPoint> contourList = new ArrayList<MatOfPoint>();

        Imgproc.cvtColor(originalMat,grayMat,Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Canny(grayMat,cannyEdges,50,100);
        Imgproc.findContours(cannyEdges,contourList,hierarchy,Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_SIMPLE);

        Mat contours = new Mat();
        contours.create(cannyEdges.rows(),cannyEdges.cols(),CvType.CV_8UC3);
        Random r = new Random();

        for (int i = 0; i< contourList.size();i++){
            Imgproc.drawContours(contours,contourList,i,new Scalar(r.nextInt(255),r.nextInt(255),r.nextInt(255)),-1);
        }

        Imgproc.threshold(cannyEdges,contours,0,255,Imgproc.THRESH_OTSU);
        Bitmap currentBitmap = Bitmap.createBitmap(originalbitmap.getWidth(),originalbitmap.getHeight(),Bitmap.Config.RGB_565);
        Utils.matToBitmap(contours,currentBitmap);
        imageView.setImageBitmap(currentBitmap);
    }


    public void k_Mean(){
        Mat rgba = new Mat();
        Mat mHSV = new Mat();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),images[current_image]);
        Bitmap outputBitmap = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(bitmap,rgba);

        //must convert to 3 channel image
        Imgproc.cvtColor(rgba, mHSV, Imgproc.COLOR_RGBA2RGB,3);
        Imgproc.cvtColor(rgba, mHSV, Imgproc.COLOR_RGB2HSV,3);
        Mat clusters = cluster(mHSV, 3).get(0);
        Utils.matToBitmap(clusters,outputBitmap);

        imageView.setImageBitmap(outputBitmap);
    }


    public  List<Mat> cluster(Mat cutout, int k) {
        Mat samples = cutout.reshape(1, cutout.cols() * cutout.rows());
        Mat samples32f = new Mat();
        samples.convertTo(samples32f, CvType.CV_32F, 1.0 / 255.0);

        Mat labels = new Mat();
        //criteria means the maximum loop
        TermCriteria criteria = new TermCriteria(TermCriteria.COUNT, 20, 1);
        Mat centers = new Mat();
        Core.kmeans(samples32f, k, labels, criteria, 1, Core.KMEANS_PP_CENTERS, centers);

        return showClusters(cutout, labels, centers);
    }

    private static List<Mat> showClusters (Mat cutout, Mat labels, Mat centers) {
        centers.convertTo(centers, CvType.CV_8UC1, 255.0);
        centers.reshape(3);

        List<Mat> clusters = new ArrayList<Mat>();
        for(int i = 0; i < centers.rows(); i++) {
            clusters.add(Mat.zeros(cutout.size(), cutout.type()));
        }

        Map<Integer, Integer> counts = new HashMap<Integer, Integer>();
        for(int i = 0; i < centers.rows(); i++) counts.put(i, 0);

        int rows = 0;
        for(int y = 0; y < cutout.rows(); y++) {
            for(int x = 0; x < cutout.cols(); x++) {
                int label = (int)labels.get(rows, 0)[0];
                int r = (int)centers.get(label, 2)[0];
                int g = (int)centers.get(label, 1)[0];
                int b = (int)centers.get(label, 0)[0];
                counts.put(label, counts.get(label) + 1);
                clusters.get(label).put(y, x, b, g, r);
                rows++;
            }
        }
        System.out.println(counts);
        return clusters;
    }

    private void gradientThresholdContours(){

    }


}
