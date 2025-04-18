package snagtype.bingobongo.commands
//needs a way to read free spaces; no config files for fabric
import net.minecraft.command.CommandException
import snagtype.bingobongo.utils.BingoAdvancementPage
//import snagtype.bingobongo.utils.ItemRandomizer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import snagtype.bingobongo.BingoBongo
import snagtype.bingobongo.utils.JsonUtil
import java.io.File
import java.nio.file.Paths
import java.util.*


private const val ADVANCEMENT_DIRECTORY_SUFFIX = "/data/advancements/bingo"
private const val DEFAULT_BINGO_ITEMS = 25;

class CreateBingoCommand
{
    private var aliases: List<*>? = null
    private var itemList: List<Item>? = null
    private var advancementDirectory: File? = null

    init {
        BingoBongo.logger.info("before setting advancement Directory")
        aliases = ArrayList<Any?>()
        advancementDirectory = File( Paths.get("").toAbsolutePath().toString() + ADVANCEMENT_DIRECTORY_SUFFIX)
        BingoBongo.logger.info("Advancement Directory: "+ advancementDirectory.toString())
        //testing
        JsonUtil.getRandomItemListExcludingLargeTags(25,10)

    }
    // not sure why we had this....
    // fun getRandomItem(): Item? = itemList?.random()

    @Throws(CommandException::class)
     fun execute(server: MinecraftServer?, args: Array<String?>?) {
        //itemList = JsonUtil.getRandomItemListWithoutTags(DEFAULT_BINGO_ITEMS)
        val process: BingoAdvancementPage = BingoAdvancementPage(
            this.advancementDirectory,
            itemList
        ) // need  list of 25 items

        val bingoAdvancementPageThread: Thread = Thread(process)
        this.startService("BingoMod Creating Advancements Page", bingoAdvancementPageThread)

        BingoBongo.logger.info( "Bingo Card generated in Advancements")
    }

    private fun startService(serviceName: String, thread: Thread) {
        thread.name = serviceName
        thread.priority = Thread.MIN_PRIORITY
        BingoBongo.logger.info("Starting $serviceName")
        thread.start()
    }

}