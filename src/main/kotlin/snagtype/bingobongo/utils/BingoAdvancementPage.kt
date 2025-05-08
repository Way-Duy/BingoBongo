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
import snagtype.bingobongo.BingoBongo
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
                true
            )
            .criterion("tick", TickCriterion.Conditions.createTick())
            .build(rootId)
        saveRootAdvancementJson(rootId, rootAdvancement,"root")


        val idGrid = mutableListOf<Advancement>()
        // Register each item advancement
        fullList?.forEachIndexed { index, item ->
            val row = index % 5
            val col = index / 5
            val name = "row${row}_col${col}"
            val id = Identifier("bingobongo", "bingo/$name")
            val parentId = if (col ==0) rootAdvancement else idGrid[(col - 1) * 5 + row]

            val builder = Advancement.Builder.create()
                .parent(parentId)
                .display(
                    item,
                    Text.literal(item.name.string),
                    Text.literal("row: ${row} column: ${col}\""),
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
            val advancement = builder.build(id)
            idGrid.add(advancement)

            // Now save the advancement as JSON
            saveAdvancementAsJson(id, advancement,name,item)
            //create dummy advancement after the last column
            if (col == 4) {
                val name = "dummy_row${row}_col${col+1}"
                val id = Identifier("bingobongo", "bingo/$name")
                val parentId = idGrid[(col) * 5 + row]

                val builder = Advancement.Builder.create()
                    .parent(parentId)
                    .display(
                        Items.AIR,
                        Text.literal("Dummy Advancement"),
                        Text.literal("testing"),
                        Identifier("minecraft:textures/gui/advancements/backgrounds/air.png"),
                        AdvancementFrame.TASK,
                        false,
                        false,
                        false
                    )
                builder.criterion("Dummy Advancement", TickCriterion.Conditions.createTick())
                val advancement = builder.build(id)

                // Now save the advancement as JSON
                saveAdvancementAsJson(id, advancement,name,Items.AIR)
            }

        }


        println("Bingo advancements registered dynamically.")

        // Sync to all players
        val playerList = server.playerManager.playerList
        for (player in playerList) {
            syncAdvancements(server, player)
        }

        // Reload advancements twice
        //for some reason necessary to read our newly generated datapack this way
        forceReload(server)
        forceReload(server)
    }
    private fun saveRootAdvancementJson(
        id: Identifier,
        advancement: Advancement,
        name: String
    ) {
        val advancementJson = createRootAdvancementJson(advancement)
        val file = File(datapackSource, "data\\bingobongo\\advancements\\bingo\\$name.json")

        try {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }

            FileWriter(file).use { writer ->
                writer.write(advancementJson)
            }

            println("Advancement $id saved as JSON at ${file.absolutePath}")
        } catch (e: IOException) {
            println("Failed to save advancement $id as JSON: ${e.message}")
        }
    }


    private fun createRootAdvancementJson( //for the root json
        advancement: Advancement,
    ): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonObject = JsonObject()
        val display = advancement.display
        if (display != null) {
            val displayObj = JsonObject()

            val iconId = display.icon.toString().replace("1 ", "")
            val iconObj = JsonObject()
            iconObj.addProperty("item", iconId)

            displayObj.add("icon", iconObj)
            displayObj.addProperty("title", display.title.string)
            displayObj.addProperty("description", display.description.string)

            display.background?.let {
                displayObj.addProperty("background", it.toString())
            }

            displayObj.addProperty("frame", display.frame.name.lowercase())
            displayObj.addProperty("show_toast", false)
            displayObj.addProperty("announce_to_chat", false)
            displayObj.addProperty("hidden", true)

            jsonObject.add("display", displayObj)
        }

        if (advancement.parent != null) {
            jsonObject.addProperty("parent", advancement.parent!!.id.toString())
        }

        val criteriaObj = JsonObject()
        for ((name, _) in advancement.criteria) {
            val criterionObj = JsonObject()
            criterionObj.addProperty("trigger", "minecraft:tick")
            criteriaObj.add(name, criterionObj)
        }
        jsonObject.add("criteria", criteriaObj)

        return gson.toJson(jsonObject)
    }


    // Method to save an advancement as a JSON file
    private fun saveAdvancementAsJson(
        id: Identifier,
        advancement: Advancement,
        name: String,
        item: Item
    ) {
        val advancementJson = createAdvancementJson(advancement, item)
        val file = File(datapackSource, "data\\bingobongo\\advancements\\bingo\\$name.json")

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

    private fun createAdvancementJson(
        advancement: Advancement,
        item: Item
    ): String {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val jsonObject = JsonObject()
        val display = advancement.display
        if (display != null) {
            val displayObj = JsonObject()

            val iconId1 = display.icon.toString().replace("1 ", "") // Remove any "1 " prefix if present
            val iconId = iconId1.replace("0 ", "") // Remove any "0 " prefix if present (air)
            val iconObj = JsonObject()
            iconObj.addProperty("item", iconId)

            displayObj.add("icon", iconObj)
            displayObj.addProperty("title", display.title.string)
            displayObj.addProperty("description", display.description.string)

            display.background?.let {
                displayObj.addProperty("background", it.toString())
            }

            displayObj.addProperty("frame", display.frame.name.lowercase())
            displayObj.addProperty("show_toast", display.shouldShowToast())
            displayObj.addProperty("announce_to_chat", display.shouldAnnounceToChat())
            displayObj.addProperty("hidden", false)

            jsonObject.add("display", displayObj)
        }

        if (advancement.parent != null) {
            jsonObject.addProperty("parent", advancement.parent!!.id.toString())
        }

        val criteriaObj = JsonObject()
        for ((name, _) in advancement.criteria) {
            val criterionObj = JsonObject()
            if (item != Items.AIR) {
                criterionObj.addProperty("trigger", "minecraft:inventory_changed")
                val conditions = JsonObject()
                val itemsArray = com.google.gson.JsonArray()
                val itemObject = JsonObject()
                itemObject.add("item", gson.toJsonTree(listOf(item.asItem().toString())))
                itemsArray.add(itemObject)
                conditions.add("items", itemsArray)
                criterionObj.add("conditions", conditions)
            } else {
                criterionObj.addProperty("trigger", "minecraft:tick")
            }
            criteriaObj.add(name, criterionObj)
        }
        jsonObject.add("criteria", criteriaObj)

        return gson.toJson(jsonObject)
    }

    fun forceReload(server: MinecraftServer) {
        val commandManager = server.commandManager
        val commandSource = server.commandSource

        // Run the /reload command
        commandManager.executeWithPrefix(commandSource, "reload")
    }

}
