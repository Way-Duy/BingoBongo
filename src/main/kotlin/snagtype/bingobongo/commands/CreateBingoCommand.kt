package snagtype.bingobongo.commands
//needs a way to read free spaces; no config files for fabric
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.item.Item
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import snagtype.bingobongo.BingoBongo
import java.io.File
import java.nio.file.Paths


private const val ADVANCEMENT_DIRECTORY_SUFFIX = "/data/advancements/bingo"
private const val DEFAULT_BINGO_ITEMS = 25;



class CreateBingoCommand
{
    private var aliases: List<*>? = null
    private val itemList: List<Item>? = null
    private var advancementDirectory: File? = null

    init {
        BingoBongo.logger.info("before setting advancement Directory")
        aliases = ArrayList<Any?>()
        advancementDirectory = File( Paths.get("").toAbsolutePath().toString() + ADVANCEMENT_DIRECTORY_SUFFIX)

    }
}
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
