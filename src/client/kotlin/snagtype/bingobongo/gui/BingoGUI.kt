package snagtype.bingobongo.gui

import com.terraformersmc.modmenu.api.ModMenuApi
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import dev.isxander.yacl3.api.YetAnotherConfigLib
import com.terraformersmc.modmenu.api.ConfigScreenFactory

class BingoGUI : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> {
        return ConfigScreenFactory { parent ->
            YetAnotherConfigLib.createBuilder()
                .title(Text.literal("BingoBongo"))
                .build()
                .generateScreen(parent)
        }
    }
}