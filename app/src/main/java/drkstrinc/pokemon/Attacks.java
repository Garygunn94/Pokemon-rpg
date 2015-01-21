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
// Attacks
//-----------------------------------------------------------------
// This class contains data regarding each individual attack a Pokemon
// may learn, use, forget, etc. It is currently hardcoded but eventually
// a load() method will parse an xml file containing every Attack's
// data.
//-----------------------------------------------------------------

import java.util.Random;

import android.util.Log;
import android.widget.Toast;

public class Attacks {
	
	public String move = "WATERGUN";
	public String type;
	public String description;
	public int pp = 10;
	public int cur_pp;
	public int cur_damage;
	public String sprite;
	
	public double mod = 1;

    public Attacks(String name) {
    	move = name;
    	cur_pp = pp;
    }
    
    public int getCurrentDamage() {
    	return cur_damage;
    }
    
    public int getPP() {
    	return pp;
    }
    
    public int getCurrentPP() {
    	return cur_pp;
    }
    
    public String getType() {
    	return type;
    }
    
	public String getDescription() {
		return description;
	}
	
	public String getSprite() {
		return sprite;
    }
    
    public int damageCalclulation(Monsters attackingPokemon, Monsters defendingPokemon) {
    	//Hard Coded for now (IO based attacks are half implemented)
    	int power;
    	if (move.equals("GROWL")) {
    		description = "Reduces the foe's ATTACK.";
    		type = "NORMAL";
    		pp = 40;
    		if ((defendingPokemon.attack - defendingPokemon.cur_attack < 5) && defendingPokemon.cur_attack > 1) {
    			defendingPokemon.cur_attack -= 1;
    			Log.v("BATTLE", "" + defendingPokemon.getName() + "'s attack has been lowered to " + defendingPokemon.cur_attack + " / " + defendingPokemon.attack);
    		}
    		else {
    			Log.v("BATTLE", attackingPokemon.getName() + "'s " + move + " had no effect.");
    		}
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("TAILWHIP")) {
    		description = "Lowers the foe's DEFENSE.";
    		type = "NORMAL";
    		pp = 40;
    		if ((defendingPokemon.def - defendingPokemon.cur_def < 5) && defendingPokemon.cur_def > 1) {
    			defendingPokemon.cur_def -= 1;
    			Log.v("BATTLE", "" + defendingPokemon.getName() + "'s attack has been lowered to " + defendingPokemon.cur_def + " / " + defendingPokemon.def);
    		}
    		else {
    			Log.v("BATTLE", attackingPokemon.getName() + "'s " + move + " had no effect!");
    		}
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("LEER")) {
    		description = "Lowers the foe's DEFENSE.";
    		type = "NORMAL";
    		pp = 40;
    		if ((defendingPokemon.def - defendingPokemon.cur_def < 5) && defendingPokemon.cur_def > 1) {
    			defendingPokemon.cur_def -= 1;
    			Log.v("BATTLE", "" + defendingPokemon.getName() + "'s attack has been lowered to " + defendingPokemon.cur_def + " / " + defendingPokemon.def);
    		}
    		else {
    			Log.v("BATTLE", attackingPokemon.getName() + "'s " + move + " had no effect!");
    		}
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("DEFENSECURL")) {
    		description = "Increases DEFENSE.";
    		type = "NORMAL";
    		pp = 40;
    		attackingPokemon.cur_def += 1;
    		Log.v("BATTLE", attackingPokemon.getName() + "'s DEFENSE increased by 1!");
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("LIGHTSCREEN")) {
    		description = "Increases SPECIAL when hit by a SPECIAL ATTACK.";
    		type = "PSYCHIC";
    		pp = 30;
    		attackingPokemon.cur_spAttack += 1;
    		Log.v("BATTLE", attackingPokemon.getName() + "'s SPECIALATTACK increased by 1!");
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("HELPINGHAND")) {
    		description = "Increases ATTACK of an ally.";
    		type = "NORMAL";
    		pp = 20;
    		attackingPokemon.cur_attack += 1;
    		Log.v("BATTLE", attackingPokemon.getName() + "'s ATTACK increased by 1!");
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("ODORSLEUTH")) {
    		description = "Negates the foe’s efforts to heighten evasiveness.";
    		type = "NORMAL";
    		pp = 40;
    		Log.v("BATTLE", defendingPokemon.getName() + " has been identified!");
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("FORESIGHT")) {
    		description = "Negates the foe’s efforts to heighten evasiveness.";
    		type = "NORMAL";
    		pp = 40;
    		Log.v("BATTLE", defendingPokemon.getName() + " has been identified!");
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("SMOKESCREEN")) {
    		description = "Lowers the foe's accuracy.";
    		type = "NORMAL";
    		pp = 20;
    		Log.v("BATTLE", defendingPokemon.getName() + " had its Accuracy lowered!");
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("MUDSPORT")) {
    		description = "Covers the user in mud to raise electrical resistance.";
    		type = "GROUND";
    		pp = 15;
    		Log.v("BATTLE", "ELECTRIC has been weakened!");
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("THUNDERWAVE")) {
    		description = "A move that may cause paralysis.";
    		type = "ELECTRIC";
    		pp = 20;
    		if (defendingPokemon.getStatusEffect() == 1) {
    			Log.v("BATTLE", "" + "It had no effect!");
    		}
    		else {
    			defendingPokemon.setStatusEffect(1);
    			Log.v("BATTLE", "" + defendingPokemon.getName() + " has been paralyzed!");
    		}
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("TOXIC")) {
    		description = "A poison move with increasing damage.";
    		type = "POISON";
    		pp = 10;
    		if (defendingPokemon.getStatusEffect() == 3) {
    			Log.v("BATTLE", "" + "It had no effect!");
    		}
    		else {
    			defendingPokemon.setStatusEffect(3);
    			Log.v("BATTLE", "" + defendingPokemon.getName() + " has been posioned!");
    		}
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("SLEEPPOWDER")) {
    		description = "May cause the foe to fall asleep.";
    		type = "GRASS";
    		pp = 15;
    		if (defendingPokemon.getStatusEffect() == 4) {
    			Log.v("BATTLE", "" + "It had no effect!");
    		}
    		else {
    			defendingPokemon.setStatusEffect(4);
    			Log.v("BATTLE", "" + defendingPokemon.getName() + " has fallen asleep!");
    		}
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("SING")) {
    		description = "A soothing song lulls the foe into a deep slumber.";
    		type = "NORMAL";
    		pp = 15;
    		if (defendingPokemon.getStatusEffect() == 4) {
    			Log.v("BATTLE", "" + "It had no effect!");
    		}
    		else {
    			defendingPokemon.setStatusEffect(4);
    			Log.v("BATTLE", "" + defendingPokemon.getName() + " has fallen asleep!");
    		}
    		return 0; //Non-Damaging Attack
    	}
    	else if (move.equals("FIRESPIN")) {
    		description = "Traps foe in fire for 2-5 turns.";
    		type = "FIRE";
    		pp = 15; 
    		if (defendingPokemon.getStatusEffect() == 2) {
    			power = 15;
    		}
    		else {
    			defendingPokemon.setStatusEffect(2);
    			Log.v("BATTLE", "" + defendingPokemon.getName() + " has been burned!");
    			power = 15;
    		}
    	}
    	else if (move.equals("PECK")) {
    		description = "Jabs the foe with a beak, etc.";
    		type = "FLYING";
    		pp = 35;
    		power = 35;
    	}
    	else if (move.equals("PURSUIT")) {
    		description = "An attack move that works especially well on a foe that is switching out.";
    		type = "DARK";
    		pp = 20;
    		power = 40;
    	}
    	else if (move.equals("TACKLE")) {
    		description = "A full-body charge attack.";
    		type = "NORMAL";
    		pp = 35;
    		power = 35;
    	}
    	else if (move.equals("QUICKATTACK")) {
    		description = "An attack that always strikes first.";
    		type = "NORMAL";
    		pp = 30;
    		power = 40;
    	}
    	else if (move.equals("DOUBLEEDGE")) {
    		description = "A tackle that also hurts the user.";
    		type = "NORMAL";
    		pp = 15;
    		power = 120;
    	}
    	else if (move.equals("DIG")) {
    		description = "An attack that hits on the 2nd turn. Can also be used to exit dungeons.";
    		type = "GROUND";
    		pp = 10;
    		power = 80;
    	}
    	else if (move.equals("ROCKTHROW")) {
    		description = "Drops rocks on the enemy.";
    		type = "ROCK";
    		pp = 15;
    		power = 50;
    	}
    	else if (move.equals("EMBER")) {
    		description = "A weak fire attack that may inflict a burn.";
    		type = "FIRE";
    		pp = 25;
    		power = 40;
    	}
    	else if (move.equals("SCRATCH")) {
    		description = "Scratches the foe with sharp claws.";
    		type = "NORMAL";
    		pp = 40;
    		power = 30;
    	}
    	else if (move.equals("POUND")) {
    		description = "Pounds with forelegs or tail.";
    		type = "NORMAL";
    		pp = 35;
    		power = 40;
    	}
    	else if (move.equals("ROLLOUT")) {
    		description = "Attacks five turns with rising power.";
    		type = "ROCK";
    		pp = 20;
    		power = 30;
    	}
    	else if (move.equals("ICYWIND")) {
    		description = "An icy attack that lowers SPEED.";
    		type = "ICE";
    		pp = 15;
    		power = 55;
    	}
    	else if (move.equals("THUNDERSHOCK")) {
    		description = "An electrical attack that may paralyze the foe.";
    		type = "ELECTRIC";
    		pp = 30;
    		power = 40;
    	}
    	else if (move.equals("WATERGUN")) {
    		description = "Squirts water to attack.";
    		type = "WATER";
    		pp = 25;
    		power = 40;
    	}
    	else if (move.equals("RAZORLEAF")) {
    		description = "The foe is hit with a cutting leaf. It has a high critical-hit ratio.";
    		type = "GRASS";
    		pp = 25;
    		power = 40;
    	}
    	else {
    		//Struggle
    		description = "Used only if all PP are exhausted.";
    		type = "NORMAL";
    		pp = Integer.MAX_VALUE;
    		power = 30;
    		Log.v("BATTLE", "Unknown Move or STRUGGLE was used!");
    	}
    	
    	double A = ((2 * attackingPokemon.level + 10));
    	double B = (attackingPokemon.cur_attack / defendingPokemon.cur_def);
    	double C = ((A * B * power) / 255);
    	mod =  attackingPokemon.getWeaknessModifier(type, defendingPokemon);
    	double STAB = 1.0;
    	if (attackingPokemon.type1.equals(type) || attackingPokemon.type2.equals(type)) {
    		STAB = 1.5;
    	}
    	Log.v("BATTLE", "Move Type: " + getType() + " Enemy Type1: " + defendingPokemon.getType1() 
    			+ " Enemy Type2: " + defendingPokemon.getType2() +" Damage Muliplier: " + mod);
    	int damage = (int) (((C + 2) * mod) * STAB);
    	cur_damage = (int) damage;
    	//Special Cases (DOUBLEEDGE, TAKEDOWN, etc)
    	if (move.equals("DOUBLEEDGE")) {
    		attackingPokemon.cur_HP -= (damage/4);
    	}
    	return damage;
    }
    
}