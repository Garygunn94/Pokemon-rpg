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
// Global Variables
//-----------------------------------------------------------------
// This class allows any of the other classes to access the very same
// instance of global variables and flags set by the other various
// classes.
//-----------------------------------------------------------------

public class myGlobal {

	private static final myGlobal instance = new myGlobal();

	private boolean boolValue = false;
	private boolean continued = false;
	private String nameValue = "";

	public myGlobal() {
		
	}

	public static myGlobal getInstance() {
		return instance;
	}

	public boolean getValue() {
		return boolValue;
	}

	public void setValue(boolean newValue) {
		boolValue = newValue;
	}
	
	public String getName() {
		return nameValue;
	}
	
	public void setName(String s) {
		nameValue = s;
	}
	
	public void setContinued(boolean newValue) {
		continued = newValue;
	}
	
	public boolean getContinued() {
		return continued;
	}

}