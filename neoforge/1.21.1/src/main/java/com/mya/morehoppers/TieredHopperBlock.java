package com.mya.morehoppers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class TieredHopperBlock extends HopperBlock {
   public static final MapCodec<TieredHopperBlock> CODEC = RecordCodecBuilder.mapCodec(
      instance -> instance.group(HopperTier.CODEC.fieldOf("tier").forGetter(block -> block.tier), propertiesCodec()).apply(instance, TieredHopperBlock::new)
   );
   public final HopperTier tier;

   public TieredHopperBlock(HopperTier tier, Properties properties) {
      super(properties);
      this.tier = tier;
   }

   @SuppressWarnings("unchecked")
   public MapCodec<HopperBlock> codec() {
      return (MapCodec<HopperBlock>) (MapCodec<?>) CODEC;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TieredHopperBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return level.isClientSide ? null : createTickerHelper(type, (BlockEntityType)ModBlockEntities.TIERED_HOPPER.get(), TieredHopperBlockEntity::serverTick);
   }

   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         if (level.getBlockEntity(pos) instanceof TieredHopperBlockEntity hopper) {
            player.openMenu(hopper);
            player.awardStat(Stats.INSPECT_HOPPER);
         }

         return InteractionResult.CONSUME;
      }
   }

   protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
      if (!level.isClientSide) {
         if (entity instanceof ItemEntity item
            && !item.getItem().isEmpty()
            && level.getBlockEntity(pos) instanceof TieredHopperBlockEntity hopper
            && (Boolean)state.getValue(ENABLED)) {
            hopper.pickUp(item);
         }
      }
   }
}
