package gungun974.btadaybreaker.mixin;

import gungun974.btadaybreaker.BTADayBreakerMinecraftServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class, remap = false)
public abstract class PlayerListMixin {
	@Shadow
	private MinecraftServer server;

	@Inject(method = "playerLoggedIn(Lnet/minecraft/server/entity/player/PlayerServer;)V", at = @At("TAIL"))
	private void watchPlayerLoggedIn(PlayerServer player, CallbackInfo ci){
		((BTADayBreakerMinecraftServer)server).btadaybreaker$handlePlayerTraffic();
	}

	@Inject(method = "playerLoggedOut(Lnet/minecraft/server/entity/player/PlayerServer;)V", at = @At("TAIL"))
	private void watchPlayerLoggedOut(PlayerServer player, CallbackInfo ci){
		((BTADayBreakerMinecraftServer)server).btadaybreaker$handlePlayerTraffic();
	}
}
