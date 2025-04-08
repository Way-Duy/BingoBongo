package snagtype.bingobongo.utils
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import snagtype.bingobongo.BingoBongo
import java.io.File
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.nio.file.Paths

class JsonUtil{
    companion object {
        private var jsonDirectory: File? = null
        private const val JSON_FILE_NAME = "\\ItemList.json"
        private val tagBlacklist: List<String> = mutableListOf("tools, breaks_decorated_blocks, foods")
        //todo: creates/writes to the json file a list of items with some attributes
        // following this format
        // {
        //  "items": [
        //    "oak_boat": [
        //          {
        //          "itemID": "oak_boat"
        //          "modName": "minecraft"
        //          "boolHasTag": "True"
        //          "tag1": blah
        //          "tag2": blagh
        //          }
        //    "oak_sign": [
        //          {
        //          "itemID": "oak_sign"
        //          "modName": "minecraft"
        //          "boolHasTag": "False"
        //          }
        //    ...
        //  ],
        //  "tags": {
        //    "stone_crafting_materials": [
        //      {
        //        "cobbled_deepslate": [
        //          {
        //            "itemID": "cobbled_deepslate",
        //            "modName": "minecraft"
        //          }
        //        ]
        //      },
        //      {
        //        "cobblestone": [
        //          {
        //            "itemID": "cobblestone",
        //            "modName": "minecraft"
        //          }
        //        ]
        //      },
        //      {
        //        "blackstone": [
        //          {
        //            "itemID": "blackstone",
        //            "modName": "minecraft"
        //          }
        //        ]
        //      }
        //    ],
        //}
        //Mod name, item name, NBT list
        fun jsonExportList(completeItemList: MutableList<MutableList<String>>){
            val gson = GsonBuilder().setPrettyPrinting().create()
            val root = JsonObject()
            val itemsObject = JsonObject()
            val tagsObject = JsonObject()

            val tagMap = mutableMapOf<String, MutableList<Pair<String, String>>>()

            for (item in completeItemList) {
                if (item.size >= 2) {
                    val itemID = item[0]
                    val modName = item[1]
                    val hasTags = item.size > 2
                    val tagList = item.drop(2)

                    // Add to items
                    val itemInfo = JsonObject()
                    itemInfo.addProperty("itemID", itemID)
                    itemInfo.addProperty("modName", modName)
                    itemInfo.addProperty("boolHasTag", hasTags.toString())

                    // Add tag fields (tag1, tag2, ...)
                    for ((index, tagName) in tagList.withIndex()) {
                        itemInfo.addProperty("tag${index + 1}", tagName)
                    }
                    val wrapperArray = JsonArray()
                    wrapperArray.add(itemInfo)
                    itemsObject.add(itemID, wrapperArray)

                    // Collect tags for the tags section
                    for (tag in tagList) {
                        tagMap.computeIfAbsent(tag) { mutableListOf() }
                            .add(Pair(itemID, modName))
                    }
                }
            }

            // Build the "tags" section
            for ((tagName, items) in tagMap) {
                val tagArray = JsonArray()

                for ((itemID, modName) in items) {
                    val tagItemObj = JsonObject()
                    val innerArray = JsonArray()
                    val infoObj = JsonObject()
                    infoObj.addProperty("itemID", itemID)
                    infoObj.addProperty("modName", modName)
                    innerArray.add(infoObj)
                    tagItemObj.add(itemID, innerArray)
                    tagArray.add(tagItemObj)
                }

                tagsObject.add(tagName, tagArray)
            }

            // Final structure
            root.add("items", itemsObject)
            root.add("tags", tagsObject)

            jsonDirectory = File( Paths.get("").toAbsolutePath().toString()+JSON_FILE_NAME)
            BingoBongo.logger.info(jsonDirectory.toString())
            File(jsonDirectory.toString()).writeText(gson.toJson(root))
        }

        //todo: returns a list of items
        //Mod Name, item name, NBT list
        //also includes a function to give all NBT's of an item read from the json
        fun jsonImportList():List<Item>? {
            var completeItemList: List<Item>? = null
            return completeItemList
        }

        //untested code to convert from String to Item
        fun getItemFromStringId(idString: String): Item? {
            val identifier = Identifier.tryParse(idString) ?: return null
            return Registries.ITEM.get(identifier)
        }
    }
}