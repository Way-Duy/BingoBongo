package snagtype.bingobongo.utils

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootManager
import net.minecraft.server.MinecraftServer
import net.minecraft.recipe.Recipe
import net.minecraft.util.Identifier
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.Inventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.inventory.SimpleInventory
import net.minecraft.loot.LootDataType
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandler
import snagtype.bingobongo.BingoBongo
import java.lang.reflect.Field

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
        return VillagerProfession.values().any { profession ->
            VillagerTrades.PROFESSION_TO_TRADES[profession]?.values?.flatten()?.any { trade ->
                trade.output.item == item
            } == true
        }
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
                        result.item == item
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
    fun isBlockDrop(item: Item): Boolean {
        return Registries.BLOCK.iterator().asSequence().any { block ->
            val drops = block.getDroppedStacks(block.defaultState, LootContext.Builder(null)) // Requires context
            drops.any { it.item == item }
        }
    }
     */

    }