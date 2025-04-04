package snagtype.bingobongo
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys

import net.minecraft.registry.tag.TagKey
import net.minecraft.util.Identifier
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import org.slf4j.LoggerFactory
import snagtype.bingobongo.commands.CreateBingoCommand
import java.util.*


object BingoBongo : ModInitializer {

	 var isFreeSpaceEnabled: Boolean = false
     val logger = LoggerFactory.getLogger("BingoBongo")


	override fun onInitialize() {
		 CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>, registryAccess: CommandRegistryAccess?, environment: RegistrationEnvironment? ->
			dispatcher.register(
				CommandManager.literal("CreateBingoCommand").executes { context: CommandContext<ServerCommandSource> ->

					context.source.sendFeedback(

						{ Text.literal("Called /CreateBingoCommand with no arguments.") },
						false
					)
					logger.info("Before Command Init")
					CreateBingoCommand();
					logger.info("After Command Init")
					1
				})
		})
		//this will be moved to a command
		fun toggleFreeSpace() {isFreeSpaceEnabled= !isFreeSpaceEnabled}

		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")
		//todo: export json here

		for (item in Registries.ITEM) {
			//val itemId = Registries.ITEM.getId(item) ?: continue // itemId format: "ModName:ItemName
			val itemStack = ItemStack(item)
			val tagList = itemStack.streamTags().toList() // gets a list of tags for each item
			println(tagList)
		}
		//JsonUtil.jsonExportList(List)

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
}