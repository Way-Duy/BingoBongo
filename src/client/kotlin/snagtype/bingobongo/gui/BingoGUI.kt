package snagtype.bingobongo.gui
import com.terraformersmc.modmenu.api.ModMenuApi
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.gui.YACLScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import net.minecraft.client.gui.DrawContext
import java.util.function.Consumer

class BingoGUI : ModMenuApi {
    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> {
        return ConfigScreenFactory { parent ->
            val builder = YetAnotherConfigLib.createBuilder()
                .title(Text.literal("BingoSheet"))
                .save {} // no-op

// --- Single Category: Settings ---
            val settingsCategory = ConfigCategory.createBuilder()
                .name(Text.literal("Settings"))

// Enable Free Space Option
            val freeSpaceOption = Option.createBuilder<Boolean>()
                .name(Text.literal("Enable Free Space"))
                .binding(true, { true }, { })
                .controller { BooleanControllerBuilder.create(it) }
                .build()

// Enable Random Rewards Option
            val randomRewardsOption = Option.createBuilder<Boolean>()
                .name(Text.literal("Enable Random Rewards"))
                .binding(true, { true }, { })
                .controller { BooleanControllerBuilder.create(it) }
                .build()

// Generate Button
            val generateButton = ButtonOption.createBuilder()
                .name(Text.literal("Generate"))
                .action { screen: YACLScreen, button: ButtonOption ->
                    println("Generate clicked")
                }  // Using BiConsumer with the correct parameters
                .build()

// Finalize Button
            val finalizeButton = ButtonOption.createBuilder()
                .name(Text.literal("Finalize"))
                .action { screen: YACLScreen, button: ButtonOption ->
                    println("Finalize clicked")
                }  // Using BiConsumer with the correct parameters
                .build()

// Create a dummy option for the preview
            val previewPlaceholder = Option.createBuilder<Boolean>()
                .name(Text.literal("▼ Preview Area ▼")) // Visually indicates the preview box area
                .binding(false, { false }, { })          // No real state
                .controller { BooleanControllerBuilder.create(it) } // Just creates a toggle, we ignore its use
                .build()
            /*possible icon preview solution
                      val iconButton = TexturedButtonWidget(
                          x, y, width, height,
                          0, 0, 20, 20, // Texture coordinates for the image
                          Identifier("mymod:textures/gui/icon.png") // Your icon path
                      ) {
                          println("Button with icon clicked")
                      }
                       */

// --- Mode Selection ---
            var mode = "line"

// Blackout Toggle
            val blackoutToggle = Option.createBuilder<Boolean>()
                .name(Text.literal("Blackout"))
                .binding(false, { mode == "blackout" }, { selected ->
                    if (selected) {
                        mode = "blackout"
                    }
                })
                .controller { BooleanControllerBuilder.create(it) }
                .build()

// Line Toggle
            val lineToggle = Option.createBuilder<Boolean>()
                .name(Text.literal("Line"))
                .binding(true, { mode == "line" }, { selected ->
                    if (selected) {
                        mode = "line"
                    }
                })
                .controller { BooleanControllerBuilder.create(it) }
                .build()

// Options Button
            val optionsButton = ButtonOption.createBuilder()
                .name(Text.literal("Options"))
                .action { screen: YACLScreen, button: ButtonOption ->
                    println("Options clicked")
                }  // Using BiConsumer with the correct parameters
                .build()

// Add all options to the category, visually split into two columns
            settingsCategory
                .option(freeSpaceOption)
                .option(randomRewardsOption)
                .option(generateButton)
                .option(previewPlaceholder)
                .option(finalizeButton)
                .option(blackoutToggle)
                .option(lineToggle)
                .option(optionsButton)

// Set the category to the builder
            builder.category(settingsCategory.build())

// Generate screen
            builder.build().generateScreen(parent)

        }
    }
}
