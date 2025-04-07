package snagtype.bingobongo.utils
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class JsonUtil{

    companion object {
        //todo: creates/writes to the json file a list of items with some attributes
        //Mod name, item name, NBT list
        fun jsonExportList(completeItemList: MutableList<MutableList<String>>){

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