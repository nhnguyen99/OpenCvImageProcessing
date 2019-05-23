package com.example.finalsolution;

import android.Manifest;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class MainActivity extends AppCompatActivity {

    Button programmerView, ManagerView, selectPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        programmerView = findViewById(R.id.programmerView);
        ManagerView = findViewById(R.id.managerView);
        selectPhoto = findViewById(R.id.takePicture);
        programmerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, programmer.class);
                startActivity(intent);
            }
        });

        ManagerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, manager.class);
                startActivity(intent);
            }
        });

        selectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto.setText("not implement");
            }
        });

    }

//    load OpenCV
    private BaseLoaderCallback mOpenCVCallBack = new
            BaseLoaderCallback(this) {
                @Override
                public void onManagerConnected(int status) {
                    switch (status){
                        case LoaderCallbackInterface
                                .SUCCESS:
                            break;
                        default:
                            super.onManagerConnected(status);
                            break;
                    }
                }
            };


    @Override
    protected void  onResume(){
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mOpenCVCallBack);
        } else {
            mOpenCVCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }


    }



}
