package snagtype.bingobongo.utils

class ModBlacklist {
    //not sure exactly how we'll save the mod blacklist yet, will depend on the config file.
    //for now just assume it is a string List that we can work with.
    private val modBlacklist: List<String>? = null

    companion object {
        fun addMod(modName: String)  {
        }
        fun removeMod(modName: String){
        }

        fun filterList(itemList: MutableList<MutableList<String>> ): MutableList<MutableList<String>>  {

            return itemList
        }
    }
}