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