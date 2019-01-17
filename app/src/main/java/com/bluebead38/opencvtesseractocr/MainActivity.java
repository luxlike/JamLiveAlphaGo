package com.bluebead38.opencvtesseractocr;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String GOOGLE_SERACH_URL = "https://www.google.com/search?q=";
    private static final String TAG = "MainActivity";
    private Button mBtnCameraView;
    private Button mBtnCapture;
    private EditText mEditOcrResult;
    private String datapath = "";
    private String lang = "";

    private int ACTIVITY_REQUEST_CODE = 1;
    private static final int MEDIA_REQUEST_CODE = 100;

    static TessBaseAPI sTess;
    private WebView webView;
    private static final int SYSTEM_ALERT_WINDOW_PERMISSION = 2084;

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    

                } break;


                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OpenCVLoader.initDebug();// No implementation found for long org.opencv.core.Mat.n_Mat 에러 방지
        Log.i(TAG, "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mOpenCVCallBack))
        {
            Log.e(TAG, "Cannot connect to OpenCV Manager");
        }
        else {
            Log.i(TAG, "opencv successfull");
        }

        askPermission();

        // 뷰 선언
        mBtnCameraView = (Button) findViewById(R.id.btn_camera);
        mBtnCapture = (Button)findViewById(R.id.btn_capture);
        mEditOcrResult = (EditText) findViewById(R.id.edit_ocrresult);
        sTess = new TessBaseAPI();


        // Tesseract 인식 언어를 한국어로 설정 및 초기화
        lang = "kor";
        datapath = getFilesDir()+ "/tesseract";

        if(checkFile(new File(datapath+"/tessdata")))
        {
            sTess.init(datapath, lang);
        }

        mBtnCameraView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 버튼 클릭 시
                // Camera 화면 띄우기
                Intent mIttCamera = new Intent(MainActivity.this, com.bluebead38.opencvtesseractocr.CameraView.class);
                startActivityForResult(mIttCamera, ACTIVITY_REQUEST_CODE);

            }
        });

        findViewById(R.id.btn_capture).setOnClickListener(this);

//        mBtnCapture.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                startService(new Intent(MainActivity.this, FloatingViewService.class));
//                finish();
//
//            }
//        });
    }

    private void askPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    boolean checkFile(File dir)
    {
        //디렉토리가 없으면 디렉토리를 만들고 그 후에 파일을 카피
        if(!dir.exists() && dir.mkdirs()) {
            copyFiles();
        }
        //디렉토리가 있지만 파일이 없으면 파일카피 진행
        if(dir.exists()) {
            String datafilepath = datapath + "/tessdata/" + lang + ".traineddata";
            File datafile = new File(datafilepath);
            if(!datafile.exists()) {
                copyFiles();
            }
        }
        return true;
    }

    void copyFiles()
    {
        AssetManager assetMgr = this.getAssets();

        InputStream is = null;
        OutputStream os = null;

        try {
            is = assetMgr.open("tessdata/"+lang+".traineddata");

            String destFile = datapath + "/tessdata/" + lang + ".traineddata";

            os = new FileOutputStream(destFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            is.close();
            os.flush();
            os.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode== ACTIVITY_REQUEST_CODE)
            {
                // 받아온 OCR 결과 출력
                String resultStr = data.getStringExtra("STRING_OCR_RESULT");
                mEditOcrResult.setText(resultStr);

                webView = (WebView)findViewById(R.id.webView);
                webView.getSettings().setJavaScriptEnabled(true);

                webView.loadUrl(GOOGLE_SERACH_URL + resultStr);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startService(new Intent(MainActivity.this, FloatingViewService.class));
            finish();
        } else if (Settings.canDrawOverlays(this)) {
//            Intent intent= new Intent();
//            intent.setClass(MainActivity.this,FloatingViewService.class);
//            intent.setAction("startService");

//            CustomData data = new CustomData();
//            data.setsMediaProjection(sMediaProjection);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("value",data);
//
//            intent.putExtras(bundle);

//            startService(intent);

            startService(new Intent(MainActivity.this, FloatingViewService.class));
            finish();
        } else {
            askPermission();
            Toast.makeText(this, "You need System Alert Window Permission to do this", Toast.LENGTH_SHORT).show();
        }
    }


}