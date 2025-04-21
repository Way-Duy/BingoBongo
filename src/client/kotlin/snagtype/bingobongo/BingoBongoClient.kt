package snagtype.bingobongo

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW
import snagtype.bingobongo.gui.BingoGUI

object BingoBongoClient : ClientModInitializer {
	private lateinit var openScreenKey: KeyBinding

	override fun onInitializeClient() {
		openScreenKey = KeyBindingHelper.registerKeyBinding(
			KeyBinding(
				"key.bingobongo.openscreen",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_B,
				"key.categories.misc"
			)
		)

		ClientTickEvents.END_CLIENT_TICK.register { client ->
			if (openScreenKey.wasPressed()) {
				client.setScreen(BingoGUI())
			}
		}
	}
}