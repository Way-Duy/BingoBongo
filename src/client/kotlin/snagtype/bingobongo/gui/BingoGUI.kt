package snagtype.bingobongo.gui
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.client.gui.widget.*

class BingoGUI : Screen(Text.literal("Bingo Sheet")) {
    private lateinit var freeSpaceCheckbox: CheckboxWidget
    private lateinit var randomRewardsCheckbox: CheckboxWidget

    private var mode = "line"

    override fun init() {
        val columnWidth = width / 2 - 40
        val columnSpacing = 20
        val widgetHeight = 20
        val spacing = 30

        val leftX = 20
        val rightX = width / 2 + columnSpacing
        var leftY = 60
        var rightY = 90

        // --- LEFT COLUMN ---

        //Free Space Checkbox
        freeSpaceCheckbox = CheckboxWidget(leftX, leftY, columnWidth, widgetHeight, Text.literal("Enable Free Space"), true)
        addDrawableChild(freeSpaceCheckbox)
        leftY += spacing

        //Random Rewards  Checkbox
        randomRewardsCheckbox = CheckboxWidget(leftX, leftY, columnWidth, widgetHeight, Text.literal("Enable Random Rewards"), true)
        addDrawableChild(randomRewardsCheckbox)
        leftY += spacing

        //Generate Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Generate")) {
            println("Generate clicked")
        }.dimensions(leftX, leftY, columnWidth, widgetHeight).build())
        leftY += spacing

        //Finalize Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Finalize")) {
            println("Finalize clicked")
        }.dimensions(leftX, leftY, columnWidth, widgetHeight).build())
        leftY += spacing

        // Options button
        addDrawableChild(ButtonWidget.builder(Text.literal("Options")) {
            println("Options clicked")
        }.dimensions(width - 110, height - 30, 100, 20).build())

        // --- RIGHT COLUMN ---

        val modeLabel = TextWidget(Text.literal("Mode"), textRenderer)
        modeLabel.x = rightX + (columnWidth - textRenderer.getWidth(modeLabel.message)) / 2
        modeLabel.y = rightY - spacing
        addDrawableChild(modeLabel)

        //blackout Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Blackout")) {
            mode = "blackout"
            println("Mode set to blackout")
        }.dimensions(rightX, rightY, columnWidth, widgetHeight).build())
        rightY += spacing

        //Line Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Line")) {
            mode = "line"
            println("Mode set to line")
        }.dimensions(rightX, rightY, columnWidth, widgetHeight).build())
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        val title = Text.literal("Generate Bingo")
        val titleX = (width - textRenderer.getWidth(title)) / 2
        context.drawText(textRenderer, title, titleX, 20, 0xFFFFFF, false)
        super.render(context, mouseX, mouseY, delta)
    }
}