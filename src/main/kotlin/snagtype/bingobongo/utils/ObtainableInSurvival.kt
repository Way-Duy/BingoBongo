package snagtype.bingobongo.utils

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.recipe.Recipe
import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.CraftingInventory
import net.minecraft.inventory.RecipeInputInventory
import net.minecraft.item.Items
import net.minecraft.loot.LootDataType
import net.minecraft.loot.entry.ItemEntry
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenHandler
import net.minecraft.util.math.BlockPos
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
                    if (!stack.isEmpty && stack.item == item) {
                        BingoBongo.logger.info("Item $item is dropped from block: ${block.name.string}")
                        return true
                    }
                }
            }

            return false
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
                            BingoBongo.logger.info("air recipe: $recipe")
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


        }

    }