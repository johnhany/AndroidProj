package net.johnhany.changeimage;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageView;
import android.view.View;
import android.view.View.OnClickListener;

public class ChangeImage extends Activity {

	ImageView image;
	Button button;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_image);
		addListenerOnButton();
	}

	public void addListenerOnButton() {
		image = (ImageView) findViewById(R.id.imageView1);
		 
		button = (Button) findViewById(R.id.btnChangeImage);
		button.setOnClickListener(new OnClickListener() {
 
			@Override
			public void onClick(View arg0) {
				image.setImageResource(R.drawable.testpic2);
			}
 
		});
	}

}
