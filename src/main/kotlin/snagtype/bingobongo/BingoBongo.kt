package snagtype.bingobongo
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import snagtype.bingobongo.utils.GenerateBingo
import snagtype.bingobongo.config.BingoSettings
import snagtype.bingobongo.mixin.AccessorAdvancementManager
import snagtype.bingobongo.mixin.AccessorServerAdvancementLoader
//import snagtype.bingobongo.mixin.AccessorAdvancementManager
import snagtype.bingobongo.utils.CreateItemList
import snagtype.bingobongo.utils.JsonUtil


object BingoBongo : ModInitializer {

	 var isFreeSpaceEnabled: Boolean = false
     val logger = LoggerFactory.getLogger("BingoBongo")
	 lateinit var globalServer: MinecraftServer


	override fun onInitialize() {
		GenerateBingo.createRootAdvancement("BingoBongo")
		BingoSettings.load()

		fun toggleFreeSpace() {isFreeSpaceEnabled= !isFreeSpaceEnabled}

		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		//todo: export json here
		registerWorldLoadListener() // registers listener for post World creation



		/* Java code
		final JsonExportProcess process = new JsonExportList(this.configDirectory, this.exportConfig);
		final Thread exportProcessThread = new Thread( process);
		this.startService( "BingoMod Json Export", exportProcessThread);
		*/

		/*creating config file for the commands
		com.snagtype.modbingo.BingoModRandomizer.logger = event.getModLog()
		com.snagtype.modbingo.BingoModRandomizer.configDirectory =
			File(event.getModConfigurationDirectory().getPath(), "BingoMod")
		val recipeFile: File = File(com.snagtype.modbingo.BingoModRandomizer.configDirectory, "CustomRecipes.cfg")
		val recipeConfiguration: Configuration = Configuration(recipeFile)
		this.exportConfig = ForgeExportConfig(recipeConfiguration)

		val configFile: File =
			File(com.snagtype.modbingo.BingoModRandomizer.configDirectory, "Bingo.cfg") //creating bingo.cfg empty
		val bingoConfiguration: Configuration = Configuration(configFile)
		bingoConfig = BingoAdvancementConfig(bingoConfiguration)
		*/

	}
	fun registerWorldLoadListener() {// after world loaded
		ServerLifecycleEvents.SERVER_STARTED.register { server: MinecraftServer ->
			println("World has finished loading!")
			globalServer = server
			val itemList = CreateItemList.getListBottomUp(server) //what we will send to the Json Util; List of List<String>
			//change for testing all items.
			//val itemList = CreateItemList.getListForTesting(server)
			JsonUtil.jsonExportList(itemList)
		}
	}

}