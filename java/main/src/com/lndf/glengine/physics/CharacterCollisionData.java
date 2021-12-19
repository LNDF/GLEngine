package com.lndf.glengine.physics;

import physx.character.PxControllerCollisionFlagEnum;
import physx.character.PxControllerCollisionFlags;

public class CharacterCollisionData {
	
	private boolean down;
	private boolean up;
	private boolean side;
	
	public CharacterCollisionData(PxControllerCollisionFlags flags) {
		this.up = flags.isSet(PxControllerCollisionFlagEnum.eCOLLISION_UP);
		this.side = flags.isSet(PxControllerCollisionFlagEnum.eCOLLISION_SIDES);
		this.down = flags.isSet(PxControllerCollisionFlagEnum.eCOLLISION_DOWN);
	}
	
	public boolean collidesDown() {
		return down;
	}
	
	public boolean collidesUp() {
		return up;
	}
	
	public boolean collidesSide() {
		return side;
	}
	
}
