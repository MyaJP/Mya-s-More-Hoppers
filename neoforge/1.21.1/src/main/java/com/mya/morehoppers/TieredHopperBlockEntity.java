package com.mya.morehoppers;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class TieredHopperBlockEntity extends RandomizableContainerBlockEntity implements Hopper {
   private final HopperTier tier;
   private NonNullList<ItemStack> items;
   private int cooldown;

   public TieredHopperBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.TIERED_HOPPER.get(), pos, state);
      this.tier = state.getBlock() instanceof TieredHopperBlock block ? block.tier : HopperTier.GOLD;
      this.items = NonNullList.withSize(this.tier.slots(), ItemStack.EMPTY);
   }

   public HopperTier getTier() {
      return this.tier;
   }

   public static void serverTick(Level level, BlockPos pos, BlockState state, TieredHopperBlockEntity hopper) {
      if (--hopper.cooldown <= 0) {
         hopper.cooldown = 0;
         if ((Boolean)state.getValue(HopperBlock.ENABLED)) {
            boolean moved = false;
            if (!hopper.isEmpty()) {
               moved = hopper.pushItems(level, pos, state);
            }

            if (!hopper.isFull()) {
               moved |= hopper.pullItems(level, pos);
            }

            if (moved) {
               hopper.cooldown = hopper.tier.cooldown;
               setChanged(level, pos, state);
            }
         }
      }
   }

   private boolean pushItems(Level level, BlockPos pos, BlockState state) {
      Direction facing = (Direction)state.getValue(HopperBlock.FACING);
      IItemHandler target = getHandler(level, pos.relative(facing), facing.getOpposite());
      if (target == null) {
         return false;
      } else {
         int budget = this.tier.perTransfer;

         for (int i = 0; i < this.items.size() && budget > 0; i++) {
            ItemStack stack = (ItemStack)this.items.get(i);
            if (!stack.isEmpty()) {
               int amount = Math.min(budget, stack.getCount());
               ItemStack rest = ItemHandlerHelper.insertItemStacked(target, stack.copyWithCount(amount), false);
               int moved = amount - rest.getCount();
               if (moved > 0) {
                  stack.shrink(moved);
                  budget -= moved;
               }
            }
         }

         return budget < this.tier.perTransfer;
      }
   }

   private boolean pullItems(Level level, BlockPos pos) {
      BlockPos above = pos.above();
      IItemHandler source = getHandler(level, above, Direction.DOWN);
      if (source != null) {
         IItemHandler self = new InvWrapper(this);
         int budget = this.tier.perTransfer;

         for (int i = 0; i < source.getSlots() && budget > 0; i++) {
            ItemStack simulated = source.extractItem(i, budget, true);
            if (!simulated.isEmpty()) {
               int fits = simulated.getCount() - ItemHandlerHelper.insertItemStacked(self, simulated, true).getCount();
               if (fits > 0) {
                  ItemStack extracted = source.extractItem(i, fits, false);
                  ItemStack left = ItemHandlerHelper.insertItemStacked(self, extracted, false);
                  if (!left.isEmpty()) {
                     source.insertItem(i, left, false);
                  }

                  budget -= extracted.getCount() - left.getCount();
               }
            }
         }

         return budget < this.tier.perTransfer;
      } else {
         BlockState aboveState = level.getBlockState(above);
         if (aboveState.isCollisionShapeFullBlock(level, above) && !aboveState.is(BlockTags.DOES_NOT_BLOCK_HOPPERS)) {
            return false;
         } else {
            boolean any = false;

            for (ItemEntity item : HopperBlockEntity.getItemsAtAndAbove(level, this)) {
               any |= this.pickUp(item);
            }

            return any;
         }
      }
   }

   public boolean pickUp(ItemEntity item) {
      return HopperBlockEntity.addItem(this, item);
   }

   @Nullable
   private static IItemHandler getHandler(Level level, BlockPos pos, Direction side) {
      IItemHandler handler = (IItemHandler)level.getCapability(ItemHandler.BLOCK, pos, side);
      if (handler != null) {
         return handler;
      } else {
         Container container = HopperBlockEntity.getContainerAt(level, pos);
         if (container == null) {
            return null;
         } else {
            return (IItemHandler)(container instanceof WorldlyContainer worldly ? new SidedInvWrapper(worldly, side) : new InvWrapper(container));
         }
      }
   }

   private boolean isFull() {
      for (ItemStack stack : this.items) {
         if (stack.isEmpty() || stack.getCount() < stack.getMaxStackSize()) {
            return false;
         }
      }

      return true;
   }

   public double getLevelX() {
      return this.worldPosition.getX() + 0.5;
   }

   public double getLevelY() {
      return this.worldPosition.getY() + 0.5;
   }

   public double getLevelZ() {
      return this.worldPosition.getZ() + 0.5;
   }

   public boolean isGridAligned() {
      return true;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   protected NonNullList<ItemStack> getItems() {
      return this.items;
   }

   protected void setItems(NonNullList<ItemStack> items) {
      this.items = items;
   }

   protected Component getDefaultName() {
      return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
   }

   protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
      MenuType<ChestMenu> type = switch (this.tier.rows) {
         case 1 -> MenuType.GENERIC_9x1;
         case 2 -> MenuType.GENERIC_9x2;
         case 3 -> MenuType.GENERIC_9x3;
         case 4 -> MenuType.GENERIC_9x4;
         case 5 -> MenuType.GENERIC_9x5;
         default -> MenuType.GENERIC_9x6;
      };
      return new ChestMenu(type, containerId, inventory, this, this.tier.rows);
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (!this.tryLoadLootTable(tag)) {
         ContainerHelper.loadAllItems(tag, this.items, registries);
      }

      this.cooldown = tag.getInt("TransferCooldown");
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      if (!this.trySaveLootTable(tag)) {
         ContainerHelper.saveAllItems(tag, this.items, registries);
      }

      tag.putInt("TransferCooldown", this.cooldown);
   }
}
