package snagtype.bingobongo.utils

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.minecraft.command.CommandException
import net.minecraft.item.Item
import net.minecraft.server.MinecraftServer
import snagtype.bingobongo.BingoBongo
import snagtype.bingobongo.config.BingoSettings
import snagtype.bingobongo.config.TagOption
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.ArrayList

class GenerateBingo (server: MinecraftServer)
{
    private var aliases: List<*>? = null
    private var bingoList: List<Item>? = null
    private var advancementDirectory: File? = null

    companion object {
        private const val ADVANCEMENT_DIRECTORY_SUFFIX = "/data/advancements/bingo"
        private const val DEFAULT_BINGO_ITEMS = 25;
        fun createRootAdvancement(modId: String) {
            val path: Path = Paths.get("src/main/resources/data/$modId/advancements/bingo/root.json")

            if (!Files.exists(path)) {
                Files.createDirectories(path.parent)

                val rootAdvancement = JsonObject().apply {
                    add("display", JsonObject().apply {
                        add("icon", JsonObject().apply {
                            addProperty("item", "minecraft:diamond")
                        })
                        add("title", JsonObject().apply {
                            addProperty("translate", "advancements.$modId.root.title")
                        })
                        add("description", JsonObject().apply {
                            addProperty("translate", "advancements.$modId.root.description")
                        })
                        addProperty("background", "minecraft:textures/gui/advancements/backgrounds/stone.png")
                        addProperty("frame", "task")
                        addProperty("show_toast", false)
                        addProperty("announce_to_chat", false)
                        addProperty("hidden", false)
                    })
                    add("criteria", JsonObject().apply {
                        add("tick", JsonObject().apply {
                            addProperty("trigger", "minecraft:tick")
                        })
                    })
                }

                val gson = GsonBuilder().setPrettyPrinting().create()
                Files.writeString(path, gson.toJson(rootAdvancement))
                println("Generated root advancement for $modId at $path")
            } else {
                println("Root advancement for $modId already exists.")
            }
        }
    }
    init {
        BingoBongo.logger.info("before setting advancement Directory")
        aliases = ArrayList<Any?>()
        advancementDirectory = File(Paths.get("").toAbsolutePath().toString() + Companion.ADVANCEMENT_DIRECTORY_SUFFIX)
        BingoBongo.logger.info("Advancement Directory: "+ advancementDirectory.toString())
        //testing

        // Free space check
        val bingoSize: Int
        if(BingoSettings.config.enableFreeSpace)
            bingoSize = DEFAULT_BINGO_ITEMS-1
        else
            bingoSize = DEFAULT_BINGO_ITEMS
        // selects the correct Tag Option
            bingoList = when (BingoSettings.config.tagOption) {
            TagOption.TAGS -> JsonUtil.getRandomItemListWithTags(bingoSize)
            TagOption.IGNORE_TAGS -> JsonUtil.getRandomItemListWithoutTags(bingoSize)
            TagOption.EXCLUDE_LARGE_TAGS -> JsonUtil.getRandomItemListExcludingLargeTags(bingoSize, BingoSettings.config.excludeTagLimit)
            TagOption.WEIGHTED_TAGS -> JsonUtil.getRandomItemListWithWeightedTags(bingoSize)
        }
        BingoBongo.logger.info("BingoList: $bingoList")
        var bingoPage = BingoAdvancementPage(bingoList, server )
        //val advancementPage: BingoAdvancementPage = BingoAdvancementPage(this.advancementDirectory, bingoList)


    }

}