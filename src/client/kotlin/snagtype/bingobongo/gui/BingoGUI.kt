package snagtype.bingobongo.gui

import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.client.gui.widget.*
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.tooltip.Tooltip
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.ScrollableWidget
import net.minecraft.client.gui.widget.CyclingButtonWidget
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import snagtype.bingobongo.BingoBongo
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

        // Free Space Checkbox
        val freeSpaceCheckbox = object : CheckboxWidget(
            leftX, leftY, columnWidth, widgetHeight,
            Text.literal("Enable Free Space"),
            BingoSettings.config.enableFreeSpace // Correct setting
        ) {
            override fun onPress() {
                super.onPress()
                BingoSettings.config.enableFreeSpace = this.isChecked
                BingoSettings.save()
            }
        }
        addDrawableChild(freeSpaceCheckbox)

        leftY += spacing // Move down for the next widget

        // Random Rewards Checkbox
        val randomRewardsCheckbox = object : CheckboxWidget(
            leftX, leftY, columnWidth, widgetHeight,
            Text.literal("Enable Random Rewards"),
            BingoSettings.config.enableRandomRewards // FIXED: correct setting
        ) {
            override fun onPress() {
                super.onPress()
                BingoSettings.config.enableRandomRewards = this.isChecked
                BingoSettings.save()
            }
        }
        addDrawableChild(randomRewardsCheckbox)
        leftY += spacing // Adjust the position for the next widget

        //Generate Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Generate")) {
            if(BingoSettings.config.modWhiteList == null)
                    BingoBongo.logger.info("No available mods to choose from")
            else {
                GenerateBingo(BingoBongo.globalServer)
            };
        }.dimensions(leftX, leftY, columnWidth, widgetHeight).build())
        leftY += spacing

        //Finalize Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Finalize")) {
            println("Finalize clicked")
        }.dimensions(leftX, leftY, columnWidth, widgetHeight).build())
        leftY += spacing

        // Options button (opens new screen)
        addDrawableChild(ButtonWidget.builder(Text.literal("Options")) {
            BingoSettings.save()
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
    private lateinit var maxTagSizeField: TextFieldWidget

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

        // Tag Options
        val buttonLabels = listOf(
            "Use Regular Tags",
            "Ignore Tags",
            "Exclude Large Tags",
            "Use Weighted Tags"
        )

        val buttonActions = listOf(
            {
                BingoSettings.config.tagOption = TagOption.TAGS
                println("Tag Option getRandomItemListWithTags clicked")
            },
            {
                BingoSettings.config.tagOption = TagOption.IGNORE_TAGS
                println("Tag Option getRandomItemListWithoutTags clicked")
            },
            {
                BingoSettings.config.tagOption = TagOption.EXCLUDE_LARGE_TAGS
                println("Tag Option getRandomItemListExcludingLargeTags clicked")
            },
            {
                BingoSettings.config.tagOption = TagOption.WEIGHTED_TAGS
                println("Tag Option getRandomItemListWithWeightedTags clicked")
            }
        )

        tagOptionButtons = buttonLabels.mapIndexed { index, label ->
            val button = ButtonWidget.builder(Text.literal(label)) {
                // When clicked: re-enable all buttons, disable only this one
                tagOptionButtons.forEach { it.active = true }
                it.active = false
                buttonActions[index]() // Run corresponding action
            }.dimensions(leftX, leftY + spacing * index, columnWidth, widgetHeight).build()

            addDrawableChild(button)

            // After adding "Exclude Large Tags" button (index == 2), add number input
            if (index == 2) {
                maxTagSizeField = TextFieldWidget(
                    textRenderer,
                    leftX + columnWidth + 5,
                    leftY + spacing * index,
                    50,
                    widgetHeight,
                    Text.literal("Max Tag Size")
                ).apply {
                    text = BingoSettings.config.excludeTagLimit.toString()
                    setChangedListener { newText ->
                        if (!newText.matches(Regex("\\d*"))) {
                            text = newText.filter { it.isDigit() }
                        }
                        else if(newText == "") //if user changes the input to nothing
                        {
                            text = "0"
                        }
                    }
                }
                addDrawableChild(maxTagSizeField)
            }
            button
        }

        // --- This is the FIXED PART ---
// Disable the button based on BingoSettings.config.tagOption, not hardcoded
        val selectedIndex = when (BingoSettings.config.tagOption) {
            TagOption.TAGS -> 0
            TagOption.IGNORE_TAGS -> 1
            TagOption.EXCLUDE_LARGE_TAGS -> 2
            TagOption.WEIGHTED_TAGS -> 3
        }
        tagOptionButtons.forEach { it.active = true } // Reset all active
        tagOptionButtons[selectedIndex].active = false // Disable the selected one

        // Configure Mod Blacklist Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Configure Mod Blacklist")) {
            BingoSettings.config.excludeTagLimit = maxTagSizeField.text.toInt()
            BingoSettings.save()
            MinecraftClient.getInstance().setScreen(ModBlacklistPopup(this))
        }.dimensions(rightX, rightY, columnWidth, widgetHeight).build())

        // Done Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Done")) {
            BingoSettings.config.excludeTagLimit = maxTagSizeField.text.toInt()
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
    private var allowedMods = mutableListOf<String>()
    private var blacklistedMods = mutableListOf<String>()
    private lateinit var allowedScroll: ScrollableWidget
    private lateinit var blacklistedScroll: ScrollableWidget
    private val modsToBlacklist = mutableListOf<String>()
    private val modsToAllow = mutableListOf<String>()
    private var allowedScrollY: Double = 0.0
    private var blacklistScrollY: Double = 0.0
    init {
        //test with all
        //val allMods = FabricLoader.getInstance().allMods
               //allowedMods.addAll(allMods.map { it.metadata.name })
        // Load installed mods' names from the mod loader
        allowedMods = BingoSettings.config.modWhiteList as? MutableList<String> ?: mutableListOf()
        blacklistedMods = BingoSettings.config.modBlackList as? MutableList<String> ?: mutableListOf()

    }

    override fun init() {
        val columnWidth = width / 2 - 40
        val lineHeight = 15
        val leftX = 20
        val rightX = width / 2 + 20
        val scrollHeight = height - 100
        val leftY = 60
        val rightY = 60


        val titleWidget = TextWidget(Text.literal("Configure Mod Blacklist"), textRenderer)
        titleWidget.x = (width - textRenderer.getWidth(titleWidget.message)) / 2
        titleWidget.y = 20
        addDrawableChild(titleWidget)

        // Add column titles
        val allowedTitle = TextWidget(Text.literal("Allowed Mods"), textRenderer)
        allowedTitle.x = leftX
        allowedTitle.y = leftY - 15
        addDrawableChild(allowedTitle)

        val blacklistedTitle = TextWidget(Text.literal("Blacklisted Mods"), textRenderer)
        blacklistedTitle.x = rightX
        blacklistedTitle.y = rightY - 15
        addDrawableChild(blacklistedTitle)

        // Scroll widget for allowed mods
         allowedScroll = object : ScrollableWidget(leftX, leftY, columnWidth, scrollHeight, Text.literal("Allowed Mods")) {
            override fun getContentsHeight(): Int = allowedMods.size * lineHeight
            override fun getDeltaYPerScroll(): Double = lineHeight.toDouble()
             init {
                 scrollY = allowedScrollY
             }

            override fun renderContents(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
                val startIndex = (scrollY / lineHeight).toInt()
                val visibleLines = height/lineHeight
                val endIndex = (startIndex + visibleLines).coerceAtMost(allowedMods.size-1)

                for (i in startIndex  ..  endIndex) {
                    //scrolling widget is treated as a large screen of getContentsHeight with a small visible portion
                    val itemY = leftY + (i* lineHeight)+5
                    context.drawText(textRenderer, allowedMods[i], x + 4, itemY, 0xFFFFFF, false)
                }
            }

             override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
                 // Check if the mouse click is within the widget's visible bounds
                 allowedScrollY= scrollY
                 if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height)  return false
                 val localMouseY = (mouseY - y + scrollY).toInt()
                 val clickedIndex = localMouseY / lineHeight
                 if (clickedIndex in allowedMods.indices) {
                     val modName = allowedMods[clickedIndex]

                     modsToBlacklist.add(modName)
                 }

                 return false
             }
             override fun setScrollY(value: Double) {
                 val maxScroll = (contentsHeight - this.height).coerceAtLeast(0)
                 super.setScrollY(value.coerceIn(0.0, maxScroll.toDouble()))
             }
            override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
            }
        }
        addDrawableChild(allowedScroll)


        // Scroll widget for blacklisted mods
         blacklistedScroll = object : ScrollableWidget(rightX, rightY, columnWidth, scrollHeight, Text.literal("Blacklisted Mods")) {
             init {
                 scrollY = blacklistScrollY
             }
             override fun getContentsHeight(): Int = blacklistedMods.size * lineHeight
            override fun getDeltaYPerScroll(): Double = lineHeight.toDouble()

            override fun renderContents(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
                if(blacklistedMods.isNotEmpty()) {
                    val startIndex = (scrollY / lineHeight).toInt()
                    val visibleLines = contentsHeight / lineHeight
                    val endIndex = (startIndex + visibleLines).coerceAtMost(blacklistedMods.size-1)
                    for (i in startIndex..endIndex) {
                        //scrolling widget is treated as a large screen of getContentsHeight with a small visible portion
                        val itemY = leftY + (i * lineHeight)+5
                        context.drawText(textRenderer, blacklistedMods[i], x + 4, itemY, 0xFFFFFF, false)
                    }
                }
            }

             override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
                 // Check if the mouse click is within the widget's visible bounds
                 blacklistScrollY= scrollY
                 if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) return false

                 val localMouseY = (mouseY - y + scrollY).toInt()
                 val clickedIndex = localMouseY / lineHeight
                 if (clickedIndex in blacklistedMods.indices) {
                     val modName = blacklistedMods[clickedIndex]
                     modsToAllow.add(modName)

                     return true
                 }

                 return false
             }
            override fun appendClickableNarrations(builder: NarrationMessageBuilder?) {
            }
        }
        addDrawableChild(blacklistedScroll)

        // Done Button
        addDrawableChild(ButtonWidget.builder(Text.literal("Done")) {
            BingoSettings.config.modWhiteList = allowedMods
            BingoSettings.config.modBlackList = blacklistedMods
            BingoSettings.save()
            client?.setScreen(parent)
        }.dimensions(width / 2 - 50, height - 30, 100, 20).build())
    }
    // Custom function to update both mod lists dynamically without reloading the entire screen
    private fun updateLists() {
        allowedScroll?.let { remove(it) }
        blacklistedScroll?.let { remove(it) }

        // Remove all buttons
        children().clear()

        // Reinitialize the screen (title and lists)
        init()
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context)
        super.render(context, mouseX, mouseY, delta)

        if (modsToBlacklist.isNotEmpty() || modsToAllow.isNotEmpty()) {
            allowedMods = allowedMods.toMutableList()
            blacklistedMods = blacklistedMods.toMutableList()
            allowedMods.removeAll(modsToBlacklist)
            blacklistedMods.addAll(modsToBlacklist)
            modsToBlacklist.clear()

            blacklistedMods.removeAll(modsToAllow)
            allowedMods.addAll(modsToAllow)
            modsToAllow.clear()
            updateLists()
        }
    }

    override fun close() {
        super.close()
        BingoSettings.config.modWhiteList = allowedMods
        BingoSettings.config.modBlackList = blacklistedMods
        BingoSettings.save()
    }
}