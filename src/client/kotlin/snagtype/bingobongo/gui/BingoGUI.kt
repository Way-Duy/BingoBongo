package snagtype.bingobongo.gui

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.client.gui.widget.*
//import net.minecraft.client.gui.screen.ScreenTexts

import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.CyclingButtonWidget
import net.minecraft.client.MinecraftClient
import snagtype.bingobongo.utils.GenerateBingo
import snagtype.bingobongo.config.BingoSettings
import snagtype.bingobongo.config.TagOption

class BingoGUI : Screen(Text.literal("Bingo Sheet")) {
    private lateinit var freeSpaceCheckbox: CheckboxWidget
    private lateinit var randomRewardsCheckbox: CheckboxWidget
    private lateinit var modeDropdown: CyclingButtonWidget<String>

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

        val freeSpaceCheckbox = object : CheckboxWidget(leftX, leftY, columnWidth, widgetHeight, Text.literal("Enable Free Space"), BingoSettings.config.enableFreeSpace) {
            override fun onPress() {
                super.onPress() // Call the original onPress functionality
                // Toggle the value whenever the checkbox is clicked
                BingoSettings.config.enableFreeSpace = !BingoSettings.config.enableFreeSpace
                BingoSettings.save() // Optionally save after the change
            }
        }
        addDrawableChild(freeSpaceCheckbox)

        leftY += spacing // Adjust the position for the next widget

        //Random Rewards  Checkbox
        val randomRewardsCheckbox = object : CheckboxWidget(leftX, leftY, columnWidth, widgetHeight, Text.literal("Enable Random Rewards"), BingoSettings.config.enableFreeSpace) {
            override fun onPress() {
                super.onPress() // Call the original onPress functionality
                // Toggle the value whenever the checkbox is clicked
                BingoSettings.config.enableRandomRewards = !BingoSettings.config.enableRandomRewards
                BingoSettings.save() // Optionally save after the change
            }
        }
        addDrawableChild(randomRewardsCheckbox)
        leftY += spacing // Adjust the position for the next widget

        //Generate Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Generate")) {
            println("Generate clicked")
            GenerateBingo();
        }.dimensions(leftX, leftY, columnWidth, widgetHeight).build())
        leftY += spacing

        //Finalize Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Finalize")) {
            println("Finalize clicked")
        }.dimensions(leftX, leftY, columnWidth, widgetHeight).build())
        leftY += spacing

        // Options button (opens new screen)
        addDrawableChild(ButtonWidget.builder(Text.literal("Options")) {
            MinecraftClient.getInstance().setScreen(OptionsPopup(this))
        }.dimensions(width - 110, height - 30, 100, 20).build())

        // --- RIGHT COLUMN ---

        modeDropdown = CyclingButtonWidget.builder<String> { value: String ->
            Text.literal(value.replaceFirstChar { it.uppercase() })
        }
            .values(listOf("line", "blackout"))
            .initially(mode)
            .tooltip { value -> Tooltip.of(Text.literal("Current mode: $value")) }
            .build(rightX, rightY, columnWidth, widgetHeight, Text.literal("Mode")) { _, selected ->
                mode = selected
                println("Mode set to $mode")
            }
        addDrawableChild(modeDropdown)
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        val title = Text.literal("Generate Bingo")
        val titleX = (width - textRenderer.getWidth(title)) / 2
        context.drawText(textRenderer, title, titleX, 20, 0xFFFFFF, false)
        super.render(context, mouseX, mouseY, delta)
    }
}

class OptionsPopup(private val parent: Screen) : Screen(Text.literal("Options")) {

