package snagtype.bingobongo.mixin.client

import snagtype.bingobongo.utils.configs.BingoAdvancementConfig
import snagtype.bingobongo.utils.configs.AdvancementConfig

import net.minecraft.item.Item
import java.io.File

private const val ADVANCEMENT_DIRECTORY_SUFFIX = "/data/advancements/bingo"
private const val DEFAULT_BINGO_ITEMS = 25;

class CreateBingoCommand
{

    private var aliases: List<*>? = null
    private val itemList: List<Item>? = null
    private var advancementDirectory: File? = null
    //private val config: BingoAdvancementConfig = null
    fun CreateBingoCommand(config: BingoAdvancementConfig) {
        aliases = ArrayList<Any?>()
       /* this.config = config
        this.advancementDirectory =
            File(DimensionManager.getCurrentSaveRootDirectory() + CreateBingoCommand.ADVANCEMENT_DIRECTORY_SUFFIX)
        ModBingoLog.info(advancementDirectory.toString())
    */
    }}
