package org.goober.linkmod.villagerstuff;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.goober.linkmod.itemstuff.LmodItemRegistry;

import java.util.Optional;

public class LmodVillagerTrades {
    
    public static void initialize() {
        // villager levels:
        // level 1 (Novice): 0-9 XP
        // level 2 (Apprentice): 10-69 XP  
        // level 3 (Journeyman): 70-149 XP
        // level 4 (Expert): 150-249 XP
        // level 5 (Master): 250+ XP
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC, 3, factories -> {
            factories.add(new SilverBulletTradeFactory());
        });
    }

    static class SilverBulletTradeFactory implements TradeOffers.Factory {
        @Override
        public TradeOffer create(Entity entity, Random random) {
            // 16-32 emeralds for 4 silver bullets
            int emeraldCost = 3 + random.nextInt(12); // 16-32 emeralds (nextInt(17) gives 0-16)
            return new TradeOffer(
                new TradedItem(Items.EMERALD, emeraldCost),
                new ItemStack(LmodItemRegistry.SILVER_BULLET, 6),
                5, // max uses before restocking
                15, // villager experience
                0.08f // price multiplier
            );
        }
    }
}