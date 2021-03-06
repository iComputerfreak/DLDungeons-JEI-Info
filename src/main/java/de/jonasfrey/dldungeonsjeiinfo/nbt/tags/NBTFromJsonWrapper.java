package de.jonasfrey.dldungeonsjeiinfo.nbt.tags;

/* 
 * Doomlike Dungeons by is licensed the MIT License
 * Copyright (c) 2014-2018 Jared Blackburn
 * 
 * This class added by Hubry at GitHub, January 2019
 */	

import de.jonasfrey.dldungeonsjeiinfo.nbt.NBTType;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTFromJsonWrapper extends ITag {
	private NBTTagCompound wrapped;

	NBTFromJsonWrapper(String label, String data) {
		super(label, label);
		try {
			wrapped = JsonToNBT.getTagFromJson(data);
		} catch (NBTException e) {
			System.err.println("Exception reading json-nbt string: " + e.getMessage());
			wrapped = new NBTTagCompound();
		}
	}

	@Override
	public void write(NBTTagCompound cmp) {
		cmp.merge(wrapped);
	}

	@Override
	public void write(NBTTagList in) {
		in.appendTag(wrapped.copy());
	}

	@Override
	public NBTType getType() {
		return NBTType.COMPOUND;
	}
}
