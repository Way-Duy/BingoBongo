package snagtype.bingobongo.mixin;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementManager;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * Targets the class that `MinecraftServer.advancementLoader` returns.
 */
@Mixin(ServerAdvancementLoader.class)
public interface AccessorServerAdvancementLoader {
	@Accessor("manager")
	AdvancementManager getManager();
}