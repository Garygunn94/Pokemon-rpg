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
// Monsters
//-----------------------------------------------------------------
// This class is responsible for all the data on the individual Pokemon
// creatures that can be found in both Wild Encounters and Trainer Battles.
// Individual stats of each Pokemon are currently hardcoded but a load()
// method will be created later to parse through an xml file containing
// all Pokemon data.
//-----------------------------------------------------------------

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

public class Monsters implements Serializable {
	
	public String name;
	public int level, number, hp, attack, def, spAttack, spDef, spd, catchRate;
	public int base_hp, base_attack, base_def, base_spAttack, base_spDef, base_spd;
	public int cur_HP, cur_attack, cur_def, cur_spAttack, cur_spDef, cur_spd, cur_catchRate;
	public int ev_HP, ev_attack, ev_def, ev_spAttack, ev_spDef, ev_spd;
	public int iv_HP, iv_attack, iv_def, iv_spAttack, iv_spDef, iv_spd;
	public double base_exp, exp, cur_exp;
	public String back_sprite, front_sprite, back_sprite_s, front_sprite_s;
	public String party_icon;
	
	public String type1;
	public String type2;

	public String move1 = "";
	public String move2 = "";
	public String move3 = "";
	public String move4 = "";
	public String[] moveSet = new String[50];
	public String[] evolutions = new String[32];
	public int move1PP, move2PP, move3PP, move4PP;
	public int cur_move1PP, cur_move2PP, cur_move3PP, cur_move4PP;
	public int attack_damage;
	
	private enum statusEffects {
		BRN, FRZ, PAR, PSN, SLP, NORM
	}
	private statusEffects statusEffect = statusEffects.NORM;
	private int statusAilment = 0;
	
	private boolean shiny = false;
	
	public Monsters() {
		//Randomly generate Individual Values
		iv_HP = (int) (Math.random()*15);
		iv_attack  = (int) (Math.random()*15);
		iv_def  = (int) (Math.random()*15);
		iv_spAttack = (int) (Math.random()*15);
		iv_spDef  = (int) (Math.random()*15);
		iv_spd  = (int) (Math.random()*15);
		//Randomly generate Individual Values (TEMP)
		ev_HP = (int) (Math.random()*(level*1000));
		ev_attack = (int) (Math.random()*(level*1000));
		ev_def = (int) (Math.random()*(level*1000));
		ev_spAttack = (int) (Math.random()*(level*1000));
		ev_spDef = (int) (Math.random()*(level*1000));
		ev_spd = (int) (Math.random()*(level*1000));
	}
	
