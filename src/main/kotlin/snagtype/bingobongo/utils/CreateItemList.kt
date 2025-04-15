package snagtype.bingobongo.utils

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.*
import net.minecraft.registry.Registries
import snagtype.bingobongo.BingoBongo
import net.minecraft.loot.LootDataType
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.recipe.Recipe
import net.minecraft.screen.ScreenHandler
import net.minecraft.server.MinecraftServer
import net.minecraft.util.math.BlockPos


class CreateItemList {
    companion object {
        //theoretically should save time on world load
        fun getListBottomUp(server: MinecraftServer): MutableList<MutableList<String>> {
            val itemList = mutableListOf<MutableList<String>>()
            // every itemList element is a list of itemStrings for a particular itemID
            addDroppedFromBlocks(server, itemList)
            addInAnyLootTable(server, itemList)
            addCraftable(server, itemList)

            return itemList
        }
        fun itemToStringList(item: Item): MutableList<String>
        {
            val itemStrings = mutableListOf<String>()
            val itemID = Registries.ITEM.getId(item) // itemId format: "ModName:ItemName
            itemStrings.add(Parser.getItemName(itemID)) // element 0 = item name
            itemStrings.add(Parser.getItemModName(itemID)) //element 1 = mod name
            val itemStack = ItemStack(item)
            val tagList = itemStack.streamTags().toList() // gets a list of tags for each item

            for (tagListItem in tagList.listIterator()) {
                itemStrings.add(Parser.getTagName(tagListItem)) // element 1 + x = associated tag names
            }
            return itemStrings
        }

        fun addDroppedFromBlocks(server: MinecraftServer, itemList: MutableList<MutableList<String>> ) {
            val world = server.overworld
            val blockPos = BlockPos.ORIGIN

            for (block in Registries.BLOCK) {
                if (block == Blocks.AIR) continue

                val blockState = block.defaultState

                // Try with normal tool (no enchantment)
                val normalTool = ItemStack(Items.DIAMOND_PICKAXE)
                val normalDrops = Block.getDroppedStacks(blockState, world, blockPos, null, null, normalTool)

                // Try with Silk Touch
                val silkTool = ItemStack(Items.DIAMOND_PICKAXE)
                silkTool.addEnchantment(Enchantments.SILK_TOUCH, 1)
                val silkDrops = Block.getDroppedStacks(blockState, world, blockPos, null, null, silkTool)

                // Combine and check
                val allDrops = normalDrops + silkDrops

                for (stack in allDrops) {
                    if (!stack.isEmpty) {
                        //add item to list ItemStrings, then check if its itemList
                        val itemStrings = itemToStringList(stack.item)
                        if (!itemList.contains(itemStrings)){
                            itemList.add(itemStrings)
                            BingoBongo.logger.info("Item "+ stack.item + " is dropped from block: ${block.name.string}")
                        }
                      }
                }
            }
        }

        fun addCraftable(server: MinecraftServer, itemList: MutableList<MutableList<String>> ): MutableList<MutableList<String>> {
            val recipeManager = server.recipeManager
            val world = server.overworld

            // Create a dummy 3x3 crafting grid
            val dummyInventory = CraftingInventory(
                object : ScreenHandler(null, -1) {
                    override fun canUse(player: PlayerEntity?): Boolean = false
                    override fun quickMove(player: PlayerEntity, slot: Int): ItemStack {
                        return ItemStack.EMPTY
                    }
                }, 3, 3
            )

                 recipeManager.values().any { recipe ->
                if (recipe is Recipe<*>) {
                    @Suppress("UNCHECKED_CAST")
                    val typedRecipe = recipe as Recipe<RecipeInputInventory>

                    try {
                        val result = typedRecipe.craft(dummyInventory, world.registryManager)
                        if (!result.isEmpty) {
                            //add item to list ItemStrings, then check if its itemList
                            val itemStrings = itemToStringList(result.item)
                            if (!itemList.contains(itemStrings)){
                                BingoBongo.logger.info("Item is craftable: "+ result.item)
                                itemList.add(itemStrings)
                            }
                            true
                        }else {
                            false
                        }
                    } catch (e: Exception) {
                        false // Catch crafting exceptions like missing ingredients
                    }
                } else {
                    false
                }
            }
            return itemList
        }


        fun addInAnyLootTable(server: MinecraftServer, itemList: MutableList<MutableList<String>> ): MutableList<MutableList<String>> {
            val lootManager = server.lootManager
            val lootTableIds = lootManager.getIds(LootDataType.LOOT_TABLES)

            for (id in lootTableIds) {
                val lootTable = lootManager.getElementOptional(LootDataType.LOOT_TABLES, id).orElse(null) ?: continue

                for (pool in lootTable.pools) {
                    for (entry in pool.entries) {
                        if (entry is ItemEntry) {
                            try {
                                val itemField = ItemEntry::class.java.getDeclaredField("item")
                                itemField.isAccessible = true
                                val entryItem = itemField.get(entry) as? Item
                                    //add item to list ItemStrings, then check if its itemList
                                    val itemStrings = itemToStringList(entryItem!!)
                                    if (!itemList.contains(itemStrings)){
                                        BingoBongo.logger.info("Found ${entryItem} in loot table $id")
                                        itemList.add(itemStrings)
                                    }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            return itemList
        }

        //useful for testing, gets every item in registry and checks them
        fun getListForTesting(server: MinecraftServer): MutableList<MutableList<String>> {
            val itemList = mutableListOf<MutableList<String>>()
            val notFoundItemList = mutableListOf<MutableList<String>>()
            for (item in Registries.ITEM) {
                if ( !isInAnyLootTable(server,item) && !isCraftable(server,item)&& !isDroppedFromBlocks(server,item))
                {
                    notFoundItemList.add(itemToStringList(item))
                    continue
                }
                // every itemList element is a list of itemStrings for a particular itemID

                val itemStrings = itemToStringList(item)
                itemList.add(itemStrings)
            }
            JsonUtil.jsonExportItemsNotFound(notFoundItemList)
            return itemList
        }


        fun isCraftable(server: MinecraftServer, item: Item): Boolean {
            val recipeManager = server.recipeManager
            val world = server.overworld

            // Create a dummy 3x3 crafting grid
            val dummyInventory = CraftingInventory(
                object : ScreenHandler(null, -1) {
                    override fun canUse(player: PlayerEntity?): Boolean = false
                    override fun quickMove(player: PlayerEntity, slot: Int): ItemStack {
                        return ItemStack.EMPTY
                    }
                }, 3, 3
            )

            return recipeManager.values().any { recipe ->
                if (recipe is Recipe<*>) {
                    @Suppress("UNCHECKED_CAST")
                    val typedRecipe = recipe as Recipe<RecipeInputInventory>

                    try {
                        val result = typedRecipe.craft(dummyInventory, world.registryManager)
                        if(result.isEmpty)
                        {
                            //BingoBongo.logger.info("air recipe: $recipe")
                        }
                        if (!result.isEmpty && result.item == item) {
                            BingoBongo.logger.info("Item is craftable: $item")
                            true
                        }else {
                            false
                        }
                    } catch (e: Exception) {
                        false // Catch crafting exceptions like missing ingredients
                    }
                } else {
                    false
                }
            }
        }

        fun isInAnyLootTable(server: MinecraftServer, item: Item): Boolean {
            val lootManager = server.lootManager
            val lootTableIds = lootManager.getIds(LootDataType.LOOT_TABLES)

            for (id in lootTableIds) {
                val lootTable = lootManager.getElementOptional(LootDataType.LOOT_TABLES, id).orElse(null) ?: continue

                for (pool in lootTable.pools) {
                    for (entry in pool.entries) {
                        if (entry is ItemEntry) {
                            try {
                                val itemField = ItemEntry::class.java.getDeclaredField("item")
                                itemField.isAccessible = true
                                val entryItem = itemField.get(entry) as? Item
                                if (entryItem == item) {
                                    BingoBongo.logger.info("Found ${item} in loot table $id")
                                    return true
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }

            return false
        }

        fun isDroppedFromBlocks(server: MinecraftServer, item: Item): Boolean {
            val world = server.overworld
            val blockPos = BlockPos.ORIGIN

            for (block in Registries.BLOCK) {
                if (block == Blocks.AIR) continue

                val blockState = block.defaultState

                // Try with normal tool (no enchantment)
                val normalTool = ItemStack(Items.DIAMOND_PICKAXE)
                val normalDrops = Block.getDroppedStacks(blockState, world, blockPos, null, null, normalTool)

                // Try with Silk Touch
                val silkTool = ItemStack(Items.DIAMOND_PICKAXE)
                silkTool.addEnchantment(Enchantments.SILK_TOUCH, 1)
                val silkDrops = Block.getDroppedStacks(blockState, world, blockPos, null, null, silkTool)

                // Combine and check
                val allDrops = normalDrops + silkDrops

                for (stack in allDrops) {
                    if (!stack.isEmpty && stack.item ==item) {
                        BingoBongo.logger.info("Item $item is dropped from block: ${block.name.string}")
                        return true
                    }
                }
            }

            return false
        }
        /*
        fun isTradeable(item: Item): Boolean {
            // Iterate through each profession's trades
            for ((_, tradesByLevel) in TradeOffers.PROFESSION_TO_LEVELED_TRADE) {
                for (levelTrades in tradesByLevel) {
                    // Convert levelTrades to an iterable form if it's not already one
                    val tradeList = if (levelTrades is Iterable<*>) {
                        levelTrades
                    } else {
                        listOf(levelTrades)  // Wrap it in a list if it's not iterable
                    }

                    // Iterate through trade offers
                    for (offer in tradeList) {
                        if (offer is TradeOffer) {
                            // Check if the output item matches the item we are checking for
                            if (offer.sellItem.item == item) {
                                BingoBongo.logger.info("Item $item is tradeable.")
                                return true
                            }
                        }
                    }
                }
            }
            return false
        }
        */

    }
}