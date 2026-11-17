package com.mya.morehoppers;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum HopperTier implements StringRepresentable {
   GOLD("gold", 4, 4, 2),
   DIAMOND("diamond", 3, 8, 3),
   EMERALD("emerald", 2, 16, 4),
   NETHERITE("netherite", 1, 64, 6);

   public static final Codec<HopperTier> CODEC = StringRepresentable.fromEnum(HopperTier::values);
   private final String name;
   public final int cooldown;
   public final int perTransfer;
   public final int rows;

   private HopperTier(String name, int cooldown, int perTransfer, int rows) {
      this.name = name;
      this.cooldown = cooldown;
      this.perTransfer = perTransfer;
      this.rows = rows;
   }

   public int slots() {
      return this.rows * 9;
   }

   public String getSerializedName() {
      return this.name;
   }
}
