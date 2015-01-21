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
// Continue Screen
//-----------------------------------------------------------------
// This is the screen in which the Player may choose to continue a game
// already in progress or to start a new game. If a game is continued,
// the main Pokemon class is called, if a new game is chosen then the
// Gender Chooser activity is called.
//-----------------------------------------------------------------

import drkstrinc.pokemon.R;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.app.Activity;
import android.app.AlertDialog;

public class ContinueScreen extends Activity {
	private MediaPlayer bgm;
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestFullScreen();
		this.setContentView(R.layout.activity_continue);
		bgm = MediaPlayer.create(getApplicationContext(), R.raw.continuescreen);
		bgm.setLooping(true);
		bgm.start();
		final Intent continued = new Intent(this, Pokemon.class);
		final Intent newgame = new Intent(this, GenderChooser.class);
		final Intent credits = new Intent(this, Credits.class);
		
		Button continueGame = (Button) findViewById(R.id.continueButton);
		continueGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				bgm.pause();
				myGlobal.getInstance().setContinued(true);
				startActivity(continued);
			}
		});
		
		Button newGame = (Button) findViewById(R.id.newGameButton);
		newGame.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				bgm.pause();
				myGlobal.getInstance().setContinued(false);
				startActivity(newgame);
			}
		});
		
		Button about = (Button) findViewById(R.id.about);
		about.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				/*AlertDialog ad = new AlertDialog.Builder(ContinueScreen.this).create();
        		ad.setTitle("About");
        		ad.setMessage("Written by:" +
        				"\nZach Harsh" +
        				"\nJustin Kuzma");
        		ad.show();*/
				startActivity(credits);
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