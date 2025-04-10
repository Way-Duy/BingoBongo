package snagtype.bingobongo.utils

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.loot.LootTable
import net.minecraft.server.MinecraftServer
import net.minecraft.recipe.RecipeManager
import net.minecraft.recipe.Recipe
import net.minecraft.util.Identifier
import net.minecraft.recipe.CraftingRecipe
import net.minecraft.block.Block
import net.minecraft.block.BlockState

class ObtainableInSurvival {
    /*
    fun isItemInLootTable(item: Item): Boolean {
        val lootTables: LootTable = MinecraftServer.getLootManager().getTable(item.id) // Get Loot Table by item ID
        return lootTables.hasLootItem(item) // Check if the item is in any loot table
    }
    fun isItemInVillagerTrades(item: Item): Boolean {
        for (profession in VillagerTrades.PROFESSION_TO_TRADES.keys) {
            for (level in VillagerTrades.PROFESSION_TO_TRADES[profession]?.keys ?: emptyList()) {
                val trades = VillagerTrades.PROFESSION_TO_TRADES[profession]?.get(level)
                trades?.forEach { (trade) ->
                    if (trade.output == item) {
                        return true
                    }
                }
            }
        }
        return false
    }
    fun isItemInCraftingRecipes(item: Item): Boolean {
        val recipes = RecipeManager.INSTANCE.recipes
        return recipes.any { recipe ->
            if (recipe is CraftingRecipe) {
                recipe.recipeOutput == item
            } else {
                false
            }
        }
    }
    fun isItemObtainedByBreakingBlock(block: Block): Boolean {
        val drops: List<ItemStack> = block.getDroppedStacks(BlockState, null, LootContext.EMPTY)
        return drops.any { it.item == item }  // Check if the item is a possible drop from the block
    }
*/
}