package snagtype.bingobongo.utils

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.block.Block
import net.minecraft.block.BlockWithEntity
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
            addCraftable(server, itemList)
            addDroppedFromBlocks(server, itemList)
            addInAnyLootTable(server, itemList)
            // creating not found list by cross-checking our itemList with every item in registry
            val notFoundItemList = mutableListOf<MutableList<String>>()
            for (item in Registries.ITEM) {
                val itemStringList = Parser.itemToStringList(item)
                if (!itemList.contains(itemStringList))
                {
                    notFoundItemList.add(itemStringList)
                }
            }
            JsonUtil.jsonExportItemsNotFound(notFoundItemList)

        return itemList
        }


        fun addDroppedFromBlocks(server: MinecraftServer, itemList: MutableList<MutableList<String>> ) {
            val world = server.overworld
            val blockPos = BlockPos.ORIGIN

            for (block in Registries.BLOCK) {
                if (block == Blocks.AIR) continue

                val blockState = block.defaultState
                val normalTool = ItemStack(Items.DIAMOND_PICKAXE)
                val silkTool = ItemStack(Items.DIAMOND_PICKAXE).apply {
                    addEnchantment(Enchantments.SILK_TOUCH, 1)
                }

                val blockEntity = if (block is BlockWithEntity) {
                    try {
                        block.createBlockEntity(blockPos, blockState)
                    } catch (e: Exception) {
                        null
                    }
                } else null

                val normalDrops = try {
                    Block.getDroppedStacks(blockState, world, blockPos, blockEntity, null, normalTool)
                } catch (e: Exception) {
                    //BingoBongo.logger.warn("Failed to get normal drops for block ${block.name}: ${e.message}")
                    emptyList()
                }

                val silkDrops = try {
                    Block.getDroppedStacks(blockState, world, blockPos, blockEntity, null, silkTool)
                } catch (e: Exception) {
                    //BingoBongo.logger.warn("Failed to get silk touch drops for block ${block.name}: ${e.message}")
                    emptyList()
                }
                // Combine and check
                val allDrops = normalDrops + silkDrops

                for (stack in allDrops) {
                    if (!stack.isEmpty) {
                        //add item to list ItemStrings, then check if its itemList
                        val itemStrings = Parser.itemToStringList(stack.item)
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

            for (recipe in recipeManager.values()) {
                if (recipe is Recipe<*>) {
                    @Suppress("UNCHECKED_CAST")
                    val typedRecipe = recipe as Recipe<RecipeInputInventory>
                    try {
                        val result = typedRecipe.craft(dummyInventory, world.registryManager)
                        if (!result.isEmpty) {
                            val itemStrings = Parser.itemToStringList(result.item)
                            if (!itemList.contains(itemStrings)) {
                                BingoBongo.logger.info("Item is craftable: ${result.item}")
                                itemList.add(itemStrings)
                            }
                        }
                    } catch (e: Exception) {
                        // Silently catch and continue
                    }
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
                                val itemField = entry.javaClass.declaredFields.find { it.type == Item::class.java }
                                itemField?.let {
                                    //reflection that skips items that have been specially modified by other mods
                                    // In a modded modpack, especially large ones, other mods or mixins may alter ItemEntry,
                                    //which can completely remove or hide the item field.
                                    it.isAccessible = true
                                    val entryItem = it.get(entry) as? Item
                                    val itemStrings = Parser.itemToStringList(entryItem!!)
                                    if (!itemList.contains(itemStrings)){
                                        BingoBongo.logger.info("Found ${entryItem} in loot table $id")
                                        itemList.add(itemStrings)
                                    }else {
                                        //BingoBongo.logger.debug("Skipping unknown loot entry type: ${entry.javaClass.name}")
                                    }
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
                    notFoundItemList.add(Parser.itemToStringList(item))
                    continue
                }
                // every itemList element is a list of itemStrings for a particular itemID

                val itemStrings = Parser.itemToStringList(item)
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
                        /*
                        if(result.isEmpty)
                        {
                            //BingoBongo.logger.info("air recipe: $recipe")
                        }
                         */
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
                                val itemField = entry.javaClass.declaredFields.find { it.type == Item::class.java }
                                itemField?.let {
                                    //reflection that skips items that have been specially modified by other mods
                                    // In a modded modpack, especially large ones, other mods or mixins may alter ItemEntry,
                                    //which can completely remove or hide the item field.
                                    it.isAccessible = true
                                    val entryItem = it.get(entry) as? Item
                                    if (entryItem == item) {
                                        BingoBongo.logger.info("Found $item in loot table $id")
                                        return true
                                    }else {
                                        //BingoBongo.logger.debug("Skipping unknown loot entry type: ${entry.javaClass.name}")
                                    }
                                }
                            } catch (e: Exception) {
                                //BingoBongo.logger.warn("Could not access item for loot entry: $entry in $id", e)
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

                val normalTool = ItemStack(Items.DIAMOND_PICKAXE)
                val silkTool = ItemStack(Items.DIAMOND_PICKAXE).apply {
                    addEnchantment(Enchantments.SILK_TOUCH, 1)
                }

                val blockEntity = if (block is BlockWithEntity) {
                    try {
                        block.createBlockEntity(blockPos, blockState)
                    } catch (e: Exception) {
                        null
                    }
                } else null

                val normalDrops = try {
                    Block.getDroppedStacks(blockState, world, blockPos, blockEntity, null, normalTool)
                } catch (e: Exception) {
                    //BingoBongo.logger.warn("Failed to get normal drops for block ${block.name}: ${e.message}")
                    emptyList()
                }

                val silkDrops = try {
                    Block.getDroppedStacks(blockState, world, blockPos, blockEntity, null, silkTool)
                } catch (e: Exception) {
                    //BingoBongo.logger.warn("Failed to get silk touch drops for block ${block.name}: ${e.message}")
                    emptyList()
                }

                val allDrops = normalDrops + silkDrops

                for (stack in allDrops) {
                    if (!stack.isEmpty && stack.item == item) {
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