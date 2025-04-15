package snagtype.bingobongo.utils

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import snagtype.bingobongo.BingoBongo
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemGroups
import net.minecraft.server.MinecraftServer


class CreateItemList {
    companion object {
        fun getList(server: MinecraftServer): MutableList<MutableList<String>> {
            val itemList = mutableListOf<MutableList<String>>()
                // Now you have the MinecraftServer instance
                for (item in Registries.ITEM) {
                    if ( !ObtainableInSurvival.isInAnyLootTable(server,item) && !ObtainableInSurvival.isCraftable(server,item)&& !ObtainableInSurvival.isDroppedFromBlocks(server,item))
                    {
                        BingoBongo.logger.info("not found in loot tables or crafting recipes: $item")
                        continue
                    }
                    // every itemList element is a list of itemStrings for a particular itemID
                    val itemStrings = mutableListOf<String>()
                    val itemID = Registries.ITEM.getId(item) ?: continue // itemId format: "ModName:ItemName

                    itemStrings.add(Parser.getItemName(itemID)) // element 0 = item name
                    itemStrings.add(Parser.getItemModName(itemID)) //element 1 = mod name
                    val itemStack = ItemStack(item)
                    val tagList = itemStack.streamTags().toList() // gets a list of tags for each item
                    BingoBongo.logger.info("item ID: $itemID")
                    BingoBongo.logger.info("list of Tags: $tagList")
                    for (tagListItem in tagList.listIterator()) {
                        itemStrings.add(Parser.getTagName(tagListItem)) // element 1 + x = associated tag names
                    }
                    itemList.add(itemStrings)
                }
            return itemList
        }
    }
}