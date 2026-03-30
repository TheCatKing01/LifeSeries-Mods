package net.mat0u5.lifeseries.seasons.other;

import net.mat0u5.lifeseries.utils.other.IdentifierHelper;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;

public class MidnightChimes {
    private static final String MIDNIGHT_CHIMES_SOUND = "nicelife_midnight_chimes";
    private boolean playedMidnightChimes = false;

		public boolean tick(MinecraftServer server, boolean enabled, int minTime, int maxTime) {
			if (!enabled || server == null) {
				return false;
			}

			//? if <= 1.21.11 {
			long dayTime = server.overworld().getDayTime() % 24000L;
			//?} else {
			/*long dayTime = server.overworld().getOverworldClockTime() % 24000L;
			*///?}

			if (dayTime < minTime) {
				playedMidnightChimes = false;
			}

                        if (!playedMidnightChimes && dayTime >= minTime - 20 && dayTime <= maxTime - 1) {
				playedMidnightChimes = true;
				PlayerUtils.playSoundToPlayers(PlayerUtils.getAllPlayers(),
						SoundEvent.createVariableRangeEvent(IdentifierHelper.vanilla(MIDNIGHT_CHIMES_SOUND)),
						1f, 1);
				return true;
			}

			return false;
		}
}
