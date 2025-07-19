package org.goober.linkmod.gunstuff;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record GunBloomComponent(float currentBloom, long lastUpdateTime) {
    public static final GunBloomComponent DEFAULT = new GunBloomComponent(0f, 0L);
    
    public static final Codec<GunBloomComponent> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.FLOAT.fieldOf("bloom").forGetter(GunBloomComponent::currentBloom),
            Codec.LONG.fieldOf("time").forGetter(GunBloomComponent::lastUpdateTime)
        ).apply(instance, GunBloomComponent::new)
    );
    
    public static final PacketCodec<RegistryByteBuf, GunBloomComponent> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.FLOAT, GunBloomComponent::currentBloom,
        PacketCodecs.VAR_LONG, GunBloomComponent::lastUpdateTime,
        GunBloomComponent::new
    );
    
    public GunBloomComponent withBloom(float bloom) {
        return new GunBloomComponent(bloom, System.currentTimeMillis());
    }
    
    public GunBloomComponent withDecay(float decayRate) {
        long currentTime = System.currentTimeMillis();
        float secondsPassed = (currentTime - lastUpdateTime) / 1000f;
        float decayAmount = secondsPassed * decayRate;
        float newBloom = Math.max(0, currentBloom - decayAmount);
        return new GunBloomComponent(newBloom, currentTime);
    }
}