package com.ruinscraft.chat.players;

import java.util.UUID;

public class MinecraftIdentity {

	private String identity;
	
	public MinecraftIdentity(String identity) {
		this.identity = identity;
	}
	
	public MinecraftIdentity(UUID mojangUUID) {
		this.identity = mojangUUID.toString();
	}
	
	public String getIdentity() {
		return identity;
	}
	
	public boolean isUUID() {
		if (identity.length() <= 16) {
			return false;
		}
		
		try {
			UUID.fromString(identity);
		} catch (IllegalArgumentException e) {
			return false;
		}
		
		return true;
	}
	
}
