package net.mat0u5.lifeseries.seasons.lists;

import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static net.mat0u5.lifeseries.Main.seasonConfig;

public class Lists {
    public UUID uuid;
    public String name;
    public boolean cured = false;
    public boolean failed = false;
    public boolean died = false;
    public Time timeLists = Time.zero();

    public Lists(ServerPlayer player) {
        uuid = player.getUUID();
        name = player.getScoreboardName();
    }

    public ServerPlayer getPlayer() {
        return PlayerUtils.getPlayer(uuid);
    }

    public void tick() {
        timeLists.tick();
    }
}