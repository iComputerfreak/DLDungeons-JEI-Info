package de.jonasfrey.dldungeonsjeiinfo.nbt.tags;

/* 
 * Doomlike Dungeons by is licensed the MIT License
 * Copyright (c) 2014-2018 Jared Blackburn
 */			


import de.jonasfrey.dldungeonsjeiinfo.nbt.NBTType;
import de.jonasfrey.dldungeonsjeiinfo.parser.Tokenizer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class NBTCompound extends ITag {
	public final List<ITag> data;  // The data carried by the tag in the NBT
	
	
	/**
	 * The constructor for use with text data.
	 * 
	 * @param label
	 * @param name
	 * @param data
	 */
	NBTCompound(String label, String name, String data) {
		super(label, name);
		this.data  = new ArrayList<ITag>();
		parseData(data);
	}

	@Override
	public void write(NBTTagCompound in) {
		NBTTagCompound sub = in.getCompoundTag(name);
		in.setTag(name, sub);
		for(ITag child : data) {
			child.write(sub);
		}
	}

	@Override
	public void write(NBTTagList in) {
        NBTTagCompound sub = new NBTTagCompound();
		for(ITag child : data) {
			child.write(sub);
		}
        in.appendTag(sub);
	}
	
	
	private void parseData(String in) {
		Tokenizer tokens = new Tokenizer(in, ",");
		while(tokens.hasMoreTokens()) {
			data.add(Tags.registry.get(tokens.nextToken()));
		}
	}

	@Override
	public NBTType getType() {
		return NBTType.COMPOUND;
	}
}