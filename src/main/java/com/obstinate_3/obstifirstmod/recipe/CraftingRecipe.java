package com.obstinate_3.obstifirstmod.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.obstinate_3.obstifirstmod.ObstiFirstMod;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CraftingRecipe implements IRecipe<IInventory> {
    public static final CraftingRecipe.Serializer SERIALIZER = new Serializer();
    public final ResourceLocation location;
    public final Ingredient ingredient;
    public final ItemStack output;

    public final int duration;
    public final int ingredientCost;
    public final NonNullList<Ingredient> ingredientList;

    public CraftingRecipe(ResourceLocation location, Ingredient ingredient, int ingredientCost, ItemStack output, int duration)
    {
        this.location = location;
        this.ingredient = ingredient;
        this.output = output;
        this.duration = duration;
        this.ingredientCost = ingredientCost;

        ingredientList = NonNullList.create();
    }
    @Override
    public boolean matches(IInventory inv, World worldIn)
    {
        return ingredient.test(inv.getStackInSlot(0));
    }


    @Override
    public ItemStack getCraftingResult(IInventory inv)
    {
        return output.copy();
    }

    @Override
    public boolean canFit(int width, int height)
    {
        return true;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return output;
    }

    @Override
    public ResourceLocation getId()
    {
        return location;
    }

    @Override
    public IRecipeSerializer<?> getSerializer()
    {
        return SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType()
    {
        return ModRecipe.CRAFTING;
    }

    @Override
    @Nonnull
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CraftingRecipe>
    {

        public Serializer()
        {
            setRegistryName(ObstiFirstMod.MODID, "powered_furnace");
        }

        @Override
        public CraftingRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            JsonElement element = (JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
            Ingredient ingredient = Ingredient.deserialize(element);
            int ingredientCost = JSONUtils.getInt(json.getAsJsonObject("ingredient"), "count", 1);
            String result = JSONUtils.getString(json, "result");
            int count = JSONUtils.getInt(json, "count", 1);

            ResourceLocation resultLocation = new ResourceLocation(result);

            ItemStack resultStack = new ItemStack(Registry.ITEM.getValue(resultLocation)
                    .orElseThrow(() -> new IllegalArgumentException("Item " + result + " does not exist")), count);

            int duration = JSONUtils.getInt(json, "duration", 1200);
            return new CraftingRecipe(recipeId, ingredient, ingredientCost, resultStack, duration);
        }

        @Nullable
        @Override
        public CraftingRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            Ingredient ingredient = Ingredient.read(buffer);
            int ingredientCost = buffer.readInt();
            ItemStack output = buffer.readItemStack();
            int duration = buffer.readInt();
            return new CraftingRecipe(recipeId, ingredient, ingredientCost, output, duration);
        }

        @Override
        public void write(PacketBuffer buffer, CraftingRecipe recipe)
        {
            recipe.ingredient.write(buffer);
            buffer.writeInt(recipe.ingredientCost);
            buffer.writeItemStack(recipe.output);
            buffer.writeInt(recipe.duration);
        }
    }

}
