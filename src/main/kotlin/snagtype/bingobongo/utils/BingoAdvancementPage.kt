package snagtype.bingobongo.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementFrame
import net.minecraft.advancement.criterion.InventoryChangedCriterion
import net.minecraft.advancement.criterion.TickCriterion
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.server.MinecraftServer
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import snagtype.bingobongo.BingoBongo.datapackSource
import snagtype.bingobongo.mixin.AccessorAdvancementManager
import snagtype.bingobongo.mixin.AccessorServerAdvancementLoader
import java.io.File
import java.io.FileWriter
import java.io.IOException
import snagtype.bingobongo.BingoBongo.syncAdvancements

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
        if (itemList != null && itemList.size == 24) {
            fullList?.add(12, Items.AIR)
        }

        // Build and register root advancement unconditionally
        val rootId = Identifier("bingobongo", "bingo/root")
        val rootAdvancement = Advancement.Builder.create()
            .display(
                Items.NETHER_STAR,
                Text.literal("Bingo Start"),
                Text.literal("Complete the tasks!"),
                Identifier("minecraft:textures/gui/advancements/backgrounds/stone.png"),
                AdvancementFrame.TASK,
                true,
                true,
                false
            )
            .criterion("tick", TickCriterion.Conditions.createTick())
            .build(rootId)
        saveAdvancementAsJson(rootId, rootAdvancement,0)

        // Register each item advancement
        fullList?.forEachIndexed { index, item ->
            val id = Identifier("bingobongo", "bingo/item_$index")
            val builder = Advancement.Builder.create()
                .parent(rootAdvancement)
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

            // Only add real criteria if item isn't AIR
            if (item != Items.AIR) {
                builder.criterion("has_item", InventoryChangedCriterion.Conditions.items(item))
            } else {
                // AIR cannot be detected via InventoryChangedCriterion, so use tick
                builder.criterion("free_space", TickCriterion.Conditions.createTick())
            }

            // Double-check for empty criteria (should never happen)
            if (builder.criteria.isEmpty()) {
                println("ERROR: Advancement at $id has no criteria! Item: $item")
                throw IllegalStateException("Advancement at $id has no criteria!")
            }

            val advancement = builder.build(id)

            // Now save the advancement as JSON
            saveAdvancementAsJson(id, advancement, index)
        }

        println("Bingo advancements registered dynamically.")

        // Sync to all players
        val playerList = server.playerManager.playerList
        for (player in playerList) {
            syncAdvancements(server, player)
        }

        // Reload advancements
        reloadAdvancements(server)
    }

    // Method to save an advancement as a JSON file
    private fun saveAdvancementAsJson(id: Identifier, advancement: Advancement, index: Int) {
        val advancementJson = createAdvancementJson(advancement, index)
        val file = File(datapackSource, "data\\bingobongo\\advancements\\${id.path}.json")

        try {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs() // Create directories if they don't exist
            }

            FileWriter(file).use { writer ->
                writer.write(advancementJson)
            }

            println("Advancement $id saved as JSON at ${file.absolutePath}")
        } catch (e: IOException) {
            println("Failed to save advancement $id as JSON: ${e.message}")
        }
    }

    private fun createAdvancementJson(advancement: Advancement, index: Int): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonObject = JsonObject()
        val computedX = (index % 5).toDouble()
        val computedY = (index / 5).toDouble()
        val display = advancement.display
        if (display != null) {
            val displayObj = JsonObject()

            val iconId = display.icon.toString().replace("1 ", "") // Remove any "1 " prefix if present
            // Create the icon JSON object with the proper format
            val iconObj = JsonObject()
            iconObj.addProperty("item", iconId)

            displayObj.add("icon", iconObj) // Add the icon to the display object
            displayObj.addProperty("title", display.title.string)
            displayObj.addProperty("description", display.description.string)

            // Background is optional, so check for it
            display.background?.let {
                displayObj.addProperty("background", it.toString())
            }

            displayObj.addProperty("frame", display.frame.name.lowercase())  // Lowercase the frame name
            displayObj.addProperty("show_toast", display.shouldShowToast())
            displayObj.addProperty("announce_to_chat", display.shouldAnnounceToChat())
            displayObj.addProperty("hidden", display.isHidden)

            jsonObject.add("display", displayObj)
        }

        if (advancement.parent != null) {
            jsonObject.addProperty("parent", advancement.parent!!.id.toString())
        }

        jsonObject.addProperty("x", computedX)
        jsonObject.addProperty("y", computedY)

        val criteriaObj = JsonObject()
        for ((name, _) in advancement.criteria) {
            // Add trigger for each criterion
            val criterionObj = JsonObject()
            criterionObj.addProperty("trigger", "minecraft:tick")  // Add the correct trigger type here
            criteriaObj.add(name, criterionObj)
        }
        jsonObject.add("criteria", criteriaObj)

        return gson.toJson(jsonObject)
    }

    // Method to reload advancements after saving them as JSON
    private fun reloadAdvancements(server: MinecraftServer) {
        val loader = server.advancementLoader
        val manager = (loader as AccessorServerAdvancementLoader).manager

        // Manually reload advancements
        // (manager as AccessorAdvancementManager).reload()  // This forces a reload of advancements

        //println("Advancements reloaded.")
    }
}
