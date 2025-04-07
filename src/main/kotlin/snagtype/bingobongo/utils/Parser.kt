package snagtype.bingobongo.utils

import net.minecraft.item.Item
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier

class Parser {
    companion object {
        fun getItemName(itemID: Identifier): String {

            return itemID.toString()
        }

        fun getItemModName(itemID: Identifier): String {
            return itemID.toString()
        }
        fun getTagName(tagListElement: TagKey<Item>): String{
            return tagListElement.toString()
        }
    }
}