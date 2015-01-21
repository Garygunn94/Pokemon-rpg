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
// MenuScene
//-----------------------------------------------------------------
// This class is an instance of the In Game Menu. It contains all
// variables related to the actual menu, while the main Pokemon
// class handles all of the graphics rendering.
//-----------------------------------------------------------------

import java.util.*;
import java.io.*;
import java.net.*;

import android.util.Log;

public class MenuScene {
	
	private Pokemon game;
	private Random r = new Random();
	
	public boolean inMain = false;
	public boolean inPokeDex = false;
	public boolean inPokemon = false;
	public boolean inPokemonSubMenu = false;
	public boolean inBag = false;
	public boolean inBagSelected = false;
	public boolean inBagPokemonSelection = false;
	public boolean inPokeGear = false;
	public boolean inTrainerCard = false;
	public boolean inSave = false;
	public boolean inOption = false;
	public boolean inExit = false;
	public int currentSelectionMain;
	public int currentSelectionPartyY;
	public int currentSelectionPartyX;
	public int currentSelectionItemX;
	public int currentSelectionItemY;
	public int currentSelectionItemPokemon;
	public int currentSelectionPokeGear;
	public int currentSelectionSave;
	public int currentSelectionOption;
	public Monsters playerPokemon;
	public Monsters enemyPokemon;
	public Items[] battleItems;
	public boolean cancelbutton = false;

    public MenuScene(Pokemon pkmn) {
    	game = pkmn;
    	Start();
    }
    
    public void Start() {
    	currentSelectionMain = 2;
		currentSelectionItemX = 0;
		currentSelectionItemY = 0;
		currentSelectionPokeGear = 0;
		currentSelectionSave = 0;
    	inMain = true;
    }
    
    public void PokeDex() {
    	inMain = false;
    	inPokeDex = true;
    }
    
    public void Pokemon() {
    	inMain = false;
    	inPokemon = true;
    	Log.v("MENU", "Pokemon");
    }
    
    public void Bag() {
    	inMain = false;
    	inBag = true;
    	Log.v("MENU", "Bag");
    }
    
    public void PokeGear() {
    	inMain = false;
    	inPokeGear = true;
    	Log.v("MENU", "PokeGear");
    }
    
    public void TrainerCard() {
    	inMain = false;
    	inTrainerCard = true;
    	Log.v("MENU", "Trainer Card");
    }
    
    public void Save() {
    	inMain = false;
    	inSave = true;
		Log.v("MENU", "Save");
    }
    
    public void Option() {
    	inMain = false;
    	inOption = true;
		Log.v("MENU", "Option");
    }
    
    public void Exit() {
    	currentSelectionMain = 2;
		currentSelectionItemX = 0;
		currentSelectionItemY = 0;
		currentSelectionSave = 0;
    	inMain = false;
    	game.inMenu = false;
		Log.v("MENU", "Exiting Menu");
    }
    
}