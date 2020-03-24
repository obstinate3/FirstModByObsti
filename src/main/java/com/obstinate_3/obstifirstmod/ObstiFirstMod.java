package com.obstinate_3.obstifirstmod;

import com.obstinate_3.obstifirstmod.block.FirstBlock;
import com.obstinate_3.obstifirstmod.container.FirstBlockContainer;
import com.obstinate_3.obstifirstmod.tileentity.FirstBlockTile;
import com.obstinate_3.obstifirstmod.block.ModBlocks;
import com.obstinate_3.obstifirstmod.setup.ClientInit;
import com.obstinate_3.obstifirstmod.util.ClientStuff;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ObstiFirstMod.MODID)
public class ObstiFirstMod
{

    public static final String MODID = "obstifirstmod";
    public static ItemGroup GROUP = new ItemGroup(MODID) {

        @Override
        public ItemStack createIcon()
        {
            return new ItemStack(ModBlocks.FIRST_BLOCK);
        }
    };

    public ObstiFirstMod() {


        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::doClientStuff);
        modEventBus.addListener(this::setup);

        modEventBus.addGenericListener(Block.class, this::registerBlocks);
        modEventBus.addGenericListener(Item.class, this::registerItems);
        modEventBus.addGenericListener(TileEntityType.class, this::registerTileEntity);
        modEventBus.addGenericListener(ContainerType.class, this::registerContainer);

        IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;


        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> {

        });
    }

    private void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
        event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            return new FirstBlockContainer(windowId, ClientStuff.getClientWorld(),pos,inv, ClientStuff.getClientPlayer());
        }).setRegistryName("first_block"));
    }

    private void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event) {
        event.getRegistry().register(TileEntityType.Builder.create(FirstBlockTile::new, ModBlocks.FIRST_BLOCK).build(null).setRegistryName("first_block"));
    }

    private void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new BlockItem(ModBlocks.FIRST_BLOCK, new Item.Properties().group(GROUP)).setRegistryName("first_block"));
    }

    private void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(new FirstBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(2)).setRegistryName("first_block"));
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        ClientInit.registerScreens();
    }
}
