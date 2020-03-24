package com.obstinate_3.obstifirstmod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class ClientStuff {

    public static World getClientWorld() { return Minecraft.getInstance().world;}

    public static PlayerEntity getClientPlayer() { return Minecraft.getInstance().player;}
}
