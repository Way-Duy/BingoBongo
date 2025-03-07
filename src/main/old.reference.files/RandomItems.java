package com.snagtype.utils;

import com.google.common.collect.Lists;
import com.snagtype.modbingo.BingoModRandomizor;
import com.snagtype.modbingo.ModBingoLog;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import com.google.gson.*;



public class RandomItems {

    public static File configDirectory = BingoModRandomizor.configDirectory;
    private static Iterable<Item> items = ForgeRegistries.ITEMS;
    private static List<Item> itemList = Lists.newArrayList( items);
    private static List<String> modBlackList = null;
    public static Item getRandomItem() {


        Collections.shuffle(itemList);

        // checks that item can be found in creative tabs
        final CreativeTabs creativeTab = itemList.get(0).getCreativeTab();
        final NonNullList<ItemStack> stacks = NonNullList.create();
        try {
            if (creativeTab.equals(null)) {
            }
        }
        catch (Exception e) {
            ModBingoLog.info("exception block not found in Creativetabs = " + itemList.get(0));
            ModBingoLog.info("shuffling items and trying again");
            return getRandomItem();
        }
        // need to add code for meta values here



        return itemList.get(0);
    }
        //now with list parameter for mod weights
    public static Item getRandomItemFromMod(List<Item> itemListFromMod, List <Item> finalList) {


        Collections.shuffle(itemListFromMod);

        // checks that item can be found in creative tabs
        final CreativeTabs creativeTab = itemListFromMod.get(0).getCreativeTab();
        final NonNullList<ItemStack> stacks = NonNullList.create();
        try {
            if (creativeTab.equals(null)) {
            }
        }
        catch (Exception e) {
            ModBingoLog.info("exception block not found in Creativetabs = " + itemListFromMod.get(0));
            ModBingoLog.info("shuffling items and trying again");
            return getRandomItemFromMod(itemListFromMod, finalList);
        }
        // need to add code for meta values here


        for (Item name : finalList)// no duplicate items on our bingo card
        {
            if (name == itemListFromMod.get(0))
            {

                return getRandomItemFromMod(pullItemListFromRandomMod(), finalList);
            }
        }
        return itemListFromMod.get(0);
    }
    public static String getRandomMod(List<String> modList) { // picks a random mod not found on blacklist


        Collections.shuffle(modList);

       //if mod found on blacklist
        /*
       if(blacklist.find(modList.get(0))
            return getRandomMod(modList));

         */
        return modList.get(0);
    }
    public static List <Item> pullItemListFromRandomMod()
    {
        List<String> modList = new ArrayList<>();
        try {
            Object obj = new JsonParser().parse(new FileReader(configDirectory + "\\items.json"));
            JsonObject itemsJson= (JsonObject) obj;
            JsonArray modNamesArray = (JsonArray) itemsJson.get("modnames");
            //Iterating the contents of the array

            ModBingoLog.info(modNamesArray.toString());
            Iterator<JsonElement> iterator = modNamesArray.iterator();

            while(iterator.hasNext()) {
                String name = iterator.next().toString();
                modList.add(name);
            }

            /*
            for(String blacklist : modBlackList)// removes items from blacklist
            {
                for (String entrant : modList)
                {
                    if (entrant.substring(1,entrant.length()-1) == blacklist)
                        modList.remove(entrant);
                }
            }*/
            Collections.shuffle(modList);
            String modName = modList.get(0);
            List <Item>itemListFromMod = new ArrayList<>();
            ModBingoLog.info(" modname :" + modName.substring(1,modName.length()-1) );
            JsonObject modObj = itemsJson.getAsJsonObject(modName.substring(1,modName.length()-1));
            //JsonObject modObj = itemsJson.getAsJsonObject("minecraft");
            JsonArray itemList = modObj.getAsJsonArray("items");
            List<String> nameList = new ArrayList<>();
            iterator = itemList.iterator();
            while(iterator.hasNext()) {
                //ModBingoLog.info("iterating item: " + iterator.next());
                JsonObject item = (JsonObject) iterator.next();

                String name = item.get("name").toString();
                name = name.substring(1,name.length()-1);
                //ModBingoLog.info("item name: " + item.get("name").toString());
                itemListFromMod.add(Item.getByNameOrId(name));
            }
            ModBingoLog.info("end iteration, sending itemlist :" + itemListFromMod.toString() );
            return itemListFromMod;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }



    public static List<Item> getRandomItemList(int numItems) throws IllegalArgumentException{

        // ** todo going to need to pull the modlist from json file instead here

        if(numItems > itemList.size() - 1){
            throw new IllegalArgumentException();
        }

        // randomly saves i items into a list returns the list
    /*
        for (int i = 1; i < numItems; i++)
            {
                itemList.add(getRandomItem());
            }
        return itemList.subList(0, numItems);


     */
        // with mod weights

         List<Item> itemListFromMod = new ArrayList<>();
        for (int i = 0; i < numItems; i++)
        {

            itemListFromMod.add(getRandomItemFromMod(pullItemListFromRandomMod(), itemListFromMod));
        }

        ModBingoLog.info("itemlist" + itemListFromMod.toString() );
        return itemListFromMod;

    }

}
