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
//BattleScene
//-----------------------------------------------------------------
// This class is an instance of a Wild Pokemon Encounter. It contains
// all variables related to the actual fighting, while the main Pokemon
// class handles all of the graphics rendering.
//-----------------------------------------------------------------

import java.util.*;

import android.util.Log;

public class BattleScene {
	
	private Pokemon game;

	private Random r = new Random();
	
	public boolean playerTurn;
	public int elapsedTurns;
	public boolean inMain = true;
	public boolean inFight = false;
	public boolean inItem = false;
	public boolean inPokemon = false;
	public boolean inPokemonSubMenu = false;
	public boolean inRun = false;
	public boolean playerWon = false;
	public boolean pokemonfainted = false;
	public boolean confirmBattleEnd = false;
	public int currentSelectionMainX = 0;
	public int currentSelectionMainY = 0;
	public int currentSelectionFightX = 0;
	public int currentSelectionFightY = 0;
	public int currentSelectionPartyX = 0;
	public int currentSelectionPartyY = 0;
	public int currentSelectionItemX = 0;
	public int currentSelectionItemY = 0;
	public Monsters playerPokemon;
	public Monsters enemyPokemon;
	public Items[] battleItems;

    public BattleScene(Pokemon pkmn, Monsters[] playerparty, Monsters wild, Items[] items) {
    	game = pkmn;
    	playerPokemon = playerparty[0];
    	enemyPokemon = wild;
    	battleItems = items;
    	playerTurn = true;
    	Start();
    }
    
    public void Start() {
    	Log.v("BATTLE", "Player's Pokemon: " + playerPokemon.getName() + 
        		" Level: " + playerPokemon.getLevel() +
        		" HP: " + playerPokemon.getCurrentHP() +
        		" / " + playerPokemon.getHP() +
        		" ATK: " + playerPokemon.cur_attack +
        		" / " + playerPokemon.attack +
        		" DEF: " + playerPokemon.cur_def +
        		" / " + playerPokemon.def);
    	Log.v("BATTLE", "Wild Pokemon: " + enemyPokemon.getName() + 
    		" Level: " + enemyPokemon.getLevel() +
    		" HP: " + enemyPokemon.getCurrentHP() +
    		" / " + enemyPokemon.getHP() +
    		" ATK: " + enemyPokemon.cur_attack +
    		" / " + enemyPokemon.attack +
    		" DEF: " + enemyPokemon.cur_def +
    		" / " + enemyPokemon.def);
    	currentSelectionMainX = 0;
    	currentSelectionFightX = 0;
    	currentSelectionMainY = 0;
    	currentSelectionFightY = 0;
    	inMain = true;
    }
    
    public void Fight() {
    	inMain = false;
    	inFight = true;
    	Log.v("BATTLE", "FIGHT");
    }
    
    public void Item() {
    	inMain = false;
    	inItem = true;
    	Log.v("BATTLE", "ITEM");
    }
    
    public void Pokemon() {
    	inMain = false;
    	inPokemon = true;
    	Log.v("BATTLE", "POKEMON");
    }
    
    public void giveEXP() {
    	playerPokemon.cur_exp += (enemyPokemon.exp * enemyPokemon.getLevel() / (7)) + 35;
    	//playerPokemon.cur_exp += 200;
    	Log.v("BATTLE", "Current EXP: " + playerPokemon.cur_exp + " / " + playerPokemon.exp);
    }
    
    public void Run() {
    	int chance = r.nextInt(255);
    	if (playerPokemon.cur_spd >= enemyPokemon.cur_spd || chance >= 120) {
	    	inMain = false;
	    	inRun = true;
	    	enemyPokemon.setStatusEffect(0);
	    	Log.v("BATTLE", "Got away safely!");
    	}
    	else {
    		Log.v("BATTLE", "Can't Escape!");
    	}
    }
    
    public void Win() {
    	giveEXP();
    	inMain = false;
    	inRun = true;
    	enemyPokemon.setStatusEffect(0);
    }
    
    public void Lose() {
    	inMain = false;
    	inRun = true;
    	enemyPokemon.setStatusEffect(0);
    }
    
    public void End() {
    	//Reset Stats
    	playerPokemon.cur_attack = playerPokemon.attack;
    	playerPokemon.cur_spAttack = playerPokemon.spAttack;
    	playerPokemon.cur_def = playerPokemon.def;
    	playerPokemon.cur_spDef = playerPokemon.spDef;
    	playerPokemon.cur_spd = playerPokemon.spd;
    }
    
    public void whiteOut() {
    	pokemonfainted = true;
    	Lose();
    }
    
