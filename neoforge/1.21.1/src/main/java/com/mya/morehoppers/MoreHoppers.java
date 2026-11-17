package com.mya.morehoppers;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

@Mod("morehoppers")
public class MoreHoppers {
   public static final String MODID = "morehoppers";

   public MoreHoppers(IEventBus modEventBus, ModContainer modContainer) {
      ModBlocks.BLOCKS.register(modEventBus);
      ModItems.ITEMS.register(modEventBus);
      ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
      modEventBus.addListener(this::addCreative);
      modEventBus.addListener(this::registerCapabilities);
   }

   private void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerBlockEntity(ItemHandler.BLOCK, ModBlockEntities.TIERED_HOPPER.get(), (hopper, side) -> new InvWrapper(hopper));
   }

   private void addCreative(BuildCreativeModeTabContentsEvent event) {
      if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
         event.accept(ModItems.GOLD_HOPPER);
         event.accept(ModItems.DIAMOND_HOPPER);
         event.accept(ModItems.EMERALD_HOPPER);
         event.accept(ModItems.NETHERITE_HOPPER);
      }
   }
}
