package snagtype.bingobongo.utils

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootManager
import net.minecraft.server.MinecraftServer
import net.minecraft.recipe.Recipe
import net.minecraft.util.Identifier
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.village.TradeOffer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.Items
import net.minecraft.loot.LootDataType
import net.minecraft.loot.context.LootContextParameterSet
import net.minecraft.loot.context.LootContextParameters
import net.minecraft.loot.context.LootContextTypes
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.random.Random
import net.minecraft.village.TradeOffers
import snagtype.bingobongo.BingoBongo

class ObtainableInSurvival {
    companion object {
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


        }
    /*
    fun isDroppedFromBlocks(server: MinecraftServer, item: Item): Boolean {
        val world = server.overworld
        val lootManager = server.lootManager
        val toolStack = ItemStack(Items.IRON_PICKAXE) // simulate breaking with an iron pickaxe

        val parameterSet = LootContextParameterSet.Builder(world)
            .add(LootContextParameters.TOOL, toolStack)
            .add(LootContextParameters.ORIGIN, Vec3d.ZERO)
            .build(LootContextTypes.BLOCK)

        for (block in Registries.BLOCK) {
            val lootTableId = block.lootTableId
            val lootTable = lootManager.getLootTable(lootTableId)

            val context = LootContext.Builder(parameterSet)
                .random(Random.create())
                .luck(0.0f) // you can simulate Fortune by adjusting luck or context
                .build(LootContextTypes.BLOCK)

            val drops = lootTable.generateLoot(context)

            if (drops.any { it.item == item }) {
                BingoBongo.logger.info("Item $item is dropped from block: ${Registries.BLOCK.getId(block)}")
                return true
            }
        }

        return false
        }
     */
    }