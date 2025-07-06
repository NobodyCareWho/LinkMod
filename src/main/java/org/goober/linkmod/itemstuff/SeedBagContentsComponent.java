package org.goober.linkmod.itemstuff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.collection.DefaultedList;

import java.util.ArrayList;
import java.util.List;

public record SeedBagContentsComponent(List<ItemStack> items) {
    public static final SeedBagContentsComponent EMPTY = new SeedBagContentsComponent(DefaultedList.of());
    
    public static final Codec<SeedBagContentsComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(component -> 
                component.items.stream().filter(stack -> !stack.isEmpty()).toList()
            )
        ).apply(instance, SeedBagContentsComponent::new)
    );
    
    public static final PacketCodec<RegistryByteBuf, SeedBagContentsComponent> PACKET_CODEC = new PacketCodec<RegistryByteBuf, SeedBagContentsComponent>() {
        @Override
        public SeedBagContentsComponent decode(RegistryByteBuf buf) {
            int size = PacketCodecs.VAR_INT.decode(buf);
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                items.add(ItemStack.PACKET_CODEC.decode(buf));
            }
            return new SeedBagContentsComponent(items);
        }

        @Override
        public void encode(RegistryByteBuf buf, SeedBagContentsComponent component) {
            // Write only non-empty stacks
            List<ItemStack> nonEmptyStacks = component.items().stream()
                .filter(stack -> !stack.isEmpty())
                .toList();
            PacketCodecs.VAR_INT.encode(buf, nonEmptyStacks.size());
            for (ItemStack stack : nonEmptyStacks) {
                ItemStack.PACKET_CODEC.encode(buf, stack);
            }
        }
    };
    
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    public ItemStack get(int index) {
        if (index >= 0 && index < items.size()) {
            return items.get(index);
        }
        return ItemStack.EMPTY;
    }
    
    public int getTotalCount() {
        int count = 0;
        for (ItemStack stack : items) {
            count += stack.getCount();
        }
        return count;
    }
    
    public static class Builder {
        private final DefaultedList<ItemStack> items;
        
        public Builder() {
            this.items = DefaultedList.ofSize(8, ItemStack.EMPTY);
        }
        
        public Builder(SeedBagContentsComponent component) {
            this.items = DefaultedList.ofSize(8, ItemStack.EMPTY);
            for (int i = 0; i < component.items.size() && i < 8; i++) {
                this.items.set(i, component.items.get(i).copy());
            }
        }
        
        public void setStack(int slot, ItemStack stack) {
            if (slot >= 0 && slot < 8) {
                this.items.set(slot, stack.copy());
            }
        }
        
        public ItemStack getStack(int slot) {
            if (slot >= 0 && slot < 8) {
                return this.items.get(slot);
            }
            return ItemStack.EMPTY;
        }
        
        public void clear() {
            for (int i = 0; i < 8; i++) {
                this.items.set(i, ItemStack.EMPTY);
            }
        }
        
        public SeedBagContentsComponent build() {
            return new SeedBagContentsComponent(DefaultedList.copyOf(ItemStack.EMPTY, items.toArray(new ItemStack[0])));
        }
    }
}