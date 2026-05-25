package net.mat0u5.lifeseries.seasons.lists;

import net.mat0u5.lifeseries.utils.other.Time;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

import static net.mat0u5.lifeseries.LifeSeries.seasonConfig;

public class Lists {
    public UUID uuid;
    public String name;
    public ListType listType;
    public boolean cured = false;
    public boolean failed = false;
    public boolean died = false;

    public Time timeLists = Time.zero();

    public Lists(ServerPlayer player, ListType listType) {
        uuid = player.getUUID();
        name = player.getScoreboardName();
        this.listType = listType;
    }

    public ServerPlayer getPlayer() {
        return PlayerUtils.getPlayer(uuid);
    }

    public void tick() {
        timeLists.tick();
    }

    public enum ListType {
        NICE,
        NAUGHTY
    }
}
