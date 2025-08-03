package org.goober.linkmod.blockstuff.blockentities;

import com.mojang.serialization.JsonOps;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.goober.linkmod.blockstuff.LmodBlockEntityTypes;
import net.minecraft.block.entity.LidOpenable;
import org.goober.linkmod.screenstuff.ExpChestScreenHandler;
import org.jetbrains.annotations.Nullable;

public class ExpChestBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, Inventory, LidOpenable {
    private static final int INVENTORY_SIZE = 54; // 6 rows * 9 columns
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    private Text customName;
    
    // animation fields
    private float lidAngle;
    private float prevLidAngle;
    private int viewerCount;

    public ExpChestBlockEntity(BlockPos pos, BlockState state) {
        super(LmodBlockEntityTypes.EXP_CHEST, pos, state);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);

        // inventory
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        Inventories.readData(view, this.inventory);   // ⇐ new helper

        // custom name
        this.customName = BlockEntity.tryParseCustomName(view, "CustomName");
        
        // viewer count for client animation
        view.getOptionalInt("ViewerCount").ifPresent(count -> this.viewerCount = count);
    }


    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);

        Inventories.writeData(view, this.inventory);

        if (this.customName != null) {
            var jsonElement = TextCodecs.CODEC
                    .encodeStart(JsonOps.INSTANCE, this.customName)  // note JsonOps
                    .getOrThrow();

            view.putString("CustomName", jsonElement.toString());   // or new Gson().toJson(...)
        }
        
        // sync viewer count for client animation
        view.putInt("ViewerCount", this.viewerCount);
    }


    @Nullable
    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public Text getDisplayName() {
        return this.customName != null ? this.customName : Text.translatable("container.lmod.exp_chest");
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ExpChestScreenHandler(syncId, playerInventory, this);
    }

    // inventory implementation
    @Override
    public int size() {
        return INVENTORY_SIZE;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : this.inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        return slot >= 0 && slot < this.inventory.size() ? this.inventory.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(this.inventory, slot, amount);
        if (!result.isEmpty()) {
            this.markDirty();
        }
        return result;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(this.inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 0 && slot < this.inventory.size()) {
            this.inventory.set(slot, stack);
            if (stack.getCount() > this.getMaxCountPerStack()) {
                stack.setCount(this.getMaxCountPerStack());
            }
            this.markDirty();
        }
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return Inventory.canPlayerUse(this, player);
    }

    @Override
    public void clear() {
        this.inventory.clear();
        this.markDirty();
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world != null) {
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), 3);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, ExpChestBlockEntity blockEntity) {
        blockEntity.updateLidAnimation();
    }
    
    private void updateLidAnimation() {
        this.prevLidAngle = this.lidAngle;
        if (this.viewerCount > 0 && this.lidAngle == 0.0F) {
            this.playSound(net.minecraft.sound.SoundEvents.BLOCK_CHEST_OPEN);
        }
        
        if (this.viewerCount == 0 && this.lidAngle > 0.0F || this.viewerCount > 0 && this.lidAngle < 1.0F) {
            float prevAngle = this.lidAngle;
            if (this.viewerCount > 0) {
                this.lidAngle += 0.1F;
            } else {
                this.lidAngle -= 0.1F;
            }
            
            if (this.lidAngle > 1.0F) {
                this.lidAngle = 1.0F;
            }
            
            if (this.lidAngle < 0.5F && prevAngle >= 0.5F) {
                this.playSound(net.minecraft.sound.SoundEvents.BLOCK_CHEST_CLOSE);
            }
            
            if (this.lidAngle < 0.0F) {
                this.lidAngle = 0.0F;
            }
        }
    }
    
    private void playSound(net.minecraft.sound.SoundEvent soundEvent) {
        double x = (double)this.pos.getX() + 0.5;
        double y = (double)this.pos.getY() + 0.5;
        double z = (double)this.pos.getZ() + 0.5;
        this.world.playSound(null, x, y, z, soundEvent, net.minecraft.sound.SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
    }
    
    @Override
    public float getAnimationProgress(float tickDelta) {
        return MathHelper.lerp(tickDelta, this.prevLidAngle, this.lidAngle);
    }
    
    @Override
    public void onOpen(PlayerEntity player) {
        if (!player.isSpectator()) {
            this.viewerCount++;
            if (this.world != null && !this.world.isClient) {
                this.markDirty();
            }
        }
    }
    
    @Override
    public void onClose(PlayerEntity player) {
        if (!player.isSpectator()) {
            this.viewerCount--;
            if (this.world != null && !this.world.isClient) {
                this.markDirty();
            }
        }
    }
}