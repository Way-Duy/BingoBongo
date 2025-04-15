package snagtype.bingobongo.utils

import net.minecraft.item.Item
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import snagtype.bingobongo.BingoBongo

class Parser {
    companion object {
        fun getItemName(itemID: Identifier): String {
            // example itemID:
            // item ID: minecraft:cobbled_deepslate
            // return only:
            // cobbled_deepslate
            var itemIDString = itemID.toString()
            // from index 0, search for first ':' and return the following as a string
            for ((index, char) in itemIDString.withIndex())
            {
                if (char == ':')
                {
                    itemIDString= itemIDString.substring(index+1,itemIDString.length)
                    break
                }
            }
            //BingoBongo.logger.info("ItemID Only: $itemIDString")
            return itemIDString
        }

        fun getItemModName(itemID: Identifier): String {
            // example itemID:
            // item ID: minecraft:cobbled_deepslate
            // return only:
            // minecraft
            var modNameString = itemID.toString()
            // from index 0, search for first ':' and return what precedes as a string
            for ((index, char) in modNameString.withIndex())
            {
                if (char == ':')
                {
                    modNameString= modNameString.substring(0,index)
                    break
                }
            }
            //BingoBongo.logger.info("ModName Only: $modNameString")
            return modNameString
        }
        fun getTagName(tagListElement: TagKey<Item>): String{
            //example tagKey:
            //TagKey[minecraft:item / minecraft:stone_tool_materials], TagKey[minecraft:item / minecraft:stone_crafting_materials]
            //example element:
            //TagKey[minecraft:item / minecraft:stone_tool_materials]
            //return only:
            //stone_tool_materials
            var tagString = tagListElement.toString()

            // from the index.length(), search for first ':' (going backwards)
            // and return the ending as a string except the 1st index
            for (index in tagString.length - 1 downTo 0) {
                if (tagString[index] == ':') {
                    tagString = tagString.substring(index+1,tagString.length-1)
                    break
                }
            }
            //BingoBongo.logger.info("Tag name only: $tagString")
            return tagString
        }
    }
}