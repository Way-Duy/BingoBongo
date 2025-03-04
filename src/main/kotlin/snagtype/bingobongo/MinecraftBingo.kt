package snagtype.bingobongo

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object MinecraftBingo : ModInitializer {
    private val logger = LoggerFactory.getLogger("minecraft-bingo")

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
	}

	@EventHandler
	fun serverLoad(event: FMLServerStartingEvent) {
		event.registerServerCommand(CreateBingoCommand(this.bingoConfig))
		//todo: add server commands
		//event.registerServerCommand(ToggleFreeSpaceCommand(this.bingoConfig))
		//event.registerServerCommand(new AddModToBlacklistCommand(ModBlacklistDirectory,this.bingoConfig));
		// event.registerServerCommand(new PrintModBlacklistCommand(ModBlacklistDirectory));
		//event.registerServerCommand(new RemoveModFromBlacklistCommand(ModBlacklistDirectory));
	}
}