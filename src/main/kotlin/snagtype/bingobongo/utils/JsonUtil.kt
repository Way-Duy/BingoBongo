package snagtype.bingobongo.utils
import net.minecraft.item.Item


class JsonUtil{

    companion object {
        //todo: creates/writes to the json file a list of items with some attributes
        //Mod name, item name, NBT list
        fun JsonExportList(completeItemList: List<Item>?){

        }

        //todo: returns a list of items
        //Mod Name, item name, NBT list
        //also includes a function to give all NBT's of an item read from the json
        fun JsonImportList():List<Item>? {
            var completeItemList: List<Item>? = null
            return completeItemList
        }
    }

}