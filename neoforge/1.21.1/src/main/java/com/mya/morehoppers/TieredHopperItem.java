package com.mya.morehoppers;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Item.TooltipContext;

public class TieredHopperItem extends BlockItem {
   private final HopperTier tier;

   public TieredHopperItem(TieredHopperBlock block, Properties properties) {
      super(block, properties);
      this.tier = block.tier;
   }

   public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
      tooltip.add(Component.translatable("tooltip.morehoppers.cooldown", new Object[]{this.tier.cooldown}).withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable("tooltip.morehoppers.per_transfer", new Object[]{this.tier.perTransfer}).withStyle(ChatFormatting.GRAY));
      tooltip.add(Component.translatable("tooltip.morehoppers.slots", new Object[]{this.tier.slots()}).withStyle(ChatFormatting.GRAY));
   }
}
