package snagtype.bingobongo

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory


object MinecraftBingo : ModInitializer {
    private val logger = LoggerFactory.getLogger("BingoBongo")


	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		//todo: export json here
		/* Java code
		final JsonExportProcess process = new JsonExportList(this.configDirectory, this.exportConfig);
		final Thread exportProcessThread = new Thread( process);
		this.startService( "BingoMod Json Export", exportProcessThread);
		*/

		/*creating config file for the commands
		com.snagtype.modbingo.BingoModRandomizor.logger = event.getModLog()
		com.snagtype.modbingo.BingoModRandomizor.configDirectory =
			File(event.getModConfigurationDirectory().getPath(), "BingoMod")
		val recipeFile: File = File(com.snagtype.modbingo.BingoModRandomizor.configDirectory, "CustomRecipes.cfg")
		val recipeConfiguration: Configuration = Configuration(recipeFile)
		this.exportConfig = ForgeExportConfig(recipeConfiguration)

		val configFile: File =
			File(com.snagtype.modbingo.BingoModRandomizor.configDirectory, "Bingo.cfg") //creating bingo.cfg empty
		val bingoConfiguration: Configuration = Configuration(configFile)
		bingoConfig = BingoAdvancementConfig(bingoConfiguration)
		*/
	}

	/*
	@EventHandler
	fun serverLoad(event: FMLServerStartingEvent) {
		//todo: add server commands
		//event.registerServerCommand(CreateBingoCommand(this.bingoConfig))
		//event.registerServerCommand(ToggleFreeSpaceCommand(this.bingoConfig))
	}
 	*/
}