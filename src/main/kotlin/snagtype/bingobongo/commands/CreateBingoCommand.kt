package snagtype.bingobongo.commands
//needs a way to read free spaces; no config files for fabric
import net.minecraft.command.CommandException
//import snagtype.bingobongo.utils.BingoAdvancementPage
//import snagtype.bingobongo.utils.ItemRandomizer
import net.minecraft.item.Item
import net.minecraft.server.MinecraftServer
import snagtype.bingobongo.BingoBongo
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
    }

    fun getRandomItem(): Item? = itemList?.random()

//finish advancement/randomizer classes
/*
    @Throws(CommandException::class)
     fun execute(server: MinecraftServer?, args: Array<String?>?) {
        itemList = ItemRandomizer.getRandomItemList(DEFAULT_BINGO_ITEMS)
        val process: BingoAdvancementPage = BingoAdvancementPage(
            this.advancementDirectory,
            itemList
        ) // need itemforge list of 25 items

        //thread overloaded issue *ask dylan
        val bingoAdvancementPageThread: Thread = Thread(process)

        //FMLCommonHandler.instance().getMinecraftServerInstance().setMOTD("BINGO SERVER")
        this.startService("BingoMod Creating Advancements Page", bingoAdvancementPageThread)

        BingoBongo.logger.info( "Bingo Card generated in Advancements")
    }
 */
    private fun startService(serviceName: String, thread: Thread) {
        thread.name = serviceName
        thread.priority = Thread.MIN_PRIORITY
        BingoBongo.logger.info("Starting $serviceName")
        thread.start()
    }
}