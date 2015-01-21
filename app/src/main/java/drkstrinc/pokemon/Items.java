package drkstrinc.pokemon;

import java.io.Serializable;

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
// Items
//-----------------------------------------------------------------
// This class contains data on individual items usable in the overworld,
// in-battle, etc. Eventually a load() method will parse an xml file 
// containing every Items's data.
//-----------------------------------------------------------------

public class Items  implements Serializable {

	private int itemnumber;
	private int numOfItem;
	private int itemEffect;
	private int itemType;
	private String itemName;
	private String itemDescription;
	private String sprite;
	private String battleSprite;
	private boolean inBattle;
	
    public Items(int i, int n) {
    	itemnumber = i;
    	numOfItem = n;
    }
    
    public String getItemName() {
    	if (itemnumber == 0) {
    		itemName = "";
    	}
    	
    	else if (itemnumber == 1) {
    		itemName = "POTION";
    	}
    	
    	else if (itemnumber == 2) {
    		itemName = "MASTERBALL";
    	}
    	
    	else if (itemnumber == 3) {
    		itemName = "BERRY";
    	}
    	
    	else if (itemnumber == 4) {
    		itemName = "BICYCLE";
    	}
    	return itemName;
    }
    
    public String getItemDescription() {
    	if (itemnumber == 0) {
    		itemDescription = "";
    	}
    	else if (itemnumber == 1) {
    		itemDescription = "Heal a Pokemon for 20HP.";
    	}
    	else if (itemnumber == 2) {
    		itemDescription = "Throw at a Wild Pokemon to capture it!";
    	}
    	else if (itemnumber == 3) {
    		itemDescription = "A Pokemon may Hold this item. Restores 10HP.";
    	}
    	else if (itemnumber == 4) {
    		itemDescription = "A super-fast Bike that you can ride!";
    	}
    	return itemDescription;
    }
    
    public int getItemEffect() {
    	if (itemnumber == 0) {
    		itemEffect = 0;
    	}
    	else if (itemnumber == 1) {
    		itemEffect = 1;
    	}
    	else if (itemnumber == 2) {
    		itemEffect = 2;
    	}
    	else if (itemnumber == 3) {
    		itemEffect = 1;
    	}
    	else if (itemnumber == 4) {
    		itemEffect = 3;
    	}
    	return itemEffect;
    }
    
    public int getItemType() {
    	if (itemnumber == 0) {
    		itemType = 0;
    	}
    	else if (itemnumber == 1) {
    		itemType = 1;
    	}
    	else if (itemnumber == 2) {
    		itemType = 2;
    	}
    	else if (itemnumber == 3) {
    		itemType = 1;
    	}
    	else if (itemnumber == 4) {
    		itemType = 3;
    	}
    	return itemType;
    }
    
    public boolean isItemUsableInBattle() {
    	if (itemnumber == 0) {
    		inBattle = false;
    	}
    	else if (itemnumber == 1) {
    		inBattle = true;
    	}
    	else if (itemnumber == 2) {
    		inBattle = true;
    	}
    	else if (itemnumber == 3) {
    		inBattle = true;
    	}
    	else if (itemnumber == 4) {
    		inBattle = false;
    	}
    	return inBattle;
    }
    
    public String getSprite() {
    	return sprite;
    }
    
    public String getBattleSprite() {
    	return battleSprite;
    }
    
    public int numberOfItem() {
    	return numOfItem;
    }
    
    public void useItem() {
    	numOfItem--;
    }
}