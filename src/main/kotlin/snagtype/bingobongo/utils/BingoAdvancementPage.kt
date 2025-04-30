package snagtype.bingobongo.utils

import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.server.MinecraftServer
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import snagtype.bingobongo.mixin.AccessorAdvancementManager
import snagtype.bingobongo.mixin.AccessorServerAdvancementLoader

class BingoAdvancementPage(itemList: List<Item>?, server: MinecraftServer) {

    init {
        // Validate input
        if (itemList == null || itemList.size !in 24..25) {
            println("Invalid item list")
        }

        // Injected AdvancementManager
        val loader = server.advancementLoader
        val manager = (loader as AccessorServerAdvancementLoader).manager
        val advancements = (manager as AccessorAdvancementManager).advancements

        // Add AIR to center if it's a 24-item board (free space)
        val fullList = itemList?.toMutableList()
        if (itemList != null) {
            if (itemList.size == 24) {
                fullList?.add(12, Items.AIR)
            }
        }

        // Register each item advancement
        fullList?.forEachIndexed { index, item ->
            val id = Identifier("bingobongo", "bingo/item_$index")

            val builder = Advancement.Builder.create()
                .parent(Identifier("bingobongo", "bingo/root"))
                .display(
                    item,
                    Text.literal(item.name.string),
                    Text.literal("Collect this item."),
                    Identifier("minecraft:textures/gui/advancements/backgrounds/stone.png"),
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
                )

            if (item != Items.AIR) {
                builder.criterion("has_item", InventoryChangedCriterion.Conditions.items(item))
            }
            else
            {
                builder.criterion("free_space", InventoryChangedCriterion.Conditions.items(Items.AIR))
            }
            if (builder.criteria.isEmpty()) {
                throw IllegalStateException("Advancement at $id has no criteria!")
            }
            val advancement = builder.build(id)
            advancements[id] = advancement
        }

        println("Bingo advancements registered dynamically.")
    }
}