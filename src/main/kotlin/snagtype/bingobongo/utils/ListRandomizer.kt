package snagtype.bingobongo.utils
import net.minecraft.item.Item

//todo: given a list of all obtainable items, randomly select given count (24/25 depending on free space)
// random selection should ignore NBT
//weight each mod proportional to the number of items in each mod. (subject to change)
// note that mods with just a few items in them will have a higher chance of selection with this method.
// (later) ignore blacklisted mods
// (optionally) select a random NBT after creating the list.

class ListRandomizer() {
    init{

    }

    companion object {
        fun getRandomItemList(completeItemList: List<Item>?, bingoSize: Int): List<Item>? {
            var bingoItemList: List<Item>? = null
            return bingoItemList
        }
    }
}
