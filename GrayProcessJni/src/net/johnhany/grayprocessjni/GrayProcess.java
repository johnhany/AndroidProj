package net.johnhany.grayprocessjni; 

import org.opencv.android.BaseLoaderCallback;  
import org.opencv.android.LoaderCallbackInterface;  
import org.opencv.android.OpenCVLoader; 
import android.os.Bundle;
import android.app.Activity;  
import android.graphics.Bitmap;
import android.graphics.BitmapFactory; 
import android.graphics.Bitmap.Config;        
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;  
  
public class GrayProcess extends Activity implements OnClickListener{  
	  
    private Button btnProc;  
    private ImageView imageView;  
    private Bitmap bmp;  
      
     private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {  
        @Override  
        public void onManagerConnected(int status) {  
            switch (status) {  
                case LoaderCallbackInterface.SUCCESS:{  
                    System.loadLibrary("image_proc");  
                } break;  
                default:{  
                    super.onManagerConnected(status);  
                } break;  
            }  
        }  
    };  
      
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_gray_process);  
        btnProc = (Button) findViewById(R.id.btn_gray_process);  
        imageView = (ImageView) findViewById(R.id.image_view);  
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.testpic1);  
        imageView.setImageBitmap(bmp);  
        btnProc.setOnClickListener(this);  
    }  
  
    @Override  
    public void onClick(View v) {  
           
        int w = bmp.getWidth();  
        int h = bmp.getHeight();  
        int[] pixels = new int[w*h];       
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);  
        int[] resultInt = ImageProc.grayProc(pixels, w, h);  
        Bitmap resultImg = Bitmap.createBitmap(w, h, Config.ARGB_8888);  
        resultImg.setPixels(resultInt, 0, w, 0, 0, w, h);  
        imageView.setImageBitmap(resultImg);      
    }  
      
    @Override  
    public void onResume(){  
        super.onResume();  
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback);  
    }  
}  