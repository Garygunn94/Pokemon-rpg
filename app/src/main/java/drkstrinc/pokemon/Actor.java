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
// Actor
//-----------------------------------------------------------------
// This class is a superclass of other classes such as NPC. Not much
// happen here besides location information.
//-----------------------------------------------------------------

public class Actor {

	public int loc_x;
	public int loc_y;
	public int o_loc_x;
	public int o_loc_y;

    public Actor(int x, int y) {
    	loc_x = x;
    	loc_y = y;
    	o_loc_x = x;
    	o_loc_y = y;
    }
    
    public int getX() {
    	return loc_x;
    }
    
    public int getY() {
    	return loc_y;
    }
    
    public void act() {
    	
    }
    
    public void move() {
    	
    }
    
    public String getText() {
    	return "";
    }
    
}