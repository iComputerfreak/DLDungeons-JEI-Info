package de.jonasfrey.dldungeonsjeiinfo;

import de.jonasfrey.dldungeonsjeiinfo.nbt.NBTHelper;
import de.jonasfrey.dldungeonsjeiinfo.nbt.tags.ITag;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class InfoData {
    
    String id = "";
    int damage = 0;
    ArrayList<String> dungeons = new ArrayList<>();
    String minAmount = "", maxAmount = "";
    ITag tag = null;
    
}

@JEIPlugin
public class JEI implements IModPlugin {
    
    @Override
    public void register(@Nonnull IModRegistry registry) {
        
        File specialChests = new File(DLDungeonsJEIInfo.DLDungeonsRoot + "SpecialChests/");
        if (!specialChests.isDirectory()) {
            DLDungeonsJEIInfo.logger.error("DLDungeons SpecialChests folder is missing!");
            return;
        }
        
        // Prepare Tags
        HashMap<String, ITag> tags = new HashMap<>();
        File nbtConfig = new File(DLDungeonsJEIInfo.DLDungeonsRoot + "nbt.cfg");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(nbtConfig));
            String line = reader.readLine();
            while (line != null) {
                if (line.trim().startsWith("#") || line.trim().isEmpty()) {
                    line = reader.readLine();
                    continue;
                }
                ITag tag = NBTHelper.parseNBTLine(line.trim());
                String label = line.split(" ")[0].trim().split("\t")[0].trim();
                tags.put(label, tag);
                
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Create Info data
        HashMap<String, InfoData> infoData = new HashMap<>();
        
        
        for (File file : specialChests.listFiles()) {
            DLDungeonsJEIInfo.logger.info("Parsing file " + file.getName() + "...");
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
                String line = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    // Parse config line
                    if (line.trim().startsWith("#") || line.trim().isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length < 5 || parts.length > 6) {
                        // Line has wrong format!
                        DLDungeonsJEIInfo.logger.warn("Error parsing config line: '" + line + "'.");
                        continue;
                    }
                    String category = parts[0].trim();
                    String tier = parts[1].trim();
                    String rawID = parts[2].trim();
                    String id = rawID;
                    String minAmount = parts[3].trim();
                    String maxAmount = parts[4].trim();
                    String nbt = (parts.length == 6 ? parts[5].trim() : null);
                    int damage = 0;
                    if (rawID.contains("(")) {
                        // Parse damage. Format: ID(DAMAGE)
                        String[] idParts = id.split("\\(");
                        id = idParts[0];
                        try {
                            damage = Integer.parseInt(idParts[1].replace(")", ""));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
    
                    // Add NBT Tag
                    ITag tag = tags.get(nbt);
                    
                    if (id.equalsIgnoreCase("minecraft:air")) {
                        continue;
                    }
                    
                    // Put required data into InfoData hashmap
                    if (infoData.get(id) == null) {
                        infoData.put(id, new InfoData());
                    }
                    infoData.get(id).dungeons.add(file.getName().replace(".cfg", ""));
                    infoData.get(id).minAmount = minAmount;
                    infoData.get(id).maxAmount = maxAmount;
                    infoData.get(id).tag = tag;
                    infoData.get(id).id = id;
                    infoData.get(id).damage = damage;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        
        // Register the descriptions
        for (String keyID : infoData.keySet()) {
            DLDungeonsJEIInfo.logger.info("Adding description for item " + keyID);
            
            InfoData data = infoData.get(keyID);
            // Remove duplicates
            ArrayList<String> dungeons = new ArrayList<>(new HashSet<>(data.dungeons));
            
            String dungeonString = "";
            if (dungeons.size() == 0) {
                continue;
            } else if (dungeons.size() == 1) {
                dungeonString = dungeons.get(0);
            } else {
                for (int i = 0; i < dungeons.size() - 1; i++) {
                    dungeonString += dungeons.get(i) + ", ";
                }
                // Remove last comma + space
                dungeonString = dungeonString.substring(0, dungeonString.length() - 2);
                dungeonString += " and " + dungeons.get(dungeons.size() - 1);
            }
            String amountString = data.minAmount;
            if (!data.minAmount.equals(data.maxAmount)) {
                amountString += "-" + data.maxAmount;
            }
            String description = "Can be found in " + dungeonString + " dungeons.\nAmount: " + amountString;
            
            try {
                Item item = Item.getByNameOrId(data.id);
                if (item == null) {
                    continue;
                }
                ItemStack itemStack = new ItemStack(item, 1, data.damage);
                if (data.tag != null) {
                    NBTHelper.setNbtTag(itemStack, data.tag);
                }
                registry.addIngredientInfo(itemStack, ItemStack.class, description);
            } catch (Exception e) {
                DLDungeonsJEIInfo.logger.warn("Error adding description for item " + data.id + ". Invalid Item ID");
                e.printStackTrace();
            }
        }
    }
    
}
