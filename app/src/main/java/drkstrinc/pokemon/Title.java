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
// Title Screen
//-----------------------------------------------------------------
// This is the first activity of the engine. It is the title screen
// portion of the engine. When the screen is tapped, the continue
// screen activity will be called.
//-----------------------------------------------------------------

import java.util.ArrayList;


import android.content.Intent;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.widget.Toast;

import com.e3roid.E3Activity;
import com.e3roid.E3Engine;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Shape;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.texture.AssetTexture;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.drawable.tmx.TMXException;
import com.e3roid.drawable.tmx.TMXLayer;
import com.e3roid.drawable.tmx.TMXTiledMap;
import com.e3roid.drawable.tmx.TMXTiledMapLoader;
import com.e3roid.event.SceneUpdateListener;
import com.e3roid.util.Debug;

import drkstrinc.pokemon.R;

public class Title extends E3Activity implements SceneUpdateListener {

	private final static int WIDTH  = 480;
	private final static int HEIGHT = 320;
	
	private TMXTiledMap map;
	private ArrayList<TMXLayer> mapLayers;
	private Sprite titleOverlay;
	private AnimatedSprite pokemonOverlay;
	private ArrayList<AnimatedSprite.Frame> pokemon = new ArrayList<AnimatedSprite.Frame>();
	private MediaPlayer bgm;
	
	private E3Scene scene;

	private int mapStartX = 0;

	@Override
	public E3Engine onLoadEngine() {
		E3Engine engine = new E3Engine(this, WIDTH, HEIGHT);
		engine.setSize(WIDTH, HEIGHT, E3Engine.RESOLUTION_STRETCH_SCENE);
		engine.requestFullScreen();
		engine.requestLandscape();
		
		bgm = MediaPlayer.create(getApplicationContext(), R.raw.title);
		bgm.setLooping(true);
		bgm.start();
		
		Toast.makeText(Title.this, "Tap to Play!", Toast.LENGTH_SHORT).show();
		
		return engine;
	}

	@Override
	public E3Scene onLoadScene() {
		scene = new E3Scene();
		scene.registerUpdateListener(10, this);
		
		if (mapLayers != null) {
			for (TMXLayer layer : mapLayers) {
				layer.setSceneSize(getWidth(), getHeight());
				layer.loop(true);
				scene.getTopLayer().add(layer);
				scene.getTopLayer().add(titleOverlay);
				scene.getTopLayer().add(pokemonOverlay);
			}
			
		} 
		else {
			Toast.makeText(this, "Failed to load TMX map.", Toast.LENGTH_LONG).show();
		}
		
		scene.addEventListener(titleOverlay);
		scene.setBackgroundColor(0f, 0f, 0f, 1);
		pokemonOverlay.move(186, 132);
		pokemonOverlay.animate(200, pokemon);
		
		return scene;
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
		Toast.makeText(Title.this, "Tap to Play!", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onLoadResources() {
		try {
			TMXTiledMapLoader mapLoader = new TMXTiledMapLoader();
			map = mapLoader.loadFromAsset("title.tmx", this);
			mapLayers = map.getLayers();
		} 
		catch (TMXException e) {
			Debug.e(e);
		}
		
		final Intent intent = new Intent(this, ContinueScreen.class);
		
		titleOverlay = new Sprite(new AssetTexture("titleOverlay.png", 480, 320, this)) {
            @Override
            public boolean onTouchEvent(E3Scene scene, Shape shape, MotionEvent motionEvent, int localX, int localY) {
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                	startActivity(intent);
                }
                return false;
            }
        };
        
		pokemonOverlay = new AnimatedSprite(new TiledTexture("titlePokemon.png", 128, 128, 0, 0, 0, 0, this), 0, 0);
		pokemon = new ArrayList<AnimatedSprite.Frame>();
		pokemon.add(new AnimatedSprite.Frame(0, 0));
		pokemon.add(new AnimatedSprite.Frame(1, 0));
		pokemon.add(new AnimatedSprite.Frame(2, 0));
		pokemon.add(new AnimatedSprite.Frame(3, 0));
		
	}

	@Override
	public void onUpdateScene(E3Scene scene, long elapsedMsec) {
		if (mapLayers != null) {
			for (TMXLayer layer : mapLayers) {
				mapStartX += 4;
				layer.setPosition(mapStartX, 0);
				mapStartX = layer.getX();
			}
		}
		
	}
}
