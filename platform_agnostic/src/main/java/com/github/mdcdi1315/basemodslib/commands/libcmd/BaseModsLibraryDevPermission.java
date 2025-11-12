package com.github.mdcdi1315.basemodslib.commands.libcmd;

import com.github.mdcdi1315.basemodslib.commands.CommandPermission;

import net.minecraft.commands.CommandSourceStack;

import net.minecraft.server.MinecraftServer;

public final class BaseModsLibraryDevPermission
    extends CommandPermission
{
    @Override
    protected boolean IsSatisfied(CommandSourceStack stack) {
        MinecraftServer server = stack.getServer();
        return !server.isDedicatedServer() || AllPlayersAreOPSAndCreative(server);
    }

    private boolean AllPlayersAreOPSAndCreative(MinecraftServer server)
    {
        var pll = server.getPlayerList();
        for (var player : pll.getPlayers())
        {
            if (!pll.isOp(player.getGameProfile()) || !player.isCreative()) {
                return false;
            }
        }
        return true;
    }
}
