package net.johnhany.grayslidebar;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.graphics.drawable.BitmapDrawable;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

public class GraySlidebar extends Activity {

	ImageView srcImage;
	Bitmap srcBitmap;
	SeekBar blendbar;
	
	double blender;
	int w,h;
	
	IplImage srcImg = null;
	IplImage tmp1Img = null;
	IplImage tmp3Img = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gray_slidebar);
		srcImage = (ImageView) findViewById(R.id.imageView1);
		blendbar = (SeekBar)findViewById(R.id.sbBlender);
		
		srcBitmap = ((BitmapDrawable)srcImage.getDrawable()).getBitmap();
		w = srcBitmap.getWidth();
		h = srcBitmap.getHeight();
		
		srcImg = IplImage.create(w, h, IPL_DEPTH_8U, 4);
		srcBitmap.copyPixelsToBuffer(srcImg.getByteBuffer());
		
		tmp1Img = IplImage.create(w, h, IPL_DEPTH_8U, 1);
		tmp3Img = IplImage.create(w, h, IPL_DEPTH_8U, 4);
		cvCvtColor(srcImg, tmp1Img, CV_BGR2GRAY);
		cvCvtColor(tmp1Img, tmp3Img, CV_GRAY2BGR);
		//cvMerge(tmp1Img, tmp1Img, tmp1Img, null, tmp3Img);
		
		addListenerOnSeekBar();
	}
	
	public void addListenerOnSeekBar() {

		OnSeekBarChangeListener barListener = new OnSeekBarChangeListener() {  
			  
            @Override  
            public void onProgressChanged(SeekBar seekBar, int progress,  boolean fromUser) {
            	blender = (double)blendbar.getProgress() / (double)blendbar.getMax();

        		IplImage dstImg = IplImage.create(w, h, IPL_DEPTH_8U, 4);

				cvAddWeighted(srcImg, 1-blender, tmp3Img, blender, 0, dstImg);
				
				srcBitmap.copyPixelsFromBuffer(dstImg.getByteBuffer());
				srcImage.setImageBitmap(srcBitmap);
				
				dstImg.release();
            }
            
            @Override  
            public void onStartTrackingTouch(SeekBar seekBar) {  
                ;  
            }  
  
            @Override  
            public void onStopTrackingTouch(SeekBar seekBar) {  
                ;
            }  
        };  
  
        blendbar.setOnSeekBarChangeListener(barListener);
	}

}