	public static Object clone(Serializable obj) {   
	    ObjectInputStream is = null; 
	    ObjectOutputStream os = null; 
	    try{ 
	      ByteArrayOutputStream bos = new 
	      ByteArrayOutputStream(); 
	      os = new ObjectOutputStream(bos);   
	      os.writeObject(obj);    
	      ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray()); 
	      is = new ObjectInputStream(bin);   
	      Object clone = is.readObject();    
	      return clone; 
	    }catch (Exception ex){ex.printStackTrace();} 
	    finally { 
	      try { 
	        if(os != null) os.close(); 
	        if(is != null) is.close(); 
	      }catch(Exception ex){} 
	    } 
	    return null; 
	  }
	
	public String getName() {
		return name;
	}
	
	public String getFrontSprite() {
		if (shiny) return front_sprite_s;
		return front_sprite;
    }
	
	public String getBackSprite() {
		if (shiny) return back_sprite_s;
		return back_sprite;
    }
	
	public String getPartyIcon() {
		return party_icon;
    }
	
	public int getLevel() {
		return level;
	}
	
	public int getHP() {
		return hp;
	}
	
	public String getType1() {
		return type1;
	}
	
	public String getType2() {
		return type2;
	}
	
	public void takeDamage(int damage) {
		cur_HP = cur_HP - damage;
	}
	
	public void healPokemon() {
		cur_HP = hp;
		cur_attack = attack;
		cur_def = def;
		cur_spAttack = spAttack;
		cur_spDef = spDef;
		cur_spd = spd;
		statusEffect = statusEffects.NORM;
	}
	
	public int getCurrentHP() {
		return cur_HP;
	}
	
	public double getEXP() {
		return cur_exp;
	}
	
	public void levelUp() {
		if (level < 100) {
			level += 1;
			Log.v("BATTLE", name + " has reached level " + level);
			//TEMP
			ev_HP += (int) (Math.random()*(level*300));
			ev_attack += (int) (Math.random()*(level*100));
			ev_def += (int) (Math.random()*(level*100));
			ev_spAttack += (int) (Math.random()*(level*100));
			ev_spDef += (int) (Math.random()*(level*100));
			ev_spd += (int) (Math.random()*(level*100));
			makeStats();
			checkNewMoves();
		}
		else {
			Log.v("BATTLE", name + " is already at max level.");
		}
	}
	
	public void checkNewMoves() {
		for (int i = 0; i < moveSet.length-1; i+=2) {
			if (Integer.parseInt(moveSet[i]) == level) {
				if (move3.equals("")) {
					if (!move1.equals(moveSet[i+1]) && !move2.equals(moveSet[i+1]) && !move3.equals(moveSet[i+1]) && !move4.equals(moveSet[i+1])) {
						move3 = moveSet[i+1];
					}
					else {
						Log.v("BATTLE", moveSet[i+1] + " is already known by " + name);
					}
				}
				else if (move4.equals("")) {
					if (!move1.equals(moveSet[i+1]) && !move2.equals(moveSet[i+1]) && !move3.equals(moveSet[i+1]) && !move4.equals(moveSet[i+1])) {
						move3 = moveSet[i+1];
					}
					else {
						Log.v("BATTLE", moveSet[i+1] + " is already known by " + name);
					}
				}
				else {
					Log.v("BATTLE", "No free move slots! Which move should be replaced to make room for " + moveSet[i+1] + "?");
				}
			}
		}
	}
	
	public double getLevelEXP() {
		return exp;
	}
	
	public int getNumber() {
		return number;
	}
	
	public int getStatusAilment() {
		if (statusEffect == statusEffects.FRZ || statusEffect == statusEffects.SLP) {
			return 25;
		}
		else if (statusEffect == statusEffects.PSN || statusEffect == statusEffects.BRN || statusEffect == statusEffects.PAR) {
			return 12;
		}
		return 0;
	}
	
	public int getStatusEffect() {
		if (statusEffect == statusEffects.FRZ) {
			return 5;
		}
		if (statusEffect == statusEffects.SLP) {
			return 4;
		}
		if (statusEffect == statusEffects.PSN) {
			return 2;
		}
		if (statusEffect == statusEffects.BRN) {
			return 3;
		}
		if (statusEffect == statusEffects.PAR) {
			return 1;
		}
		return 0;
	}
	
	public void setStatusEffect(int n) {
		if (n == 5) {
			statusEffect = statusEffects.FRZ;
		}
		else if (n == 4) {
			statusEffect = statusEffects.SLP;
		}
		else if (n == 3) {
			statusEffect = statusEffects.BRN;
		}
		else if (n == 2) {
			statusEffect = statusEffects.PSN;
		}
		else if (n == 1) {
			statusEffect = statusEffects.PAR;
		}
		else {
			statusEffect = statusEffects.NORM;
		}
	}
	
	public int capture(int ballMod) {
		int f = (hp * 255 / ballMod) / (cur_HP / 4);
		int p0 = getStatusAilment() / (ballMod + 1);
		int p1 = ((catchRate + 1)/(ballMod + 1)) * ((f + 1) / 256);
		return p0 + p1;
	}
	
	public void create(int low, int high) {
		//Randomly create a Pokemon between level low and level high
		Random r = new Random();
		int l = r.nextInt(high - low + 1) + low;
		Log.v("BATTLE", "Level Generated: " + l + " High: " + high + " Low: " + low);
		create(l);
	}
	
	public void create(int l) {
		level  = l;
		makeStats();
	}

	public void makeStats() {
		hp = (int) (((iv_HP + base_hp + (Math.sqrt(ev_HP)/8 + 50) * level)/50) + 10) + 6;
		attack = (int) (((iv_attack + base_attack + (Math.sqrt(ev_attack)/8) * level)/50) + 5);
		def = (int) (((iv_def + base_def + (Math.sqrt(ev_def)/8) * level)/50) + 5);
		spAttack = (int) (((iv_spAttack + base_spAttack + (Math.sqrt(ev_spAttack)/8) * level)/50) + 5);
		spDef = (int) (((iv_spDef + base_spDef + (Math.sqrt(ev_spDef)/8) * level)/50) + 5);
		spd = (int) (((iv_spd + base_spd + (Math.sqrt(ev_spd)/8) * level)/50) + 5);
		cur_HP = hp;
		cur_attack = attack;
		cur_def = def;
		cur_spAttack = spAttack;
		cur_spDef = spDef;
		cur_spd = spd;
		cur_catchRate = catchRate;
		exp = Math.pow(level,3);
		cur_exp = 0;
	}
	
	public double getWeaknessModifier(String attack, Monsters defender) {
		return getWeaknessType1(attack, defender) * getWeaknessType2(attack, defender);
	}
	
	public double getWeaknessType1(String attack, Monsters defender) {
		double weak = 1;
		if (attack.equals("NORMAL")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 1;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 1;
			if (defender.getType1().equals("ICE")) weak = 1;				
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 1;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 0;
			if (defender.getType1().equals("ROCK")) weak = 0.5;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("FIRE")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 0.5;
			if (defender.getType1().equals("WATER")) weak = 0.5;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 2;
			if (defender.getType1().equals("ICE")) weak = 2;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 1;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 2;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 0.5;
			if (defender.getType1().equals("DRAGON")) weak = 0.5;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 2;
		}
		if (attack.equals("WATER")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 2;
			if (defender.getType1().equals("WATER")) weak = 0.5;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 0.5;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 2;
			if (defender.getType1().equals("FLYING")) weak = 1;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 2;
			if (defender.getType1().equals("DRAGON")) weak = 0.5;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 1;
		}
		if (attack.equals("ELECTRIC")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 1;
			if (defender.getType1().equals("WATER")) weak = 2;
			if (defender.getType1().equals("ELECTRIC")) weak = 0.5;
			if (defender.getType1().equals("GRASS")) weak = 0.5;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 0;
			if (defender.getType1().equals("FLYING")) weak = 2;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 1;
			if (defender.getType1().equals("DRAGON")) weak = 0.5;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 1;
		}
		if (attack.equals("GRASS")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 0.5;
			if (defender.getType1().equals("WATER")) weak = 2;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 0.5;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 0.5;
			if (defender.getType1().equals("GROUND")) weak = 2;
			if (defender.getType1().equals("FLYING")) weak = 0.5;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 0.5;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 2;
			if (defender.getType1().equals("DRAGON")) weak = 0.5;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("ICE")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 0.5;
			if (defender.getType1().equals("WATER")) weak = 0.5;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 2;
			if (defender.getType1().equals("ICE")) weak = 0.5;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 2;
			if (defender.getType1().equals("FLYING")) weak = 2;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 1;
			if (defender.getType1().equals("DRAGON")) weak = 2;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 1;
		}
		if (attack.equals("FIGHTING")) {
			if (defender.getType1().equals("NORMAL")) weak = 2;
			if (defender.getType1().equals("FIRE")) weak = 1;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 1;
			if (defender.getType1().equals("ICE")) weak = 2;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 0.5;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 0.5;
			if (defender.getType1().equals("PSYCHIC")) weak = 0.5;
			if (defender.getType1().equals("BUG")) weak = 0.5;
			if (defender.getType1().equals("GHOST")) weak = 0;
			if (defender.getType1().equals("ROCK")) weak = 2;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 2;
			if (defender.getType1().equals("STEEL")) weak = 2;
		}
		if (attack.equals("POISON")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 1;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 2;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 0.5;
			if (defender.getType1().equals("GROUND")) weak = 0.5;
			if (defender.getType1().equals("FLYING")) weak = 1;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 0.5;
			if (defender.getType1().equals("ROCK")) weak = 0.5;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 0;
		}
		if (attack.equals("GROUND")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 2;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 2;
			if (defender.getType1().equals("GRASS")) weak = 0.5;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 2;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 0;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 0.5;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 2;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 2;
		}
		if (attack.equals("FLYING")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 1;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 0.5;
			if (defender.getType1().equals("GRASS")) weak = 2;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 2;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 1;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 2;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 0.5;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("PSYCHIC")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 1;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 1;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 2;
			if (defender.getType1().equals("POISON")) weak = 2;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 1;
			if (defender.getType1().equals("PSYCHIC")) weak = 0.5;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 1;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 0;
			if (defender.getType1().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("BUG")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 0.5;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 2;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 0.5;
			if (defender.getType1().equals("POISON")) weak = 0.5;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 0.5;
			if (defender.getType1().equals("PSYCHIC")) weak = 2;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 0.5;
			if (defender.getType1().equals("ROCK")) weak = 1;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 2;
			if (defender.getType1().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("ROCK")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 2;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 1;
			if (defender.getType1().equals("ICE")) weak = 2;
			if (defender.getType1().equals("FIGHTING")) weak = 0.5;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 0.5;
			if (defender.getType1().equals("FLYING")) weak = 2;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 2;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 2;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("GHOST")) {
			if (defender.getType1().equals("NORMAL")) weak = 0;
			if (defender.getType1().equals("FIRE")) weak = 1;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 1;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 1;
			if (defender.getType1().equals("PSYCHIC")) weak = 2;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 2;
			if (defender.getType1().equals("ROCK")) weak = 1;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 0.5;
			if (defender.getType1().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("DRAGON")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 1;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 1;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 1;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 1;
			if (defender.getType1().equals("DRAGON")) weak = 2;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("DARK")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 1;
			if (defender.getType1().equals("WATER")) weak = 1;
			if (defender.getType1().equals("ELECTRIC")) weak = 1;
			if (defender.getType1().equals("GRASS")) weak = 1;
			if (defender.getType1().equals("ICE")) weak = 1;
			if (defender.getType1().equals("FIGHTING")) weak = 0.5;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 1;
			if (defender.getType1().equals("PSYCHIC")) weak = 2;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 2;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 0.5;
			if (defender.getType1().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("STEEL")) {
			if (defender.getType1().equals("NORMAL")) weak = 1;
			if (defender.getType1().equals("FIRE")) weak = 0.5;
			if (defender.getType1().equals("WATER")) weak = 0.5;
			if (defender.getType1().equals("ELECTRIC")) weak = 0.5;
			if (defender.getType1().equals("GRASS")) weak = 1;
			if (defender.getType1().equals("ICE")) weak = 2;
			if (defender.getType1().equals("FIGHTING")) weak = 1;
			if (defender.getType1().equals("POISON")) weak = 1;
			if (defender.getType1().equals("GROUND")) weak = 1;
			if (defender.getType1().equals("FLYING")) weak = 1;
			if (defender.getType1().equals("PSYCHIC")) weak = 1;
			if (defender.getType1().equals("BUG")) weak = 1;
			if (defender.getType1().equals("GHOST")) weak = 1;
			if (defender.getType1().equals("ROCK")) weak = 2;
			if (defender.getType1().equals("DRAGON")) weak = 1;
			if (defender.getType1().equals("DARK")) weak = 1;
			if (defender.getType1().equals("STEEL")) weak = 0.5;
		}
		return weak;
	}
	
	public double getWeaknessType2(String attack, Monsters defender) {
		double weak = 1;
		if (attack.equals("NORMAL")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 1;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 1;
			if (defender.getType2().equals("ICE")) weak = 1;				
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 1;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 0;
			if (defender.getType2().equals("ROCK")) weak = 0.5;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("FIRE")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 0.5;
			if (defender.getType2().equals("WATER")) weak = 0.5;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 2;
			if (defender.getType2().equals("ICE")) weak = 2;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 1;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 2;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 0.5;
			if (defender.getType2().equals("DRAGON")) weak = 0.5;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 2;
		}
		if (attack.equals("WATER")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 2;
			if (defender.getType2().equals("WATER")) weak = 0.5;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 0.5;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 2;
			if (defender.getType2().equals("FLYING")) weak = 1;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 2;
			if (defender.getType2().equals("DRAGON")) weak = 0.5;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 1;
		}
		if (attack.equals("ELECTRIC")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 1;
			if (defender.getType2().equals("WATER")) weak = 2;
			if (defender.getType2().equals("ELECTRIC")) weak = 0.5;
			if (defender.getType2().equals("GRASS")) weak = 0.5;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 0;
			if (defender.getType2().equals("FLYING")) weak = 2;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 1;
			if (defender.getType2().equals("DRAGON")) weak = 0.5;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 1;
		}
		if (attack.equals("GRASS")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 0.5;
			if (defender.getType2().equals("WATER")) weak = 2;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 0.5;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 0.5;
			if (defender.getType2().equals("GROUND")) weak = 2;
			if (defender.getType2().equals("FLYING")) weak = 0.5;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 0.5;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 2;
			if (defender.getType2().equals("DRAGON")) weak = 0.5;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("ICE")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 0.5;
			if (defender.getType2().equals("WATER")) weak = 0.5;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 2;
			if (defender.getType2().equals("ICE")) weak = 0.5;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 2;
			if (defender.getType2().equals("FLYING")) weak = 2;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 1;
			if (defender.getType2().equals("DRAGON")) weak = 2;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 1;
		}
		if (attack.equals("FIGHTING")) {
			if (defender.getType2().equals("NORMAL")) weak = 2;
			if (defender.getType2().equals("FIRE")) weak = 1;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 1;
			if (defender.getType2().equals("ICE")) weak = 2;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 0.5;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 0.5;
			if (defender.getType2().equals("PSYCHIC")) weak = 0.5;
			if (defender.getType2().equals("BUG")) weak = 0.5;
			if (defender.getType2().equals("GHOST")) weak = 0;
			if (defender.getType2().equals("ROCK")) weak = 2;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 2;
			if (defender.getType2().equals("STEEL")) weak = 2;
		}
		if (attack.equals("POISON")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 1;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 2;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 0.5;
			if (defender.getType2().equals("GROUND")) weak = 0.5;
			if (defender.getType2().equals("FLYING")) weak = 1;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 0.5;
			if (defender.getType2().equals("ROCK")) weak = 0.5;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 0;
		}
		if (attack.equals("GROUND")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 2;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 2;
			if (defender.getType2().equals("GRASS")) weak = 0.5;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 2;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 0;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 0.5;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 2;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 2;
		}
		if (attack.equals("FLYING")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 1;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 0.5;
			if (defender.getType2().equals("GRASS")) weak = 2;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 2;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 1;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 2;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 0.5;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("PSYCHIC")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 1;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 1;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 2;
			if (defender.getType2().equals("POISON")) weak = 2;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 1;
			if (defender.getType2().equals("PSYCHIC")) weak = 0.5;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 1;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 0;
			if (defender.getType2().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("BUG")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 0.5;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 2;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 0.5;
			if (defender.getType2().equals("POISON")) weak = 0.5;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 0.5;
			if (defender.getType2().equals("PSYCHIC")) weak = 2;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 0.5;
			if (defender.getType2().equals("ROCK")) weak = 1;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 2;
			if (defender.getType2().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("ROCK")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 2;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 1;
			if (defender.getType2().equals("ICE")) weak = 2;
			if (defender.getType2().equals("FIGHTING")) weak = 0.5;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 0.5;
			if (defender.getType2().equals("FLYING")) weak = 2;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 2;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 2;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("GHOST")) {
			if (defender.getType2().equals("NORMAL")) weak = 0;
			if (defender.getType2().equals("FIRE")) weak = 1;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 1;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 1;
			if (defender.getType2().equals("PSYCHIC")) weak = 2;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 2;
			if (defender.getType2().equals("ROCK")) weak = 1;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 0.5;
			if (defender.getType2().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("DRAGON")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 1;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 1;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 1;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 1;
			if (defender.getType2().equals("DRAGON")) weak = 2;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("DARK")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 1;
			if (defender.getType2().equals("WATER")) weak = 1;
			if (defender.getType2().equals("ELECTRIC")) weak = 1;
			if (defender.getType2().equals("GRASS")) weak = 1;
			if (defender.getType2().equals("ICE")) weak = 1;
			if (defender.getType2().equals("FIGHTING")) weak = 0.5;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 1;
			if (defender.getType2().equals("PSYCHIC")) weak = 2;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 2;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 0.5;
			if (defender.getType2().equals("STEEL")) weak = 0.5;
		}
		if (attack.equals("STEEL")) {
			if (defender.getType2().equals("NORMAL")) weak = 1;
			if (defender.getType2().equals("FIRE")) weak = 0.5;
			if (defender.getType2().equals("WATER")) weak = 0.5;
			if (defender.getType2().equals("ELECTRIC")) weak = 0.5;
			if (defender.getType2().equals("GRASS")) weak = 1;
			if (defender.getType2().equals("ICE")) weak = 2;
			if (defender.getType2().equals("FIGHTING")) weak = 1;
			if (defender.getType2().equals("POISON")) weak = 1;
			if (defender.getType2().equals("GROUND")) weak = 1;
			if (defender.getType2().equals("FLYING")) weak = 1;
			if (defender.getType2().equals("PSYCHIC")) weak = 1;
			if (defender.getType2().equals("BUG")) weak = 1;
			if (defender.getType2().equals("GHOST")) weak = 1;
			if (defender.getType2().equals("ROCK")) weak = 2;
			if (defender.getType2().equals("DRAGON")) weak = 1;
			if (defender.getType2().equals("DARK")) weak = 1;
			if (defender.getType2().equals("STEEL")) weak = 0.5;
		}
		return weak;
	}

}