    lateinit var tagOptionButtons: List<ButtonWidget>
    override fun init() {
        val columnWidth = width / 2 - 40
        val columnSpacing = 20
        val widgetHeight = 20
        val spacing = 30

        val leftX = 20
        val rightX = width / 2 + columnSpacing
        var leftY = 60
        var rightY = 60

        // --- LEFT COLUMN TITLE --- Tag Options
        val tagOptionsLabel = TextWidget(Text.literal("Tag Options"), textRenderer)
        tagOptionsLabel.x = leftX + (columnWidth - textRenderer.getWidth(tagOptionsLabel.message)) / 2
        tagOptionsLabel.y = leftY
        addDrawableChild(tagOptionsLabel)
        leftY += spacing  // Move below the label for buttons

        //Tag Options
        val buttonLabels = listOf(
            "Use Regular Tags",
            "Ignore Tags",
            "Exclude Large Tags",
            "Use Weighted Tags"
        )

        val buttonActions = listOf(
            { BingoSettings.config.tagOption = TagOption.TAGS
                println("Tag Option getRandomItemListWithTags clicked") },
            { BingoSettings.config.tagOption = TagOption.IGNORE_TAGS
                println("Tag Option getRandomItemListWithoutTags clicked") },
            {BingoSettings.config.tagOption = TagOption.EXCLUDE_LARGE_TAGS
                println("Tag Option getRandomItemListExcludingLargeTags clicked") },
            { BingoSettings.config.tagOption = TagOption.WEIGHTED_TAGS
                println("Tag Option getRandomItemListWithWeightedTags clicked") }
        )

        tagOptionButtons = buttonLabels.mapIndexed { index, label ->
            val button = ButtonWidget.builder(Text.literal(label)) {
                // On click, set all buttons active, then disable this one
                tagOptionButtons.forEach { it.active = true }
                it.active = false
                buttonActions[index]() // Run corresponding action
            }.dimensions(leftX, leftY + spacing * index, columnWidth, widgetHeight).build()

            addDrawableChild(button)
            button
        }

// Disable the first button by default
        tagOptionButtons[0].active = false


// Configure Mod Blacklist Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Configure Mod Blacklist")) {
            MinecraftClient.getInstance().setScreen(ModBlacklistPopup(this))
        }.dimensions(rightX, rightY, columnWidth, widgetHeight).build())

        addDrawableChild(ButtonWidget.builder(Text.literal("Done")) {
            BingoSettings.save()
            client?.setScreen(parent)
        }.dimensions(width / 2 - 75, height - 30, 150, 20).build())
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        val title = Text.literal("Options")
        val titleX = (width - textRenderer.getWidth(title)) / 2
        context.drawText(textRenderer, title, titleX, 20, 0xFFFFFF, false)
        super.render(context, mouseX, mouseY, delta)
    }
}

class ModBlacklistPopup(private val parent: Screen) : Screen(Text.literal("Mod Blacklist")) {
    private val allowedMods = mutableListOf("ModA", "ModB", "ModC", "ModD")
    private val blacklistedMods = mutableListOf<String>()

    override fun init() {
        val columnWidth = width / 2 - 40
        val spacing = 25
        val widgetHeight = 20

        val leftX = 20
        val rightX = width / 2 + 20
        var leftY = 60
        var rightY = 60

        // Title
        val titleWidget = TextWidget(Text.literal("Configure Mod Blacklist"), textRenderer)
        titleWidget.x = (width - textRenderer.getWidth(titleWidget.message)) / 2
        titleWidget.y = 20
        addDrawableChild(titleWidget)

        // Allowed Mods (Left List)
        allowedMods.forEach { modName ->
            val button = ButtonWidget.builder(Text.literal(modName)) {
                allowedMods.remove(modName)
                blacklistedMods.add(modName)
                init() // Refresh screen
            }.dimensions(leftX, leftY, columnWidth, widgetHeight).build()
            addDrawableChild(button)
            leftY += spacing
        }

        // Blacklisted Mods (Right List)
        blacklistedMods.forEach { modName ->
            val button = ButtonWidget.builder(Text.literal(modName)) {
                blacklistedMods.remove(modName)
                allowedMods.add(modName)
                init() // Refresh screen
            }.dimensions(rightX, rightY, columnWidth, widgetHeight).build()
            addDrawableChild(button)
            rightY += spacing
        }

        // Done Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Done")) {
            BingoSettings.save()
            client?.setScreen(parent)
        }.dimensions(width / 2 - 50, height - 30, 100, 20).build())
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)
    }
    override fun close() {
        super.close()
        BingoSettings.save()
    }
}