	public void enemyTurn() {
		if (playerWon == false) {
			int i = 0;
			if (enemyPokemon.getStatusEffect() == 4 || enemyPokemon.getStatusEffect() == 5) {
				Random rr = new Random();
				int wakeupthaw = rr.nextInt(5);
				if (wakeupthaw <= 1) {
				    if (enemyPokemon.getStatusEffect() == 4) {
				    	Log.v("BATTLE", enemyPokemon.getName() + " has woken up.");
				    }
				    if (enemyPokemon.getStatusEffect() == 5) {
				    	Log.v("BATTLE", enemyPokemon.getName() + " has broken free from the ice.");
				    }
				    enemyPokemon.setStatusEffect(0);
				}
				else {
					if (enemyPokemon.getStatusEffect() == 4) {
						Log.v("BATTLE", enemyPokemon.getName() + " is still asleep."); 
				    }
				    if (enemyPokemon.getStatusEffect() == 5) {
				    	Log.v("BATTLE", enemyPokemon.getName() + " is frozen solid.");
				    }
				}
			}
			else if (enemyPokemon.getStatusEffect() != 4 || enemyPokemon.getStatusEffect() != 5) {
				i = r.nextInt(4) + 1;
				if (enemyPokemon.getStatusEffect() == 1) {
					Random r = new Random();
					int rand = r.nextInt(2);
					if (rand <= 0) {
						if (i == 1 && !enemyPokemon.move1.equals("")) {
							playerPokemon.takeDamage(new Attacks(enemyPokemon.move1).damageCalclulation(enemyPokemon, playerPokemon));
							Log.v("BATTLE", "" + "Enemy chose move " + i + " - Name: " + enemyPokemon.move1);
						}
						else if (i == 2 && !enemyPokemon.move2.equals("")) {
							playerPokemon.takeDamage(new Attacks(enemyPokemon.move2).damageCalclulation(enemyPokemon, playerPokemon));
							Log.v("BATTLE", "" + "Enemy chose move " + i + " - Name: " + enemyPokemon.move2);
						}
						else if (i == 3 && !enemyPokemon.move3.equals("")) {
							playerPokemon.takeDamage(new Attacks(enemyPokemon.move3).damageCalclulation(enemyPokemon, playerPokemon));
							Log.v("BATTLE", "" + "Enemy chose move " + i + " - Name: " + enemyPokemon.move3);
						}
						else if (i == 4 && !enemyPokemon.move4.equals("")) {
							playerPokemon.takeDamage(new Attacks(enemyPokemon.move4).damageCalclulation(enemyPokemon, playerPokemon));
							Log.v("BATTLE", "" + "Enemy chose move " + i + " - Name: " + enemyPokemon.move4);
						}
						else {
							playerPokemon.takeDamage(new Attacks(enemyPokemon.move1).damageCalclulation(enemyPokemon, playerPokemon));
							Log.v("BATTLE", "" + "Enemy chose move " + i + ", but it didn't exist so move 1 - Name: " + enemyPokemon.move1);
						}
						Log.v("BATTLE", "Enemy's turn is over");
					}
					else {
						Log.v("BATTLE", enemyPokemon.getName() + " is paralyzed. It can't move.");
					}
				}
				else {
					if (i == 1 && !enemyPokemon.move1.equals("")) {
						playerPokemon.takeDamage(new Attacks(enemyPokemon.move1).damageCalclulation(enemyPokemon, playerPokemon));
						Log.v("BATTLE", "" + "Enemy chose move " + i + " - Name: " + enemyPokemon.move1);
					}
					else if (i == 2 && !enemyPokemon.move2.equals("")) {
						playerPokemon.takeDamage(new Attacks(enemyPokemon.move2).damageCalclulation(enemyPokemon, playerPokemon));
						Log.v("BATTLE", "" + "Enemy chose move " + i + " - Name: " + enemyPokemon.move2);
					}
					else if (i == 3 && !enemyPokemon.move3.equals("")) {
						playerPokemon.takeDamage(new Attacks(enemyPokemon.move3).damageCalclulation(enemyPokemon, playerPokemon));
						Log.v("BATTLE", "" + "Enemy chose move " + i + " - Name: " + enemyPokemon.move3);
					}
					else if (i == 4 && !enemyPokemon.move4.equals("")) {
						playerPokemon.takeDamage(new Attacks(enemyPokemon.move4).damageCalclulation(enemyPokemon, playerPokemon));
						Log.v("BATTLE", "" + "Enemy chose move " + i + " - Name: " + enemyPokemon.move4);
					}
					else {
						playerPokemon.takeDamage(new Attacks(enemyPokemon.move1).damageCalclulation(enemyPokemon, playerPokemon));
						Log.v("BATTLE", "" + "Enemy chose move " + i + ", but it didn't exist so move 1 - Name: " + enemyPokemon.move1);
					}
					Log.v("BATTLE", "Enemy's turn is over");
				}
				if (enemyPokemon.getStatusEffect() == 2) {
					enemyPokemon.cur_HP -= 2;
					Log.v("BATTLE", enemyPokemon.getName() + " has been hurt by its burn");
				}
				if (enemyPokemon.getStatusEffect() == 3) {
					enemyPokemon.cur_HP -= 2;
					Log.v("BATTLE", enemyPokemon.getName() + " has been hurt by its poison");
				}
				
			}
			playerTurn = true;
		}
	}
}