package mekanism.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.BubbleParticle;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;

import javax.annotation.Nonnull;


public class ScubaBubbleParticle extends BubbleParticle {

    private ScubaBubbleParticle(World world, double posX, double posY, double posZ, double velX, double velY, double velZ) {
        super(world, posX, posY, posZ, velX, velY, velZ);
        maxAge *= 2;
    }

    @Override
    public void tick() {
        super.tick();
        age++;
    }

    @Override
    public void renderParticle(IVertexBuilder vertexBuilder, ActiveRenderInfo renderInfo, float partialTicks) {
        if (age > 0) {
            super.renderParticle(vertexBuilder, renderInfo, partialTicks);
        }
    }

    public static class Factory implements IParticleFactory<BasicParticleType> {

        private final IAnimatedSprite spriteSet;

        public Factory(IAnimatedSprite spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle makeParticle(@Nonnull BasicParticleType type, @Nonnull World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            ScubaBubbleParticle particle = new ScubaBubbleParticle(world, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.selectSpriteRandomly(this.spriteSet);
            return particle;
        }
    }
}