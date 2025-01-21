package gungun974.btadaybreaker.mixin;

import gungun974.btadaybreaker.BTADayBreaker;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.net.packet.PacketGameRule;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = PlayerList.class, remap = false)
public abstract class PlayerListMixin {
	@Shadow
	private MinecraftServer server;

	@Shadow
	private List<PlayerServer> playerEntities;

	public void btadaybreaker$handlePlayerTraffic() {
		final World world = this.server.getDimensionWorld(Dimension.OVERWORLD.id);

		if (this.playerEntities.isEmpty()) {
			BTADayBreaker.LOGGER.info("Stop day time cycle in the OVERWORLD");

			world.getLevelData().getGameRules().setValue(GameRules.DO_DAY_CYCLE, false);
			this.server.playerList.sendPacketToAllPlayers(new PacketGameRule(world.getLevelData().getGameRules()));
			return;
		}
		BTADayBreaker.LOGGER.info("Start day time cycle in the OVERWORLD");

		world.getLevelData().getGameRules().setValue(GameRules.DO_DAY_CYCLE, true);
		this.server.playerList.sendPacketToAllPlayers(new PacketGameRule(world.getLevelData().getGameRules()));
	}


	@Inject(method = "playerLoggedIn(Lnet/minecraft/server/entity/player/PlayerServer;)V", at = @At("TAIL"))
	private void watchPlayerLoggedIn(PlayerServer player, CallbackInfo ci){
		btadaybreaker$handlePlayerTraffic();
	}

	@Inject(method = "playerLoggedOut(Lnet/minecraft/server/entity/player/PlayerServer;)V", at = @At("TAIL"))
	private void watchPlayerLoggedOut(PlayerServer player, CallbackInfo ci){
		btadaybreaker$handlePlayerTraffic();
	}
}
