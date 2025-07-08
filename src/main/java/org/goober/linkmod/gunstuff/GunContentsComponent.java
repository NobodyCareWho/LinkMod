package org.goober.linkmod.gunstuff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;

public record GunContentsComponent(List<ItemStack> items) {
    public static final GunContentsComponent EMPTY = new GunContentsComponent(new ArrayList<>());
    
    public static final Codec<GunContentsComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(component -> 
                component.items.stream().filter(stack -> !stack.isEmpty()).toList()
            )
        ).apply(instance, GunContentsComponent::new)
    );
    
    public static final PacketCodec<RegistryByteBuf, GunContentsComponent> PACKET_CODEC = new PacketCodec<RegistryByteBuf, GunContentsComponent>() {
        @Override
        public GunContentsComponent decode(RegistryByteBuf buf) {
            int size = PacketCodecs.VAR_INT.decode(buf);
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                items.add(ItemStack.PACKET_CODEC.decode(buf));
            }
            return new GunContentsComponent(items);
        }

        @Override
        public void encode(RegistryByteBuf buf, GunContentsComponent component) {
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
        private final List<ItemStack> items;
        
        public Builder() {
            this.items = new ArrayList<>();
        }
        
        public Builder(GunContentsComponent component) {
            this.items = new ArrayList<>();
            for (ItemStack stack : component.items) {
                this.items.add(stack.copy());
            }
        }
        
        public int add(ItemStack stack, int maxCapacity) {
            if (stack.isEmpty()) return 0;
            
            int remainingCapacity = maxCapacity - getTotalCount();
            int toAdd = Math.min(stack.getCount(), remainingCapacity);
            if (toAdd <= 0) return 0;
            
            int added = 0;
            
            // First try to merge with existing stacks
            for (ItemStack existing : items) {
                if (ItemStack.areItemsAndComponentsEqual(existing, stack)) {
                    int canAdd = Math.min(toAdd, existing.getMaxCount() - existing.getCount());
                    if (canAdd > 0) {
                        existing.increment(canAdd);
                        added += canAdd;
                        toAdd -= canAdd;
                        if (toAdd <= 0) break;
                    }
                }
            }
            
            // Add as new stack if needed
            if (toAdd > 0) {
                ItemStack newStack = stack.copy();
                newStack.setCount(toAdd);
                items.add(newStack);
                added += toAdd;
            }
            
            return added;
        }
        
        // overload for backwards compatibility
        public int add(ItemStack stack) {
            return add(stack, Integer.MAX_VALUE);
        }
        
        public ItemStack removeFirst() {
            if (items.isEmpty()) return ItemStack.EMPTY;
            ItemStack stack = items.remove(0);
            return stack;
        }
        
        public ItemStack removeOne() {
            if (items.isEmpty()) return ItemStack.EMPTY;
            ItemStack stack = items.get(0);
            ItemStack result = stack.split(1);
            if (stack.isEmpty()) {
                items.remove(0);
            }
            return result;
        }
        
        public void clear() {
            items.clear();
        }
        
        public int getTotalCount() {
            return items.stream().mapToInt(ItemStack::getCount).sum();
        }
        
        public int getRemainingCapacity(int maxCapacity) {
            return maxCapacity - getTotalCount();
        }
        
        public GunContentsComponent build() {
            // Remove any empty stacks
            items.removeIf(ItemStack::isEmpty);
            return new GunContentsComponent(new ArrayList<>(items));
        }
    }
}