package snagtype.bingobongo.utils

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.YetAnotherConfigLib
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.gui.YACLScreen
//import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

class BingoGUI {

    companion object {
        var enableBingo: Boolean = true
/*
        fun create(parent: Screen?): Screen {
            return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Bingo Config"))
                .category(
                    ConfigCategory.createBuilder()
                        .name(Text.literal("General"))
                        .option(Option.createBuilder<Boolean>()
                                .name(Text.literal("Enable Bingo"))
                                .description(Text.literal("Toggle to enable or disable Bingo"))
                                .binding(
                                    true,  // default
                                    { enableBingo },
                                    { enableBingo = it }
                                )
                                .controller { option ->
                                    Option.toggleController(option)
                                }
                                .build())
                        .build()
                )
                .save { println("Config saved! Bingo enabled: $enableBingo") }
                .build()
                .generateScreen(parent)

        }

 */
    }

}