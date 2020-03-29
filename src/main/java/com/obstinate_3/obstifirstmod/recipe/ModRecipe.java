package com.obstinate_3.obstifirstmod.recipe;

import com.obstinate_3.obstifirstmod.ObstiFirstMod;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ObjectHolder;

public class ModRecipe {
    public static void init() {

    }

    public static final IRecipeType<CraftingRecipe> CRAFTING = register("crafting");

    private static <T extends IRecipe<?>> IRecipeType<T> register(final String key)
    {
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(ObstiFirstMod.MODID, key), new IRecipeType<T>()
        {
            @Override
            public String toString()
            {
                return key;
            }
        });
    }
}
