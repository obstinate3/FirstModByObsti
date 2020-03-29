package com.obstinate_3.obstifirstmod.tileentity;

import com.obstinate_3.obstifirstmod.container.FirstBlockContainer;
import com.obstinate_3.obstifirstmod.util.IContentHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicBoolean;


public class FirstBlockTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider, IContentHolder {

    private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
    public static final int INPUT = 0;
    public static final int OUTPUT = 1;
    protected NonNullList<ItemStack> stacks;
    protected LazyOptional<ItemStackHandler> outputHandler;
    protected LazyOptional<ItemStackHandler> inputHandler;
    protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    public static final ItemStack Diamonds = new ItemStack(Items.DIAMOND);
    private int craftTime;
    private int craftTimeTotal = 10;
    protected IInventory inventoryInput;
    public final IRecipeType<? extends AbstractCookingRecipe> recipeType;



    public FirstBlockTile(IRecipeType<? extends AbstractCookingRecipe> recipeType) {

        super(ModTile.FIRST_BLOCK);
        inputHandler = LazyOptional.of(this::createInputHandler);
        outputHandler = LazyOptional.of(this::createOutputHandler);
        this.recipeType = recipeType;

        inventoryInput = new IInventory()
        {
            ItemStack stack = ItemStack.EMPTY;

            @Override
            public int getSizeInventory()
            {
                return 1;
            }

            @Override
            public boolean isEmpty()
            {
                return false;
            }

            @Override
            public ItemStack getStackInSlot(int index)
            {
                inputHandler.ifPresent
                        (
                                handler ->
                                {
                                    stack = handler.getStackInSlot(index);
                                }
                        );

                return stack;
            }

            @Override
            public ItemStack decrStackSize(int index, int count)
            {
                return null;
            }

            @Override
            public ItemStack removeStackFromSlot(int index)
            {
                return null;
            }

            @Override
            public void setInventorySlotContents(int index, ItemStack stack)
            {
            }

            @Override
            public void markDirty()
            {
            }

            @Override
            public boolean isUsableByPlayer(PlayerEntity player)
            {
                return false;
            }

            @Override
            public void clear()
            {
            }
        };


    }

    private IItemHandler createHandler() {
        return new ItemStackHandler(2) {


            @Nonnull @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    protected ItemStackHandler createOutputHandler()
    {
        return new ItemStackHandler()
        {
            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
            {
                return stack;
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return false;
            }
        };
    }

    protected ItemStackHandler createInputHandler()
    {
        return new ItemStackHandler()
        {
            @Nonnull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate)
            {
                return ItemStack.EMPTY;
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack)
            {
                return true;
            }

            @Override
            protected void onContentsChanged(int slot)
            {
                markDirty();
                super.onContentsChanged(slot);
            }
        };
    }

    @Override
    public void tick() {
        boolean crafting = isCrafting();
        boolean dirty = false;

        if(crafting) {
            craftTime--;
            dirty = true;
        }


        if(!world.isRemote) {
            IRecipe<?> recipe =getRecipe();
            if(recipe == null) {
                if (craftTime > 0) {
                    dirty = true;
                }

                craftTime = 0;
            } else {
                ItemStack target = recipe.getRecipeOutput();
                if (canOutput(target)) {
                    if(!crafting || isCrafting()) {
                        if(!isCrafting()) {
                            dirty = true;
                            craftTime = 0;
                        }
                    } else {
                        work(target);
                    }
                }
            }
            if (dirty) {
                markDirty();
            }
        }
    }

    private IRecipe<?> getRecipe()
    {
        if (inventoryInput.getStackInSlot(0).getCount() <= 0)
        {
            return null;
        }
        return this.world.getRecipeManager().getRecipe(recipeType, inventoryInput, this.world).orElse(null);
    }

    public boolean isCrafting()
    {
        return craftTime > 0;
    }

    private void work(ItemStack target) {
        craftTime++;
        if (craftTime >= craftTimeTotal)
        {
            craftTime = 0;
            inputHandler.ifPresent(handler -> {
                ItemStack input = handler.getStackInSlot(0);
                input.shrink(1);
                handler.setStackInSlot(0, input);
            });

            outputHandler.ifPresent(handler -> {
                ItemStack output = handler.getStackInSlot(0);
                if (output == ItemStack.EMPTY) {
                    handler.setStackInSlot(0, target.copy());
                } else {
                    output.grow(target.getCount());
                    handler.setStackInSlot(0, output);
                }
            });
        }
    }

    private boolean canOutput(ItemStack target)
    {
        AtomicBoolean canOutput = new AtomicBoolean(false);
        outputHandler.ifPresent(handler -> {
            ItemStack current = handler.getStackInSlot(0);
            if (current.isEmpty()) {
                canOutput.set(true);
            }
            else {
                int maxSize = current.getMaxStackSize();
                int size = current.getCount() + target.getCount();
                canOutput.set(current.getItem() == target.getItem() && (size <= maxSize));
            }
        });
        return canOutput.get();
    }
    @Override
    public void read(CompoundNBT tag) {
        CompoundNBT invTag = tag.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(invTag));
        super.read(tag);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        handler.ifPresent(h-> {
            CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            tag.put("inv", compound);
        });
        return super.write(tag);
    }

    @Nonnull
    @Override
    public <T>LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }

        return super.getCapability(cap, side);

    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent(getType().getRegistryName().getPath());
    }


    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new FirstBlockContainer(i, world, pos, playerInventory, playerEntity);
    }

    @Override
    public void dropContents() {

    }
}
