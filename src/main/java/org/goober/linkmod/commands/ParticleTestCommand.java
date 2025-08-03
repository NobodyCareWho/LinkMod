package org.goober.linkmod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.goober.linkmod.particlestuff.LmodParticleRegistry;

public class ParticleTestCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("particletest")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.argument("particle", StringArgumentType.word())
                .suggests((context, builder) -> {
                    builder.suggest("smoke_ring");
                    builder.suggest("exp_chest");
                    builder.suggest("portal");
                    return builder.buildFuture();
                })
                .executes(context -> {
                    return spawnParticles(context.getSource(), StringArgumentType.getString(context, "particle"), 10);
                })
                .then(CommandManager.argument("count", IntegerArgumentType.integer(1, 100))
                    .executes(context -> {
                        return spawnParticles(context.getSource(), StringArgumentType.getString(context, "particle"), IntegerArgumentType.getInteger(context, "count"));
                    })
                )
            )
        );
    }
    
    private static int spawnParticles(ServerCommandSource source, String particleType, int count) {
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) return 0;
        
        ServerWorld world = player.getWorld();
        Vec3d pos = player.getPos().add(0, 1.5, 0);
        
        for (int i = 0; i < count; i++) {
            double offsetX = (world.random.nextDouble() - 0.5) * 2.0;
            double offsetY = world.random.nextDouble() * 2.0;
            double offsetZ = (world.random.nextDouble() - 0.5) * 2.0;
            
            double velocityX = (world.random.nextDouble() - 0.5) * 0.1;
            double velocityY = world.random.nextDouble() * 0.1;
            double velocityZ = (world.random.nextDouble() - 0.5) * 0.1;
            
            switch (particleType.toLowerCase()) {
                case "smoke_ring" -> {
                    world.spawnParticles(LmodParticleRegistry.SMOKERING, 
                        pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ, 
                        1, 0, 0, 0, 0);
                }
                case "exp_chest" -> {
                    world.spawnParticles(LmodParticleRegistry.EXP_CHEST, 
                        pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ, 
                        1, velocityX, velocityY, velocityZ, 0);
                }
                case "portal" -> {
                    world.spawnParticles(ParticleTypes.PORTAL, 
                        pos.x + offsetX, pos.y + offsetY, pos.z + offsetZ, 
                        1, velocityX, velocityY, velocityZ, 0);
                }
                default -> {
                    source.sendError(Text.literal("Unknown particle type: " + particleType));
                    return 0;
                }
            }
        }
        
        source.sendFeedback(() -> Text.literal("Spawned " + count + " " + particleType + " particles"), false);
        return 1;
    }
}