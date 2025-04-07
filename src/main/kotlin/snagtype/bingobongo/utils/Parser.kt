package snagtype.bingobongo.utils

import net.minecraft.item.Item
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

class Parser {
    companion object {
        fun getItemName(itemID: Identifier): String {
            // example itemID:
            // item ID: minecraft:cobbled_deepslate
            // return only:
            // cobbled_deepslate

            return itemID.toString()
        }

        fun getItemModName(itemID: Identifier): String {
            // example itemID:
            // item ID: minecraft:cobbled_deepslate
            // return only:
            // minecraft
            return itemID.toString()
        }
        fun getTagName(tagListElement: TagKey<Item>): String{
            //example tagKey:
            //TagKey[minecraft:item / minecraft:stone_tool_materials], TagKey[minecraft:item / minecraft:stone_crafting_materials]
            //example element:
            //TagKey[minecraft:item / minecraft:stone_tool_materials]
            //return only:
            //stone_tool_materials
            println(tagListElement)
            return tagListElement.toString()
        }
    }
}