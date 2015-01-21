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
// Pokemon
//-----------------------------------------------------------------
// This is the main class of the Engine. It handles player input, drawing
// event handling, sound, etc. This class creates instances of many of
// the other classes found in this Engine such as the Monsters, Attacks,
// Items, BattleScene and other classes. This class is responsible for
// all game logic.
//-----------------------------------------------------------------

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.e3roid.E3Activity;
import com.e3roid.E3Engine;
import com.e3roid.E3Scene;
import com.e3roid.drawable.Layer;
import com.e3roid.drawable.Sprite;
import com.e3roid.drawable.controls.DigitalController;
import com.e3roid.drawable.controls.StickController;
import com.e3roid.drawable.modifier.AlphaModifier;
import com.e3roid.drawable.modifier.SpanModifier;
import com.e3roid.drawable.sprite.AnimatedSprite;
import com.e3roid.drawable.sprite.TextSprite;
import com.e3roid.drawable.texture.AssetTexture;
import com.e3roid.drawable.texture.TiledTexture;
import com.e3roid.drawable.tmx.TMXException;
import com.e3roid.drawable.tmx.TMXLayer;
import com.e3roid.drawable.tmx.TMXTile;
import com.e3roid.drawable.tmx.TMXTiledMap;
import com.e3roid.drawable.tmx.TMXTiledMapLoader;
import com.e3roid.event.ControllerEventListener;
import com.e3roid.event.SceneOnKeyListener;
import com.e3roid.event.SceneUpdateListener;
import com.e3roid.util.Debug;

import drkstrinc.pokemon.R;

public class Pokemon extends E3Activity implements SceneOnKeyListener, SceneUpdateListener, ControllerEventListener {
	//-----------------------------------------------------------------
	// Engine Variables
	//-----------------------------------------------------------------
		public E3Scene scene = new E3Scene();; //Global scene of the Engine
		private final static int WIDTH  = 480; //Game "Window" Width is 480 Pixels
		private final static int HEIGHT = 320; //Game "Window" Height is 320 Pixels
		private Typeface pokefont; //Pokemon FR/LG Font
		private boolean continuedGame = false; //New Game
		private boolean DEBUG = true; //Enables Logs, On Screen Coordinates, etc
		private boolean noClip = false; //Disable Collision Detection
		private boolean noBattle = false; //Disable Wild Encounters
		private boolean hapticFeedback = true; //Enable Vibration
		private boolean zeldaMessageStyle = false; //Zelda Styled Messages
		private boolean keyboardControlsEnabled = false; //Enable Hardware Keyboard Controls (Incomplete)
		private boolean soundEnabled = true; //Enable Sound (BGM and SE)
		private boolean enableDayNightSystem = true; //Enable the Day/Night System
		private boolean bgmContinued = false; //Whether or not a BGM has been continued
		private float alphaLevel = 0.9f; //Level of Controller Transparency
		private static Random randGen; //Global Random Number Generator for the Engine
		Monsters[] monsters = new Monsters[252]; //251 + 1 (MISSINGNO)
		Attacks[] attacks = new Attacks[560]; //559 + 1 (STRUGGLE)
	//-----------------------------------------------------------------
	
	//-----------------------------------------------------------------
	//Controller Variables
	//-----------------------------------------------------------------
		private DigitalController dpad; //D-Pad
		private DigitalController abbuttons; //A and B Buttons
		private DigitalController ssbuttons; //Start and Select Buttons
		private AssetTexture controlBaseTexture; //D-Pad Texture
		private AssetTexture ab; //A and B Button Texture
		private AssetTexture ss; //Start and Select Button Texture
		private AssetTexture nullKnob; //Blank Image for the A and B Buttons
		private Sprite arrow; //Arrow Selection Sprite
		private boolean ignoreInput = false; //Temporarily Ignore Input from Buttons
	//-----------------------------------------------------------------
	
	//-----------------------------------------------------------------
	// Player Variables
	//-----------------------------------------------------------------
		private AnimatedSprite player; //Players Animated Spritesheet
		private ArrayList<AnimatedSprite.Frame> playerDown = new ArrayList<AnimatedSprite.Frame>(); //Player Down Frames
		private ArrayList<AnimatedSprite.Frame> playerUp = new ArrayList<AnimatedSprite.Frame>(); //Player Up Frames
		private ArrayList<AnimatedSprite.Frame> playerLeft = new ArrayList<AnimatedSprite.Frame>(); //Player Left Frames
		private ArrayList<AnimatedSprite.Frame> playerRight = new ArrayList<AnimatedSprite.Frame>(); //Player Right Frames
		private TiledTexture texture; //Players Texture
		private TextSprite coords; //On Screen Coordinates of the Player (Tile Based)
		private boolean isMale = true; //Is the Player a boy or a girl
		private int x_loc = 42; //Starting X Location on the Map
		private int y_loc = 141; //Starting Y Location on the Map
		private int lastPCX = 42; //Last PokeCenter Location X
		private int lastPCY = 141; //Last PokeCenter Location Y
		private int xstep = 0; //Left or Right
		private int ystep = 0; //Up or Down
		private int xstepold = 0; //Left or Right (Previous step)
		private int ystepold = 0; //Up or Down (previous step)
		private int animateSpeed = 80; //Speed that the Players Animated Frames will play (Lower = Faster Movement)
		private int direction = 0; //Current direction Player is going
		private int previousDirection = 0; //What was the last direction the Player went
		private boolean movable = true; //Can the Player move
		private int stepscount = 0; //Current Steps Total (Used for Egg Hatching, status effects, etc)
		private String playerName = "Gold"; //Players Name
		private int badges = 0; //Number of the Players Badges Obtained
		private int money = 2000; //Player's Money
		private long timePlayed = 0; //Time Played
		private long currentTime = 0; //Current Time of the System
		private int trainerID = 0; //5 Digit Trainer ID
		private int pokedexCompletion = 1; //Amount of Pokemon the Player has captured
		private Monsters playerPokemon1 = new Monsters(); //Pokemon in Player party 1
		private Monsters playerPokemon2 = new Monsters(); //Pokemon in Player party 2
		private Monsters playerPokemon3 = new Monsters(); //Pokemon in Player party 3
		private Monsters playerPokemon4 = new Monsters(); //Pokemon in Player party 4
		private Monsters playerPokemon5 = new Monsters(); //Pokemon in Player party 5
		private Monsters playerPokemon6 = new Monsters(); //Pokemon in Player party 6
		private Monsters pokemonparty[] = new Monsters[6];  //Players Pokemon Party
		private Monsters pokemonPCStorage[] = new Monsters[30]; //PC Storage System (Bill's PC)
		private Items[] items = new Items[30]; //Players Items
	//-----------------------------------------------------------------
	
	//-----------------------------------------------------------------
	// Battle Variables
	//-----------------------------------------------------------------
		private Layer battleLayer; //Layer for the Battle Scene
		private BattleScene encounter; //Wild Pokemon Encounter Instance
		private Monsters wildPokemon = new Monsters(); //The Wild Pokemon in the Encounter
		private Sprite battleBG1; //Main Battle Image (FIGHT, PKMN, ITEM, RUN)
		private Sprite battleBG2; //Move Selection Battle Image
		private Sprite battleBG3; //Item Selection
		private Sprite battleBG4; //Pokemon Party
		private boolean inBattle = false; //Player is in a Pokemon Battle
		private int rndWildModifier = 10; //Modifier to trigger a Wild Encounter
		private int r; //Temporary Encounter Variable
		private int battleFontSize = 16; //Size of the font in Battles
		private TextSprite Battle_playerPokemonName; //Name of the Players first Pokemon
		private TextSprite Battle_playerPokemonLevel; //Level of the Players first Pokemon
		private TextSprite Battle_playerPokemonCurrentHP; //Current HP value of the Players first Pokemon
		private TextSprite Battle_playerPokemonHP; //Total HP value of the Players first Pokemon
		private TextSprite Battle_enemyPokemonName; //Name of the Wild Pokemon
		private TextSprite Battle_enemyPokemonLevel; //Level of the Wild Pokemon
		private TextSprite Battle_enemyPokemonCurrentHP; //Current HP value of the Wild Pokemon
		private TextSprite Battle_enemyPokemonHP; //Total HP of the Wild Pokemon
		private TextSprite Battle_wildNameAppeared; //Wild [INSERT NAME] Appeared
		private TextSprite Battle_FIGHT; //Fight Text (BattleBG1)
		private TextSprite Battle_PKMN; //Pkmn Text (BattleBG1)
		private TextSprite Battle_ITEM; //Item Text (BattleBG1)
		private TextSprite Battle_RUN; //Run Text (BattleBG1)
		private TextSprite Battle_SelectAMove; //Select a move (BattleBG2)
		private TextSprite Battle_playerPokemonMove1; //Name of Move 1 of the Players first Pokemon
		private TextSprite Battle_playerPokemonMove2; //Name of Move 2 of the Players first Pokemon
		private TextSprite Battle_playerPokemonMove3; //Name of Move 3 of the Players first Pokemon
		private TextSprite Battle_playerPokemonMove4; //Name of Move 4 of the Players first Pokemon
		private Sprite Battle_playerPokemonBackSprite; //Back Sprite of the Players first Pokemon
		private Sprite Battle_enemyPokemonFrontSprite; //Front Sprite of the Wild Pokemon
		public Sprite Battle_statusBRN; //BRN Status Effect Icon
		public Sprite Battle_statusFRZ; //FRZ Status Effect Icon
		public Sprite Battle_statusPAR; //PAR Status Effect Icon
		public Sprite Battle_statusPSN; //PSN Status Effect Icon
		public Sprite Battle_statusSLP; //SLP Status Effect Icon
		
		private TextSprite Battle_Item1; //Name of Item in Item Slot 1 in battle
		private TextSprite Battle_Item2; //Name of Item in Item Slot 2 in battle
		private TextSprite Battle_Item3; //Name of Item in Item Slot 3 in battle
		private TextSprite Battle_Item4; //Name of Item in Item Slot 4 in battle
		private int battleItemIndexSlot = 0; //Current Index of the Item Menu Selected
		
		private TextSprite Battle_pkmn1; //Name, Current HP, and Total HP of Party Pokemon 1
		private TextSprite Battle_pkmn2; //Name, Current HP, and Total HP of Party Pokemon 2
		private TextSprite Battle_pkmn3; //Name, Current HP, and Total HP of Party Pokemon 3
		private TextSprite Battle_pkmn4; //Name, Current HP, and Total HP of Party Pokemon 4
		private TextSprite Battle_pkmn5; //Name, Current HP, and Total HP of Party Pokemon 5
		private TextSprite Battle_pkmn6; //Name, Current HP, and Total HP of Party Pokemon 6
		private TextSprite Battle_SelectionCancel; //Cancel Text in the Pokemon Party Screen
		private TextSprite Battle_Switch; //Switch Text in the Pokemon Party Screen
		
		private AnimatedSprite pokemonIcon1; //Icon of Party Pokemon 1 (Animated)
		private AnimatedSprite pokemonIcon2; //Icon of Party Pokemon 2 (Animated)
		private AnimatedSprite pokemonIcon3; //Icon of Party Pokemon 3 (Animated)
		private AnimatedSprite pokemonIcon4; //Icon of Party Pokemon 4 (Animated)
		private AnimatedSprite pokemonIcon5; //Icon of Party Pokemon 5 (Animated)
		private AnimatedSprite pokemonIcon6; //Icon of Party Pokemon 6 (Animated)
		private ArrayList<AnimatedSprite.Frame> pkIcons; //Icon Frames
		
		private String[][] trainerTypes; //Pokemon Trainer Types (Trainer Battles)
	//-----------------------------------------------------------------
	
	//-----------------------------------------------------------------
	// Menu Variables
	//-----------------------------------------------------------------
		public boolean inMenu = false; //Player is in the In Game Menu
		private Layer menuLayer; //Layer for the Menu Scene
		private MenuScene menu; //Instance of the Menu
		private int menuFontSize = 18; //Size of the font in Menus
		
		private Sprite menuBG; //In Game Menu (Pokemon, Save, Options, etc) Sprite
		private Sprite pokedex; //Background for the Pokedex Menu Screen
		private Sprite bag; //Background for the Bag Menu Screen
		private Sprite pokegear; //Background for the Pokegear Menu Screen
		private Sprite trainercard; //Background for the Trainercard Menu Screen
		private Sprite save; //Background for the Save Menu Screen
		private Sprite option; //Background for the Option Menu Screen
		
		private Sprite entryMap; //Map Selection of the PokeGear
		private Sprite entryRadio; //Radio Selection of the PokeGear
		private Sprite entryPhone; //Phone Selection of the PokeGear
		private Sprite entryExit; //Exit Selection of the PokeGear
		
		private TextSprite save_PlayerName; //Player's Name Text on the Save Menu
		private TextSprite save_PlayerBadges; //Player's number of Badges Text on the Save Menu
		private TextSprite save_PlayerPokedex; //Player's Pokedex completion Text on the Save Menu
		private TextSprite save_PlayerTime; //Time played Text on the Save Menu
		private TextSprite save_currentMapName; //Name of the current Map zone on the Save Menu
		
		private Sprite cardSprite; //Card Sprite for the Player's Trainer Info Screen
		private TextSprite trainercard_trainerID; //ID Text of the Player for the Trainer Info Screen
		private TextSprite trainercard_playerName; //Player's Name for the Trainer Info Screen
		private TextSprite trainercard_playerMoney; //Player's Money for the Trainer Info Screen
		private TextSprite trainercard_pokemonOwned; //Number of Pokemon owned by the Player for the Trainer Info Screen
		
		private Sprite message_Text; //NPC Message Box Sprite
		private Sprite message_Sign; //Sign Message Box Sprite
	//-----------------------------------------------------------------
	
	//-----------------------------------------------------------------
	// Map Variables
	//-----------------------------------------------------------------
		private TMXTiledMap map; //The current Map loaded by the Game Engine
		private ArrayList<TMXLayer> mapLayers; //All of the Non-Collision Layers of the Map
		private TMXLayer collisionLayer; //Collision Layer of the Map
		private TMXLayer npcLayer; //NPC Layer
		private String currentMapName = "NEWBARKTOWN"; //Name of the Current Map
		private String previousMapName = "NEWBARKTOWN"; //Name of the previous Town/City with a PC
		private boolean dayTime = true; //Daytime or Nighttime
		private Sprite night; //Nighttime overlay
		private boolean insideBuilding = true; //Is the player inside or outside
		private boolean insideCave = false; //Is the player inside a cave
		private boolean insideFlashCave = false; //Is the player inside a dark cave
		private Sprite grassanimation; //Grass animation for when player walks in grass
		private String[][] encounters; //2D Array containing all wild pokemon encounter data
		private String[][] connections; //2D Array containing all map connection data
		private int tileX, tileY; //X and Y Location on the Map (Tile Based)
	//-----------------------------------------------------------------
		
	//-----------------------------------------------------------------
	// NPC Variables
	//-----------------------------------------------------------------
		private NPC currentNPC = new NPC(0, 0, "", "", ""); //Current NPC
		private TextSprite ts; //Current line of text from the current NPC
		private boolean showMessage = false; //Whether or not the message box is shown
		private int numMessages = 0; //Number of message lines of the current NPC
		private int currentMessage = 0; //Current message line
		private int totalMessageWords = 0; //Number of words of the current line
		private int curMessageWord = 0; //Current word of the current message line
	//-----------------------------------------------------------------
	
	//-----------------------------------------------------------------
	//Sound Variables
	//-----------------------------------------------------------------
		private MediaPlayer bgm; //Current BGM
		private SoundPool collisionSoundPool; //Collision Sound Effect
		private SoundPool selectionSoundPool; //Selection Sound Effect
		private SoundPool openmenuSoundPool; //Menu Opening Sound Effect
		private SoundPool doorSoundPool; //Door/Stairs Sound Effect
		private SoundPool daytimeSoundPool; //Menu Opening Sound Effect
		private SoundPool nighttimeSoundPool; //Door/Stairs Sound Effect
		private SoundPool wordSoundPool; //Word Sound Effect
		private SoundPool wordFinishedSoundPool; //Last word Sound Effect
		private SoundPool jumpSoundPool; //Last word Sound Effect
		private SoundPool saveSoundPool; //Last word Sound Effect
		private SoundPool damageSoundPool; //Damage Sound Effect
		private SoundPool damageLowSoundPool; //Damage Sound Effect
		private SoundPool damageHighSoundPool; //Damage Sound Effect
		private int collisionSound; //ID of Collision SE for the SoundPool
		private int selectionSound; //ID of Selection SE for the SoundPool
		private int openmenuSound; //ID of Open Menu SE for the SoundPool
		private int doorSound; //ID of Door SE for the SoundPool
		private int daytimeSound; //ID of the Daytime SE for the SoundPool
		private int nighttimeSound; //ID of the Nighttime SE for the SoundPool
		private int wordSound; //ID of the Word SE for the SoundPool
		private int wordFinishedSound; //ID of the Last Word SE for the SoundPool
		private int jumpSound; //ID of the Jump SE for the SoundPool
		private int saveSound; //ID of the Jump SE for the SoundPool
		private int damageSound; //Battle Damage Sound Effect
		private int damageHighSound; //Battle Damage Sound Effect
		private int damageLowSound; //Battle Damage Sound Effect
		private float volume; //Volume of the System
		private boolean loadedSE = false; //Is the SE for the SoundPool loaded
	//-----------------------------------------------------------------

	@Override
	public void onPause() {
		super.onPause();
		if (soundEnabled) bgm.pause();
		scene.onPause(); //Saves the Game State
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (soundEnabled) bgm.start();
		scene.onResume(); //Loads the Game State
	}
	
	@Override
	public void onBackPressed() {
		//Disables the Back Button
		//Use the In-Game Menu instead!
	}
	
	/**
	 * Initial method called upon starting this E3Activity. It is responsible
	 * for initializing the engine itself, and handles creating the graphical
	 * space for the Engine's content to be displayed upon.
	 * <p>
	 * This method returns the instance of the E3Engine created. 
	 */
	@Override
	public E3Engine onLoadEngine() {
		//Instantiate all necessary Engine Variables and globals if required
		E3Engine engine = new E3Engine(this, WIDTH, HEIGHT);
		//Ensures the Engines Resolution is exactly the same on all devices
		engine.setSize(WIDTH, HEIGHT, E3Engine.RESOLUTION_STRETCH_SCENE);
		engine.requestFullScreen();
		engine.requestLandscape();
		randGen = new Random();
		continuedGame = myGlobal.getInstance().getContinued();
		loadAudio();
		return engine;
	}
	
	/**
	 * Calls several other methods that will initialize the Game Engine
	 * and its Scene to their correct states. All important variables
	 * will be initialized and the HUD and several arrays will be filled
	 * with their correct data from text files found in the raw folder.
	 * <p>
	 * This method returns the instance of the scene being created.
	 */
	@Override
	public E3Scene onLoadScene() {
		initializeGame(); //Initializes important Game Variables
		loadHUD(); //Loads the Controller
		updateSky(); //Determines if it is day/night and adjusts the display accordingly
		startGame(continuedGame);
		return scene;
	}

	/**
	 * This method loads important resources that are shared throughout the
	 * entire Pokemon E3Activity, such as the character sprite, on screen
	 * controls, DEBUG information, message boxes, and the nighttime sprite
	 * overlay.
	 */
	@Override
	public void onLoadResources() {
		//Player Texture and Sprites (Animated)
		loadPokemon(); //Loads Pokemon data from a text file into an array
		loadAttacks(); //Loads Attack data from a text file into an array
		loadTrainerTypes();
		loadMapConnections(); //Loads Map Connection data from a text file into an array
		if (continuedGame) startGame(continuedGame);
		if (!continuedGame) isMale = myGlobal.getInstance().getValue();
		if (isMale) texture = new TiledTexture("gold.png", 48, 64, 8, 7, 0, 0, this);
		else texture = new TiledTexture("kris.png", 48, 64, 8, 7, 0, 0, this);
		playerDown = new ArrayList<AnimatedSprite.Frame>();
		playerDown.add(new AnimatedSprite.Frame(0, 0));
		playerDown.add(new AnimatedSprite.Frame(1, 0));
		playerDown.add(new AnimatedSprite.Frame(2, 0));
		playerDown.add(new AnimatedSprite.Frame(3, 0));
		playerLeft = new ArrayList<AnimatedSprite.Frame>();
		playerLeft.add(new AnimatedSprite.Frame(0, 1));
		playerLeft.add(new AnimatedSprite.Frame(1, 1));
		playerLeft.add(new AnimatedSprite.Frame(2, 1));
		playerLeft.add(new AnimatedSprite.Frame(3, 1));
		playerRight = new ArrayList<AnimatedSprite.Frame>();
		playerRight.add(new AnimatedSprite.Frame(0, 2));
		playerRight.add(new AnimatedSprite.Frame(1, 2));
		playerRight.add(new AnimatedSprite.Frame(2, 2));
		playerRight.add(new AnimatedSprite.Frame(3, 2));
		playerUp = new ArrayList<AnimatedSprite.Frame>();
		playerUp.add(new AnimatedSprite.Frame(0, 3));
		playerUp.add(new AnimatedSprite.Frame(1, 3));
		playerUp.add(new AnimatedSprite.Frame(2, 3));
		playerUp.add(new AnimatedSprite.Frame(3, 3));
		
		pkIcons = new ArrayList<AnimatedSprite.Frame>();
		pkIcons.add(new AnimatedSprite.Frame(0, 0));
		pkIcons.add(new AnimatedSprite.Frame(1, 0));
		
		//Player Coordinates Display
		coords = new TextSprite("", 18, this);
		coords.setColor(Color.WHITE);
		
		//Controller Textures
		controlBaseTexture = new AssetTexture("dpad.png", this);
		ab = new AssetTexture("ab.png", this);
		ss = new AssetTexture("ss.png", this);
		nullKnob = new AssetTexture("null.png", this);
		
		//Message Box Sprites
		message_Text = new Sprite(new AssetTexture("message_Text.png", 480, 320, this));
		message_Text.setVisible(false);
		message_Sign = new Sprite(new AssetTexture("message_Sign.png", 480, 320, this));
		message_Sign.setVisible(false);
		ts = new TextSprite("", menuFontSize, this);
		ts.setTypeface(pokefont);
		ts.hide();
		
		//Animation when player walks in tall grass
		grassanimation = new Sprite(new AssetTexture("grassanimation.png", 32, 32, this));
		grassanimation.hide();
		
		//Nighttime overlay
		night = new Sprite(new AssetTexture("night.png", 1280, 960, this));
		if (dayTime && !insideBuilding) night.setVisible(false);
	}

