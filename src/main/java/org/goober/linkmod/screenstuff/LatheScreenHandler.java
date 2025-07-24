package org.goober.linkmod.screenstuff;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.display.CuttingRecipeDisplay;
import net.minecraft.recipe.display.CuttingRecipeDisplay.Grouping;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.recipe.input.SingleStackRecipeInput;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import org.goober.linkmod.blockstuff.LmodBlockRegistry;
import org.goober.linkmod.recipestuff.LatheRecipe;
import org.goober.linkmod.recipestuff.LatheRecipeRegistry;
import org.goober.linkmod.recipestuff.LmodRecipeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LatheScreenHandler extends ScreenHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LatheScreenHandler.class);
    public static final int INPUT_ID = 0;
    public static final int OUTPUT_ID = 1;
    private static final int INVENTORY_START = 2;
    private static final int INVENTORY_END = 29;
    private static final int OUTPUT_START = 29;
    private static final int OUTPUT_END = 38;
    private final ScreenHandlerContext context;
    final Property selectedRecipe;
    private final World world;
    private CuttingRecipeDisplay.Grouping<LatheRecipe> availableRecipes;
    private ItemStack inputStack;
    long lastTakeTime;
    final Slot inputSlot;
    final Slot outputSlot;
    Runnable contentsChangedListener;
    public final Inventory input;
    final CraftingResultInventory output;

    public LatheScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY);
    }

    public LatheScreenHandler(int syncId, PlayerInventory playerInventory, final ScreenHandlerContext context) {
        super(LmodScreenHandlerType.LATHE, syncId);
        this.selectedRecipe = Property.create();
        this.availableRecipes = Grouping.empty();
        this.inputStack = ItemStack.EMPTY;
        this.contentsChangedListener = () -> {
        };
        this.input = new SimpleInventory(1) {
            public void markDirty() {
                super.markDirty();
                LatheScreenHandler.this.onContentChanged(this);
                LatheScreenHandler.this.contentsChangedListener.run();
            }
        };
        this.output = new CraftingResultInventory();
        this.context = context;
        this.world = playerInventory.player.getWorld();
        this.inputSlot = this.addSlot(new Slot(this.input, 0, 20, 33));
        this.outputSlot = this.addSlot(new Slot(this.output, 1, 143, 33) {
            public boolean canInsert(ItemStack stack) {
                return false;
            }

            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                stack.onCraftByPlayer(player, stack.getCount());
                LatheScreenHandler.this.output.unlockLastRecipe(player, this.getInputStacks());
                ItemStack itemStack = LatheScreenHandler.this.inputSlot.takeStack(1);
                if (!itemStack.isEmpty()) {
                    LatheScreenHandler.this.populateResult(LatheScreenHandler.this.selectedRecipe.get());
                }

                context.run((world, pos) -> {
                    long l = world.getTime();
                    if (LatheScreenHandler.this.lastTakeTime != l) {
                        world.playSound((Entity)null, pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        LatheScreenHandler.this.lastTakeTime = l;
                    }

                });
                super.onTakeItem(player, stack);
            }

            private List<ItemStack> getInputStacks() {
                return List.of(LatheScreenHandler.this.inputSlot.getStack());
            }
        });
        this.addPlayerSlots(playerInventory, 8, 84);
        this.addProperty(this.selectedRecipe);
    }

    public int getSelectedRecipe() {
        return this.selectedRecipe.get();
    }

    public CuttingRecipeDisplay.Grouping<LatheRecipe> getAvailableRecipes() {
        return this.availableRecipes;
    }

    public int getAvailableRecipeCount() {
        return this.availableRecipes.size();
    }

    public boolean canCraft() {
        return this.inputSlot.hasStack() && !this.availableRecipes.isEmpty();
    }

    public boolean canUse(PlayerEntity player) {
        return canUse(this.context, player, LmodBlockRegistry.LATHE);
    }

    public boolean onButtonClick(PlayerEntity player, int id) {
        if (this.selectedRecipe.get() == id) {
            return false;
        } else {
            if (this.isInBounds(id)) {
                this.selectedRecipe.set(id);
                this.populateResult(id);
            }

            return true;
        }
    }

    private boolean isInBounds(int id) {
        return id >= 0 && id < this.availableRecipes.size();
    }

    public void onContentChanged(Inventory inventory) {
        ItemStack itemStack = this.inputSlot.getStack();
        if (!itemStack.isOf(this.inputStack.getItem())) {
            this.inputStack = itemStack.copy();
            this.updateInput(itemStack);
        }

    }

    private void updateInput(ItemStack stack) {
        LOGGER.info("updateInput called with stack: {}", stack);
        this.selectedRecipe.set(-1);
        this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
        if (!stack.isEmpty()) {
            LOGGER.info("World type: {}, isClient: {}", this.world.getClass().getName(), this.world.isClient);
            
            // Use the synced LatheRecipeRegistry which works on both client and server
            this.availableRecipes = LatheRecipeRegistry.getFilteredRecipes(stack);
            LOGGER.info("Found {} matching recipes from registry", this.availableRecipes.size());
        } else {
            this.availableRecipes = Grouping.empty();
        }
    }

    void populateResult(int selectedId) {
        Optional<RecipeEntry<LatheRecipe>> optional;
        if (!this.availableRecipes.isEmpty() && this.isInBounds(selectedId)) {
            CuttingRecipeDisplay.GroupEntry<LatheRecipe> groupEntry = (CuttingRecipeDisplay.GroupEntry)this.availableRecipes.entries().get(selectedId);
            optional = groupEntry.recipe().recipe();
        } else {
            optional = Optional.empty();
        }

        // Handle the case where we don't have a RecipeEntry (client-side)
        if (optional.isEmpty() && !this.availableRecipes.isEmpty() && this.isInBounds(selectedId)) {
            // Get the recipe directly from the registry
            Optional<LatheRecipe> recipeOpt = LatheRecipeRegistry.getRecipeByIndex(this.input.getStack(0), selectedId);
            recipeOpt.ifPresent(recipe -> {
                this.outputSlot.setStackNoCallbacks(recipe.craft(new SingleStackRecipeInput(this.input.getStack(0)), this.world.getRegistryManager()));
            });
            if (recipeOpt.isEmpty()) {
                this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
            }
        } else {
            optional.ifPresentOrElse((recipe) -> {
                this.output.setLastRecipe(recipe);
                this.outputSlot.setStackNoCallbacks(((LatheRecipe)recipe.value()).craft(new SingleStackRecipeInput(this.input.getStack(0)), this.world.getRegistryManager()));
            }, () -> {
                this.outputSlot.setStackNoCallbacks(ItemStack.EMPTY);
                this.output.setLastRecipe((RecipeEntry)null);
            });
        }
        this.sendContentUpdates();
    }

    public ScreenHandlerType<?> getType() {
        return LmodScreenHandlerType.LATHE;
    }

    public void setContentsChangedListener(Runnable contentsChangedListener) {
        this.contentsChangedListener = contentsChangedListener;
    }

    public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
        return slot.inventory != this.output && super.canInsertIntoSlot(stack, slot);
    }

    private boolean isValidIngredient(ItemStack stack) {
        // Use the synced LatheRecipeRegistry which works on both client and server
        return LatheRecipeRegistry.isValidInput(stack);
    }
    public ItemStack quickMove(PlayerEntity player, int slot) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = (Slot)this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            Item item = itemStack2.getItem();
            itemStack = itemStack2.copy();
            if (slot == 1) {
                item.onCraftByPlayer(itemStack2, player);
                if (!this.insertItem(itemStack2, 2, 38, true)) {
                    return ItemStack.EMPTY;
                }

                slot2.onQuickTransfer(itemStack2, itemStack);
            } else if (slot == 0) {
                if (!this.insertItem(itemStack2, 2, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.isValidIngredient(itemStack2)) {
                if (!this.insertItem(itemStack2, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot >= 2 && slot < 29) {
                if (!this.insertItem(itemStack2, 29, 38, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slot >= 29 && slot < 38 && !this.insertItem(itemStack2, 2, 29, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            }

            slot2.markDirty();
            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot2.onTakeItem(player, itemStack2);
            if (slot == 1) {
                player.dropItem(itemStack2, false);
            }

            this.sendContentUpdates();
        }

        return itemStack;
    }

    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.output.removeStack(1);
        this.context.run((world, pos) -> {
            this.dropInventory(player, this.input);
        });
    }

    private void addPlayerSlots(PlayerInventory playerInventory, int x, int y) {
        int i;
        int j;
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }

        for (i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, x + i * 18, y + 58));
        }
    }
}