package net.johnhany.imageprocess; 

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;  
import org.opencv.android.LoaderCallbackInterface;  
import org.opencv.android.OpenCVLoader; 

import android.os.Bundle;
import android.app.Activity;  
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory; 
import android.graphics.Bitmap.Config;        
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;  
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

public class ImageProcess extends Activity{  
	
	private Spinner spinner1, spinner2;
    private Button btnProc;
    private Button btnChange;
    private ImageView imageView;  
    private Bitmap bmp;
    
    private String GRAY_PROCESS = "Grayscale process";
    private String EDGE_DETECT = "Edge detection";
    private String CONTRAST_ENHANCE = "Contrast enhancement";
    private String GAUSSIAN_BLUR = "Gaussian blur";
    private String MOTION_BLUR = "Motion blur";
    private String MEAN_FILTER = "Mean filter";
    
    private String PIC_1 = "Rock No.1";
    private String PIC_2 = "Rock No.2";
    
    private int methodSelected = 0;
    private int picSelected = 1;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {  
        @Override  
        public void onManagerConnected(int status) {  
            switch (status) {  
                case LoaderCallbackInterface.SUCCESS:{  
                    System.loadLibrary("opencv_jni");  
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
        
        setContentView(R.layout.activity_image_process);
        
    	btnProc = (Button) findViewById(R.id.btn_proc);
    	btnChange = (Button) findViewById(R.id.btn_change);
    	imageView = (ImageView) findViewById(R.id.image_view);
        
        Toast.makeText(ImageProcess.this,"Please select method and picture.",Toast.LENGTH_SHORT).show();
        
        initSpinner();
    	addListenerOnSpinner();
    	addListenerOnButton();
    }
     
	public void initSpinner() {
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		List<String> list1 = new ArrayList<String>();
    	list1.add(GRAY_PROCESS);
    	list1.add(EDGE_DETECT);
    	list1.add(CONTRAST_ENHANCE);
    	list1.add(GAUSSIAN_BLUR);
    	list1.add(MOTION_BLUR);
    	list1.add(MEAN_FILTER);
    	
    	ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setPrompt("Choose a method");

        
        spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list2 = new ArrayList<String>();
        list2.add(PIC_1);
        list2.add(PIC_2);
        
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list2);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setPrompt("Choose a picture");
	}
	
	public void addListenerOnSpinner() {

		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				Object method = parent.getItemAtPosition(pos);
				
				if(method.toString().equals(GRAY_PROCESS)) {
					methodSelected = 1;
				}else if(method.toString().equals(EDGE_DETECT)) {
					methodSelected = 2;
				}else if(method.toString().equals(CONTRAST_ENHANCE)) {
					methodSelected = 3;
				}else if(method.toString().equals(GAUSSIAN_BLUR)) {
					methodSelected = 4;
				}else if(method.toString().equals(MOTION_BLUR)) {
					methodSelected = 5;
				}else if(method.toString().equals(MEAN_FILTER)) {
					methodSelected = 6;
				}
				
				loadPicture();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
				Object pic = parent.getItemAtPosition(pos);
				
				if(pic.toString().equals(PIC_1)) {
					picSelected = 1;
				}
				else if(pic.toString().equals(PIC_2)) {
					picSelected = 2;
				}
				
				loadPicture();
			}
				 
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}
	
	public void addListenerOnButton() {
		
    	btnProc.setOnClickListener(new OnClickListener() {
    		@Override
    		public void onClick(View v) {
     
    			int w = bmp.getWidth();
    	        int h = bmp.getHeight();
    	        int[] pixels = new int[w*h];
    	        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
    	        Bitmap resultImg = Bitmap.createBitmap(w, h, Config.ARGB_8888);
    	        
    	        switch(methodSelected) {
    	        	case 1:
    	        		resultImg.setPixels(OpencvJni.gray(pixels, w, h), 0, w, 0, 0, w, h);
    	        		break;
    	        	case 2:
    	        		resultImg.setPixels(OpencvJni.edgeDetect(pixels, w, h), 0, w, 0, 0, w, h);
    	        		break;
    	        	case 3:
    	        		resultImg.setPixels(OpencvJni.contrastEnhance(pixels, w, h), 0, w, 0, 0, w, h);
    	        		break;
    	        	case 4:
    	        		resultImg.setPixels(OpencvJni.gaussianBlur(pixels, w, h), 0, w, 0, 0, w, h);
    	        		break;
    	        	case 5:
    	        		resultImg.setPixels(OpencvJni.motionBlur(pixels, w, h), 0, w, 0, 0, w, h);
    	        		break;
    	        	case 6:
    	        		resultImg.setPixels(OpencvJni.meanFilter(pixels, w, h), 0, w, 0, 0, w, h);
    	        		break;
    	        	default:
    	        		break;
    	        }
    	        imageView.setImageBitmap(resultImg);
    	        
    			Toast.makeText(ImageProcess.this,
    					"Apply "+ String.valueOf(spinner1.getSelectedItem()) +  
    					" on "+ String.valueOf(spinner2.getSelectedItem()),
    					Toast.LENGTH_SHORT).show();
    		}
    	});
    	
    	final Context context = this;
		btnChange.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
 
			    Intent intent = new Intent(context, TouchProcess.class);
                            startActivity(intent);   
 
			}
 
		});
	}
	
	public void loadPicture() {
		int picResource;
		
		if(picSelected == 1)
			picResource = R.drawable.testpic1;
		else
			picResource = R.drawable.testpic2;
		
		bmp = BitmapFactory.decodeResource(getResources(), picResource);
        imageView.setImageBitmap(bmp);
	}
	
	@Override  
    public void onResume() {  
        super.onResume();  
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_8, this, mLoaderCallback);  
    }
}