	/**
	 * Sets important global variables to their correct states and also
	 * initializes many variables shared by most methods of the Pokemon
	 * class. An asynchronous task is created to load the tmx map file
	 * and after completion it will add the map, player, on screen controls
	 * and optional debug info to the current scene (Map)
	 * <p>
	 * This method initializes an Asynchronous task.
	 */
	private void initializeGame() {
		//Center of the Screen (Where to Draw the Player Sprite)
		final int centerX = (WIDTH - texture.getTileWidth()) / 2;
		final int centerY = (HEIGHT - texture.getTileHeight()) / 2 + 2;
		player = new AnimatedSprite(texture, centerX, centerY) {
			@Override
			public Rect getCollisionRect() {
				//Player's Collision Box
				Rect rect = this.getRect();
				rect.left = rect.left + this.getWidth() / 3;
				rect.right = rect.right - this.getWidth() / 3;
				rect.top = rect.top + this.getHeight() / 3 + 24;
				rect.bottom = rect.bottom - this.getHeight() / 3;
				return rect;
			}
		};
		player.setTile(0,0); //Down Stationary Frame (Default Frame)
		scene.setBackgroundColor(0, 0, 0); //Black
		
		String fontPath = "pkmnfl.ttf";
	    pokefont = Typeface.createFromAsset(getAssets(), fontPath);
		
		//Show a Message stating that the Map is loading instead of just a white screen
		final TextSprite loadingText = new TextSprite("Loading", 24, this);
		loadingText.setColor(Color.WHITE);
		loadingText.move((getWidth() - loadingText.getWidth()) / 2, (getHeight() - loadingText.getHeight()) / 2);
		loadingText.addModifier(new SpanModifier(500L, new AlphaModifier(0, 0, 1)));
		scene.getTopLayer().add(loadingText);
		
		//Limit the Refresh Rate of the Screen to free up some CPU cycles and finish loading the Map faster
		engine.setRefreshMode(E3Engine.REFRESH_LIMITED);
		engine.setPreferredFPS(10);
		
		//Load the Map (All Layers)
		new AsyncTask<Void, Integer, TMXTiledMap>() {
			@Override
			protected TMXTiledMap doInBackground(Void... params) {
				try {
					TMXTiledMapLoader mapLoader = new TMXTiledMapLoader();
					TMXTiledMap map = mapLoader.loadFromAsset("johto.tmx",
							Pokemon.this);
					return map;
				} 
				catch (TMXException e) {
					Debug.e(e.getMessage());
				}
				return null;
			}
	
			@Override
			protected void onPostExecute(TMXTiledMap tmxTiledMap) {
				map = tmxTiledMap;
				if (tmxTiledMap != null && (mapLayers = map.getLayers()) != null) {
					for (TMXLayer layer : mapLayers) {
						layer.setSceneSize(getWidth(), getHeight());
						layer.setPosition(x_loc * 32 - (7 * 32), y_loc * 32
								- (5 * 32));
						if ("Ground".equals(layer.getName())) {
							layer.addChild(player);
						}
						if ("Characters".equals(layer.getName())) {
							npcLayer = layer;
						}
						if ("Collision".equals(layer.getName())) {
							collisionLayer = layer;
							continue;
						}
	
						scene.getTopLayer().remove(loadingText);
						scene.getTopLayer().add(layer);
						scene.getTopLayer().add(player);
						scene.getTopLayer().add(grassanimation);
						grassanimation.move(centerX + 8, centerY + 32);
	
						scene.getTopLayer().add(night);
						night.hide();
	
						scene.getTopLayer().add(message_Text);
						scene.getTopLayer().add(message_Sign);
						scene.getTopLayer().add(ts);
	
						if (DEBUG) {
							coords.move(4, 4);
							scene.getTopLayer().add(coords);
						}
	
						scene.addHUD(dpad);
						scene.addHUD(abbuttons);
						scene.addHUD(ssbuttons);
	
						engine.setRefreshMode(E3Engine.REFRESH_DEFAULT);
						loaded();
					}
				} 
				else {
					loadingText.setText("Failed to load!");
					loadingText.setAlpha(1);
					loadingText.clearModifier();
				}
	
			}
		}.execute();
	}
	
	/**
	 * Called by the Asynchronous tasks onPostExecute() method to let
	 * the engine know that the game is loaded entirely and to start
	 * the global SceneUpdateListener responsible for handling many
	 * important parts of the engine.
	 */
	private void loaded() {
		scene.registerUpdateListener(96, this);
	}

	/**
	 * Initializes the On Screen Controls of the Engine and registers
	 * them with the Scene's EventListener.
	 */
	private void loadHUD() {
		// D-Pad
		dpad = new DigitalController(controlBaseTexture, nullKnob, 8, HEIGHT - controlBaseTexture.getHeight() - 8, scene, this);
		dpad.setAlpha(alphaLevel);
		dpad.setUpdateInterval(animateSpeed);
		scene.addEventListener(dpad);

		// A and B Buttons
		abbuttons = new DigitalController(ab, nullKnob, getWidth() - ab.getWidth() - 8, HEIGHT - ab.getHeight() - 24, scene, this);
		abbuttons.setAlpha(alphaLevel);
		abbuttons.setUpdateInterval(1);
		scene.addEventListener(abbuttons);

		// Start and Select Buttons
		ssbuttons = new DigitalController(ss, nullKnob, (getWidth() / 2) - ss.getWidth() + 56, HEIGHT - ss.getHeight() + 36, scene, this);
		ssbuttons.setAlpha(alphaLevel);
		ssbuttons.setUpdateInterval(1);
		scene.addEventListener(ssbuttons);
	}
	
