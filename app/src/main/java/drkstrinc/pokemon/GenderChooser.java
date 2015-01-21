package drkstrinc.pokemon;

//-----------------------------------------------------------------
// Pokemon Android Engine
//-----------------------------------------------------------------
// Zach Harsh
// Justin Kuzma
//-----------------------------------------------------------------
// This Engine is developed using the E3roid OpenGL Engine for Android
// that is open source and freely redistributed. This engine is open
// source as well and comes with no warranty or license agreements. It
// is not without its bugs and is in an early stage of development. If
// you decide to use this engine or modify it in any way, please give
// credit to its original authors.
//-----------------------------------------------------------------
// Gender Chooser
//-----------------------------------------------------------------
// This activity is called when the player starts a new game. The
// player is asked whether they are a boy or a girl and then the main
// Pokemon class is called after setting a global flag to true or 
// false (boy or girl, respectively).
//-----------------------------------------------------------------

import drkstrinc.pokemon.R;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.app.Activity;

public class GenderChooser extends Activity {
	private MediaPlayer bgm;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestFullScreen();
		this.setContentView(R.layout.activity_gender);
		bgm = MediaPlayer.create(getApplicationContext(), R.raw.intro);
		bgm.setLooping(true);
		bgm.start();
		final Intent intent = new Intent(this, Pokemon.class);
		ImageButton male = (ImageButton) findViewById(R.id.malebutton);
		male.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				bgm.pause();
				myGlobal.getInstance().setValue(true);
				startActivity(intent);
			}
		});
		
		ImageButton female = (ImageButton) findViewById(R.id.girlbutton);
		female.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				bgm.pause();
				myGlobal.getInstance().setValue(false);
				startActivity(intent);
			}
		});
	}
	
	@Override
	public void onPause() {
		super.onPause();
		bgm.pause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		bgm.start();
	}

	private void requestFullScreen() {
		Window window = this.getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		window.requestFeature(Window.FEATURE_NO_TITLE);
	}
}