package snagtype.bingobongo.config

import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import java.io.File

object BingoSettings {
    var config = BingoConfig()

    private val configFile = File(FabricLoader.getInstance().configDir.toFile(), "bingobongoconfig.json")
    private val json = Json { prettyPrint = true }

    fun load() {
        if (configFile.exists()) {
            config = json.decodeFromString(configFile.readText())
        }
    }

    fun save() {
        configFile.writeText(json.encodeToString(config))
    }
}