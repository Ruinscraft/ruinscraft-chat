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
	
	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MinecraftIdentity)) {
			return false;
		}
		
		MinecraftIdentity minecraftIdentity = (MinecraftIdentity) object;
		
		return identity.toLowerCase().equals(minecraftIdentity.getIdentity().toLowerCase());
	}
	
	@Override
	public int hashCode() {
		return identity.toLowerCase().hashCode();
	}
	
}
