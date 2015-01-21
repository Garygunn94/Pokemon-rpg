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
// NPC
//-----------------------------------------------------------------
// This is an instance of a single NPC. It contains all of their 
// information such as overworld text, sprite, battle text, etc.
//-----------------------------------------------------------------

public class NPC extends Actor {

	private String name;
	public String[][] message;
	private String battleText;
	private String sprite;
	private String battleSprite;
	public Monsters[] pokemonparty = new Monsters[6];
	private int dir = 1;

    public NPC(int x, int y, String n, String s, String bs) {
    	super(x,y);
    	name = n;
    	message = new String[1][1];
    	battleSprite = bs;
    	sprite = s;
    }
    
    public int getOriginalX() {
    	return super.o_loc_x;
    }
    
    public int getOriginalY() {
    	return super.o_loc_y;
    }
    
    public int getCurrentX() {
    	return super.getX();
    }
    
    public int getCurrentY() {
    	return super.getY();
    }
    
    public void setDirection(int i) {
    	dir = i;
    }
    
    public int getDirection() {
    	return dir;
    }
    
    public void act() {
    	
    }
    
    public void move() {
    	
    }
    
    public void moveUp() {
    	super.loc_y--;
    }
    
    public void moveDown() {
    	super.loc_y++;
    }
    
    public void moveLeft() {
    	super.loc_x--;
    }
    
    public void moveRight() {
    	super.loc_x++;
    }
    
    public String getName() {
    	return name;
    }
    
    public String getSprite() {
    	return sprite;
    }
    
    public String getBattleSprite() {
    	return battleSprite;
    }
    
    public boolean getTalkable(NPC other) {
    	if ((other.getCurrentY()+1) == getCurrentY()) {
    		if ((other.getCurrentX()) == getCurrentX()) {
    			return true;
    		}
    	}
    	if ((other.getCurrentY()-1) == getCurrentY()) {
    		if ((other.getCurrentX()) == getCurrentX()) {
    			return true;
    		}
    	}
    	if ((other.getCurrentX()+1) == getCurrentX()) {
    		if ((other.getCurrentY()) == getCurrentY()) {
    			return true;
    		}
    	}
    	if ((other.getCurrentX()-1) == getCurrentX()) {
    		if ((other.getCurrentY()) == getCurrentY()) {
    			return true;
    		}
    	}
    	return false;
    }
    
}