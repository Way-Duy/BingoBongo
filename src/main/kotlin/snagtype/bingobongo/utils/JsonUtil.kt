package snagtype.bingobongo.utils
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import snagtype.bingobongo.BingoBongo
import java.io.File
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.nio.file.Paths
import java.util.Random
class JsonUtil{
    companion object {
        private var jsonDirectory: File? = null
        private const val JSON_FILE_NAME = "\\ItemList.json"
        private const val ITEMS_NOT_FOUND_FILE_NAME = "\\UnobtainableItems.json"
        private val tagIgnorelist: List<String> = mutableListOf("tools, breaks_decorated_blocks, foods")
        private val file = File( Paths.get("").toAbsolutePath().toString()+JSON_FILE_NAME)
        //todo: account for any blacklists

        // json format
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
        fun jsonExportItemsNotFound(completeItemList: MutableList<MutableList<String>>)
        {
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

                    // Add tag fields (tag1, tag2, ...)
                    for ((index, tagName) in tagList.withIndex()) {
                        itemInfo.addProperty("tag${index + 1}", tagName)
                    }
                    val wrapperArray = JsonArray()
                    wrapperArray.add(itemInfo)
                    itemsObject.add(itemID, wrapperArray)

                }
            }
            root.add("items", itemsObject)
            jsonDirectory = File( Paths.get("").toAbsolutePath().toString()+ ITEMS_NOT_FOUND_FILE_NAME)
            BingoBongo.logger.info(jsonDirectory.toString())
            File(jsonDirectory.toString()).writeText(gson.toJson(root))
        }
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
        //Mod Name, item name, tag list
        fun jsonImportList():List<Item>? {
            var completeItemList: List<Item>? = null
            return completeItemList
        }
        //randomly selects a list of 25 items inversely weighted with tags
        fun getRandomItemListWithTags(bingoSize: Int): List<Item>? {
            BingoBongo.logger.info("Randomly selecting 25 items inversely weighted with tags: ")

            val jsonRoot = JsonParser.parseReader(file.reader()).asJsonObject

            val itemPool = mutableListOf<String>()
            val tagMap = mutableMapOf<String, List<String>>()  // tag name -> list of itemIDs

            // Parse items
            val itemsObject = jsonRoot.getAsJsonObject("items")
            for ((itemID, array) in itemsObject.entrySet()) {
                val itemInfo = array.asJsonArray[0].asJsonObject
                val boolHasTag = itemInfo.get("boolHasTag")?.asString
                if (boolHasTag != "true") {
                    itemPool.add(itemID)
                }
            }

            // Parse tags
            val tagsObject = jsonRoot.getAsJsonObject("tags")
            for ((tagName, tagArray) in tagsObject.entrySet()) {
                val itemIDs = mutableListOf<String>()
                for (tagItem in tagArray.asJsonArray) {
                    val itemEntry = tagItem.asJsonObject.entrySet().first()
                    itemIDs.add(itemEntry.key)
                }
                tagMap[tagName] = itemIDs
            }

            // Combine itemIDs and tagNames into one pool
            val combinedPool = itemPool + tagMap.keys
            val selectedItems = mutableSetOf<Item>()

            while (selectedItems.size < bingoSize && combinedPool.isNotEmpty()) {
                val randomKey = combinedPool.random()

                val itemID = if (tagMap.containsKey(randomKey)) {
                    // If it's a tag, pick a random item from the tag
                    val candidates = tagMap[randomKey] ?: emptyList()
                    if (candidates.isEmpty()) continue
                    candidates.random()
                } else {
                    randomKey // it's an individual item
                }

                val item = Registries.ITEM.get(Identifier.tryParse(itemID) ?: continue)
                BingoBongo.logger.info(item.toString())
                selectedItems.add(item)
            }

            return selectedItems.toList()
        }

        //ignores tags entirely
        //randomly selects a list of 25 items
        fun getRandomItemListWithoutTags( bingoSize: Int): List<Item>? {
            BingoBongo.logger.info("Randomly selecting 25 items ignoring tags: ")

            val jsonRoot = JsonParser.parseReader(file.reader()).asJsonObject

            val itemPool = mutableListOf<String>()
            val tagMap = mutableMapOf<String, List<String>>()  // tag name -> list of itemIDs

            // Parse items
            val itemsObject = jsonRoot.getAsJsonObject("items")
            for ((itemID, array) in itemsObject.entrySet()) { itemPool.add(itemID) }

            val selectedItems = mutableSetOf<Item>()

            while (selectedItems.size < bingoSize && itemPool.isNotEmpty()) {
                val randItem = itemPool.random()
                val item = Registries.ITEM.get(Identifier.tryParse(randItem) ?: continue)
                BingoBongo.logger.info(item.toString())
                selectedItems.add(item)
            }

            return selectedItems.toList()
        }
        //excludes tags with too many items; tag.itemCount > limit
        //randomly selects a list of 25 items and tags with itemCount > limit
        fun getRandomItemListExcludingLargeTags( bingoSize: Int, tagLimit: Int): List<Item>? {
            BingoBongo.logger.info("Randomly selecting 25 items ignoring tags with item.Count > taglimit: ")
            val result = mutableListOf<Item>()

            val json = Gson().fromJson(file.readText(), JsonObject::class.java)
            val itemsJson = json.getAsJsonObject("items")
            val tagsJson = json.getAsJsonObject("tags")

            val eligibleItems = mutableListOf<Item>()
            val eligibleTagItemPools = mutableListOf<List<Item>>() // Each list = tag with small enough item count
            // Prepare eligible items
            for ((_, itemEntry) in itemsJson.entrySet()) {
                val itemArray = itemEntry.asJsonArray
                val itemObj = itemArray.firstOrNull()?.asJsonObject ?: continue
                val itemId = itemObj["itemID"].asString
                val hasTag = itemObj["boolHasTag"]?.asString?.lowercase() == "true"

                val tags = itemObj.entrySet()
                    .filter { it.key.startsWith("tag") }
                    .mapNotNull { it.value?.asString }

                val allTagsOverLimit = tags.all { tagName ->
                    val tagItems = tagsJson.getAsJsonArray(tagName)?.size() ?: Int.MAX_VALUE
                    tagItems >= tagLimit
                }

                val shouldIncludeItem = !hasTag || allTagsOverLimit

                if (shouldIncludeItem) {
                    val item = runCatching { Registries.ITEM.get(Identifier(itemId)) }.getOrNull()
                    if (item != null && !item.defaultStack.isEmpty) {
                        eligibleItems.add(item)
                    }
                }
            }

            // Prepare eligible tags as item pools
            for ((tagName, tagArray) in tagsJson.entrySet()) {
                val tagItems = mutableListOf<Item>()
                val tagEntries = tagArray.asJsonArray
                if (tagEntries.size() > tagLimit) {
                    BingoBongo.logger.info(("excluded tags: $tagName"))
                    continue
                }

                BingoBongo.logger.info(("included tags: $tagName"))

                for (itemObj in tagEntries) {
                    val innerItemId = (itemObj.asJsonObject.entrySet().first().key)
                    val item = runCatching { Registries.ITEM.get(Identifier(innerItemId)) }.getOrNull()
                    if (item != null && !item.defaultStack.isEmpty) {
                        tagItems.add(item)
                    }
                }
                if (tagItems.isNotEmpty()) {
                    eligibleTagItemPools.add(tagItems)
                }

            }

            // Combine both sources
            val allChoices = mutableListOf<() -> Item>()

            allChoices.addAll(eligibleItems.map { { it } }) // Wrap items in a lambda
            allChoices.addAll(eligibleTagItemPools.map { pool -> { pool.random() } }) // Wrap random tag selection

            // Shuffle and pick bingoSize number of unique items
            result.addAll(allChoices.shuffled().take(bingoSize).map { it() })

            //BingoBongo.logger.info(allChoices.toString())

            return result
        }
        //weights tags inversely to the number of items each tag has.
        //randomly selects a list of 25 items and weighted tags
        // i have no idea if this actually does what we hope it does
        fun getRandomItemListWithWeightedTags( bingoSize: Int): List<Item>? {
            val selectedItems = mutableListOf<Item>()
            val json = Gson().fromJson(file.readText(), JsonObject::class.java)

            val itemsJson = json["items"].asJsonObject
            val tagsJson = json["tags"].asJsonObject

            val itemPool = mutableListOf<Item>()
            val tagPool = mutableListOf<Pair<String, Double>>() // tag name + weight

            // Parse valid items (boolHasTag != true)
            for ((_, value) in itemsJson.entrySet()) {
                val array = value.asJsonArray
                val itemData = array.firstOrNull()?.asJsonObject ?: continue

                val boolHasTag = itemData["boolHasTag"].asString.toBooleanStrictOrNull() ?: false
                if (!boolHasTag) {
                    val itemID = itemData["itemID"].asString
                    Registries.ITEM.getOrEmpty(Identifier.tryParse(itemID)).ifPresent { itemPool.add(it) }
                }
            }

            // Parse tags and assign inverse weights
            for ((tagName, tagArray) in tagsJson.entrySet()) {
                val tagItemArray = tagArray.asJsonArray
                val size = tagItemArray.size()
                if (size == 0) continue
                val weight = 1.0 / size.toDouble()
                tagPool.add(tagName to weight)
            }

            // Create full weighted pool of "options"
            val random = Random()
            val weightedOptions = mutableListOf<Pair<String, Double>>() // either itemID or tagName
            itemPool.forEach { item ->
                weightedOptions.add(item.registryEntry.key.get().value.toString() to 1.0) // Items have weight 1
            }
            weightedOptions.addAll(tagPool) // Tags have inverse weights

            // Normalize weights
            val totalWeight = weightedOptions.sumOf { it.second }
            val normalized = weightedOptions.map { it.first to (it.second / totalWeight) }

            // Selection loop
            while (selectedItems.size < bingoSize && normalized.isNotEmpty()) {
                val pick = normalized.randomWeighted(random)

                if (pick in itemsJson.keySet()) {
                    // It's an item
                    Registries.ITEM.getOrEmpty(Identifier.tryParse(pick)).ifPresent { selectedItems.add(it) }
                } else if (pick in tagsJson.keySet()) {
                    // It's a tag: choose one random item from it
                    val tagArray = tagsJson[pick].asJsonArray
                    if (tagArray.isEmpty) continue
                    val randomkt = kotlin.random.Random.Default
                    val tagList = tagArray.map { it.asJsonObject } // convert JsonArray to List<JsonObject>
                    val randomItem = tagList.random(randomkt)        // use Kotlin's random with your RNG
                    val randomItemKey = randomItem.keySet().first()
                    Registries.ITEM.getOrEmpty(Identifier.tryParse(randomItemKey)).ifPresent { selectedItems.add(it) }
                }
            }

            return selectedItems
        }
        fun List<Pair<String, Double>>.randomWeighted(rand: Random): String {
            val r = rand.nextDouble()
            var cumulative = 0.0
            for ((id, weight) in this) {
                cumulative += weight
                if (r <= cumulative) return id
            }
            return this.last().first // fallback
        }

        //untested code to convert from String to Item
        fun getItemFromStringId(idString: String): Item? {
            val identifier = Identifier.tryParse(idString) ?: return null
            return Registries.ITEM.get(identifier)
        }
    }
}