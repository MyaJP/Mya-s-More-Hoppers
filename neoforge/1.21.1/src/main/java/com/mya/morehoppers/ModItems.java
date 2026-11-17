package com.mya.morehoppers;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Items;

public class ModItems {
   public static final Items ITEMS = DeferredRegister.createItems("morehoppers");
   public static final DeferredItem<BlockItem> GOLD_HOPPER = register("gold_hopper", ModBlocks.GOLD_HOPPER, new Properties());
   public static final DeferredItem<BlockItem> DIAMOND_HOPPER = register("diamond_hopper", ModBlocks.DIAMOND_HOPPER, new Properties());
   public static final DeferredItem<BlockItem> EMERALD_HOPPER = register("emerald_hopper", ModBlocks.EMERALD_HOPPER, new Properties());
   public static final DeferredItem<BlockItem> NETHERITE_HOPPER = register("netherite_hopper", ModBlocks.NETHERITE_HOPPER, new Properties().fireResistant());

   private static DeferredItem<BlockItem> register(String name, DeferredBlock<TieredHopperBlock> block, Properties properties) {
      return ITEMS.register(name, () -> new TieredHopperItem((TieredHopperBlock)block.get(), properties));
   }
}
