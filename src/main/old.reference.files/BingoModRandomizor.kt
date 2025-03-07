package com.snagtype.modbingo

import com.snagtype.modbingo.commands.*
import export.json.ExportConfig
import export.json.ForgeExportConfig
import export.json.JsonExportProcess
import net.minecraftforge.common.config.Configuration
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import org.apache.logging.log4j.Logger
import java.io.File

@Mod(modid = BingoModRandomizor.MODID, name = BingoModRandomizor.NAME, version = BingoModRandomizor.VERSION)
class BingoModRandomizor {
    private var exportConfig: ExportConfig? = null

    //private ExportConfig testExportConfig;
    private var bingoConfig: BingoAdvancementConfig? = null

    @EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        logger = event.getModLog()
        configDirectory = File(event.getModConfigurationDirectory().getPath(), "BingoMod")
        val recipeFile: File = File(configDirectory, "CustomRecipes.cfg")
        val recipeConfiguration: Configuration = Configuration(recipeFile)
        this.exportConfig = ForgeExportConfig(recipeConfiguration)

        val configFile: File = File(configDirectory, "Bingo.cfg") //creating bingo.cfg empty
        val bingoConfiguration: Configuration = Configuration(configFile)
        bingoConfig = BingoAdvancementConfig(bingoConfiguration)

        /*
        ModBlacklistDirectory = new File(this.configDirectory, "");
        new Configuration(ModBlacklistDirectory); //creating empty blacklist file hopefully only if it doesn't already exist
*/
    }

    @EventHandler
    fun init(event: FMLInitializationEvent?) {
        val process: JsonExportProcess = JsonExportProcess(
            configDirectory,
            this.exportConfig
        )
        val exportProcessThread: Thread = Thread(process)
        this.startService("BingoMod Json Export", exportProcessThread)
    }

    private fun startService(serviceName: String, thread: Thread) {
        thread.setName(serviceName)
        thread.setPriority(Thread.MIN_PRIORITY)

        logger.info("Starting " + serviceName)
        thread.start()
    }

    @EventHandler
    fun serverLoad(event: FMLServerStartingEvent) {
        event.registerServerCommand(CreateBingoCommand(this.bingoConfig))
        event.registerServerCommand(ToggleFreeSpaceCommand(this.bingoConfig))
        //event.registerServerCommand(new AddModToBlacklistCommand(ModBlacklistDirectory,this.bingoConfig));
        // event.registerServerCommand(new PrintModBlacklistCommand(ModBlacklistDirectory));
        //event.registerServerCommand(new RemoveModFromBlacklistCommand(ModBlacklistDirectory));
    }

    companion object {
        var configDirectory: File? = null
        var ModBlacklistDirectory: File? = null
        val MODID: String = "modbingo"
        val NAME: String = "Mod Bingo"
        val VERSION: String = "1.0.0"
        private var logger: Logger? = null
    }
}
