package com.mya.morehoppers;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredRegister.Blocks;

public class ModBlocks {
   public static final Blocks BLOCKS = DeferredRegister.createBlocks("morehoppers");
   public static final DeferredBlock<TieredHopperBlock> GOLD_HOPPER = register("gold_hopper", HopperTier.GOLD);
   public static final DeferredBlock<TieredHopperBlock> DIAMOND_HOPPER = register("diamond_hopper", HopperTier.DIAMOND);
   public static final DeferredBlock<TieredHopperBlock> EMERALD_HOPPER = register("emerald_hopper", HopperTier.EMERALD);
   public static final DeferredBlock<TieredHopperBlock> NETHERITE_HOPPER = register("netherite_hopper", HopperTier.NETHERITE);

   private static DeferredBlock<TieredHopperBlock> register(String name, HopperTier tier) {
      return BLOCKS.register(name, () -> new TieredHopperBlock(tier, Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.HOPPER)));
   }
}
