package org.goober.linkmod.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.goober.linkmod.Linkmod;
import org.goober.linkmod.util.ExperienceHelper;

public record ExpChestOperationC2SPacket(Operation operation, int amount) implements CustomPayload {
    public static final CustomPayload.Id<ExpChestOperationC2SPacket> ID = 
        new CustomPayload.Id<>(Identifier.of("lmod", "exp_chest_operation"));
    
    public static final PacketCodec<RegistryByteBuf, ExpChestOperationC2SPacket> CODEC = PacketCodec.of(
        (value, buf) -> write(buf, value),
        buf -> read(buf)
    );
    
    private static void write(RegistryByteBuf buf, ExpChestOperationC2SPacket packet) {
        buf.writeEnumConstant(packet.operation);
        buf.writeVarInt(packet.amount);
    }
    
    private static ExpChestOperationC2SPacket read(RegistryByteBuf buf) {
        Operation operation = buf.readEnumConstant(Operation.class);
        int amount = buf.readVarInt();
        return new ExpChestOperationC2SPacket(operation, amount);
    }
    
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
    
    public enum Operation {
        DEPOSIT,
        DEPOSIT_ALL,
        WITHDRAW,
        WITHDRAW_ALL
    }
    
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (packet, context) -> {
            context.server().execute(() -> {
                var player = context.player();
                boolean success = false;
                
                switch (packet.operation) {
                    case DEPOSIT:
                        success = ExperienceHelper.depositExp(player, packet.amount);
                        break;
                    case DEPOSIT_ALL:
                        int totalExp = Integer.parseInt(ExperienceHelper.getPlayerTotalExp(player));
                        success = ExperienceHelper.depositExp(player, totalExp);
                        break;
                    case WITHDRAW:
                        success = ExperienceHelper.withdrawExp(player, packet.amount);
                        break;
                    case WITHDRAW_ALL:
                        int bankedExp = Integer.parseInt(ExperienceHelper.getBankedExp(player));
                        success = ExperienceHelper.withdrawExp(player, bankedExp);
                        break;
                }
                
                // The attachment will automatically sync to client due to syncWith configuration
            });
        });
    }
}