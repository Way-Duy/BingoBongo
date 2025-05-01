package snagtype.bingobongo
import kotlinx.io.IOException
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.advancement.Advancement
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import net.minecraft.util.WorldSavePath
import org.slf4j.LoggerFactory
import snagtype.bingobongo.config.BingoSettings
import snagtype.bingobongo.mixin.AccessorServerAdvancementLoader
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption


object BingoBongo : ModInitializer {

	 var isFreeSpaceEnabled: Boolean = false
     val logger = LoggerFactory.getLogger("BingoBongo")
	 lateinit var globalServer: MinecraftServer
	 lateinit var datapackSource: File


	override fun onInitialize() {
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
	fun registerWorldLoadListener() {
		// After world loaded
		ServerLifecycleEvents.SERVER_STARTED.register { server: MinecraftServer ->
			globalServer = server
			// Get the world root directory
			val worldDirectory = server.getSavePath(WorldSavePath.ROOT).toFile()
			println("World root directory: ${worldDirectory.absolutePath}")

			// Now you can manipulate the directory, for example, copying the datapack
			val datapackSource = File("datapacks/bingobongo") // Correct source path to the datapack folder
			val datapackDest = File(worldDirectory, "datapacks/bingobongo") // Destination path in the world's datapacks folder
			this.datapackSource = datapackDest

			// Ensure the destination folder exists
			if (!datapackDest.exists()) {
				datapackDest.mkdirs()
			}
			try {
				// Walk through the source directory
				Files.walk(datapackSource.toPath()).forEach { sourcePath ->
					// Calculate the relative path from the source directory
					val relativePath = datapackSource.toPath().relativize(sourcePath)

					// Create the target path by resolving it with the destination datapack directory
					val targetPath = datapackDest.toPath().resolve(relativePath)

					// If it's a directory, make sure it's created first
					if (Files.isDirectory(sourcePath)) {
						Files.createDirectories(targetPath)
					} else {
						// If it's a file, copy it
						Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING)
						println("Copied file: $sourcePath to $targetPath")
					}
				}

				// After copying all files, let's handle the pack.mcmeta file
				val mcmetaFile = File(datapackSource, "pack.mcmeta") // Get the pack.mcmeta file from the source
				if (mcmetaFile.exists()) {
					val targetMcmetaFile = File(datapackDest, "pack.mcmeta")
					Files.copy(mcmetaFile.toPath(), targetMcmetaFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
					println("pack.mcmeta file copied to world datapacks folder.")
				} else {
					println("pack.mcmeta file not found in the source directory!")
				}

				println("Datapack successfully copied to world folder.")
			} catch (e: IOException) {
				println("Failed to copy datapack to world folder: ${e.message}")
			}
		}
	}
	fun onPlayerJoin(server: MinecraftServer, player: ServerPlayerEntity) {
		// Trigger the sync after world load or player join
		syncAdvancements(server, player)
	}

	fun syncAdvancements(server: MinecraftServer, player: ServerPlayerEntity) {
		val advancementManager = (server.advancementLoader as AccessorServerAdvancementLoader).getManager()

		// Prepare the list of advancements
		val advancementsToSend = mutableListOf<Advancement>()
		val advancementsToRemove = mutableSetOf<Identifier>()

		// Collect advancements
		val advancements = advancementManager.advancements
		if (advancements is Collection<*>) {
			advancements.forEach { advancement ->
				// Handle each advancement
				val advancementId = advancement.id // Assuming each advancement has an `id` property
				// Add to your sending list or manipulate as needed
			}
		}

		// Create the packet (this constructor expects a collection and a set)
		val packet = AdvancementUpdateS2CPacket(
			false, // This indicates whether it's for adding or removing
			advancementsToSend,
			advancementsToRemove,
			mutableMapOf() // Add progress tracking if needed
		)

		// Send the packet to the player
		player.networkHandler.sendPacket(packet)
	}
}