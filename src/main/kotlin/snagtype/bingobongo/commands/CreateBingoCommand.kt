package snagtype.bingobongo.mixin.client

//needs a way to read free spaces; no config files for fabric
import net.minecraft.command.CommandException
import net.minecraft.item.Item
import net.minecraft.server.MinecraftServer
import snagtype.bingobongo.utils.BingoBongoLog
import java.io.File
import java.nio.file.Paths

private const val ADVANCEMENT_DIRECTORY_SUFFIX = "/data/advancements/bingo"
private const val DEFAULT_BINGO_ITEMS = 25;

class CreateBingoCommand
{

    private var aliases: List<*>? = null
    private val itemList: List<Item>? = null
    private var advancementDirectory: File? = null
    fun CreateBingoCommand() {
        aliases = ArrayList<Any?>()
        advancementDirectory = File( Paths.get("").toAbsolutePath().toString() + ADVANCEMENT_DIRECTORY_SUFFIX)
        //will print to log if working properly
        BingoBongoLog.info(advancementDirectory.toString())
    }}
/*
@Throws(CommandException::class)
 fun execute(server: MinecraftServer?, args: Array<String?>?) {
    $itemList = RandomItems.getRandomItemList(CreateBingoCommand.DEFAULT_BINGO_ITEMS)
    val process: BingoAdvancementPage = BingoAdvancementPage(
        this.advancementDirectory,
        itemList,
        config.isFreeSpaceEnabled()
    ) // need itemforge list of 25 items
    val BingoAdvancementPageThread: Thread = Thread(process)

    FMLCommonHandler.instance().getMinecraftServerInstance().setMOTD("BINGO SERVER")
    this.startService("BingoMod Creating Advancements Page", BingoAdvancementPageThread)

    sender.sendMessage(TextComponentString(TextFormatting.GREEN + "Bingo Card generated in Advancements"))
}

private fun startService(serviceName: String, thread: Thread) {
    thread.name = serviceName
    thread.priority = Thread.MIN_PRIORITY
    ModBingoLog.info("Starting $serviceName")
    thread.start()
}
 */
