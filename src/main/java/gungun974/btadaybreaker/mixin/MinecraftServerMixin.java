package gungun974.btadaybreaker.mixin;

import gungun974.btadaybreaker.BTADayBreaker;
import gungun974.btadaybreaker.BTADayBreakerMinecraftServer;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.net.PropertyManager;
import net.minecraft.core.net.packet.PacketGameRule;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.net.PlayerList;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftServer.class, remap = false)
public abstract class MinecraftServerMixin implements BTADayBreakerMinecraftServer {
	@Shadow
	public WorldServer getDimensionWorld(int dimId) {
		return null;
	}

	@Shadow
	public PlayerList playerList;

	@Shadow
	public PropertyManager propertyManager;

	public void btadaybreaker$handlePlayerTraffic() {
		for (final Dimension dim : new Dimension[]{Dimension.OVERWORLD, Dimension.NETHER, Dimension.PARADISE}) {
			final World world = this.getDimensionWorld(dim.id);

			if (playerList.playerEntities.isEmpty()) {
				BTADayBreaker.LOGGER.info("Stop day time cycle in the {}", dim.getTranslatedName());

				world.getLevelData().getGameRules().setValue(GameRules.DO_DAY_CYCLE, false);
				this.playerList.sendPacketToAllPlayers(new PacketGameRule(world.getLevelData().getGameRules()));
				continue;
			}
			BTADayBreaker.LOGGER.info("Start day time cycle in the {}", dim.getTranslatedName());

			world.getLevelData().getGameRules().setValue(GameRules.DO_DAY_CYCLE, true);
			this.playerList.sendPacketToAllPlayers(new PacketGameRule(world.getLevelData().getGameRules()));
		}
	}

	@Inject(method = "startServer()Z", at = @At("TAIL"))
	private void startServerHandler(CallbackInfoReturnable<?> info) { btadaybreaker$handlePlayerTraffic(); }

	@Inject(method = "doTick()V", at = @At("TAIL"))
	private void doTickHandler(CallbackInfo info) {
		final World overworld = this.getDimensionWorld(Dimension.OVERWORLD.id);

		final long currentTime = overworld.getWorldTime();

		for (final Dimension dim : new Dimension[]{Dimension.NETHER, Dimension.PARADISE}) {
			if ((dim != Dimension.NETHER || this.propertyManager.getBooleanProperty("allow-nether", true))
				&& (dim != Dimension.PARADISE || this.propertyManager.getBooleanProperty("allow-paradise", false))) {
				final World world = this.getDimensionWorld(dim.id);

				world.setWorldTime(currentTime);
			}
		}
	}
}
