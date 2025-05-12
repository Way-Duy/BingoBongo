package snagtype.bingobongo.gui
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.text.Text

class CustomAdvancementScreen(client: MinecraftClient) : AdvancementsScreen(client.networkHandler?.advancementHandler) {
    override fun init() {
        super.init()

        if (BingoGUI.GuiNavigation.cameFromCustomGui) {
            // Create the back button
            val backButton = ButtonWidget.builder(Text.literal("Back to GUI")) {
                // Close advancement screen
                this@CustomAdvancementScreen.close()
                BingoGUI.GuiNavigation.cameFromCustomGui = false
                // Open your custom GUI
                client?.setScreen(BingoGUI())

            }.dimensions(width / 2 - 100, height - 30, 200, 20).build()
            addDrawableChild(backButton)
        }
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)
    }
}