	/**
	 * Loads all of the audio components of the engine. This
	 * includes all BGM and SE sounds.
	 */
	private void loadAudio() {
		//Overworld BGM
		bgm = MediaPlayer.create(getApplicationContext(), R.raw.newbarktown);
		bgm.setLooping(true);
		if (soundEnabled) bgm.start();

		//Collision Sound
		collisionSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		collisionSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
						loadedSE = true;
					}
				});
		collisionSound = collisionSoundPool.load(this, R.raw.collision, 1);

		//Selection Sound
		selectionSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		selectionSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
						loadedSE = true;
					}
				});
		selectionSound = selectionSoundPool.load(this, R.raw.select, 1);

		//Menu Open Sound
		openmenuSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		openmenuSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
					@Override
					public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
						loadedSE = true;
					}
				});
		openmenuSound = openmenuSoundPool.load(this, R.raw.openmenu, 1);

		//Door Sound
		doorSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		doorSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loadedSE = true;
			}
		});
		doorSound = doorSoundPool.load(this, R.raw.door, 1);
		
		//Door Sound
		jumpSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		jumpSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loadedSE = true;
			}
		});
		jumpSound = jumpSoundPool.load(this, R.raw.jump, 1);
		
		daytimeSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		daytimeSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loadedSE = true;
			}
		});
		daytimeSound = daytimeSoundPool.load(this, R.raw.daytime, 1);
		
		nighttimeSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		nighttimeSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loadedSE = true;
			}
		});
		nighttimeSound = nighttimeSoundPool.load(this, R.raw.nighttime, 1);
		
		wordSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		wordSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loadedSE = true;
			}
		});
		wordSound = wordSoundPool.load(this, R.raw.word, 1);
		
		wordFinishedSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		wordFinishedSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loadedSE = true;
			}
		});
		wordFinishedSound = wordFinishedSoundPool.load(this, R.raw.word_done, 1);
		
		//Damage Sound
		damageSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		damageSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loadedSE = true;
							}
		});
		damageSound = damageSoundPool.load(this, R.raw.damage, 1);
		
		damageLowSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		damageLowSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loadedSE = true;
			}
		});
		damageLowSound = damageLowSoundPool.load(this, R.raw.damagelow, 1);
		
		damageHighSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		damageHighSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loadedSE = true;
							}
		});
		damageHighSound = damageHighSoundPool.load(this, R.raw.damagehigh, 1);
		
		//Save Sound
		saveSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		saveSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				loadedSE = true;
									}
		});
		saveSound = saveSoundPool.load(this, R.raw.save, 1);

		//Required for the SoundPools
		AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		volume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}
	
	/**
	 * Loads all of the individual Pokemon creature data from the pokemon.txt
	 * file found in the res/raw folder. It parses this file and stores its data
	 * into an array called monsters that may be referenced anywhere in the
	 * engine. It is compatible with Poccil and Flameguru's Pokemon Essentials 
	 * formatting, although a modified version of that file is required.
	 */
	public void loadPokemon() {
		for (int ind = 0; ind < monsters.length; ind++) {
			monsters[ind] = new Monsters();
		}
		Scanner input;
		InputStream in;
		in = getResources().openRawResource(R.raw.pokemon);
		input = new Scanner(in);
		while(input.hasNext()) {
			for (int index = 0; index < monsters.length; index++) {
				String n = input.nextLine(); //Number
				monsters[index].number = index;
				n = input.nextLine(); //Name
				monsters[index].name = n;
			    if (DEBUG) Log.v("IO", "Name: " + n);
			    n = input.nextLine(); //InternalName
			    n = input.nextLine(); //Kind
			    n = input.nextLine(); //Pokedex
			    n = input.nextLine(); //Type1
			    monsters[index].type1 = n;
			    n = input.nextLine(); //Type 2 or Base Stats
			    if (n.contains("NORMAL")||n.contains("FIRE")||n.contains("WATER")||n.contains("ELECTRIC")||
					n.contains("GRASS")||n.contains("ICE")||n.contains("FIGHTING")||n.contains("POISON")||
					n.contains("GROUND")||n.contains("FLYING")||n.contains("PSYCHIC")||n.contains("BUG")||
					n.contains("GHOST")||n.contains("ROCK")||n.contains("DRAGON")||n.contains("DARK")||
					n.contains("STEEL")) {
			    	monsters[index].type2 = n;
			    	n = input.nextLine(); //Base Stats
			    }
			    else monsters[index].type2 = "";
			    if (DEBUG) Log.v("IO", "Type 1: " + monsters[index].type1 + " Type 2: " + monsters[index].type2);
			    String[] stats = n.split("\\,");
			    monsters[index].base_hp = Integer.parseInt(stats[0]);
			    monsters[index].base_attack = Integer.parseInt(stats[1]);
			    monsters[index].base_def = Integer.parseInt(stats[2]);
			    monsters[index].base_spAttack = Integer.parseInt(stats[3]);
			    monsters[index].base_spDef = Integer.parseInt(stats[4]);;
			    monsters[index].base_spd = Integer.parseInt(stats[5]);
			    if (DEBUG) Log.v("IO", "Stats: " + stats[0] + " " + stats[1] + " " + stats[2] + " " + stats[3] + " " + stats[4] + " " + stats[5]);
				n = input.nextLine(); //Rareness
				n = input.nextLine(); //Base EXP
				monsters[index].base_exp = Integer.parseInt(n);
				monsters[index].catchRate = 60;
				n = input.nextLine(); //Happiness
				n = input.nextLine(); //GrowthRate
				n = input.nextLine(); //StepsToHatch
				n = input.nextLine(); //Color
				n = input.nextLine(); //Habitat
				n = input.nextLine(); //EffortPoints
				n = input.nextLine(); //Abilities
				n = input.nextLine(); //Compatibility
				n = input.nextLine(); //Height
				n = input.nextLine(); //Weight
				n = input.nextLine(); //GenderRate
				n = input.nextLine(); //Moves
				String[] moves = n.split("\\,");
				monsters[index].moveSet = moves;
				for (int ind = 0; ind < moves.length; ind++) {
					monsters[index].move1 = moves[1];
					if (moves.length > 2) monsters[index].move2 = moves[3];
					if (moves.length > 4) monsters[index].move3 = moves[5];
					//if (moves.length > 6) monsters[index].move4 = moves[7];
				}
				if (DEBUG) Log.v("IO", "Move 1: " + monsters[index].move1 + " Move 2: " + monsters[index].move2 +
						" Move 3: " + monsters[index].move3 + " Move 4: " + monsters[index].move4);
				n = input.nextLine(); //EggMoves
				n = input.nextLine(); //Evolutions
				String[] evos = n.split("\\,");
				monsters[index].evolutions = evos;
				Log.v("IO", "Evolution: " + evos[0]);
				n = input.nextLine(); //BattlerPlayerY
				n = input.nextLine(); //BattlerEnemyY
				n = input.nextLine(); //BattlerAltitude
				monsters[index].back_sprite = String.format("%03d", monsters[index].number) + "b.png";
				monsters[index].front_sprite = String.format("%03d", monsters[index].number) + ".png";
				monsters[index].back_sprite_s = String.format("%03d", monsters[index].number) + "sb.png";
				monsters[index].front_sprite_s = String.format("%03d", monsters[index].number) + "s.png";
				Log.v("IO", "Sprite Name: " + monsters[index].front_sprite);
				//Get Icons
				if (monsters[index].type1.equals("BUG") || monsters[index].type2.equals("BUG")) {
					monsters[index].party_icon = "icon6.png";
				}
				else if (monsters[index].type1.equals("FLYING") || monsters[index].type2.equals("FLYING")) {
					monsters[index].party_icon = "icon5.png";
				}
				else {
					monsters[index].party_icon = "icon4.png";
				}
				//SPECIAL CASES
				if (monsters[index].getName().equals("PIKACHU") || monsters[index].getName().equals("RAICHU") || 
						monsters[index].getName().equals("PICHU")) {
					monsters[index].party_icon = "icon3.png";
				}
				if (monsters[index].getName().equals("CYNDAQUIL") || monsters[index].getName().equals("QUILAVA") || 
						monsters[index].getName().equals("TYPHLOSION")) {
					monsters[index].party_icon = "icon2.png";
				}
				if (monsters[index].getName().equals("CHIKORITA") || monsters[index].getName().equals("BAYLEEF") || 
						monsters[index].getName().equals("MEGANIUM")) {
					monsters[index].party_icon = "icon1.png";
				}
				if (monsters[index].getName().equals("GEODUDE") || monsters[index].getName().equals("GRAVELER") || 
						monsters[index].getName().equals("GOLEM")) {
					monsters[index].party_icon = "icon10.png";
				}
				if (monsters[index].getName().equals("POLIWAG") || monsters[index].getName().equals("POLIWHIRL") || 
						monsters[index].getName().equals("POLIWRATH") || monsters[index].getName().equals("POLITOED")) {
					monsters[index].party_icon = "icon13.png";
				}
				if (monsters[index].getName().equals("JIGGLYPUFF") || monsters[index].getName().equals("WIGGLYTUFF") || 
						monsters[index].getName().equals("IGGLYBUFF")) {
					monsters[index].party_icon = "icon9.png";
				}
				if (monsters[index].getName().equals("ZUBAT") || monsters[index].getName().equals("GOLBAT") || 
						monsters[index].getName().equals("CROBAT")) {
					monsters[index].party_icon = "icon11.png";
				}
				if (monsters[index].getName().equals("BELSPROUT") || monsters[index].getName().equals("WEEPINBELL") || 
						monsters[index].getName().equals("VICTREEBELL")) {
					monsters[index].party_icon = "icon12.png";
				}
				if (monsters[index].getName().equals("ODDISH") || monsters[index].getName().equals("GLOOM") || 
						monsters[index].getName().equals("VILEPLUME")) {
					monsters[index].party_icon = "icon12.png";
				}
				if (DEBUG) Log.v("IO", monsters[index].name + " created at index " + index + " of monsters array.");
			}
			break;
		}
		input.close();
	}

	/**
	 * Loads all of the individual Attacks from the moves.txt file found
	 * in the res/raw folder. It parses this file and stores all data into
	 * an array called attacks that may be accessed anywhere in the engine. It
	 * is 100% compatible with Poccil and Flameguru's Pokemon Essentials move.txt
	 * format.
	 * <p>
	 * Not yet fully implemented.
	 */
	public void loadAttacks() {
		for (int ind = 0; ind < attacks.length; ind++) {
			attacks[ind] = new Attacks("");
		}
		
		Scanner input;
		InputStream in;
		in = getResources().openRawResource(R.raw.moves);
		input = new Scanner(in);
		
		while(input.hasNext()) {
			for (int index = 1; index < attacks.length; index++) {
				String n = input.nextLine(); //Number
			    String[] move = n.split("\\,");
			    String attackNumber = move[0];
			    String attackName = move[1];
			    String attackInternalName = move[2];
			    String attackFunctionCode = move[3];
			    int attackBaseDamage = Integer.parseInt(move[4]);
			    String attackType = move[5];
			    String attackCategory = move[6];
			    int attackAccuracy = Integer.parseInt(move[7]);
			    int attackPP = Integer.parseInt(move[8]);
			    int attackAdditionalEffectChance = Integer.parseInt(move[9]);
			    Log.v("IO", "ID: " + attackNumber + " Attack Name: " + attackName + " Function: " + attackFunctionCode);
			}
			break;
		}
		
		input.close();
	}
	
	/**
	 * Loads all of the Pokemon Trainer Types from the trainernames.txt file found
	 * in the res/raw folder. It parses this file and stores all data into a two
	 * dimensional array called trainerTypes that may be accessed anywhere in the 
	 * engine.
	 */
	public void loadTrainerTypes() {
		trainerTypes = new String[70][8];
		Scanner input;
		InputStream in;
		in = getResources().openRawResource(R.raw.trainernames);
		input = new Scanner(in);
		
		while(input.hasNext()) {
			String n = "";
			for (int i = 0; i < trainerTypes.length; i++) {
				n = input.nextLine();
				String[] curLine = n.split("\\,");
				trainerTypes[i][0] = curLine[0]; //ID Number
				trainerTypes[i][1] = curLine[1]; //Internal Name
				trainerTypes[i][2] = curLine[2]; //Name
				trainerTypes[i][3] = curLine[3]; //Money Multiplier (OPTIONAL)
				trainerTypes[i][4] = curLine[4]; //Battle BGM (OPTIONAL)
				trainerTypes[i][5] = curLine[5]; //Victory BGM (OPTIONAL)
				trainerTypes[i][6] = curLine[6]; //Music Effect (OPTIONAL)
				trainerTypes[i][7] = curLine[7]; //Trainer Gender
				if (DEBUG) Log.v("IO", trainerTypes[i][1] + " trainer type was loaded into Array Index " + i);
			}
		}
		
		input.close();
	}

	/**
	 * Loads all of the wild Pokemon encounters from the encounters.txt file found
	 * in the res/raw folder. It parses this file and stores all data into
	 * a two dimensional array called encounters that may be accessed anywhere in the 
	 * engine.
	 */
	public void loadEncounters() {
		Scanner input;
		InputStream in;
		in = getResources().openRawResource(R.raw.encounters);
		input = new Scanner(in);
		encounters = new String[5][3];
		
		while(input.hasNext()) {
			String n = input.nextLine();
			while(input.hasNext()) {
				if (currentMapName.equals(n)) {
					for (int j = 0; j < 5; j++) {
						n = input.nextLine();
						String[] s = n.split("\\,");
						encounters[j][0] = s[0]; //National Dex ID of Pokemon
						encounters[j][1] = s[1]; //Lower Level bound of Pokemon
						encounters[j][2] = s[2]; //Upper Level bound of Pokemon
						Log.v("IO", currentMapName + " Encounters: " + monsters[Integer.parseInt(encounters[j][0])].getName() + " Level: " + encounters[j][1] + "-" + encounters[j][2]);
					}
					break;
				}
				else {
					n = input.nextLine();
				}
			}
		}
		
		input.close();
	}
	
	/**
	 * Loads all of the map connections from the connections.txt file found
	 * in the res/raw folder. It parses this file and stores all data into
	 * a two dimensional array called connections that may be accessed anywhere in 
	 * the engine.
	 */
	public void loadMapConnections() {
		Scanner input;
		InputStream in;
		in = getResources().openRawResource(R.raw.connections);
		input = new Scanner(in);
		connections = new String[22][8];
		
		while (input.hasNext()) {
			for (int j = 0; j < connections.length; j++) {
			String m = input.nextLine(); //Comment Line
			
			String n = input.nextLine();
			String[] locSplit = n.split("\\,");
			connections[j][0] = locSplit[0]; //Old X
			connections[j][1] = locSplit[1]; //Old Y
			
			n = input.nextLine();
			String[] locSplitNew = n.split("\\,");
			connections[j][2] = locSplitNew[0]; //New X
			connections[j][3] = locSplitNew[1]; //New Y
			
			connections[j][4] = input.nextLine(); //Direction Before
			connections[j][5] = input.nextLine(); //Direction After
			connections[j][6] = input.nextLine(); //Inside Building Flag
			connections[j][7] = input.nextLine(); //Map BGM
			Log.v("IO", "Loaded Map Connection: " + m + " - " + connections[j][0] + "," + connections[j][1] + 
					" to " + connections[j][2] + "," + connections[j][3]);
			}
		}
		
		input.close();
	}

	/**
	 * Called whenever a key on a hardware keyboard is pressed down but
	 * not when it is released. This will be responsible for moving the
	 * player, interacting, etc. Will not do anything if the keyboardControlsEnabled
	 * flag is not set to true.
	 * <p>
	 * Currently not implemented.
	 *
	 * @param	scene	The Engines current Scene
	 * @param	keyCode	ID of the key that was pressed
	 * @param	event
	 * @return	true
	 */
	@Override
    public boolean onKeyDown(E3Scene scene, int keyCode, KeyEvent event) {
		//Hardware Keyboard Controls (WIP)
		if (keyboardControlsEnabled) {
			
		}
		return true;
    }
	
	/**
	 * Called whenever a key on a hardware keyboard is released up but
	 * not when it is pressed. This will be responsible for moving the
	 * player, interacting, etc. Will not do anything if the keyboardControlsEnabled
	 * flag is not set to true.
	 * <p>
	 * Currently not implemented.
	 *
	 * @param	scene	The Engines current Scene
	 * @param	keyCode	ID of the key that was pressed
	 * @param	event
	 * @return	true
	 */
	@Override
	public boolean onKeyUp(E3Scene scene, int keyCode, KeyEvent event) {
		//Hardware Keyboard Controls (WIP)
		if (keyboardControlsEnabled) {

		}
        return true;
    }
	
	/**
	 * Called in a specified amount of ms intervals by the SceneUpdateListener.
	 * Responsible for many parts of the engine such as text display and will
	 * eventually handle all movement.
	 *
	 * @param	sc	The Engines current Scene
	 * @param	ms	Amount of time in ms elapsed
	 */
	@Override
	public void onUpdateScene(E3Scene sc, long ms) {
		if (xstep != 0) {
			xstepold = xstep;
			ystepold = ystep;
		}
		if (ystep != 0) {
			ystepold = ystep;
			xstepold = xstep;
		}
		if (showMessage) {
			if (curMessageWord < totalMessageWords && totalMessageWords != 0) {
				ts.setText(ts.getText() + " " +  currentNPC.message[currentMessage][curMessageWord]);
				if (curMessageWord == totalMessageWords - 1) {
					if (soundEnabled && zeldaMessageStyle && loadedSE) {
						wordFinishedSoundPool.play(wordFinishedSound, volume, volume, 1, 0, 1f);
					}
				}
				else {
					if (soundEnabled && zeldaMessageStyle && loadedSE) {
						wordSoundPool.play(wordSound, volume, volume, 1, 0, 1f);
					}
				}
			}
			else {
				wordSoundPool.stop(wordSound);
				wordFinishedSoundPool.stop(wordFinishedSound);
			}
			ts.reload(true);
			message_Text.show();
			movable = false;
			ts.move(32,48);
			ts.show();
			curMessageWord++;
		}
		else {
			totalMessageWords = 0;
			curMessageWord = 0;
			ts.setText("");	
		}
		if (currentMessage >= numMessages) {
			message_Text.hide();
			movable = true;
			ts.hide();
		}
	}
	
	/**
	 * Called whenever a key on a hardware keyboard is released up but
	 * not when it is pressed. This will be responsible for moving the
	 * player, interacting, etc. Will not do anything if the keyboardControlsEnabled
	 * flag is not set to true.
	 * <p>
	 * Currently not implemented.
	 *
	 * @param	controller	ID of the controller that was pressed
	 * @param	relativeX	Not used
	 * @param	relativeY	Not used
	 * @param	hasChanged	Flag whether the input of the controls has changed
	 */
	@Override
	public void onControlUpdate(StickController controller, int relativeX, int relativeY, boolean hasChanged) {
		//Get the input from the D-Pad and assign the correct Sprite Animations
		direction = controller.getDirection();
		
		//Create a Vibrator to allow for haptic feedback on button presses
		Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		
		//Menu
		if (inMenu) {
			if (hasChanged && direction != 0) {
				if (hapticFeedback) vib.vibrate(50);
				if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
				if (menu.inMain == true) {
					if (controller.equals(dpad)) {
						if (direction == StickController.UP) {
							if (menu.currentSelectionMain > 0) {
								menu.currentSelectionMain--;
							}
						}
						else if (direction == StickController.DOWN) {
							if (menu.currentSelectionMain < 7) {
								menu.currentSelectionMain++;
							}
						}
					}
						
					if (controller.equals(ssbuttons)) {
						if (direction == StickController.RIGHT) {
							if (DEBUG) Log.v("MENU", "Exiting the Menu");
							inGameMenuEnd();
						}
					}
						
					if (controller.equals(abbuttons)) {
						if (direction == StickController.RIGHT) {
							//A
							if (menu.currentSelectionMain == 0) {
								menu.PokeDex();
							}
							if (menu.currentSelectionMain == 1) {
								menu.Pokemon();
								arrow.move(2, 4);
							}
							if (menu.currentSelectionMain == 2) {
								menu.Bag();
								arrow.move(96, 32);
							}
							if (menu.currentSelectionMain == 3) {
								menu.PokeGear();
							}
							if (menu.currentSelectionMain == 4) {
								menu.TrainerCard();
							}
							if (menu.currentSelectionMain == 5) {
								menu.Save();
							}
							if (menu.currentSelectionMain == 6) {
								menu.Option();
							}
							if (menu.currentSelectionMain == 7) {
								inGameMenuEnd();
							}
							ignoreInput = true;
						}
						if (direction == StickController.LEFT) {
							//B
							inGameMenuEnd();
						}
					}
				}
				
				if (menu.inPokeDex && !ignoreInput) {
					if (controller.equals(dpad)) {
						
					}
					
					if (controller.equals(abbuttons)) {
						if (direction == StickController.RIGHT) {
							//A
						}
						if (direction == StickController.LEFT) {
							//B
							menu.inOption = false;
							menu.inMain = true;
							inGameMenuEnd();
						}
					}
				}
				
				if (menu.inPokemonSubMenu == true) {
					if (controller.equals(dpad)) {
						if (hapticFeedback) vib.vibrate(50);
						if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
						
						if (direction == StickController.UP) {
							menu.currentSelectionPartyX = 0;
						}
						
						if (direction == StickController.DOWN) {
							menu.currentSelectionPartyX = 1;
						}
						
						if (menu.currentSelectionPartyX == 0) arrow.move(352, 132);
						if (menu.currentSelectionPartyX == 1) arrow.move(352, 164);
					}
					
					if (controller.equals(abbuttons)) {
						if (hapticFeedback) vib.vibrate(50);
						if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
						
						if (direction == StickController.RIGHT) {
							if (menu.currentSelectionPartyX == 0) {
								//SWITCH
								//switchPokemonMenu();
							}
							if (menu.currentSelectionPartyX == 1) {
								//CANCEL
								menu.currentSelectionPartyX = 0;
								menu.currentSelectionPartyY = 0;
								menu.currentSelectionItemX = 0;
								menu.currentSelectionItemY = 0;
								menu.inPokemonSubMenu = false;
								menu.inMain = false;
								arrow.move(274, 240);
								inGameMenuEnd();
							}
						}
						
						ignoreInput = true;
						
						if (direction == StickController.LEFT) {
							menu.currentSelectionPartyX = 0;
							menu.currentSelectionPartyY = 0;
							menu.currentSelectionItemX = 0;
							menu.currentSelectionItemY = 0;
							menu.inPokemonSubMenu = false;
							menu.inMain = false;
							arrow.move(274, 240);
							inGameMenuEnd();
						}
					}
				}
				
				if (menu.inPokemon == true && !ignoreInput) {			
					int numAlivePokemon = 0;
					for (int i = 0; i < pokemonparty.length; i++) {
						if (pokemonparty[i].cur_HP > 0 ) {
							numAlivePokemon++;
						}
					}
					
					if (DEBUG) Log.v("MENU", "Alive Pokemon: " + numAlivePokemon);
					
					if (controller.equals(dpad)) {
						if (hapticFeedback) vib.vibrate(50);
						if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
						
						if (direction == StickController.UP) {
							if (menu.currentSelectionPartyY > 0) menu.currentSelectionPartyY--;
							if (DEBUG) Log.v("MENU", "UP  - Party Index: " + menu.currentSelectionPartyY);
						}
						
						if (direction == StickController.DOWN) {
							if ((menu.currentSelectionPartyY < 5) && (!pokemonparty[menu.currentSelectionPartyY + 1].equals(monsters[0]))) {
								if (menu.currentSelectionPartyY != 5) menu.currentSelectionPartyY++;
							}
							if (DEBUG) Log.v("MENU", "DOWN - Party Index: " + menu.currentSelectionPartyY);
						}
						
						if (menu.currentSelectionPartyY == 0) arrow.move(2, 4);
						if (menu.currentSelectionPartyY == 1) arrow.move(2, 36);
						if (menu.currentSelectionPartyY == 2) arrow.move(2, 68);
						if (menu.currentSelectionPartyY == 3) arrow.move(2, 100);
						if (menu.currentSelectionPartyY == 4) arrow.move(2, 132);
						if (menu.currentSelectionPartyY == 5) arrow.move(2, 164);
					}
					
					if (controller.equals(abbuttons)) {
						if (direction == StickController.RIGHT) {
							//A
							menu.inPokemonSubMenu = true;
							menu.inPokemon = false;
							arrow.move(352, 132);
							inGameMenuUpdate();
							Log.v("BATTLE", pokemonparty[menu.currentSelectionPartyY].getName());
						}
						if (direction == StickController.LEFT) {
							//B
							menu.inPokemon = false;
							menu.inMain = true;
							inGameMenuEnd();
						}
					}
				}
				
				if (menu.inBagSelected && !ignoreInput) {
					if (controller.equals(abbuttons)) {
						if (hapticFeedback) vib.vibrate(50);
						if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
						
						if (direction == StickController.RIGHT) {
							//A
							if (menu.currentSelectionItemX == 0) {
								//USE
								useItemOverworld();
							}
							if (menu.currentSelectionItemX == 1) {
								//GIVE
							}
							if (menu.currentSelectionItemX == 2) {
								//TOSS
								items[menu.currentSelectionItemY].useItem();
							}
							if (menu.currentSelectionItemX == 3) {
								//EXIT
								menu.inMain = true;
								inGameMenuEnd();
							}
							menu.inMain = true;
							inGameMenuEnd();
						}
						
						ignoreInput = true;
						
						if (direction == StickController.LEFT) {
							//B
							menu.inMain = true;
							inGameMenuEnd();
						}
					}
					
					if (controller.equals(dpad)) {
						if (hapticFeedback) vib.vibrate(50);
						if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
						if (direction == StickController.UP){
							if (menu.currentSelectionItemX > 0) menu.currentSelectionItemX--;
						}
						
						if (direction == StickController.DOWN){
							if (menu.currentSelectionItemX < 3) menu.currentSelectionItemX++;
						}
						
						if (menu.currentSelectionItemX == 0) arrow.move(378, 97);
						if (menu.currentSelectionItemX == 1) arrow.move(378, 128);
						if (menu.currentSelectionItemX == 2) arrow.move(378, 160);
						if (menu.currentSelectionItemX == 3) arrow.move(378, 192);
					}
				}
				
				if (menu.inBag && !ignoreInput) {
					int numItems = 0;
					for (int i = 0; i < items.length; i++) {
						if (items[i].numberOfItem() != 0) numItems++;
					}
					
					if (controller.equals(abbuttons)) {
						if (hapticFeedback) vib.vibrate(50);
						if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
						
						if (direction == StickController.RIGHT) {
							//A
							menu.inBag = false;
							menu.inBagSelected = true;
							arrow.move(378, 97);
						}
						
						ignoreInput = true;
						
						if (direction == StickController.LEFT) {
							//B
							if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
							menu.inMain = true;
							inGameMenuEnd();
						}
					}
					
					if (controller.equals(dpad)) {
						if (hapticFeedback) vib.vibrate(50);
						if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
						if (direction == StickController.UP){
							if (menu.currentSelectionItemY > 0) menu.currentSelectionItemY--;
						}
						
						if (direction == StickController.DOWN){
							if (menu.currentSelectionItemY < numItems-1) menu.currentSelectionItemY++;
						}
						
						if (menu.currentSelectionItemY == 0) arrow.move(96, 32);
						if (menu.currentSelectionItemY == 1) arrow.move(96, 64);
					}
				}
				
				if (menu.inPokeGear && !ignoreInput) {
					if (controller.equals(dpad)) {
						if (direction == StickController.UP) {
							if (menu.currentSelectionPokeGear > 0) {
					    		menu.currentSelectionPokeGear--;
					    	}
						}
						else if (direction == StickController.DOWN) {
							if (menu.currentSelectionPokeGear < 3) {
					    		menu.currentSelectionPokeGear++;
					    	}
						}
					}
					
					if (controller.equals(abbuttons)) {
						if (direction == StickController.RIGHT) {
							//A
							if (menu.currentSelectionPokeGear == 0) {
					    		
					    	}
					    	else if (menu.currentSelectionPokeGear == 1) {
					    		
					    	}
					    	else if (menu.currentSelectionPokeGear == 2) {
					    		
					    	}
					    	else if (menu.currentSelectionPokeGear == 3) {
					    		
					    		menu.currentSelectionPokeGear = 0;
					    		menu.inPokeGear = false;
					    		menu.inMain = true;
					    		inGameMenuEnd();
					    	}
						}
						if (direction == StickController.LEFT) {
							//B
							menu.inPokeGear = false;
							menu.inMain = true;
							inGameMenuEnd();
						}
					}
				}
				
				if (menu.inTrainerCard && !ignoreInput) {
					//Player is in the Save Menu
					if (controller.equals(dpad)) {
						
					}
					
					if (controller.equals(abbuttons)) {
						if (direction == StickController.RIGHT) {
							//A
						}
						if (direction == StickController.LEFT) {
							
							//B
							menu.inTrainerCard = false;
							menu.inMain = true;
							inGameMenuEnd();
						}
						
					}
				}
					
				if (menu.inSave && !ignoreInput) {
					//Player is in the Save Menu
					if (controller.equals(dpad)) {
						if (direction == StickController.UP) {
							menu.currentSelectionSave = 0;
						}
						else if (direction == StickController.DOWN) {
							menu.currentSelectionSave = 1;
						}
					}
						
					if (controller.equals(abbuttons)) {
						if (direction == StickController.RIGHT) {
							//A
							if (menu.currentSelectionSave == 0) {
								//YES
					    		try {
									saveGame();
									menu.inSave = false;
						    		menu.inMain = true;
						    		inGameMenuEnd();
								}
					    		catch (FileNotFoundException e) {
									e.printStackTrace();
								}
					    		catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
					    		Log.v("MENU", playerName + "'s Game has been saved!");
					    	}
					    	else {
					    		//NO
					    		menu.inSave = false;
					    		menu.inMain = true;
					    		inGameMenuEnd();
					    	}
						}
						if (direction == StickController.LEFT) {
							//B
							menu.inSave = false;
							menu.inMain = true;
							inGameMenuEnd();
						}
					}
				}
				
				if (menu.inOption && !ignoreInput) {
					if (controller.equals(dpad)) {
						if (direction == StickController.UP) {
							if (menu.currentSelectionOption > 0) {
					    		menu.currentSelectionOption--;
					    	}
						}
						else if (direction == StickController.DOWN) {
							if (menu.currentSelectionOption < 5) {
					    		menu.currentSelectionOption++;
					    	}
						}
					}
					
					if (controller.equals(abbuttons)) {
						if (direction == StickController.RIGHT) {
							//A
						}
						if (direction == StickController.LEFT) {
							//B
							menu.inOption = false;
							menu.inMain = true;
							inGameMenuEnd();
						}
					}
				}
			}
			direction = 0;
			inGameMenuUpdate();
			
			if (direction == 0) ignoreInput = false;
		}
		
		//Battle
		if (inBattle) {
			if (hasChanged && direction != 0) {
				if (encounter.playerTurn == true) {
					if (encounter.inItem == true) {
						int numItems = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i].numberOfItem() != 0) numItems++;
						}
						
						if (controller.equals(abbuttons)) {
							if (hapticFeedback) vib.vibrate(50);
							if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
							
							if (direction == StickController.RIGHT) {
								useItemBattle();
							}
							
							ignoreInput = true;
							
							if (direction == StickController.LEFT) {
								//B
								if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
								encounter.currentSelectionMainX = 0;
								encounter.currentSelectionMainY = 0;
								encounter.currentSelectionFightX = 0;
								encounter.currentSelectionFightY = 0;
								encounter.inItem = false;
								encounter.inMain = true;
								arrow.move(274, 240);
								battleUpdate();
							}
						}
						
						if (controller.equals(dpad)) {
							if (hapticFeedback) vib.vibrate(50);
							if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
							if (direction == StickController.UP){
								if (encounter.currentSelectionItemY > 0) encounter.currentSelectionItemY--;
							}
							
							if (direction == StickController.DOWN){
								if (encounter.currentSelectionItemY < numItems-1) encounter.currentSelectionItemY++;
							}
							
							if (encounter.currentSelectionItemY == 0) arrow.move(242, 96);
							if (encounter.currentSelectionItemY == 1) arrow.move(242, 128);
						}
					}
					
					if (encounter.inPokemonSubMenu == true) {
						if (controller.equals(dpad)) {
							if (hapticFeedback) vib.vibrate(50);
							if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
							
							if (direction == StickController.UP) {
								encounter.currentSelectionPartyX = 0;
							}
							
							if (direction == StickController.DOWN) {
								encounter.currentSelectionPartyX = 1;
							}
							
							if (encounter.currentSelectionPartyX == 0) arrow.move(352, 132);
							if (encounter.currentSelectionPartyX == 1) arrow.move(352, 164);
						}
						
						if (controller.equals(abbuttons)) {
							if (hapticFeedback) vib.vibrate(50);
							if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
							
							if (direction == StickController.RIGHT) {
								if (encounter.currentSelectionPartyX == 0) {
									//SWITCH
									switchPokemonEncounter();
								}
								if (encounter.currentSelectionPartyX == 1) {
									//CANCEL
									encounter.currentSelectionMainX = 0;
									encounter.currentSelectionMainY = 0;
									encounter.currentSelectionFightX = 0;
									encounter.currentSelectionFightY = 0;
									encounter.currentSelectionPartyX = 0;
									encounter.currentSelectionPartyY = 0;
									encounter.currentSelectionItemX = 0;
									encounter.currentSelectionItemY = 0;
									encounter.inPokemonSubMenu = false;
									encounter.inMain = true;
									arrow.move(274, 240);
									battleUpdate();
								}
							}
							
							ignoreInput = true;
							
							if (direction == StickController.LEFT) {
								encounter.currentSelectionMainX = 0;
								encounter.currentSelectionMainY = 0;
								encounter.currentSelectionFightX = 0;
								encounter.currentSelectionFightY = 0;
								encounter.currentSelectionPartyX = 0;
								encounter.currentSelectionPartyY = 0;
								encounter.currentSelectionItemX = 0;
								encounter.currentSelectionItemY = 0;
								encounter.inPokemonSubMenu = false;
								encounter.inMain = true;
								arrow.move(274, 240);
								battleUpdate();
							}
						}
					}
					
					if (encounter.inPokemon == true) {			
						int numAlivePokemon = 0;
						for (int i = 0; i < pokemonparty.length; i++) {
							if (pokemonparty[i].cur_HP > 0 ) {
								numAlivePokemon++;
							}
						}
						
						if (numAlivePokemon <= 0) {
							teleportToLastPC();
							battleEnd();
						}
						
						if (DEBUG) Log.v("BATTLE", "Alive Pokemon: " + numAlivePokemon);
						
						if (controller.equals(dpad)) {
							if (hapticFeedback) vib.vibrate(50);
							if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
							
							if (direction == StickController.UP) {
								if (encounter.currentSelectionPartyY > 0) encounter.currentSelectionPartyY--;
								if (DEBUG) Log.v("BATTLE", "UP  - Party Index: " + encounter.currentSelectionPartyY);
							}
							
							if (direction == StickController.DOWN) {
								if ((encounter.currentSelectionPartyY < 5) && (!pokemonparty[encounter.currentSelectionPartyY + 1].equals(monsters[0]))) {
									if (encounter.currentSelectionPartyY != 5) encounter.currentSelectionPartyY++;
								}
								if (DEBUG) Log.v("BATTLE", "DOWN - Party Index: " + encounter.currentSelectionPartyY);
							}
							
							if (encounter.currentSelectionPartyY == 0) arrow.move(2, 4);
							if (encounter.currentSelectionPartyY == 1) arrow.move(2, 36);
							if (encounter.currentSelectionPartyY == 2) arrow.move(2, 68);
							if (encounter.currentSelectionPartyY == 3) arrow.move(2, 100);
							if (encounter.currentSelectionPartyY == 4) arrow.move(2, 132);
							if (encounter.currentSelectionPartyY == 5) arrow.move(2, 164);
						}
						
						if (controller.equals(abbuttons)) {
							if (hapticFeedback) vib.vibrate(50);
							if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
							
							if (direction == StickController.RIGHT) {
								encounter.inPokemonSubMenu = true;
								encounter.inPokemon = false;
								arrow.move(352, 132);
								battleUpdate();
								Log.v("BATTLE", pokemonparty[encounter.currentSelectionPartyY].getName());
							}
							
							ignoreInput = true;
							
							if (direction == StickController.LEFT) {
								encounter.currentSelectionMainX = 0;
								encounter.currentSelectionMainY = 0;
								encounter.currentSelectionFightX = 0;
								encounter.currentSelectionFightY = 0;
								encounter.currentSelectionPartyX = 0;
								encounter.currentSelectionPartyY = 0;
								encounter.currentSelectionItemX = 0;
								encounter.currentSelectionItemY = 0;
								encounter.inPokemon = false;
								encounter.inMain = true;
								arrow.move(274, 240);
								battleUpdate();
							}
						}
					}
					
					if (encounter.inFight == true) {
						if (controller.equals(abbuttons)) {
							if (hapticFeedback) vib.vibrate(50);
							if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
							
							if (direction == StickController.RIGHT) {
								battleAttack();
							}
							
							ignoreInput = true;
							
							if (direction == StickController.LEFT) {
								encounter.currentSelectionMainX = 0;
								encounter.currentSelectionMainY = 0;
								encounter.currentSelectionFightX = 0;
								encounter.currentSelectionFightY = 0;
								encounter.currentSelectionItemX = 0;
								encounter.currentSelectionItemY = 0;
								encounter.currentSelectionPartyX = 0;
								encounter.currentSelectionPartyY = 0;
								encounter.inFight = false;
								encounter.inMain = true;
								arrow.move(274, 240);
								battleUpdate();
							}
						}
						
						if (controller.equals(dpad)) {
							if (hapticFeedback) vib.vibrate(50);
							if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
							if (direction == StickController.UP) {
								encounter.currentSelectionFightY = 0;
							} 
							else if (direction == StickController.DOWN) {
								if (!encounter.playerPokemon.move3.equals("")) {
									encounter.currentSelectionFightY = 1;
								}
							} 
							else if (direction == StickController.LEFT) {
								encounter.currentSelectionFightX = 0;
							} 
							else if (direction == StickController.RIGHT) {
								encounter.currentSelectionFightX = 1;
							}
							
							if (encounter.currentSelectionFightX == 0 && encounter.currentSelectionFightY == 0) {
								arrow.move(184, 240);
							}
							else if (encounter.currentSelectionFightX == 0 && encounter.currentSelectionFightY == 1) {
								arrow.move(184, 270);
							} 
							else if (encounter.currentSelectionFightX == 1 && encounter.currentSelectionFightY == 0) {
								arrow.move(329, 240);
							} 
							else if (encounter.currentSelectionFightX == 1 && encounter.currentSelectionFightY == 1) {
								arrow.move(329, 270);
							}
						}
					}
					
					if (encounter.inMain == true) {
						if (controller.equals(dpad)) {
							if (hapticFeedback) vib.vibrate(50);
							if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
							if (direction == StickController.UP) {
								encounter.currentSelectionMainY = 0;
							} 
							else if (direction == StickController.DOWN) {
								encounter.currentSelectionMainY = 1;
							} 
							else if (direction == StickController.LEFT) {
								encounter.currentSelectionMainX = 0;
							} 
							else if (direction == StickController.RIGHT) {
								encounter.currentSelectionMainX = 1;
							}
	
							if (encounter.currentSelectionMainX == 0 && encounter.currentSelectionMainY == 0) {
								arrow.move(274, 240);
							} 
							else if (encounter.currentSelectionMainX == 0 && encounter.currentSelectionMainY == 1) {
								arrow.move(274, 270);
							} 
							else if (encounter.currentSelectionMainX == 1 && encounter.currentSelectionMainY == 0) {
								arrow.move(384, 240);
							} 
							else if (encounter.currentSelectionMainX == 1 && encounter.currentSelectionMainY == 1) {
								arrow.move(384, 270);
							}
						}
						
						if (controller.equals(abbuttons) && !ignoreInput) {
							if (hapticFeedback) vib.vibrate(50);
							if (direction == StickController.RIGHT) {
								if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
								if (encounter.currentSelectionMainX == 0 && encounter.currentSelectionMainY == 0) {
									encounter.Fight();
									arrow.move(184, 240);
								}
								if (encounter.currentSelectionMainX == 1 && encounter.currentSelectionMainY == 0) {
									encounter.Pokemon();
									arrow.move(2, 4);
								}
								if (encounter.currentSelectionMainX == 0 && encounter.currentSelectionMainY == 1) {
									encounter.Item();
									arrow.move(242, 96);
								}
								if (encounter.currentSelectionMainX == 1 && encounter.currentSelectionMainY == 1) {
									if (hapticFeedback) vib.vibrate(50);
									if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
									int chance = randGen.nextInt(255);
							    	if (encounter.playerPokemon.cur_spd > encounter.enemyPokemon.cur_spd || chance >= 120) {
							    		encounter.confirmBattleEnd = true;
							    		battleEnd();
								    	Log.v("BATTLE", "Got away safely!" + " " + encounter.playerPokemon.cur_spd + " >= " + encounter.enemyPokemon.cur_spd);
							    	}
							    	else {
							    		encounter.playerTurn = false;
										encounter.inMain = true;
										encounter.inFight = false;
										encounter.currentSelectionMainX = 0;
										encounter.currentSelectionMainY = 0;
										encounter.currentSelectionFightX = 0;
										encounter.currentSelectionFightY = 0;
										encounter.currentSelectionItemX = 0;
										encounter.currentSelectionItemY = 0;
										encounter.currentSelectionPartyX = 0;
										encounter.currentSelectionPartyY = 0;
							    		Log.v("BATTLE", "Can't Escape!" + " Chance: " + chance + " >= 120");
							    		encounter.enemyTurn();
							    		battleUpdate();
							    	}
								}
							}
							if (direction == StickController.LEFT){
								if (DEBUG) {
									int tempHP = encounter.playerPokemon.cur_HP;
									encounter.playerPokemon.levelUp();
									encounter.playerPokemon.cur_HP = tempHP;
									checkEvolution();
									battleUpdate();
								}
							}
						}
						
						ignoreInput = false;
					}
				}
			}
			battleUpdate(); //Update the Battle Scene (Turn is over)
		}
		
		//Overworld
		if (!inBattle && !inMenu) {
			updateSky();
			
			if (controller.equals(abbuttons)) {
				if (hasChanged) {
					if (direction == StickController.RIGHT) {
						//A Button
						if (hapticFeedback) vib.vibrate(50);
						if (DEBUG) Log.v("INPUT", "A");
						Log.v("TIMER", "NUMBER OF WORDS: " + totalMessageWords);
						if (currentMessage >= numMessages) {
							currentMessage = 0;
						}
						totalMessageWords = currentNPC.message[currentMessage].length;
						numMessages = currentNPC.message.length;
						
						if (curMessageWord >= totalMessageWords && currentMessage < numMessages) {
							currentMessage++;
						}
						checkNPCEvents();
					}
					else if (direction == StickController.LEFT) {
						//B Button
						if (hapticFeedback) vib.vibrate(50);
						if (DEBUG) Log.v("INPUT", "B");
					}
				}
			}
			
			if (controller.equals(ssbuttons)) {
				if (hasChanged) {
					if (direction == StickController.RIGHT) {
						//Start Button
						if (hapticFeedback) vib.vibrate(50);
						if (DEBUG) Log.v("INPUT", "START");
						if (!inMenu) inGameMenu();
					}
					else if (direction == StickController.LEFT) {
						//Select Button
						if (hapticFeedback) vib.vibrate(50);
						if (DEBUG) {
							Log.v("INPUT", "SELECT");
							if (enableDayNightSystem) enableDayNightSystem = false;
							else enableDayNightSystem = true;
							Log.v("TMX", "DAY/NIGHT TOGGLED");
							updateSky();
							if (dayTime) {
								if (soundEnabled && loadedSE) daytimeSoundPool.play(daytimeSound, volume, volume, 1, 0, 1f);
							}
							else {
								if (soundEnabled && loadedSE) nighttimeSoundPool.play(nighttimeSound, volume, volume, 1, 0, 1f);
							}
						}
					}
				}
			}
			
			if (movable && controller.equals(dpad)) {
				if (hasChanged && direction != 0) {
					if (direction == StickController.LEFT) {
						player.animate(animateSpeed, playerLeft);
						previousDirection = 1;
						if (hapticFeedback) vib.vibrate(50);
					}
					else if (direction == StickController.RIGHT) {
						player.animate(animateSpeed, playerRight);
						previousDirection = 2;
						if (hapticFeedback) vib.vibrate(50);
					}
					else if (direction == StickController.UP) {
						player.animate(animateSpeed, playerUp);
						previousDirection = 3;
						if (hapticFeedback) vib.vibrate(50);
					}
					else if (direction == StickController.DOWN) {
						player.animate(animateSpeed, playerDown);
						previousDirection = 4;
						if (hapticFeedback) vib.vibrate(50);
					}
					checkStatusConditions(); //When the player moves status conditions should be handled
				}
				movePlayer();
			}
		}
	}
	
	/**
	 * Called by the onControlUpdate method and is responsible for moving
	 * the player sprite, translating the map in the correct direction, and
	 * much more. Checks for Map Connections and will update the Players
	 * current Location, if changed.
	 */
	private void movePlayer() {
		//Move the Player and Scroll the Map
		xstep = 0;
		ystep = 0;
		int x = 0;
		int y = 0;
		
		if (direction == StickController.LEFT) xstep = -32;
		else if (direction == StickController.RIGHT) xstep = 32;
		else if (direction == StickController.UP) ystep = -32;
		else if (direction == StickController.DOWN) ystep = 32;
		if (xstep != 0) {
			xstepold = xstep;
			ystepold = ystep;
		}
		if (ystep != 0) {
			ystepold = ystep;
			xstepold = xstep;
		}
		
		int r = randGen.nextInt(16) + rndWildModifier;
		
		if (!checkTileCollisions(player, xstep, ystep) && !checkNPCCollisions(player, xstep, ystep) && isInTheScene(player, xstep, ystep)) {
			for (TMXLayer layer : mapLayers) {
				TMXTile underTile = layer.getTileAt(((layer.getX() / 32) + 7), ((layer.getY() / 32) + 5));
				TMXTile belowTile = layer.getTileAt(((layer.getX() / 32) + 7), ((layer.getY() / 32) + 6));
				TMXTile aboveTile = layer.getTileAt(((layer.getX() / 32) + 7), ((layer.getY() / 32) + 4));
				TMXTile leftTile = layer.getTileAt(((layer.getX() / 32) + 6), ((layer.getY() / 32) + 5));
				TMXTile rightTile = layer.getTileAt(((layer.getX() / 32) + 8), ((layer.getY() / 32) + 5));
				//if (DEBUG) Log.v("TMX", "GID: " + underTile.getGID()); //Get the Tile # under the player
				
				//Tall Grass
				if ("Ground".equals(layer.getName()) && underTile.getGID() == 728) {
					grassanimation.show();
					if (!noBattle) stepscount++; // Increment the Steps Counter for Wild Pokemon Battle
					if (stepscount >= r && !noBattle) {
						//Start a Pokemon Battle
						battle();
						stepscount = 0;
					}
					if (DEBUG) Log.v("TMX", "TALL GRASS");
				}
				else if ("Ground".equals(layer.getName()) && underTile.getGID() != 728) {
					grassanimation.hide();
				}
				
				//Ledge Jumps
				if ("Ground".equals(layer.getName()) && belowTile.getGID() == 931 && previousDirection == StickController.DOWN) {
					if (DEBUG) Log.v("TMX", "Ledge Jump - Down");
					ystep = 64;
					if (soundEnabled && loadedSE) jumpSoundPool.play(jumpSound, volume, volume, 1, 0, 1f);
				}
				if ("Ground".equals(layer.getName()) && rightTile.getGID() == 726 && previousDirection == StickController.RIGHT) {
					if (DEBUG) Log.v("TMX", "Ledge Jump - Right");
					xstep = 64;
					if (soundEnabled && loadedSE) jumpSoundPool.play(jumpSound, volume, volume, 1, 0, 1f);
				}
				if ("Ground".equals(layer.getName()) && leftTile.getGID() == 929 && previousDirection == StickController.LEFT) {
					if (DEBUG) Log.v("TMX", "Ledge Jump - Left");
					xstep = -64;
					if (soundEnabled && loadedSE) jumpSoundPool.play(jumpSound, volume, volume, 1, 0, 1f);
				}
				
				//Scroll the Map
				layer.setPosition(layer.getX() + xstep, layer.getY() + ystep);
				x = layer.getX();
				y = layer.getY();
				tileX = x;
				tileY = y;
			}
			checkMapConnections(x, y);
			updateLocationInfo(x, y);
		}
		
		//Player isn't moving, set the Players Sprite to a stationary frame and stop animation
		if (direction == 0) {
			if (previousDirection == 1) player.setTile(0, 1); //Left Stationary Frame
			if (previousDirection == 2) player.setTile(0, 2); //Right Stationary Frame
			if (previousDirection == 3) player.setTile(0, 3); //Up Stationary Frame
			if (previousDirection == 4) player.setTile(0, 0); //Down Stationary Frame
			player.stop(); //Freeze Player Animation Frame (Not Walking)
		}
	}
	
	/**
	 * Checks the Player's current location against a bunch of
	 * pre-defined locations to see if they changed zones on the map
	 * and to update variables as necessary depending on the zone.
	 */
	private void updateLocationInfo(int x, int y) {
		//Updates the Players current zone location on the Map
		x = (x / 32) + 7;
		y = (y / 32) + 5;
		
		if (x == 155 && (y == 110 || y == 111)) {
			previousMapName = "NEWBARKTOWN";
			currentMapName = "NEWBARKTOWN";
			updateBGM();
		}
		if (x == 154 && (y == 110 || y == 111)) {
			previousMapName = "NEWBARKTOWN";
			currentMapName = "ROUTE29";
			loadEncounters();
			updateBGM();
		}
		
		if (x == 94 && (y == 108 || y == 109)) {
			previousMapName = "CHERRYGROVECITY";
			currentMapName = "CHERRYGROVECITY";
			updateBGM();
		}
		if (x == 95 && (y == 108 || y == 109)) {
			previousMapName = "CHERRYGROVECITY";
			currentMapName = "ROUTE29";
			loadEncounters();
			updateBGM();
		}
		
		if ((x == 73 || x == 74) && y == 100) {
			previousMapName = "CHERRYGROVECITY";
			currentMapName = "ROUTE30";
			loadEncounters();
			updateBGM();
		}
		if ((x == 73 || x == 74) && y == 101) {
			previousMapName = "CHERRYGROVECITY";
			currentMapName = "CHERRYGROVECITY";
			updateBGM();
		}
		
		if ((x == 73 || x == 74) && y == 51) {
			previousMapName = "CHERRYGROVECITY";
			currentMapName = "ROUTE31"; //No BGM Change
			loadEncounters();
		}
		if ((x == 71 || x == 72 || x == 73 || x == 74) && y == 52) {
			previousMapName = "CHERRYGROVECITY";
			currentMapName = "ROUTE30"; //No BGM Change
			loadEncounters();
		}
		
		if (x == 50 && (y == 36|| y == 37)) {
			previousMapName = "VIOLETCITY";
			currentMapName = "VIOLETCITY";
			updateBGM();
		}
		if (x == 51 && (y == 36 || y == 37)) {
			previousMapName = "VIOLETCITY";
			currentMapName = "ROUTE31";
			loadEncounters();
			updateBGM();
		}
		
		if ((x == 21 || x == 22) && y == 45) {
			previousMapName = "VIOLETCITY";
			currentMapName = "ROUTE32";
			loadEncounters();
			updateBGM();
		}
		if ((x == 21 || x == 22) && y == 44) {
			previousMapName = "VIOLETCITY";
			currentMapName = "VIOLETCITY";
			updateBGM();
		}
		
		coords.setText(x + ", " + y + " " + currentMapName);
		coords.reload(true);
	}
	
	/**
	 * Checks to see if the player is in the map and prevents the Player
	 * from going out of the bounds of the map.
	 *
	 * @param	sprite	The Sprite that should be checked
	 * @param	xstep	Left or Right
	 * @param	ystep	Up or Down
	 * @return	Whether or not Sprite is in the scene.
	 */
	private boolean isInTheScene(Sprite sprite, int xstep, int ystep) {
		//Check to see if the Player is on the edge of the map and prevent movement
		int x = sprite.getRealX() + xstep;
		int y = sprite.getRealY() + ystep;
		return x > 0 && y > 0 && x < getWidth() - sprite.getWidth() && y < getHeight() - sprite.getHeight();
	}
	
	/**
	 * Checks the Player's current position against a two dimensional
	 * array of map connections and will teleport the Player to the
	 * correct locations. Updates Location Data, BGM, Encounters, etc
	 * as necessary.
	 * <p>
	 * Currently using hardcoded connections
	 *
	 * @param	x	The Engines current Scene
	 * @param	y	ID of the key that was pressed
	 */
	private void checkMapConnections(int x, int y) {
		//Handles teleporting the player when they step onto teleport tiles (Doors, Stairs, etc)
		x = (x / 32) + 7;
		y = (y / 32) + 5;
		
		int newx = 0;
		int newy = 0;
		
		if (movable) {
			for (TMXLayer layer : mapLayers) {
				/*for (int i = 0; i < connections.length; i++) {
					if (x == Integer.parseInt(connections[i][0]) && y == Integer.parseInt(connections[i][1])) {
						newx = Integer.parseInt(connections[i][2]);
						newy = Integer.parseInt(connections[i][3]);
						layer.setPosition((newx-7)*32, (newy-5)*32);
						updateSky();
						updateBGM();
						loadEncounters();
						if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					}
				}*/
				//Players Bedroom to Downstairs
				if (x == 47 && y == 137 && direction == StickController.UP) {
					newx = 66;
					newy = 136;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Players Downstairs to Bedroom
				if (x == 66 && y == 135 && direction == StickController.UP) {
					newx = 47;
					newy = 138;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Players Downstairs to NEWBARKTOWN
				if ((x == 64 || x == 65) && y == 142 && direction == StickController.DOWN) {
					newx = 170;
					newy = 108;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//NEWBARKTOWN to Players House
				if (x == 170 && y == 107 && direction == StickController.UP) {
					newx = 65;
					newy = 141;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//NEWBARKTOWN to Elms House
				if (x == 168 && y == 115 && direction == StickController.UP) {
					newx = 78;
					newy = 141;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Elms House to NEWBARKTOWN
				if (x == 78 && y == 142 && direction == StickController.DOWN) {
					newx = 168;
					newy = 116;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Elms House to NEWBARKTOWN
				if (x == 79 && y == 142 && direction == StickController.DOWN) {
					newx = 168;
					newy = 116;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//NEWBARKTOWN to Neighbor House
				if (x == 160 && y == 113 && direction == StickController.UP) {
					newx = 95;
					newy = 142;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Neighbor House to NEWBARKTOWN
				if ((x == 95 || x == 96) && y == 143 && direction == StickController.DOWN) {
					newx = 160;
					newy = 114;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//NEWBARKTOWN to Elms Lab
				if (x == 163 && y == 105 && direction == StickController.UP) {
					newx = 113;
					newy = 143;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.elmslab);
						bgm.setLooping(true);
						bgm.start();	
					}
				}
				//Elms Lab  to NEWBARKTOWN
				if ((x == 113 || x == 114) && y == 144 && direction == StickController.DOWN) {
					newx = 163;
					newy = 106;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					updateBGM();
				}
				//ROUTE29 to Route House (29-42)
				if (x == 124 && y == 103 && direction == StickController.UP) {
					newx = 138;
					newy = 11;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//ROUTE46 to Route House (29-42)
				if ((x == 124 || x == 125)&& y == 99 && direction == StickController.DOWN) {
					newx = 138;
					newy = 8;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Route House (29-42) to ROUTE46
				if ((x == 138 || x == 139) && y == 7 && direction == StickController.UP) {
					newx = 124;
					newy = 98;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					currentMapName = "ROUTE46";
					loadEncounters();
					updateBGM();
				}
				//Route House (29-42) to ROUTE29
				if ((x == 138 || x == 139) && y == 12 && direction == StickController.DOWN) {
					newx = 124;
					newy = 104;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					currentMapName = "ROUTE29";
					loadEncounters();
					updateBGM();
				}
				//CHERRYGROVECITY to Pokecenter
				if (x == 86 && y == 105 && direction == StickController.UP) {
					newx = 131;
					newy = 140;
					lastPCX = newx;
					lastPCY = newy-3;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.pokecenter);
						bgm.setLooping(true);
						bgm.start();	
					}
				}
				//Pokecenter to CHERRYGROVECITY
				if ((x == 131 || x == 132) && y == 141 && direction == StickController.DOWN) {
					newx = 86;
					newy = 106;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					updateBGM();
				}
				//CHERRYGROVECITY to Pokemart
				if (x == 80 && y == 105 && direction == StickController.UP) {
					newx = 136;
					newy = 25;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.pokemart);
						bgm.setLooping(true);
						bgm.start();	
					}
				}
				//Pokemart to CHERRYGROVECITY
				if ((x == 136 || x == 137) && y == 26 && direction == StickController.DOWN) {
					newx = 80;
					newy = 106;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					updateBGM();
				}
				//CHERRYGROVECITY to House1
				if (x == 74 && y == 109 && direction == StickController.UP) {
					newx = 105;
					newy = 13;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//CHERRYGROVECITY to House2
				if (x == 82 && y == 111 && direction == StickController.UP) {
					newx = 120;
					newy = 13;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//CHERRYGROVECITY to House3
				if (x == 88 && y == 113 && direction == StickController.UP) {
					newx = 105;
					newy = 25;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//House1 to CHERRYGROVECITY
				if ((x == 105 || x == 106) && y == 14 && direction == StickController.DOWN) {
					newx = 74;
					newy = 110;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//House2 to CHERRYGROVECITY
				if ((x == 120 || x == 121) && y == 14 && direction == StickController.DOWN) {
					newx = 82;
					newy = 112;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//House3 to CHERRYGROVECITY
				if ((x == 105 || x == 106) && y == 26 && direction == StickController.DOWN) {
					newx = 88;
					newy = 114;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//ROUTE30 to BERRYHOUSE
				if (x == 74 && y == 87 && direction == StickController.UP) {
					newx = 120;
					newy = 25;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//BERRYHOUSE to ROUTE30
				if ((x == 120 || x == 121) && y == 26 && direction == StickController.DOWN) {
					newx = 74;
					newy = 88;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//ROUTE30 to MRPOKEMONHOUSE
				if (x == 84 && y == 53 && direction == StickController.UP) {
					newx = 105;
					newy = 38;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//MRPOKEMONHOUSE to ROUTE30
				if ((x == 105 || x == 106) && y == 38 && direction == StickController.DOWN) {
					newx = 84;
					newy = 54;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//VIOLETCITY to Pokecenter
				if (x == 38 && y == 37 && direction == StickController.UP) {
					newx = 120;
					newy = 38;
					lastPCX = newx;
					lastPCY = newy-3;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.pokecenter);
						bgm.setLooping(true);
						bgm.start();	
					}
				}
				//Pokecenter to VIOLETCITY
				if ((x == 120 || x == 121) && y == 39 && direction == StickController.DOWN) {
					newx = 38;
					newy = 38;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.violetcity);
						bgm.setLooping(true);
						bgm.start();
					}
				}
				//VIOLETCITY to Pokemart
				if (x == 16 && y == 29 && direction == StickController.UP) {
					newx = 135;
					newy = 38;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.pokemart);
						bgm.setLooping(true);
						bgm.start();	
					}
				}
				//Pokemart to VIOLETCITY
				if ((x == 135 || x == 136) && y == 39 && direction == StickController.DOWN) {
					newx = 16;
					newy = 30;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.violetcity);
						bgm.setLooping(true);
						bgm.start();	
					}
				}
				//VIOLETCITY to House 1 (Rocky)
				if (x == 28 && y == 41 && direction == StickController.UP) {
					newx = 105;
					newy = 53;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//House 1 (Rocky) to VIOLETCITY
				if ((x == 105 || x == 106) && y == 54 && direction == StickController.DOWN) {
					newx = 28;
					newy = 42;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//VIOLETCITY to School
				if (x == 37 && y == 29 && direction == StickController.UP) {
					newx = 173;
					newy = 25;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//School to VIOLETCITY
				if ((x == 173 || x == 174) && y == 26 && direction == StickController.DOWN) {
					newx = 37;
					newy = 30;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//VIOLETCITY to Sprout Tower Floor 1
				if (x == 30 && y == 17 && direction == StickController.UP) {
					newx = 159;
					newy = 50;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.sprouttower);
						bgm.setLooping(true);
						bgm.start();	
					}
				}
				//Sprout Tower Floor 1 to VIOLETCITY
				if ((x == 159 || x == 160) && y == 51 && direction == StickController.DOWN) {
					newx = 30;
					newy = 18;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.violetcity);
						bgm.setLooping(true);
						bgm.start();	
					}
				}
				//VIOLETCITY to GYM
				if (x == 25 && y == 29 && direction == StickController.UP) {
					newx = 157;
					newy = 25;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.gym);
						bgm.setLooping(true);
						bgm.start();	
					}
				}
				//GYM to VIOLETCITY
				if ((x == 157 || x == 158) && y == 26 && direction == StickController.DOWN) {
					newx = 25;
					newy = 30;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
					if (soundEnabled) {
						bgm.stop();
						bgm.release();
						bgm = MediaPlayer.create(getApplicationContext(), R.raw.violetcity);
						bgm.setLooping(true);
						bgm.start();	
					}
				}
				//VIOLETCITY to other House 2
				if (x == 10 && y == 27 && direction == StickController.UP) {
					newx = 105;
					newy = 66;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//House 2 to VIOLETCITY
				if ((x == 105 || x == 106) && y == 67 && direction == StickController.DOWN) {
					newx = 10;
					newy = 28;
					insideBuilding = false;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Sprout Tower Floor 1 to Sprout Tower Floor 2
				if (x == 156 && y == 40) {
					newx = 121;
					newy = 51;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Sprout Tower Floor 2 to Sprout Tower Floor 1
				if (x == 122 && y == 51) {
					newx = 156;
					newy = 41;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Sprout Tower Floor 1 to Sprout Tower Floor 2
				if (x == 152 && y == 42) {
					newx = 119;
					newy = 53;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Sprout Tower Floor 2 to Sprout Tower Floor 1
				if (x == 118 && y == 53) {
					newx = 152;
					newy = 41;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Sprout Tower Floor 1 to Sprout Tower Floor 2
				if (x == 167 && y == 39) {
					newx = 133;
					newy = 52;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Sprout Tower Floor 2 to Sprout Tower Floor 1
				if (x == 133 && y == 51) {
					newx = 167;
					newy = 40;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Sprout Tower Floor 2 to Sprout Tower Floor 3
				if (x == 126 && y == 61) {
					newx = 149;
					newy = 71;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
				//Sprout Tower Floor 3 to Sprout Tower Floor 2
				if (x == 150 && y == 71) {
					newx = 126;
					newy = 62;
					insideBuilding = true;
					updateSky();
					layer.setPosition((newx-7)*32, (newy-5)*32);
					player.setTile(0, 0);
					if (soundEnabled && loadedSE) doorSoundPool.play(doorSound, volume, volume, 1, 0, 1f);
				}
			}
		}
		movable = true;
	}

	/**
	 * Handles all collision detection of the Player and the map.
	 *
	 * @param	sprite	AnimatedSprite that should be checked
	 * @param	xstep	Left or Right
	 * @parem	ystep	Up or Down
	 * @return	Whether or not the player can pass through this tile or object
	 */
	private boolean checkTileCollisions(AnimatedSprite sprite, int xstep, int ystep) {
		if (collisionLayer == null || noClip) return false;
		boolean col = collisionLayer.getTileFromRect(sprite.getCollisionRect(), xstep, ystep).size() != 0;
		if (soundEnabled && loadedSE && col) collisionSoundPool.play(collisionSound, volume, volume, 1, 0, 1f);
		return col;
	}
	
	/**
	 * Handles all collision detection of the Player and all NPC's. All responsible
	 * for getting the information from each individual NPC such as their message
	 * text or events.
	 * <p>
	 * Event Handling is not yet implemented.
	 *
	 * @param	scene	AnimatedSprite that should be checked
	 * @param	xstep	Left or Right
	 * @param	ystep	Up or Down
	 * @return	Whether or not the player can pass through this tile or object
	 */
	private boolean checkNPCCollisions(AnimatedSprite sprite, int xstep, int ystep) {
		if (npcLayer == null || noClip) return false;
		boolean col = npcLayer.getTileFromRect(sprite.getCollisionRect(), xstep, ystep).size() != 0;
		if (soundEnabled && loadedSE && col) collisionSoundPool.play(collisionSound, volume, volume, 1, 0, 1f);
		int xx = (tileX / 32) + 7;
		int yy = (tileY / 32) + 5;
		int[] x = {xx+1, xx-1};
		int[] y = {yy+1, yy-1};
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < y.length; j++) {
				//ADD ALL NPC STUFF HERE
				//Mom
				if (x[i] == 64 && y[j] == 138) {
					currentNPC = new NPC(64, 138, "MOM", "", "");
					currentNPC.message = new String[][] {
							{"Oh,", playerName + "...!"},
							{"Our", "neighbor,", "PROF. ELM", "was", "looking", "for", "you."}, 
							{"He", "said", "he", "had", "an", "errand", "to", "run."},
							{"Oh!", "I", "almost", "forgot!"},
							{"Your", "POKEMON", "GEAR", "is", "back", "from", "the", "shop."},
							{"Here", "you", "go!"},
							{playerName, "received", "the", "POKEGEAR!"},
							{"Phone", "numbers", "are", "stored", "in", "memory."},
							{"Just", "choose", "a", "name", "you", "want", "to", "call."},
							{"Gee,", "isn't", "that", "convenient?"}
							};
				}
				
				//Man
				else if (x[i] == 169 && y[j] == 111) {
					currentNPC = new NPC(169, 111, "MAN", "", "");
					currentNPC.message = new String[][] {
							{"Yo", playerName + "!"},
							{"I", "hear", "PROF.ELM", "discovered", "some", "new", "POKEMON."}
							};
				}
				
				//Girl
				else if (x[i] == 163 && y[j] == 109) {
					currentNPC = new NPC(163, 109, "GIRL", "", "");
					currentNPC.message = new String[][] {
							{"Wow,", "your", "POKEGEAR", "is", "impressive!"},
							{"Did", "your", "mom", "get", "it", "for", "you?"}
							};
				}
				
				//Kamon
				else if (x[i] == 160 && y[j] == 104) {
					currentNPC = new NPC(160, 104, "???", "", "");
					currentNPC.message = new String[][] {
							{"...", "..."},
							{"So", "this", "is", "the", "famous", "ELM", "POKEMON", "LAB."},
							{"...What", "are", "you", "staring", "at?"}
							};
				}
				
				//ELMS Wife
				else if (x[i] == 94 && y[j] == 141) {
					currentNPC = new NPC(94, 141, "WOMAN", "", "");
					currentNPC.message = new String[][] {
							{"Hi,", "GOLD!"},
							{"My", "husband's", "always", "so", "busy-"},
							{"-I", "hope", "he's", "OK."},
							{"When", "he's", "caught", "up", "in", "his", "POKEMON", "research,"},
							{"he", "even", "forgets", "to", "eat."}
							};
				}
				
				//ELMS Kid
				else if (x[i] == 98 && y[j] == 140) {
					currentNPC = new NPC(98, 140, "GIRL", "", "");
					currentNPC.message = new String[][] {
							{"When", "I", "grow", "up,", "I'm", "going", "to", "help", "my", "Dad!"},
							{"I'm", "going", "to", "be", "a", "great", "POKEMON", "professor!"}
							};
				}
				
				//Neighbor Lady
				else if (x[i] == 78 && y[j] == 138) {
					currentNPC = new NPC(78, 138, "WOMAN", "", "");
					currentNPC.message = new String[][] {
							{"PIKACHU", "is", "an", "evolved", "POKEMON."},
							{"I", "was", "amazed", "by", "PROF.ELM's", "findings."},
							{"He's", "so", "famous", "for", "his", "research", "on", "POKEMON", "evolution."},
							{"...sigh..."},
							{"I", "wish", "I", "could", "be", "a", "research", "like", "him..."}
							};
				}
				
				//Prof Elm
				else if (x[i] == 114 && y[j] == 135) {
					currentNPC = new NPC(114, 135, "PROF.ELM", "", "");
					currentNPC.message = new String[][] {
							{"GOLD!", "There", "you", "are!"},
							{"I", "needed", "to", "ask", "you", "a", "favor."},
							{"I", "have", "an", "acquaintance", "called", "MR.POKEMON."},
							{"He", "keeps", "finding", "weird", "things"},
							{"and", "raving", "about", "his", "discoveries."},
							{"Anyway,", "I", "just", "got", "an", "e-mail", "from", "him"},
							{"saying", "that", "this", "time", "it's", "real."},
							{"It", "is", "intriguing,", "but", "we're", "busy"},
							{"with", "our", "POKEMON", "research."},
							{"Could", "you", "look", "into", "it", "for", "us?"},
							{"I'll", "give", "you", "a", "POKEMON", "for", "a", "partner."},
							{"They're", "all", "rare", "POKEMON", "that", "we", "just", "found."},
							{"Go", "on.", "Pick", "one!"}
							};
				}
				
				//ELMS Assistant
				else if (x[i] == 111 && y[j] == 142) {
					currentNPC = new NPC(111, 142, "ASSISTANT", "", "");
					currentNPC.message = new String[][] {
							{"There", "are", "only", "two", "of", "us,", "so", "we're", "always", "busy."}
					};
				}
				
				//Pokemon Choices
				else if (x[i] == 115 && y[j] == 136) {
					currentNPC = new NPC(115, 136, "BALL", "", "");
					currentNPC.message = new String[][] {
							{"So,", "you", "like", "CHIKORITA,", "the", "grass", "POKEMON?"}
							};
				}
				
				else if (x[i] == 116 && y[j] == 136) {
					currentNPC = new NPC(116, 136, "BALL", "", "");
					currentNPC.message = new String[][] {
							{"You'll", "take", "CYNDAQUIL,", "the", "fire", "POKEMON?"}
							};
				}
				
				else if (x[i] == 117 && y[j] == 136) {
					currentNPC = new NPC(117, 136, "BALL", "", "");
					currentNPC.message = new String[][] {
							{"Do", "you", "want", "TOTODILE,", "the", "water", "POKEMON?"}
							};
				}
				
				else if (x[i] == 147 && y[j] == 114) {
					currentNPC = new NPC(147, 114, "MAN", "", "");
					currentNPC.message = new String[][] {
							{"POKEMON", "hide", "in", "the", "grass."},
							{"Who", "knows", "when", "they'll", "pop", "out..."}
					};
				}
				
				else if (x[i] == 124 && y[j] == 117) {
					currentNPC = new NPC(124, 117, "BOY", "", "");
					currentNPC.message = new String[][] {
							{"Yo."},
							{"How", "are", "your", "POKEMON?"},
							{"If", "they're", "weak", "and", "not", "ready", "for", "battle,"},
							{"keep", "out", "of", "the", "grass."}
					};
				}
				
				else if (x[i] == 109 && y[j] == 105) {
					currentNPC = new NPC(109, 105, "BOY", "", "");
					currentNPC.message = new String[][] {
							{"I", "wanted", "to", "take", "a", "break,"},
							{"so", "I", "saved", "to", "record", "my", "progress."}
					};
				}
				
				else if (x[i] == 122 && y[j] == 106) {
					currentNPC = new NPC(122, 106, "MAN", "", "");
					currentNPC.message = new String[][] {{"ROUTE29", "connects", "NEWBARKTOWN", "and", "CHERRYGROVECITY."}};
				}
				
				else if (x[i] == 111 && y[j] == 113) {
					currentNPC = new NPC(111, 113, "GIRL", "", "");
					currentNPC.message = new String[][] {{"Jump", "off", "ledges", "to", "get", "back", "to", "NEWBARKTOWN", "faster!"}};
				}
				
				//CHERRYGROVE CITY PC
				else if (x[i] == 131 && y[j] == 137) {
					currentNPC = new NPC(131, 137, "NURSE JOY", "", "");
					currentNPC.message = new String[][] {
							{"Hello!"},
							{"Welcome", "to", "our", "Pokémon", "Center."},
							{"We", "can", "heal", "your", "Pokémon", "to", "perfect", "health."},
							{"Shall", "we", "heal", "your", "Pokémon?"},
							{"..."},
							{"Ok,", "may", "I", "see", "your", "Pokémon?"},
							{"..."},
							{"Thank", "you", "for", "waiting."},
							{"Your", "Pokémon", "are", "fully", "healed."},
							{"We", "hope", "to", "see", "you", "again."}
					};
				}
				
			}
		}
		return col;
	}
	
	/**
	 * Calls the checkNPCCollisions method and determines whether or
	 * not a message box should be shown.
	 */
	private void checkNPCEvents() {
		if (checkNPCCollisions(player, xstepold, ystepold)) {
			if (soundEnabled && loadedSE) selectionSoundPool.play(selectionSound, volume, volume, 1, 0, 1f);
			if (curMessageWord < totalMessageWords) {
				showMessage = true;
			}
			else {
				showMessage = false;
			}
		}
	}
	
	/**
	 * Constantly checks to see if a Pokemon in the Player's Party has
	 * a status condition and will handle HP loss. If the entire party
	 * of Pokemon have fainted outside of battle from these conditions,
	 * the player will be teleported to the last visited PokeCenter and have
	 * their party healed automatically.
	 */
	private void checkStatusConditions() {
		int numFaintedPokemon = 0;
		for (int i = 0; i < pokemonparty.length; i++) {
			if (pokemonparty[i].cur_HP <= 0 && !pokemonparty[i].equals(monsters[0])) {
				numFaintedPokemon++;
				if (DEBUG) Log.v("BATTLE", pokemonparty[i].getName() + " has fainted!");
				//if (DEBUG) pokemonparty[i].healPokemon(); //DEBUG
			}
			if (pokemonparty[i].getStatusEffect() == 2 || pokemonparty[i].getStatusEffect() == 3) {
				pokemonparty[i].cur_HP -= 1;
				if (DEBUG) Log.v("BATTLE", pokemonparty[i].getName() + " has lost HP due to a status effect.");
			}
		}
		int currentPartySize = 0;
		for (int i = 0; i < pokemonparty.length; i++) {
			if (!pokemonparty[i].equals(monsters[0])) {
				currentPartySize++;
			}
		}
		if (numFaintedPokemon >= currentPartySize) {
			if (!DEBUG) teleportToLastPC();
		}
	}
	
	/**
	 * Teleports the Player to the last PokeCenter that was visited.
	 */
	private void teleportToLastPC() {
		for (int i = 0; i < pokemonparty.length; i++) {
			if (!pokemonparty[i].equals(monsters[0])) {
				pokemonparty[i].healPokemon();
			}
		}
		
		for (TMXLayer layer : mapLayers) {
			layer.setPosition(lastPCX*32-(7*32), lastPCY*32-(5*32));
		}
		
		if (soundEnabled) {
			bgm.stop();
			bgm.release();
			if (previousMapName == "NEWBARKTOWN") {
				currentMapName = "NEWBARKTOWN";
				bgm = MediaPlayer.create(getApplicationContext(), R.raw.newbarktown);
			}
			if (previousMapName == "CHERRYGROVECITY") {
				currentMapName = "CHERRYGROVECITY";
				bgm = MediaPlayer.create(getApplicationContext(), R.raw.cherrygrovecity);
			}
			if (previousMapName == "VIOLETCITY") {
				currentMapName = "VIOLETCITY";
				bgm = MediaPlayer.create(getApplicationContext(), R.raw.violetcity);
			}
			bgm.setLooping(true);
			bgm.start();	
		}
		
		grassanimation.hide();
		
		if (DEBUG) Log.v("BATTLE", "All of the Players Pokemon have been healed!");
		if (DEBUG) Log.v("TMX", "Player has been teleported to the nearest PC.");
	}

	/**
	 * Updates the completion of the Player's Pokedex upon capture of a Pokemon.
	 * It checks the Player's party and PC Storage boxes.
	 */
	private void updatePokedexCompletion() {
		int num = 0;
		for (int i = 0; i < pokemonparty.length; i++) {
			if (!pokemonparty[i].equals(monsters[0])) {
				num++;
			}
		}
		for (int j = 0; j < pokemonPCStorage.length; j++) {
			if (!pokemonPCStorage[j].equals(monsters[0])) {
				num++;
			}
		}
		pokedexCompletion = num;
	}

	/**
	 * Determines whether or not it is day or night in the game and will show the 
	 * nighttime overlay if it is night.
	 */
	private void updateSky() {
		if (enableDayNightSystem) {
			Time now = new Time();
			now.setToNow();
			if ((now.hour > 18 || now.hour < 6) && !insideBuilding) {
				dayTime = false;
				night.show(); //6PM to 6AM (Night)
			}
			else {
				dayTime = true;
				night.hide(); //6AM to 6PM (Day)
			}
		}
		else {
			dayTime = true;
			night.hide();
		}
	}
	
	/**
	 * Changes the BGM to the current zone on the Map the player has entered.
	 */
	private void updateBGM() {
		if (soundEnabled) {
			bgm.stop();
			bgm.release();
			if (currentMapName == "NEWBARKTOWN") bgm = MediaPlayer.create(getApplicationContext(), R.raw.newbarktown);
			if (currentMapName == "ROUTE29") bgm = MediaPlayer.create(getApplicationContext(), R.raw.route29);
			if (currentMapName == "CHERRYGROVECITY") bgm = MediaPlayer.create(getApplicationContext(), R.raw.cherrygrovecity);
			if (currentMapName == "ROUTE30") bgm = MediaPlayer.create(getApplicationContext(), R.raw.route30);
			if (currentMapName == "ROUTE31") bgm = MediaPlayer.create(getApplicationContext(), R.raw.route30);
			if (currentMapName == "VIOLETCITY") bgm = MediaPlayer.create(getApplicationContext(), R.raw.violetcity);
			if (currentMapName == "ROUTE32") bgm = MediaPlayer.create(getApplicationContext(), R.raw.route36);
			if (currentMapName == "ROUTE46") bgm = MediaPlayer.create(getApplicationContext(), R.raw.route42);
			bgm.setLooping(true);
			bgm.start();	
		}
	}
	
	/**
	 * Initializes a new instance of the In Game Menu.
	 */
	private void inGameMenu() {
		movable = false;
		inMenu = true;
		coords.setVisible(false);
		
		if (soundEnabled && loadedSE) openmenuSoundPool.play(openmenuSound, volume, volume, 1, 0, 1f);
		
		dpad.setAlpha(0.3f);
		abbuttons.setAlpha(0.3f);
		ssbuttons.setAlpha(0.3f);
		
		menuLayer = new Layer();
		menu = new MenuScene(this);
		
		arrow = new Sprite(new AssetTexture("arrow.png", 16, 24, this));
		if (isMale) menuBG = new Sprite(new AssetTexture("menu.png", 480, 320, this));
		else menuBG = new Sprite(new AssetTexture("menu_f.png", 480, 320, this));
		
		pokedex = new Sprite(new AssetTexture("pokedexbg.png", 480, 320, this));
		
		battleBG4 = new Sprite(new AssetTexture("battle4.png", 480, 320, this));
		
		Battle_pkmn1 = new TextSprite(pokemonparty[0].getName() + "    Lvl: " + pokemonparty[0].getLevel() +
				"    HP: " + pokemonparty[0].cur_HP + " / " + pokemonparty[0].hp, battleFontSize, this);
		Battle_pkmn2 = new TextSprite(pokemonparty[1].getName() + "    Lvl: " + pokemonparty[1].getLevel() +
				"    HP: " + pokemonparty[1].cur_HP + " / " + pokemonparty[1].hp, battleFontSize, this);
		Battle_pkmn3 = new TextSprite(pokemonparty[2].getName() + "    Lvl: " + pokemonparty[2].getLevel() +
				"    HP: " + pokemonparty[2].cur_HP + " / " + pokemonparty[2].hp, battleFontSize, this);
		Battle_pkmn4 = new TextSprite(pokemonparty[3].getName() + "    Lvl: " + pokemonparty[3].getLevel() +
				"    HP: " + pokemonparty[3].cur_HP + " / " + pokemonparty[3].hp, battleFontSize, this);
		Battle_pkmn5 = new TextSprite(pokemonparty[4].getName() + "    Lvl: " + pokemonparty[4].getLevel() +
				"    HP: " + pokemonparty[4].cur_HP + " / " + pokemonparty[4].hp, battleFontSize, this);
		Battle_pkmn6 = new TextSprite(pokemonparty[5].getName() + "    Lvl: " + pokemonparty[5].getLevel() +
				"    HP: " + pokemonparty[5].cur_HP + " / " + pokemonparty[5].hp, battleFontSize, this);
		Battle_SelectionCancel = new TextSprite("SWITCH", battleFontSize, this);
		Battle_Switch = new TextSprite("CANCEL", battleFontSize, this);
		
		pokemonIcon1 = new AnimatedSprite(new TiledTexture(pokemonparty[0].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 4);
		pokemonIcon2 = new AnimatedSprite(new TiledTexture(pokemonparty[1].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 34);
		pokemonIcon3 = new AnimatedSprite(new TiledTexture(pokemonparty[2].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 66);
		pokemonIcon4 = new AnimatedSprite(new TiledTexture(pokemonparty[3].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 98);
		pokemonIcon5 = new AnimatedSprite(new TiledTexture(pokemonparty[4].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 130);
		pokemonIcon6 = new AnimatedSprite(new TiledTexture(pokemonparty[5].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 162);
		
		bag = new Sprite(new AssetTexture("bag.png", 480, 320, this));
		Battle_Item1 = new TextSprite(items[0].getItemName() + "  x" + items[0].numberOfItem(), menuFontSize, this);
		Battle_Item2 = new TextSprite(items[1].getItemName() + "  x" + items[1].numberOfItem(), menuFontSize, this);
		Battle_Item3 = new TextSprite(items[2].getItemName() + "  x" + items[2].numberOfItem(), menuFontSize, this);
		Battle_Item4 = new TextSprite(items[3].getItemName() + "  x" + items[3].numberOfItem(), menuFontSize, this);
		
		pokegear = new Sprite(new AssetTexture("Pokegearback.png", 480, 320, this));
		entryMap = new Sprite(new AssetTexture("MapEntry.png", 480, 320, this));
		entryPhone = new Sprite(new AssetTexture("PhoneEntry.png", 480, 320, this));
		entryRadio = new Sprite(new AssetTexture("RadioEntry.png", 480, 320, this));
		entryExit = new Sprite(new AssetTexture("ExitEntry.png", 480, 320, this));
		
		save = new Sprite(new AssetTexture("save.png", 480, 320, this));
		save_PlayerName = new TextSprite("" + playerName, menuFontSize, this);
		save_PlayerBadges = new TextSprite("" + badges, menuFontSize, this);
		save_PlayerPokedex = new TextSprite("" + pokedexCompletion, menuFontSize, this);
		save_PlayerTime = new TextSprite("" + timePlayed, menuFontSize, this);
		save_currentMapName = new TextSprite("" + currentMapName, 22, this);
		
		trainercard = new Sprite(new AssetTexture("trainerCard.png", 480, 320, this));
		if (isMale) cardSprite = new Sprite(new AssetTexture("trainer000.png", 128, 128, this));
		else cardSprite = new Sprite(new AssetTexture("trainer001.png", 128, 128, this));
		trainercard_trainerID = new TextSprite("ID:     " + trainerID, menuFontSize, this);
		trainercard_playerName = new TextSprite("Name:     " + playerName, menuFontSize, this);
		trainercard_playerMoney = new TextSprite("Money:                 $" + money, menuFontSize, this);
		trainercard_pokemonOwned = new TextSprite("Pokedex:                      " + pokedexCompletion, menuFontSize, this);
		
		option = new Sprite(new AssetTexture("option.png", 480, 320, this));
		
		save_currentMapName.setTypeface(pokefont);
		
		menuLayer.add(menuBG);
		
		menuLayer.add(pokedex);
		
		menuLayer.add(battleBG4);
		
		menuLayer.add(Battle_pkmn1);
		menuLayer.add(Battle_pkmn2);
		menuLayer.add(Battle_pkmn3);
		menuLayer.add(Battle_pkmn4);
		menuLayer.add(Battle_pkmn5);
		menuLayer.add(Battle_pkmn6);
			
		if (!pokemonparty[0].equals(monsters[0])) menuLayer.add(pokemonIcon1);
		if (!pokemonparty[1].equals(monsters[0])) menuLayer.add(pokemonIcon2);
		if (!pokemonparty[2].equals(monsters[0])) menuLayer.add(pokemonIcon3);
		if (!pokemonparty[3].equals(monsters[0])) menuLayer.add(pokemonIcon4);
		if (!pokemonparty[4].equals(monsters[0])) menuLayer.add(pokemonIcon5);
		if (!pokemonparty[5].equals(monsters[0])) menuLayer.add(pokemonIcon6);
		
		menuLayer.add(Battle_SelectionCancel);
		menuLayer.add(Battle_Switch);
		
		pokemonIcon1.animate(200, pkIcons);
		pokemonIcon2.animate(200, pkIcons);
		pokemonIcon3.animate(200, pkIcons);
		pokemonIcon4.animate(200, pkIcons);
		pokemonIcon5.animate(200, pkIcons);
		pokemonIcon6.animate(200, pkIcons);
		
		menuLayer.add(bag);
		menuLayer.add(Battle_Item1);
		menuLayer.add(Battle_Item2);
		menuLayer.add(Battle_Item3);
		menuLayer.add(Battle_Item4);
		
		menuLayer.add(pokegear);
		menuLayer.add(entryMap);
		menuLayer.add(entryPhone);
		menuLayer.add(entryRadio);
		menuLayer.add(entryExit);
		
		menuLayer.add(save);
		menuLayer.add(save_PlayerName);
		menuLayer.add(save_PlayerBadges);
		menuLayer.add(save_PlayerPokedex);
		menuLayer.add(save_PlayerTime);
		menuLayer.add(save_currentMapName);
		
		menuLayer.add(trainercard);
		menuLayer.add(cardSprite);
		menuLayer.add(trainercard_trainerID);
		menuLayer.add(trainercard_playerName);
		menuLayer.add(trainercard_playerMoney);
		menuLayer.add(trainercard_pokemonOwned);
		
		menuLayer.add(option);
		
		menuLayer.add(arrow);
		
		pokedex.hide();
		
		battleBG4.hide();
		
		Battle_pkmn1.hide();
		Battle_pkmn2.hide();
		Battle_pkmn3.hide();
		Battle_pkmn4.hide();
		Battle_pkmn5.hide();
		Battle_pkmn6.hide();
		
		pokemonIcon1.hide();
		pokemonIcon2.hide();
		pokemonIcon3.hide();
		pokemonIcon4.hide();
		pokemonIcon5.hide();
		pokemonIcon6.hide();
		
		Battle_SelectionCancel.hide();
		Battle_Switch.hide();
		
		bag.hide();
		Battle_Item1.hide();
		Battle_Item2.hide();
		Battle_Item3.hide();
		Battle_Item4.hide();
		
		pokegear.hide();
		entryPhone.hide();
		entryRadio.hide();
		entryMap.hide();
		entryExit.hide();
		
		save.hide();
		save_PlayerName.hide();
		save_PlayerBadges.hide();
		save_PlayerPokedex.hide();
		save_PlayerTime.hide();
		save_currentMapName.hide();
		
		trainercard.hide();
		cardSprite.hide();
		trainercard_trainerID.hide();
		trainercard_playerName.hide();
		trainercard_playerMoney.hide();
		trainercard_pokemonOwned.hide();
		
		option.hide();
		
		scene.addLayer(menuLayer);
	}
	
	/**
	 * Updates the current State of the In Game Menu
	 */
	private void inGameMenuUpdate() {
		if (menu.inMain) {
			menuBG.show();
			arrow.show();
			save.hide();
			bag.hide();
			Battle_Item1.hide();
			Battle_Item2.hide();
			Battle_Item3.hide();
			Battle_Item4.hide();
			
			if (menu.currentSelectionMain == 0) {
	    	 	//Pokedex
				arrow.move(335, 20);
	    	}
	    	else if (menu.currentSelectionMain == 1) {
	    	 	//Pokemon
	    		arrow.move(335, 52);
	    	}
	    	else if (menu.currentSelectionMain == 2) {
	    	 	//Bag
	    		arrow.move(335, 84);
	    	}
	    	else if (menu.currentSelectionMain == 3) {
	    	 	//Pokegear
	    		arrow.move(335, 116);
	    	}
	    	else if (menu.currentSelectionMain == 4) {
	    	 	//Gold
	    		arrow.move(335, 148);
	    	}
	    	else if (menu.currentSelectionMain == 5) {
	    	 	//Save
	    		arrow.move(335, 180);
	    	}
	    	else if (menu.currentSelectionMain == 6) {
	    	 	//Option
	    		arrow.move(335, 212);
	    	}
	    	else if (menu.currentSelectionMain == 7) {
	    	 	//Exit
	    		arrow.move(335, 244);
	    	}
		}
		
		if (menu.inPokeDex == true) {
			menuBG.hide();
			arrow.hide();
			pokedex.show();
    	}
		
		if (menu.inPokemon == true) {
    		menuBG.hide();
    		battleBG4.show();
    		if (!pokemonparty[0].equals(monsters[0])) Battle_pkmn1.show();
    		if (!pokemonparty[1].equals(monsters[0])) Battle_pkmn2.show();
    		if (!pokemonparty[2].equals(monsters[0])) Battle_pkmn3.show();
    		if (!pokemonparty[3].equals(monsters[0])) Battle_pkmn4.show();
    		if (!pokemonparty[4].equals(monsters[0])) Battle_pkmn5.show();
    		if (!pokemonparty[5].equals(monsters[0])) Battle_pkmn6.show();
    			
    		if (!pokemonparty[0].equals(monsters[0])) pokemonIcon1.show();
    		if (!pokemonparty[1].equals(monsters[0])) pokemonIcon2.show();
    		if (!pokemonparty[2].equals(monsters[0])) pokemonIcon3.show();
    		if (!pokemonparty[3].equals(monsters[0])) pokemonIcon4.show();
    		if (!pokemonparty[4].equals(monsters[0])) pokemonIcon5.show();
    		if (!pokemonparty[5].equals(monsters[0])) pokemonIcon6.show();
    			
    		Battle_pkmn1.move(52, 6);
    		Battle_pkmn2.move(52, 38);
    		Battle_pkmn3.move(52, 70);
    		Battle_pkmn4.move(52, 102);
    		Battle_pkmn5.move(52, 134);
    		Battle_pkmn6.move(52, 166);
    	}
		
		if (menu.inPokemonSubMenu == true) {
			battleBG4.show();
			
			if (!pokemonparty[0].equals(monsters[0])) Battle_pkmn1.show();
    		if (!pokemonparty[1].equals(monsters[0])) Battle_pkmn2.show();
    		if (!pokemonparty[2].equals(monsters[0])) Battle_pkmn3.show();
    		if (!pokemonparty[3].equals(monsters[0])) Battle_pkmn4.show();
    		if (!pokemonparty[4].equals(monsters[0])) Battle_pkmn5.show();
    		if (!pokemonparty[5].equals(monsters[0])) Battle_pkmn6.show();
    			
    		if (!pokemonparty[0].equals(monsters[0])) pokemonIcon1.show();
    		if (!pokemonparty[1].equals(monsters[0])) pokemonIcon2.show();
    		if (!pokemonparty[2].equals(monsters[0])) pokemonIcon3.show();
    		if (!pokemonparty[3].equals(monsters[0])) pokemonIcon4.show();
    		if (!pokemonparty[4].equals(monsters[0])) pokemonIcon5.show();
    		if (!pokemonparty[5].equals(monsters[0])) pokemonIcon6.show();
			
			Battle_SelectionCancel.show();
			Battle_Switch.show();
			
			Battle_SelectionCancel.move(364, 132);
			Battle_Switch.move(364, 164);
		}
    	
    	if (menu.inPokeGear == true) {
    		menuBG.hide();
    		arrow.hide();
    		pokegear.show();
    		
    		if (menu.currentSelectionPokeGear == 0) {
	    		entryMap.show();
	    		entryPhone.hide();
	    		entryRadio.hide();
	    		entryExit.hide();
		    }
		    else if (menu.currentSelectionPokeGear == 1) {
		    	entryRadio.show();
		    	entryPhone.hide();
				entryMap.hide();
				entryExit.hide();
		    }
		    else if (menu.currentSelectionPokeGear == 2) {
		    	entryPhone.show();
				entryRadio.hide();
				entryMap.hide();
				entryExit.hide();
		    }
		    else if (menu.currentSelectionPokeGear == 3) {
		    	entryExit.show();
		    	entryPhone.hide();
				entryRadio.hide();
				entryMap.hide();
		    }
    	}
		
		if (menu.inBag == true) {
			menuBG.hide();
			arrow.show();
			bag.show();
				
			Battle_Item1.move(118, 32);
			Battle_Item2.move(118, 64);
			Battle_Item3.move(118, 96);
			Battle_Item4.move(118, 128);
						
			if (items[battleItemIndexSlot].numberOfItem() != 0) Battle_Item1.show();
			if (items[battleItemIndexSlot+1].numberOfItem() != 0) Battle_Item2.show();
			if (items[battleItemIndexSlot+2].numberOfItem() != 0) Battle_Item3.show();
			if (items[battleItemIndexSlot+3].numberOfItem() != 0) Battle_Item4.show();
    	}
		
		if (menu.inBagSelected == true) {
			arrow.show();
			bag.show();
				
			Battle_Item1.move(118, 32);
			Battle_Item2.move(118, 64);
			Battle_Item3.move(118, 96);
			Battle_Item4.move(118, 128);
						
			if (items[battleItemIndexSlot].numberOfItem() != 0) Battle_Item1.show();
			if (items[battleItemIndexSlot+1].numberOfItem() != 0) Battle_Item2.show();
			if (items[battleItemIndexSlot+2].numberOfItem() != 0) Battle_Item3.show();
			if (items[battleItemIndexSlot+3].numberOfItem() != 0) Battle_Item4.show();
    	}
		
		if (menu.inSave) {
			menuBG.hide();
			save.show();
			arrow.show();
			save_PlayerName.show();
			save_PlayerBadges.show();
			save_PlayerPokedex.show();
			save_PlayerTime.show();
			save_currentMapName.show();
			
			save_PlayerName.move(100, 68-16);
			save_PlayerBadges.move(132, 100-16);
			save_PlayerPokedex.move(132, 132-16);
			save_PlayerTime.move(132, 164-16);
			save_currentMapName.move(12, 18);
			
			if (menu.currentSelectionSave == 0) {
				arrow.move(394, 148);
			}
			else if (menu.currentSelectionSave == 1) {
				arrow.move(394, 180);
	    	}
		}
		
		if (menu.inTrainerCard) {
			menuBG.hide();
			arrow.hide();
			trainercard.show();
			cardSprite.show();
			trainercard_trainerID.show();
			trainercard_playerName.show();
			trainercard_playerMoney.show();
			trainercard_pokemonOwned.show();
			
			cardSprite.move(320, 100-16);
			trainercard_trainerID.move(295, 54-16);
			trainercard_playerName.move(64, 93-16);
			trainercard_playerMoney.move(64, 150-16);
			trainercard_pokemonOwned.move(64, 182-16);
		}
		
		if (menu.inOption == true) {
			menuBG.hide();
			option.show();
			arrow.show();
			
    		if (menu.currentSelectionOption == 0) {
    			arrow.move(22, 85);
		    }
		    else if (menu.currentSelectionOption == 1) {
		    	arrow.move(22, 117);
		    }
		    else if (menu.currentSelectionOption == 2) {
		    	arrow.move(22, 149);
		    }
		    else if (menu.currentSelectionOption == 3) {
		    	arrow.move(22, 181);
		    }
		    else if (menu.currentSelectionOption == 4) {
		    	arrow.move(22, 213);
		    }
		    else if (menu.currentSelectionOption == 5) {
		    	arrow.move(22, 245);
		    }
    	}
	}
	
	/**
	 * Ends the current Instance of the In Game Menu
	 */
	private void inGameMenuEnd() {
		menu.Exit();
		
		inMenu = false;
		menu.inMain = false;
		menu.inSave = false;
		menu.inTrainerCard = false;
		movable = true;
		
		menuLayer.remove(menuBG);
		menuLayer.remove(save);
		menuLayer.remove(arrow);
		
		menuLayer.remove(battleBG4);
		
		menuLayer.remove(Battle_pkmn1);
		menuLayer.remove(Battle_pkmn2);
		menuLayer.remove(Battle_pkmn3);
		menuLayer.remove(Battle_pkmn4);
		menuLayer.remove(Battle_pkmn5);
		menuLayer.remove(Battle_pkmn6);
			
		menuLayer.remove(pokemonIcon1);
		menuLayer.remove(pokemonIcon2);
		menuLayer.remove(pokemonIcon3);
		menuLayer.remove(pokemonIcon4);
		menuLayer.remove(pokemonIcon5);
		menuLayer.remove(pokemonIcon6);
		
		dpad.setAlpha(alphaLevel);
		abbuttons.setAlpha(alphaLevel);
		ssbuttons.setAlpha(alphaLevel);
		
		scene.removeLayer(menuLayer);
		
		if (previousDirection == 1) player.setTile(0, 1); //Left Stationary Frame
		if (previousDirection == 2) player.setTile(0, 2); //Right Stationary Frame
		if (previousDirection == 3) player.setTile(0, 3); //Up Stationary Frame
		if (previousDirection == 4) player.setTile(0, 0); //Down Stationary Frame
		player.stop(); //Freeze Player Animation Frame (Not Walking)
		
		if (DEBUG) coords.show();
	}
	
	/**
	 * Called when an Item is used from the In Game Menu.
	 */
	private void useItemOverworld() {
		if (items[menu.currentSelectionItemY].getItemEffect() == 2) {
			Log.v("TMX", "YOU CANT USE THAT HERE.");
		}
		
		if (items[menu.currentSelectionItemY].getItemName() == "POTION") {
			//Restores 20 HP
			Log.v("TMX", pokemonparty[0].getName() + "'s HP is " +pokemonparty[0].cur_HP);
			if ((pokemonparty[0].cur_HP + 20) >= pokemonparty[0].hp) {
				pokemonparty[0].cur_HP = pokemonparty[0].hp;
			}
			else pokemonparty[0].cur_HP += 20;
			Log.v("TMX", pokemonparty[0].getName() + "'s HP Restored to " + pokemonparty[0].cur_HP);
			items[menu.currentSelectionItemY].useItem();
		}
		menu.currentSelectionItemX = 0;
		menu.currentSelectionItemY = 0;
		menu.currentSelectionPartyY = 0;
	}
	
	/**
	 * Called when an Item is used from within a Battle.
	 */
	private void useItemBattle() {
		items[encounter.currentSelectionItemY].useItem();
		
		if (items[encounter.currentSelectionItemY].getItemEffect() == 2) {
			Log.v("BATTLE", "You captured a wild " + encounter.enemyPokemon.getName() + "!");
			//Ball (Captures Wild Pokemon)
			boolean freeslot = false;
			for (int i = 0; i < pokemonparty.length; i++) {
				//Find first free slot
				if (pokemonparty[i].equals(monsters[0])) {
					pokemonparty[i] = new Monsters();
					pokemonparty[i] = monsters[encounter.enemyPokemon.getNumber()];
					pokemonparty[i].create(encounter.enemyPokemon.getLevel());
					freeslot = true;
					break;
				}
			}
			//No free slot in party, deposit to PC
			if (!freeslot) {
				for (int i = 0; i < pokemonPCStorage.length; i++) {
					if (pokemonPCStorage[i].equals(monsters[0])) {
						pokemonPCStorage[i] = new Monsters();
						pokemonPCStorage[i] = monsters[encounter.enemyPokemon.getNumber()];
						pokemonPCStorage[i].create(encounter.enemyPokemon.getLevel());
						break;
					}
				}
				Log.v("BATTLE", encounter.enemyPokemon.getName() + " has been sent to Bill's PC.");
			}
			encounter.playerTurn = true;
			encounter.playerWon = true;
			battleEnd();
		}
		
		if (items[encounter.currentSelectionItemY].getItemName() == "POTION") {
			//Restores 20 HP
			Log.v("BATTLE", encounter.playerPokemon.getName() + "'s HP is " + encounter.playerPokemon.cur_HP);
			if ((encounter.playerPokemon.cur_HP + 20) >= encounter.playerPokemon.hp) {
				encounter.playerPokemon.cur_HP = encounter.playerPokemon.hp;
			}
			else encounter.playerPokemon.cur_HP += 20;
			Log.v("BATTLE", encounter.playerPokemon.getName() + "'s HP Restored to " + encounter.playerPokemon.cur_HP);
		}
		
		if (DEBUG) Log.v("BATTLE", "Item Name: " + items[encounter.currentSelectionItemY].getItemName() + 
				" QTY: " + items[encounter.currentSelectionItemY].numberOfItem());
		
		encounter.currentSelectionMainX = 0;
		encounter.currentSelectionMainY = 0;
		encounter.currentSelectionFightX = 0;
		encounter.currentSelectionFightY = 0;
		encounter.currentSelectionItemX = 0;
		encounter.currentSelectionItemY = 0;
		encounter.currentSelectionPartyX = 0;
		encounter.currentSelectionPartyY = 0;
		encounter.playerTurn = false;
		encounter.inMain = true;
		encounter.inItem = false;
	}
	
	/**
	 * Initializes a new instance of a Wild Pokemon Encounter
	 */
	private void battle() {
		//Switch to the Wild Pokemon Encounter BGM
		if (soundEnabled) {
			bgm.stop();
			bgm.release();
			bgm = MediaPlayer.create(getApplicationContext(), R.raw.wildbattle);
			bgm.setLooping(true);
			bgm.start();	
		}
		
		//Disable Player movement and hide any Debug info
		movable = false;
		inBattle = true;
		if (DEBUG) coords.setVisible(false);
		
		//Set the Alpha of the Controls to a high transparency so they don't block the view
		dpad.setAlpha(0.3f);
		abbuttons.setAlpha(0.3f);
		ssbuttons.setAlpha(0);
		
		//Randomly create a wild Pokemon to be encountered.
		randGen = new Random();
		r = randGen.nextInt(4);
		if (r >= 4) r = 4;
		wildPokemon = monsters[Integer.parseInt(encounters[r][0])];
		wildPokemon.create(Integer.parseInt(encounters[r][1]), Integer.parseInt(encounters[r][2]));
		wildPokemon.cur_HP = wildPokemon.hp;
		
		//Initialize the Battle Layer and the Battle Scene
		battleLayer = new Layer();
		encounter = new BattleScene(this, pokemonparty, wildPokemon, items);
		encounter.Start();
		if (DEBUG) Log.v("BATTLE", "Wild " + wildPokemon.getName() + " appeared!");
		
		//Initialize all the Sprites to be used in the Battle Scene
		arrow = new Sprite(new AssetTexture("arrow.png", 16, 24, this));
		
		battleBG1 = new Sprite(new AssetTexture("battle.png", 480, 320, this));
		battleBG2 = new Sprite(new AssetTexture("battle2.png", 480, 320, this));
		battleBG3 = new Sprite(new AssetTexture("battle3.png", 480, 320, this));
		battleBG4 = new Sprite(new AssetTexture("battle4.png", 480, 320, this));
		
		Battle_statusBRN = new Sprite(new AssetTexture("statusBRN.png", 40, 16, this));
		Battle_statusFRZ = new Sprite(new AssetTexture("statusFRZ.png", 40, 16, this));
		Battle_statusPAR = new Sprite(new AssetTexture("statusPAR.png", 40, 16, this));
		Battle_statusPSN = new Sprite(new AssetTexture("statusPSN.png", 40, 16, this));
		Battle_statusSLP = new Sprite(new AssetTexture("statusSLP.png", 40, 16, this));
		
		Battle_playerPokemonName = new TextSprite("" + encounter.playerPokemon.getName(), battleFontSize, this);
		Battle_playerPokemonLevel = new TextSprite("" + encounter.playerPokemon.getLevel(), battleFontSize, this);
		Battle_playerPokemonCurrentHP = new TextSprite("" + encounter.playerPokemon.getCurrentHP(), battleFontSize, this);
		Battle_playerPokemonHP = new TextSprite("" + encounter.playerPokemon.getHP(), battleFontSize, this);
		Battle_enemyPokemonName = new TextSprite("" + encounter.enemyPokemon.getName(), battleFontSize, this);
		Battle_enemyPokemonLevel = new TextSprite("" + encounter.enemyPokemon.getLevel(), battleFontSize, this);
		Battle_enemyPokemonCurrentHP = new TextSprite("" + encounter.enemyPokemon.getCurrentHP(), battleFontSize, this);
		Battle_enemyPokemonHP= new TextSprite("" + encounter.enemyPokemon.getHP(), battleFontSize, this);
		
		Battle_wildNameAppeared = new TextSprite("Wild " + encounter.enemyPokemon.getName() + " Appeared!", battleFontSize, this);
		Battle_FIGHT = new TextSprite("FIGHT", battleFontSize, this);
		Battle_PKMN = new TextSprite("PKMN", battleFontSize, this);
		Battle_ITEM = new TextSprite("ITEM", battleFontSize, this);
		Battle_RUN = new TextSprite("RUN", battleFontSize, this);
		
		Battle_SelectAMove = new TextSprite("Select a Move", battleFontSize, this);
		Battle_playerPokemonMove1 = new TextSprite("" + encounter.playerPokemon.move1, battleFontSize, this);
		Battle_playerPokemonMove2 = new TextSprite("" + encounter.playerPokemon.move2, battleFontSize, this);
		Battle_playerPokemonMove3 = new TextSprite("" + encounter.playerPokemon.move3, battleFontSize, this);
		Battle_playerPokemonMove4 = new TextSprite("" + encounter.playerPokemon.move4, battleFontSize, this);
		
		Battle_playerPokemonBackSprite = new Sprite(new AssetTexture(encounter.playerPokemon.getBackSprite(), 96, 96, this));
		Battle_enemyPokemonFrontSprite = new Sprite(new AssetTexture(encounter.enemyPokemon.getFrontSprite(), 129, 128, this));
		
		Battle_Item1 = new TextSprite(items[0].getItemName() + "  x" + items[0].numberOfItem(), battleFontSize, this);
		Battle_Item2 = new TextSprite(items[1].getItemName() + "  x" + items[1].numberOfItem(), battleFontSize, this);
		Battle_Item3 = new TextSprite(items[2].getItemName() + "  x" + items[2].numberOfItem(), battleFontSize, this);
		Battle_Item4 = new TextSprite(items[3].getItemName() + "  x" + items[3].numberOfItem(), battleFontSize, this);
		
		Battle_pkmn1 = new TextSprite(pokemonparty[0].getName() + "    Lvl: " + pokemonparty[0].getLevel() +
				"    HP: " + encounter.playerPokemon.cur_HP + " / " + encounter.playerPokemon.hp, battleFontSize, this);
		Battle_pkmn2 = new TextSprite(pokemonparty[1].getName() + "    Lvl: " + pokemonparty[1].getLevel() +
				"    HP: " + pokemonparty[1].cur_HP + " / " + pokemonparty[1].hp, battleFontSize, this);
		Battle_pkmn3 = new TextSprite(pokemonparty[2].getName() + "    Lvl: " + pokemonparty[2].getLevel() +
				"    HP: " + pokemonparty[2].cur_HP + " / " + pokemonparty[2].hp, battleFontSize, this);
		Battle_pkmn4 = new TextSprite(pokemonparty[3].getName() + "    Lvl: " + pokemonparty[3].getLevel() +
				"    HP: " + pokemonparty[3].cur_HP + " / " + pokemonparty[3].hp, battleFontSize, this);
		Battle_pkmn5 = new TextSprite(pokemonparty[4].getName() + "    Lvl: " + pokemonparty[4].getLevel() +
				"    HP: " + pokemonparty[4].cur_HP + " / " + pokemonparty[4].hp, battleFontSize, this);
		Battle_pkmn6 = new TextSprite(pokemonparty[5].getName() + "    Lvl: " + pokemonparty[5].getLevel() +
				"    HP: " + pokemonparty[5].cur_HP + " / " + pokemonparty[5].hp, battleFontSize, this);
		Battle_SelectionCancel = new TextSprite("SWITCH", battleFontSize, this);
		Battle_Switch = new TextSprite("CANCEL", battleFontSize, this);
		
		pokemonIcon1 = new AnimatedSprite(new TiledTexture(pokemonparty[0].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 4);
		pokemonIcon2 = new AnimatedSprite(new TiledTexture(pokemonparty[1].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 34);
		pokemonIcon3 = new AnimatedSprite(new TiledTexture(pokemonparty[2].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 66);
		pokemonIcon4 = new AnimatedSprite(new TiledTexture(pokemonparty[3].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 98);
		pokemonIcon5 = new AnimatedSprite(new TiledTexture(pokemonparty[4].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 130);
		pokemonIcon6 = new AnimatedSprite(new TiledTexture(pokemonparty[5].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 162);
		
		pokemonIcon1.animate(200, pkIcons);
		pokemonIcon2.animate(200, pkIcons);
		pokemonIcon3.animate(200, pkIcons);
		pokemonIcon4.animate(200, pkIcons);
		pokemonIcon5.animate(200, pkIcons);
		pokemonIcon6.animate(200, pkIcons);
		
		//Add Sprites to the battleLayer
		battleLayer.add(battleBG1);
		battleLayer.add(battleBG2);
		
		battleLayer.add(Battle_playerPokemonName);
		battleLayer.add(Battle_playerPokemonLevel);
		battleLayer.add(Battle_playerPokemonCurrentHP);
		battleLayer.add(Battle_playerPokemonHP);
		battleLayer.add(Battle_enemyPokemonName);
		battleLayer.add(Battle_enemyPokemonLevel);
		battleLayer.add(Battle_enemyPokemonCurrentHP);
		battleLayer.add(Battle_enemyPokemonHP);
		battleLayer.add(Battle_wildNameAppeared);
		battleLayer.add(Battle_playerPokemonBackSprite);
		battleLayer.add(Battle_enemyPokemonFrontSprite);
		
		battleLayer.add(Battle_statusBRN);
		battleLayer.add(Battle_statusFRZ);
		battleLayer.add(Battle_statusPAR);
		battleLayer.add(Battle_statusPSN);
		battleLayer.add(Battle_statusSLP);
		
		battleLayer.add(battleBG3);
		battleLayer.add(battleBG4);
		
		battleLayer.add(Battle_FIGHT);
		battleLayer.add(Battle_PKMN);
		battleLayer.add(Battle_ITEM);
		battleLayer.add(Battle_RUN);
		
		battleLayer.add(arrow);
		
		battleLayer.add(Battle_SelectAMove);
		battleLayer.add(Battle_playerPokemonMove1);
		battleLayer.add(Battle_playerPokemonMove2);
		battleLayer.add(Battle_playerPokemonMove3);
		battleLayer.add(Battle_playerPokemonMove4);
		
		battleLayer.add(Battle_Item1);
		battleLayer.add(Battle_Item2);
		battleLayer.add(Battle_Item3);
		battleLayer.add(Battle_Item4);
		
		if (!pokemonparty[0].equals(monsters[0])) battleLayer.add(Battle_pkmn1);
		if (!pokemonparty[1].equals(monsters[0])) battleLayer.add(Battle_pkmn2);
		if (!pokemonparty[2].equals(monsters[0])) battleLayer.add(Battle_pkmn3);
		if (!pokemonparty[3].equals(monsters[0])) battleLayer.add(Battle_pkmn4);
		if (!pokemonparty[4].equals(monsters[0])) battleLayer.add(Battle_pkmn5);
		if (!pokemonparty[5].equals(monsters[0])) battleLayer.add(Battle_pkmn6);
		
		if (!pokemonparty[0].equals(monsters[0])) battleLayer.add(pokemonIcon1);
		if (!pokemonparty[0].equals(monsters[0])) battleLayer.add(pokemonIcon2);
		if (!pokemonparty[0].equals(monsters[0])) battleLayer.add(pokemonIcon3);
		if (!pokemonparty[0].equals(monsters[0])) battleLayer.add(pokemonIcon4);
		if (!pokemonparty[0].equals(monsters[0])) battleLayer.add(pokemonIcon5);
		if (!pokemonparty[0].equals(monsters[0])) battleLayer.add(pokemonIcon6);
		
		battleLayer.add(Battle_SelectionCancel);
		battleLayer.add(Battle_Switch);
		
		//Add the battleLayer to the scene
		scene.addLayer(battleLayer);
		
		//Hide the Sprites we don't want to see right away
		battleBG2.hide();
		battleBG3.hide();
		battleBG4.hide();
		
		Battle_statusBRN.hide();
		Battle_statusFRZ.hide();
		Battle_statusPAR.hide();
		Battle_statusPSN.hide();
		Battle_statusSLP.hide();
		
		Battle_SelectAMove.hide();
		Battle_playerPokemonMove1.hide();
		Battle_playerPokemonMove2.hide();
		Battle_playerPokemonMove3.hide();
		Battle_playerPokemonMove4.hide();
		
		Battle_Item1.hide();
		Battle_Item2.hide();
		Battle_Item3.hide();
		Battle_Item4.hide();
		
		Battle_pkmn1.hide();
		Battle_pkmn2.hide();
		Battle_pkmn3.hide();
		Battle_pkmn4.hide();
		Battle_pkmn5.hide();
		Battle_pkmn6.hide();
		Battle_SelectionCancel.hide();
		Battle_Switch.hide();
		
		pokemonIcon1.hide();
		pokemonIcon2.hide();
		pokemonIcon3.hide();
		pokemonIcon4.hide();
		pokemonIcon5.hide();
		pokemonIcon6.hide();
		
		//Position the Sprites we want to see right away
		Battle_playerPokemonName.move(396 - Battle_playerPokemonName.getWidth() - 8, 159);
		Battle_playerPokemonLevel.move(403, 159);
		Battle_playerPokemonCurrentHP.move(350, 192);
		Battle_playerPokemonHP.move(403, 192);
		Battle_enemyPokemonName.move(137 - Battle_enemyPokemonName.getWidth() - 8, 11);
		Battle_enemyPokemonLevel.move(144, 11);
		Battle_enemyPokemonCurrentHP.move(61, 30);
		Battle_enemyPokemonHP.move(112, 30);
		Battle_playerPokemonBackSprite.move(48, 132);
		Battle_enemyPokemonFrontSprite.move(308, 6);
		
		Battle_wildNameAppeared.move(16, 245);
		Battle_FIGHT.move(290, 245);
		Battle_PKMN.move(400, 245);
		Battle_ITEM.move(290, 275);
		Battle_RUN.move(400, 275);
		
		arrow.move(274, 240);
	}
	
	/**
	 * Handles the selection of an attack in the FIGHT portion of
	 * the battle system.
	 */
	private void battleAttack() {
		Random rr = new Random();
		int wakeupthaw = rr.nextInt(5);
		if (wakeupthaw <= 1) {
			if (encounter.playerPokemon.getStatusEffect() == 4) {
				Log.v("BATTLE", "" + encounter.playerPokemon.getName() + " has woken up.");
			}
			if (encounter.playerPokemon.getStatusEffect() == 5) {
				Log.v("BATTLE", "" + encounter.playerPokemon.getName() + " has broken free from the ice.");
			}
			encounter.playerPokemon.setStatusEffect(0);
		}

		if (encounter.playerPokemon.getStatusEffect() != 4 || encounter.playerPokemon.getStatusEffect() != 5) {
			String selectedAttack = "";
			
			if (encounter.currentSelectionFightX == 0 && encounter.currentSelectionFightY == 0) {
				Log.v("BATTLE", "" + "Attack 1 Selected - Name: " + encounter.playerPokemon.move1);
				selectedAttack = encounter.playerPokemon.move1;
			}
			else if (encounter.currentSelectionFightX == 1 && encounter.currentSelectionFightY == 0) {
				Log.v("BATTLE", "" + "Attack 2 Selected - Name: " + encounter.playerPokemon.move2);
				selectedAttack = encounter.playerPokemon.move2;
			}	
			else if (encounter.currentSelectionFightX == 0 && encounter.currentSelectionFightY == 1) {
				Log.v("BATTLE", "" + "Attack 3 Selected - Name: " + encounter.playerPokemon.move3);
				selectedAttack = encounter.playerPokemon.move3;
			}	
			else if (encounter.currentSelectionFightX == 1 && encounter.currentSelectionFightY == 1) {
				Log.v("BATTLE", "" + "Attack 4 Selected - Name: " + encounter.playerPokemon.move4);
				selectedAttack = encounter.playerPokemon.move4;
			}
			
			Attacks t = new Attacks(selectedAttack);
			
			if (encounter.playerPokemon.getStatusEffect() == 1) {
				Random r = new Random();
				int rand = r.nextInt(1);
				if (rand <= 0) {
					encounter.enemyPokemon.takeDamage(t.damageCalclulation(encounter.playerPokemon, encounter.enemyPokemon));
					if (t.mod > 1) {
						Pokemon.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(Pokemon.this,
										"Super Effective!", Toast.LENGTH_SHORT)
										.show();
							}
						});
						if (soundEnabled && loadedSE) damageHighSoundPool.play(damageHighSound, volume, volume, 1, 0, 1f);
					}
					else if (t.mod < 1) {
						Pokemon.this.runOnUiThread(new Runnable() {
							public void run() {
								Toast.makeText(Pokemon.this,
										"Not very Effective!",
										Toast.LENGTH_SHORT).show();
							}
						});
						if (soundEnabled && loadedSE) damageLowSoundPool.play(damageLowSound, volume, volume, 1, 0, 1f);
					}
					else {
						if (soundEnabled && loadedSE) damageSoundPool.play(damageSound, volume, volume, 1, 0, 1f);
					}
					Log.v("BATTLE", encounter.enemyPokemon.getName() + "'s Current HP: " + encounter.enemyPokemon.getCurrentHP());
				}
				else {
					Log.v("BATTLE", "" + encounter.playerPokemon.getName() + " is paralyzed. It can't move.");
				}
			}
			else {
				encounter.enemyPokemon.takeDamage(t.damageCalclulation(encounter.playerPokemon, encounter.enemyPokemon));
				if (t.mod > 1) {
					Pokemon.this.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(Pokemon.this, "Super Effective!",
									Toast.LENGTH_SHORT).show();
						}
					});
					if (soundEnabled && loadedSE) damageHighSoundPool.play(damageHighSound, volume, volume, 1, 0, 1f);
				}
				else if (t.mod < 1) {
					Pokemon.this.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(Pokemon.this, "Not very Effective!",
									Toast.LENGTH_SHORT).show();
						}
					});
					if (soundEnabled && loadedSE) damageLowSoundPool.play(damageLowSound, volume, volume, 1, 0, 1f);
				}
				else {
					if (soundEnabled && loadedSE) damageSoundPool.play(damageSound, volume, volume, 1, 0, 1f);
				}
				Log.v("BATTLE", encounter.enemyPokemon.getName() + "'s Current HP: " + encounter.enemyPokemon.getCurrentHP());
			}
			
			if (encounter.playerPokemon.getStatusEffect() == 1) {
				encounter.playerPokemon.cur_HP -= (encounter.playerPokemon.hp*(1/8));
				Log.v("BATTLE", "" + encounter.playerPokemon.getName() + " has been hurt by its burn");
			}
			else if (encounter.playerPokemon.getStatusEffect() == 3) {
				encounter.playerPokemon.cur_HP -= (encounter.playerPokemon.hp*(1/8));
				Log.v("BATTLE", "" + encounter.playerPokemon.getName() + " has been hurt by its poison");
			}
			encounter.playerTurn = false;
			encounter.inMain = true;
			encounter.inFight = false;
			encounter.currentSelectionMainX = 0;
			encounter.currentSelectionMainY = 0;
			encounter.currentSelectionFightX = 0;
			encounter.currentSelectionFightY = 0;
			encounter.currentSelectionItemX = 0;
			encounter.currentSelectionItemY = 0;
			encounter.currentSelectionPartyX = 0;
			encounter.currentSelectionPartyY = 0;
		}
		else {
			Log.v("BATTLE", "" + "Can't Attack");
		}
	}

	/**
	 * Updates the current State of the Battle Scene
	 */
	private void battleUpdate() {
		pokemonparty[0] = encounter.playerPokemon;
		Battle_playerPokemonName.setText("" + encounter.playerPokemon.getName());
		Battle_playerPokemonLevel.setText("" + encounter.playerPokemon.getLevel());
		Battle_playerPokemonCurrentHP.setText("" + encounter.playerPokemon.getCurrentHP());
		Battle_playerPokemonHP.setText("" + encounter.playerPokemon.getHP());
		Battle_playerPokemonMove1.setText("" + encounter.playerPokemon.move1);
		Battle_playerPokemonMove2.setText("" + encounter.playerPokemon.move2);
		Battle_playerPokemonMove3.setText("" + encounter.playerPokemon.move3);
		Battle_playerPokemonMove4.setText("" + encounter.playerPokemon.move4);
		Battle_playerPokemonName.move(396 - Battle_playerPokemonName.getWidth() - 8, 159);
		Battle_playerPokemonName.reload(true);
		Battle_playerPokemonLevel.reload(true);
		Battle_playerPokemonCurrentHP.reload(true);
		Battle_playerPokemonHP.reload(true);
		Battle_playerPokemonMove1.reload(true);
		Battle_playerPokemonMove2.reload(true);
		Battle_playerPokemonMove3.reload(true);
		Battle_playerPokemonMove4.reload(true);
		Battle_pkmn1.setText(pokemonparty[0].getName() + "    Lvl: " + pokemonparty[0].getLevel() +
				"    HP: " + pokemonparty[0].cur_HP + " / " + encounter.playerPokemon.hp);
		Battle_pkmn2.setText(pokemonparty[1].getName() + "    Lvl: " + pokemonparty[1].getLevel() +
				"    HP: " + pokemonparty[1].cur_HP + " / " + pokemonparty[1].hp);
		Battle_pkmn3.setText(pokemonparty[2].getName() + "    Lvl: " + pokemonparty[2].getLevel() +
				"    HP: " + pokemonparty[2].cur_HP + " / " + pokemonparty[2].hp);
		Battle_pkmn4.setText(pokemonparty[3].getName() + "    Lvl: " + pokemonparty[3].getLevel() +
				"    HP: " + pokemonparty[3].cur_HP + " / " + pokemonparty[3].hp);
		Battle_pkmn5.setText(pokemonparty[4].getName() + "    Lvl: " + pokemonparty[4].getLevel() +
				"    HP: " + pokemonparty[4].cur_HP + " / " + pokemonparty[4].hp);
		Battle_pkmn6.setText(pokemonparty[5].getName() + "    Lvl: " + pokemonparty[5].getLevel() +
				"    HP: " + pokemonparty[5].cur_HP + " / " + pokemonparty[5].hp);
		Battle_pkmn1.reload(true);
		Battle_pkmn2.reload(true);
		Battle_pkmn3.reload(true);
		Battle_pkmn4.reload(true);
		Battle_pkmn5.reload(true);
		Battle_pkmn6.reload(true);
		
		//Update HP Totals
		Battle_playerPokemonCurrentHP.setText("" + encounter.playerPokemon.cur_HP);
		Battle_enemyPokemonCurrentHP.setText("" + encounter.enemyPokemon.cur_HP);
		
		// Status Effect Icons
		if (encounter.playerPokemon.getStatusEffect() == 1) {
			Battle_statusPAR.show();
			Battle_statusPAR.move(415, 140);
		}
		else if (encounter.playerPokemon.getStatusEffect() == 2) {
			Battle_statusBRN.show();
			Battle_statusBRN.move(415, 140);
		}
		else if (encounter.playerPokemon.getStatusEffect() == 3) {
			Battle_statusPSN.show();
			Battle_statusPSN.move(415, 140);
		}
		else if (encounter.playerPokemon.getStatusEffect() == 4) {
			Battle_statusSLP.show();
			Battle_statusSLP.move(415, 140);
		}
		else if (encounter.playerPokemon.getStatusEffect() == 5) {
			Battle_statusFRZ.show();
			Battle_statusFRZ.move(415, 140);
		}
		if (encounter.enemyPokemon.getStatusEffect() == 1) {
			Battle_statusPAR.show();
			Battle_statusPAR.move(18, 60);
		}
		else if (encounter.enemyPokemon.getStatusEffect() == 2) {
			Battle_statusBRN.show();
			Battle_statusBRN.move(18, 60);
		}
		else if (encounter.enemyPokemon.getStatusEffect() == 3) {
			Battle_statusPSN.show();
			Battle_statusPSN.move(18, 60);
		}
		else if (encounter.enemyPokemon.getStatusEffect() == 4) {
			Battle_statusSLP.show();
			Battle_statusSLP.move(18, 60);
		}
		else if (encounter.enemyPokemon.getStatusEffect() == 5) {
			Battle_statusFRZ.show();
			Battle_statusFRZ.move(18, 60);
		}

		// Battle Main Interface
		if (encounter.inMain == true) {
			Battle_playerPokemonBackSprite.show();
			Battle_playerPokemonBackSprite.move(48, 132);
			
			battleBG1.show();
			
			Battle_wildNameAppeared.move(16, 245);
			Battle_FIGHT.move(290, 245);
			Battle_PKMN.move(400, 245);
			Battle_ITEM.move(290, 275);
			Battle_RUN.move(400, 275);
			
			battleBG2.hide();
			battleBG3.hide();
			battleBG4.hide();
			
			Battle_playerPokemonMove1.hide();
			Battle_playerPokemonMove2.hide();
			Battle_playerPokemonMove3.hide();
			Battle_playerPokemonMove4.hide();
			
			Battle_pkmn1.hide();
			Battle_pkmn2.hide();
			Battle_pkmn3.hide();
			Battle_pkmn4.hide();
			Battle_pkmn5.hide();
			Battle_pkmn6.hide();
			Battle_SelectionCancel.hide();
			Battle_Switch.hide();
			
			pokemonIcon1.hide();
			pokemonIcon2.hide();
			pokemonIcon3.hide();
			pokemonIcon4.hide();
			pokemonIcon5.hide();
			pokemonIcon6.hide();
			
			Battle_Item1.hide();
			Battle_Item2.hide();
			Battle_Item3.hide();
			Battle_Item4.hide();
			
			Battle_FIGHT.show();
			Battle_PKMN.show();
			Battle_ITEM.show();
			Battle_RUN.show();
			
			if (encounter.playerPokemon.getCurrentHP() <= 0) {
				encounter.playerPokemon.cur_HP = 0;
				int numAlivePokemon = 0;
				for (int i = 0; i < pokemonparty.length; i++) {
					if (pokemonparty[i].cur_HP >= 0 && !pokemonparty[i].equals(monsters[0])) {
						numAlivePokemon++;
					}
				}
				encounter.Pokemon();
				arrow.move(2, 4);
				if (numAlivePokemon <= 0) {
					teleportToLastPC();
					battleEnd();
				}
				battleUpdate();
			}
		}

		// Battle Fight Interface
		if (encounter.inFight == true) {
			Battle_wildNameAppeared.hide();
			Battle_FIGHT.hide();
			Battle_PKMN.hide();
			Battle_ITEM.hide();
			Battle_RUN.hide();
			
			battleBG2.show();
			Battle_playerPokemonBackSprite.show();
			Battle_playerPokemonMove1.show();
			Battle_playerPokemonMove2.show();
			Battle_playerPokemonMove3.show();
			Battle_playerPokemonMove4.show();
			
			Battle_SelectAMove.move(30, 260 - 15);
			Battle_playerPokemonMove1.move(200, 245);
			Battle_playerPokemonMove2.move(345, 245);
			Battle_playerPokemonMove3.move(200, 275);
			Battle_playerPokemonMove4.move(345, 275);
		}
		
		// Item Interface
		if (encounter.inItem == true) {
			Battle_wildNameAppeared.hide();
			Battle_FIGHT.hide();
			Battle_PKMN.hide();
			Battle_ITEM.hide();
			Battle_RUN.hide();
			
			Battle_Item1.move(256, 96);
			Battle_Item2.move(256, 128);
			Battle_Item3.move(256, 160);
			Battle_Item4.move(256, 192);
					
			battleBG3.show();
			Battle_playerPokemonBackSprite.show();
			if (items[battleItemIndexSlot].numberOfItem() != 0) Battle_Item1.show();
			if (items[battleItemIndexSlot+1].numberOfItem() != 0) Battle_Item2.show();
			if (items[battleItemIndexSlot+2].numberOfItem() != 0) Battle_Item3.show();
			if (items[battleItemIndexSlot+3].numberOfItem() != 0) Battle_Item4.show();
		}
		
		// Pokemon Party Interface
		if (encounter.inPokemon == true) {
			Battle_playerPokemonBackSprite.hide();
			Battle_wildNameAppeared.hide();
			Battle_FIGHT.hide();
			Battle_PKMN.hide();
			Battle_ITEM.hide();
			Battle_RUN.hide();
							
			battleBG4.show();
			Battle_pkmn1.show();
			Battle_pkmn2.show();
			Battle_pkmn3.show();
			Battle_pkmn4.show();
			Battle_pkmn5.show();
			Battle_pkmn6.show();
			
			if (!pokemonparty[0].equals(monsters[0])) pokemonIcon1.show();
			if (!pokemonparty[1].equals(monsters[0])) pokemonIcon2.show();
			if (!pokemonparty[2].equals(monsters[0])) pokemonIcon3.show();
			if (!pokemonparty[3].equals(monsters[0])) pokemonIcon4.show();
			if (!pokemonparty[4].equals(monsters[0])) pokemonIcon5.show();
			if (!pokemonparty[5].equals(monsters[0])) pokemonIcon6.show();
			
			Battle_pkmn1.move(52, 6);
			Battle_pkmn2.move(52, 38);
			Battle_pkmn3.move(52, 70);
			Battle_pkmn4.move(52, 102);
			Battle_pkmn5.move(52, 134);
			Battle_pkmn6.move(52, 166);
		}
		
		if (encounter.inPokemonSubMenu == true) {
			battleBG4.show();
			Battle_pkmn1.show();
			Battle_pkmn2.show();
			Battle_pkmn3.show();
			Battle_pkmn4.show();
			Battle_pkmn5.show();
			Battle_pkmn6.show();
			
			Battle_SelectionCancel.show();
			Battle_Switch.show();
			
			Battle_SelectionCancel.move(364, 132);
			Battle_Switch.move(364, 164);
		}
			
		//Enemy Pokemon has fainted, Player wins
		if (encounter.enemyPokemon.getCurrentHP() <= 0) {
			encounter.playerWon = true;
			Log.v("BATTLE", "Wild " + encounter.enemyPokemon.getName() + " has fainted");
			encounter.Win();
			battleEnd();
		}
			
		//Player turn is over, the enemy will take its turn and the Player will return to the main Battle Screen
		if (encounter.playerTurn == false) {
			synchronized(engine) {
				try {
					engine.wait(1000);
				}
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			battleBG2.hide();
				
			Battle_SelectAMove.hide();
			Battle_playerPokemonMove1.hide();
			Battle_playerPokemonMove2.hide();
			Battle_playerPokemonMove3.hide();
			Battle_playerPokemonMove4.hide();

			Battle_pkmn1.hide();
			Battle_pkmn2.hide();
			Battle_pkmn3.hide();
			Battle_pkmn4.hide();
			Battle_pkmn5.hide();
			Battle_pkmn6.hide();

			battleBG1.show();
			Battle_FIGHT.show();
			Battle_PKMN.show();
			Battle_ITEM.show();
			Battle_RUN.show();

			if (encounter.enemyPokemon.cur_HP > 0) {
				int temp = encounter.playerPokemon.cur_HP;
				encounter.enemyTurn();
				if (soundEnabled && loadedSE && encounter.playerPokemon.cur_HP < temp) {
					damageSoundPool.play(damageSound, volume, volume, 1, 0, 1f);
				}
			}
			
			arrow.move(274, 240);
		}
		
	}
	
	/**
	 * Ends the current instance of the Battle Scene
	 */
	private void battleEnd() {
		//Level the Player's Pokemon if they received enough EXP and restore stats
		encounter.End();
		if (encounter.playerPokemon.cur_exp >= encounter.playerPokemon.exp) {
			int tempHP = encounter.playerPokemon.cur_HP;
			encounter.playerPokemon.levelUp();
			encounter.playerPokemon.cur_HP = tempHP;
			checkEvolution();
		}
		
		//Update the Players Pokemon Party with the Encounters modifications
		pokemonparty[0] = encounter.playerPokemon;
		
		//Remove Sprites from the battleLayer
		battleLayer.remove(battleBG1);
		battleLayer.remove(battleBG2);
		
		battleLayer.remove(Battle_playerPokemonName);
		battleLayer.remove(Battle_playerPokemonLevel);
		battleLayer.remove(Battle_playerPokemonCurrentHP);
		battleLayer.remove(Battle_playerPokemonHP);
		battleLayer.remove(Battle_enemyPokemonName);
		battleLayer.remove(Battle_enemyPokemonLevel);
		battleLayer.remove(Battle_enemyPokemonCurrentHP);
		battleLayer.remove(Battle_enemyPokemonHP);
		battleLayer.remove(Battle_wildNameAppeared);
		battleLayer.remove(Battle_playerPokemonBackSprite);
		battleLayer.remove(Battle_enemyPokemonFrontSprite);
		
		battleLayer.remove(Battle_statusBRN);
		battleLayer.remove(Battle_statusFRZ);
		battleLayer.remove(Battle_statusPAR);
		battleLayer.remove(Battle_statusPSN);
		battleLayer.remove(Battle_statusSLP);
		
		battleLayer.remove(battleBG3);
		battleLayer.remove(battleBG4);
		
		battleLayer.remove(Battle_FIGHT);
		battleLayer.remove(Battle_PKMN);
		battleLayer.remove(Battle_ITEM);
		battleLayer.remove(Battle_RUN);
		
		battleLayer.remove(arrow);
		
		battleLayer.remove(Battle_SelectAMove);
		battleLayer.remove(Battle_playerPokemonMove1);
		battleLayer.remove(Battle_playerPokemonMove2);
		battleLayer.remove(Battle_playerPokemonMove3);
		battleLayer.remove(Battle_playerPokemonMove4);
		
		battleLayer.remove(Battle_Item1);
		battleLayer.remove(Battle_Item2);
		battleLayer.remove(Battle_Item3);
		battleLayer.remove(Battle_Item4);
		
		battleLayer.remove(pokemonIcon1);
		battleLayer.remove(pokemonIcon2);
		battleLayer.remove(pokemonIcon3);
		battleLayer.remove(pokemonIcon4);
		battleLayer.remove(pokemonIcon5);
		battleLayer.remove(pokemonIcon6);
		
		//Exit the Battle and allow the Player to move
		encounter.inMain = false;
		encounter.inFight = false;
		encounter.inPokemon = false;
		encounter.inItem = false;
		encounter.inRun = false;
		
		inBattle = false;
		movable = true;
		
		//Return the Controls to their original Alpha Level
		dpad.setAlpha(alphaLevel);
		abbuttons.setAlpha(alphaLevel);
		ssbuttons.setAlpha(alphaLevel);
		
		//Switch back to the current Map's Overworld BGM
		if (soundEnabled) updateBGM();
		
		//Remove the battleLayer from the scene
		scene.resetNamedLayer();
		scene.removeLayer(battleLayer);
		
		//Set the players correct facing direction and freeze the Player's fram
		if (previousDirection == 1) player.setTile(0, 1); //Left Stationary Frame
		if (previousDirection == 2) player.setTile(0, 2); //Right Stationary Frame
		if (previousDirection == 3) player.setTile(0, 3); //Up Stationary Frame
		if (previousDirection == 4) player.setTile(0, 0); //Down Stationary Frame
		player.stop(); //Freeze Player Animation Frame (Not Walking)
		
		updatePokedexCompletion();
		
		//Re-enable Debug info
		if (DEBUG) coords.show();
	}
	
	/**
	 * Checks to see if a Pokemon in the Player's party reached a level
	 * to evolve into its next stage. If it did, that Pokemon will evolve
	 * into the next evolution stage.
	 */
	public void checkEvolution() {
		if (!pokemonparty[0].evolutions[0].equals("")) {
			for (int i = 0; i < pokemonparty[0].evolutions.length; i+=3) {
				if (pokemonparty[0].evolutions[i+1].equals("Level") && Integer.parseInt(pokemonparty[0].evolutions[i+2]) == pokemonparty[0].level) {
					Monsters evo = new Monsters();
					for (int j = 0; j < monsters.length; j++) {
						if (monsters[j].getName().equals(pokemonparty[0].evolutions[i])) {
							evo = monsters[j];
						}
					}
					evo.create(pokemonparty[0].level);
					Log.v("BATTLE", pokemonparty[0].name + " evolved into " + evo.getName());
					pokemonparty[0] = evo;
					encounter.playerPokemon = pokemonparty[0];
					break;
				}
			}
		}
	}
	
	/**
	 * Handles the switching of Pokemon in battle, whether it was called
	 * from the user or because the current Player's Pokemon has fainted.
	 */
	private void switchPokemonEncounter() {
		if (pokemonparty[encounter.currentSelectionPartyY].cur_HP > 0) {
			//DEEP COPY
			Monsters temp = (Monsters) encounter.playerPokemon.clone(encounter.playerPokemon);
			pokemonparty[0] = (Monsters) pokemonparty[encounter.currentSelectionPartyY].clone(pokemonparty[encounter.currentSelectionPartyY]);
			pokemonparty[encounter.currentSelectionPartyY] = (Monsters) temp.clone(temp);
			encounter.playerPokemon = pokemonparty[0];
			
			Log.v("BATTLE", pokemonparty[encounter.currentSelectionPartyY].getName() + " has been switched with " + encounter.playerPokemon.getName());
			encounter.currentSelectionMainX = 0;
			encounter.currentSelectionMainY = 0;
			encounter.currentSelectionFightX = 0;
			encounter.currentSelectionFightY = 0;
			encounter.currentSelectionPartyX = 0;
			encounter.currentSelectionPartyY = 0;
			encounter.currentSelectionItemX = 0;
			encounter.currentSelectionItemY = 0;
			encounter.inPokemonSubMenu = false;
			
			battleLayer.remove(pokemonIcon1);
			battleLayer.remove(pokemonIcon2);
			battleLayer.remove(pokemonIcon3);
			battleLayer.remove(pokemonIcon4);
			battleLayer.remove(pokemonIcon5);
			battleLayer.remove(pokemonIcon6);
			
			pokemonIcon1 = new AnimatedSprite(new TiledTexture(pokemonparty[0].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 4);
			pokemonIcon2 = new AnimatedSprite(new TiledTexture(pokemonparty[1].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 34);
			pokemonIcon3 = new AnimatedSprite(new TiledTexture(pokemonparty[2].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 66);
			pokemonIcon4 = new AnimatedSprite(new TiledTexture(pokemonparty[3].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 98);
			pokemonIcon5 = new AnimatedSprite(new TiledTexture(pokemonparty[4].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 130);
			pokemonIcon6 = new AnimatedSprite(new TiledTexture(pokemonparty[5].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 162);
			
			battleLayer.add(pokemonIcon1);
			battleLayer.add(pokemonIcon2);
			battleLayer.add(pokemonIcon3);
			battleLayer.add(pokemonIcon4);
			battleLayer.add(pokemonIcon5);
			battleLayer.add(pokemonIcon6);
			
			pokemonIcon1.animate(200, pkIcons); 
			pokemonIcon2.animate(200, pkIcons);
			pokemonIcon3.animate(200, pkIcons);
			pokemonIcon4.animate(200, pkIcons);
			pokemonIcon5.animate(200, pkIcons);
			pokemonIcon6.animate(200, pkIcons);
			
			battleLayer.remove(Battle_playerPokemonBackSprite);
			Battle_playerPokemonBackSprite = new Sprite(new AssetTexture(encounter.playerPokemon.getBackSprite(), 96, 96, this));
			battleLayer.add(Battle_playerPokemonBackSprite);
			battleUpdate();
			
			encounter.playerTurn = false;
			encounter.inMain = true;
			arrow.move(274, 240);
		}
		else {
			Log.v("BATTLE", "No will left to fight!");
		}
	}
	
	private void switchPokemonMenu() {
		if (pokemonparty[menu.currentSelectionPartyY].cur_HP > 0) {
			//DEEP COPY
			Monsters temp = (Monsters) menu.playerPokemon.clone(menu.playerPokemon);
			pokemonparty[0] = (Monsters) pokemonparty[menu.currentSelectionPartyY].clone(pokemonparty[menu.currentSelectionPartyY]);
			pokemonparty[menu.currentSelectionPartyY] = (Monsters) temp.clone(temp);
			menu.playerPokemon = pokemonparty[0];
			
			menu.currentSelectionPartyX = 0;
			menu.currentSelectionPartyY = 0;
			menu.currentSelectionItemX = 0;
			menu.currentSelectionItemY = 0;
			menu.inPokemonSubMenu = false;
			
			menuLayer.remove(pokemonIcon1);
			menuLayer.remove(pokemonIcon2);
			menuLayer.remove(pokemonIcon3);
			menuLayer.remove(pokemonIcon4);
			menuLayer.remove(pokemonIcon5);
			menuLayer.remove(pokemonIcon6);
			
			pokemonIcon1 = new AnimatedSprite(new TiledTexture(pokemonparty[0].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 4);
			pokemonIcon2 = new AnimatedSprite(new TiledTexture(pokemonparty[1].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 34);
			pokemonIcon3 = new AnimatedSprite(new TiledTexture(pokemonparty[2].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 66);
			pokemonIcon4 = new AnimatedSprite(new TiledTexture(pokemonparty[3].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 98);
			pokemonIcon5 = new AnimatedSprite(new TiledTexture(pokemonparty[4].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 130);
			pokemonIcon6 = new AnimatedSprite(new TiledTexture(pokemonparty[5].getPartyIcon(), 32, 32, 0, 0, 0, 0, this), 18, 162);
			
			menuLayer.add(pokemonIcon1);
			menuLayer.add(pokemonIcon2);
			menuLayer.add(pokemonIcon3);
			menuLayer.add(pokemonIcon4);
			menuLayer.add(pokemonIcon5);
			menuLayer.add(pokemonIcon6);
			
			pokemonIcon1.animate(200, pkIcons); 
			pokemonIcon2.animate(200, pkIcons);
			pokemonIcon3.animate(200, pkIcons);
			pokemonIcon4.animate(200, pkIcons);
			pokemonIcon5.animate(200, pkIcons);
			pokemonIcon6.animate(200, pkIcons);
			
			inGameMenuEnd();
			arrow.move(274, 240);
		}
		else {
			Log.v("BATTLE", "No will left to fight!");
		}
	}

	/**
	 * Handles loading a game or starting a new game.
	 */
	private void startGame(boolean continued) {
		if (continued)
			try {
				loadGame();
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		else newGame();
	}

	/**
	 * Called when a new Game was started. Initializes all important
	 * player variables such as Name, Money, starting Pokemon Party, etc.
	 */
	private void newGame() {
		if (isMale) playerName = "GOLD";
		else playerName = "KRIS";
		playerPokemon1 = new Monsters();
		playerPokemon1 = monsters[158]; //TOTODILE LEVEL 5
		playerPokemon1.create(5);
		//playerPokemon1.cur_HP = 0;
		playerPokemon2 = new Monsters();
		playerPokemon2 = monsters[155]; //CYNDAQUIL LEVEL 5
		playerPokemon2.create(5);
		//playerPokemon2.cur_HP = 0;
		playerPokemon3 = new Monsters();
		playerPokemon3 = monsters[152]; //CHIKORITA LEVEL 5
		playerPokemon3.create(5);
		//playerPokemon3.cur_HP = 0;
		playerPokemon4 = new Monsters();
		playerPokemon4 = monsters[0];
		playerPokemon4.create(0);
		playerPokemon4.cur_HP = 0;
		playerPokemon5 = new Monsters();
		playerPokemon5 = monsters[0];
		playerPokemon5.create(0);
		playerPokemon5.cur_HP = 0;
		playerPokemon6 = new Monsters();
		playerPokemon6 = monsters[0];
		playerPokemon6.create(0);
		playerPokemon6.cur_HP = 0;
		pokemonparty[0] = playerPokemon1;
		pokemonparty[1] = playerPokemon2;
		pokemonparty[2] = playerPokemon3;
		pokemonparty[3] = playerPokemon4;
		pokemonparty[4] = playerPokemon5;
		pokemonparty[5] = playerPokemon6;
		for (int i = 0; i < pokemonPCStorage.length; i++) {
			pokemonPCStorage[i] = monsters[0];
			pokemonPCStorage[i].create(0);
			pokemonPCStorage[i].cur_HP = 0;
		}
		updatePokedexCompletion();
		money = 2000;
		items[0] = new Items(1, 99);
		items[1] = new Items(2, 99);
		for (int i = 2; i < items.length; i++) {
			items[i] = new Items(0, 0); //No Item
		}
		Random r = new Random();
		trainerID = r.nextInt(55535) + 10000;
	}

	/**
	 * Saves the current state of the game to the device to be continued
	 * at a later point in time.
	 * @throws ClassNotFoundException 
	 */
	private void saveGame() throws FileNotFoundException, ClassNotFoundException {
		String FILENAME = "test.sav";
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(openFileOutput(FILENAME, Context.MODE_PRIVATE));
			out.writeUTF(currentMapName);
			out.writeUTF(Integer.toString(tileX/32+7));
			out.writeUTF(Integer.toString(tileY/32+5));
			out.writeUTF(playerName);
			out.writeBoolean(isMale);
			out.writeUTF(Integer.toString(trainerID));
			out.writeUTF(Integer.toString(money));
			out.writeObject(pokemonparty);
			out.writeObject(pokemonPCStorage);
			out.writeObject(items);
			out.close();
			updatePokedexCompletion();
		} 
		catch (IOException e) {
			Log.v("IO", "Save File I/O Error");
		}
		
		try {
			ObjectInputStream in = new ObjectInputStream(openFileInput(FILENAME));
			try {
				Log.v("IO", "Save File: Map Name: " + in.readUTF());
				Log.v("IO", "Save File: X: " + in.readUTF());
				Log.v("IO", "Save File: Y: " + in.readUTF());
				Log.v("IO", "Save File: Name: " + in.readUTF());
				Log.v("IO", "Save File: isMale: " + in.readBoolean());
				Log.v("IO", "Save File: TrainerID: " + in.readUTF());
				Log.v("IO", "Save File: Money: " + in.readUTF());
				Log.v("IO", "Save File: PokemonParty: " + in.readObject());
				Log.v("IO", "Save File: PokemonPCStorage: " + in.readObject());
				Log.v("IO", "Save File: Items: " + in.readObject());
			}
			catch (EOFException e) {
				Log.v("IO", "End of Save File");
				in.close();
			}
		}
		catch (IOException e) {
			Log.v("IO", "Save File I/O Error");
		}
		
		if (soundEnabled && loadedSE) saveSoundPool.play(saveSound, volume, volume, 1, 0, 1f);
	}

	/**
	 * Loads the previous state of the game from the device to continue from
	 * where the game was left off.
	 * @throws ClassNotFoundException 
	 */
	private void loadGame() throws FileNotFoundException, ClassNotFoundException {
		String FILENAME = "test.sav";
		
		try {
			ObjectInputStream in = new ObjectInputStream(openFileInput(FILENAME));
			try {
				Log.v("IO", "Save File: Map Name: " + in.readUTF());
				Log.v("IO", "Save File: X: " + in.readUTF());
				Log.v("IO", "Save File: Y: " + in.readUTF());
				Log.v("IO", "Save File: Name: " + in.readUTF());
				Log.v("IO", "Save File: isMale: " + in.readBoolean());
				Log.v("IO", "Save File: TrainerID: " + in.readUTF());
				Log.v("IO", "Save File: Money: " + in.readUTF());
				Log.v("IO", "Save File: PokemonParty: " + in.readObject());
				Log.v("IO", "Save File: PokemonPCStorage: " + in.readObject());
				Log.v("IO", "Save File: Items: " + in.readObject());
			}
			catch (EOFException e) {
				Log.v("IO", "End of Save File");
				in.close();
			}
		}
		catch (IOException e) {
			Log.v("IO", "Save File I/O Error");
		}
		
		try {
			ObjectInputStream in = new ObjectInputStream(openFileInput(FILENAME));
			try {
				currentMapName = in.readUTF();
				previousMapName = currentMapName;
				loadEncounters();
				x_loc = Integer.parseInt(in.readUTF());
				y_loc = Integer.parseInt(in.readUTF());
				playerName = in.readUTF();
				isMale = in.readBoolean();
				trainerID = Integer.parseInt(in.readUTF());
				money= Integer.parseInt(in.readUTF());
				pokemonparty = (Monsters[]) in.readObject();
				//For whatever reason this next loop is required
				for (int i = 0; i < pokemonparty.length; i++) {
					if (pokemonparty[i].getName().equals("MISSINGNO")) {
						pokemonparty[i] = new Monsters();
						pokemonparty[i] = monsters[0];
						pokemonparty[i].create(0);
						pokemonparty[i].cur_HP = 0;
					}
				}
				pokemonPCStorage = (Monsters[]) in.readObject();
				//For whatever reason this next loop is required
				for (int i = 0; i < pokemonPCStorage.length; i++) {
					if (pokemonPCStorage[i].getName().equals("MISSINGNO")) {
						pokemonPCStorage[i] = new Monsters();
						pokemonPCStorage[i] = monsters[0];
						pokemonPCStorage[i].create(0);
						pokemonPCStorage[i].cur_HP = 0;
					}
				}
				items = (Items[]) in.readObject();
				if (isMale) texture = new TiledTexture("gold.png", 48, 64, 8, 7, 0, 0, this);
				else texture = new TiledTexture("kris.png", 48, 64, 8, 7, 0, 0, this);
			}
			catch (EOFException e) {
				Log.v("IO", "End of Save File");
				in.close();
			}
		}
		catch (IOException e) {
			Log.v("IO", "Save File I/O Error");
		}
	}
	
}