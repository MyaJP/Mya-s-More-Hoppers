package com.mya.morehoppers;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntityType.Builder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "morehoppers");
   public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TieredHopperBlockEntity>> TIERED_HOPPER = BLOCK_ENTITIES.register(
      "tiered_hopper",
      () -> Builder.of(
            TieredHopperBlockEntity::new,
            new Block[]{
               (Block)ModBlocks.GOLD_HOPPER.get(),
               (Block)ModBlocks.DIAMOND_HOPPER.get(),
               (Block)ModBlocks.EMERALD_HOPPER.get(),
               (Block)ModBlocks.NETHERITE_HOPPER.get()
            }
         )
         .build(null)
   );
}
