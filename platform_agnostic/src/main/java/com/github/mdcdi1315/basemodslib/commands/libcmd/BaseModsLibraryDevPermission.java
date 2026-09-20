package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.basemodslib.commands.CommandPermission;

import net.minecraft.commands.CommandSourceStack;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.players.NameAndId;

public final class BaseModsLibraryDevPermission
    extends CommandPermission
{
    @Override
    @SuppressWarnings("ConstantValue")
    protected boolean IsSatisfied(CommandSourceStack stack)
    {
        MinecraftServer server = stack.getServer();
        if (server == null) {
            return false;
        } else {
            return !server.isDedicatedServer() || AllPlayersAreOPSAndCreative(server);
        }
    }

    private boolean AllPlayersAreOPSAndCreative(MinecraftServer server)
    {
        var pll = server.getPlayerList();
        for (var player : pll.getPlayers())
        {
            if (!pll.isOp(new NameAndId(player.getGameProfile())) || !player.isCreative()) {
                return false;
            }
        }
        return true;
    }
}
