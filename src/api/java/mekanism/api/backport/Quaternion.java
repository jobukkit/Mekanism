package mekanism.api.backport;

import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class Quaternion {
    public static final Quaternion ONE = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion(float p_i48100_1_, float p_i48100_2_, float p_i48100_3_, float p_i48100_4_) {
        this.x = p_i48100_1_;
        this.y = p_i48100_2_;
        this.z = p_i48100_3_;
        this.w = p_i48100_4_;
    }

    public Quaternion(Vector3f p_i48101_1_, float p_i48101_2_, boolean p_i48101_3_) {
        if (p_i48101_3_) {
            p_i48101_2_ *= 0.017453292F;
        }

        float lvt_4_1_ = sin(p_i48101_2_ / 2.0F);
        this.x = p_i48101_1_.getX() * lvt_4_1_;
        this.y = p_i48101_1_.getY() * lvt_4_1_;
        this.z = p_i48101_1_.getZ() * lvt_4_1_;
        this.w = cos(p_i48101_2_ / 2.0F);
    }

    @OnlyIn(Dist.CLIENT)
    public Quaternion(float p_i48102_1_, float p_i48102_2_, float p_i48102_3_, boolean p_i48102_4_) {
        if (p_i48102_4_) {
            p_i48102_1_ *= 0.017453292F;
            p_i48102_2_ *= 0.017453292F;
            p_i48102_3_ *= 0.017453292F;
        }

        float lvt_5_1_ = sin(0.5F * p_i48102_1_);
        float lvt_6_1_ = cos(0.5F * p_i48102_1_);
        float lvt_7_1_ = sin(0.5F * p_i48102_2_);
        float lvt_8_1_ = cos(0.5F * p_i48102_2_);
        float lvt_9_1_ = sin(0.5F * p_i48102_3_);
        float lvt_10_1_ = cos(0.5F * p_i48102_3_);
        this.x = lvt_5_1_ * lvt_8_1_ * lvt_10_1_ + lvt_6_1_ * lvt_7_1_ * lvt_9_1_;
        this.y = lvt_6_1_ * lvt_7_1_ * lvt_10_1_ - lvt_5_1_ * lvt_8_1_ * lvt_9_1_;
        this.z = lvt_5_1_ * lvt_7_1_ * lvt_10_1_ + lvt_6_1_ * lvt_8_1_ * lvt_9_1_;
        this.w = lvt_6_1_ * lvt_8_1_ * lvt_10_1_ - lvt_5_1_ * lvt_7_1_ * lvt_9_1_;
    }

    public Quaternion(Quaternion p_i48103_1_) {
        this.x = p_i48103_1_.x;
        this.y = p_i48103_1_.y;
        this.z = p_i48103_1_.z;
        this.w = p_i48103_1_.w;
    }

    public boolean equals(Object p_equals_1_) {
        if (this == p_equals_1_) {
            return true;
        } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
            Quaternion lvt_2_1_ = (Quaternion)p_equals_1_;
            if (Float.compare(lvt_2_1_.x, this.x) != 0) {
                return false;
            } else if (Float.compare(lvt_2_1_.y, this.y) != 0) {
                return false;
            } else if (Float.compare(lvt_2_1_.z, this.z) != 0) {
                return false;
            } else {
                return Float.compare(lvt_2_1_.w, this.w) == 0;
            }
        } else {
            return false;
        }
    }

    public int hashCode() {
        int lvt_1_1_ = Float.floatToIntBits(this.x);
        lvt_1_1_ = 31 * lvt_1_1_ + Float.floatToIntBits(this.y);
        lvt_1_1_ = 31 * lvt_1_1_ + Float.floatToIntBits(this.z);
        lvt_1_1_ = 31 * lvt_1_1_ + Float.floatToIntBits(this.w);
        return lvt_1_1_;
    }

    public String toString() {
        StringBuilder lvt_1_1_ = new StringBuilder();
        lvt_1_1_.append("Quaternion[").append(this.getW()).append(" + ");
        lvt_1_1_.append(this.getX()).append("i + ");
        lvt_1_1_.append(this.getY()).append("j + ");
        lvt_1_1_.append(this.getZ()).append("k]");
        return lvt_1_1_.toString();
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public float getZ() {
        return this.z;
    }

    public float getW() {
        return this.w;
    }

    public void multiply(Quaternion p_195890_1_) {
        float lvt_2_1_ = this.getX();
        float lvt_3_1_ = this.getY();
        float lvt_4_1_ = this.getZ();
        float lvt_5_1_ = this.getW();
        float lvt_6_1_ = p_195890_1_.getX();
        float lvt_7_1_ = p_195890_1_.getY();
        float lvt_8_1_ = p_195890_1_.getZ();
        float lvt_9_1_ = p_195890_1_.getW();
        this.x = lvt_5_1_ * lvt_6_1_ + lvt_2_1_ * lvt_9_1_ + lvt_3_1_ * lvt_8_1_ - lvt_4_1_ * lvt_7_1_;
        this.y = lvt_5_1_ * lvt_7_1_ - lvt_2_1_ * lvt_8_1_ + lvt_3_1_ * lvt_9_1_ + lvt_4_1_ * lvt_6_1_;
        this.z = lvt_5_1_ * lvt_8_1_ + lvt_2_1_ * lvt_7_1_ - lvt_3_1_ * lvt_6_1_ + lvt_4_1_ * lvt_9_1_;
        this.w = lvt_5_1_ * lvt_9_1_ - lvt_2_1_ * lvt_6_1_ - lvt_3_1_ * lvt_7_1_ - lvt_4_1_ * lvt_8_1_;
    }

    @OnlyIn(Dist.CLIENT)
    public void multiply(float p_227065_1_) {
        this.x *= p_227065_1_;
        this.y *= p_227065_1_;
        this.z *= p_227065_1_;
        this.w *= p_227065_1_;
    }

    public void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    @OnlyIn(Dist.CLIENT)
    public void set(float p_227066_1_, float p_227066_2_, float p_227066_3_, float p_227066_4_) {
        this.x = p_227066_1_;
        this.y = p_227066_2_;
        this.z = p_227066_3_;
        this.w = p_227066_4_;
    }

    private static float cos(float p_214904_0_) {
        return (float)Math.cos((double)p_214904_0_);
    }

    private static float sin(float p_214903_0_) {
        return (float)Math.sin((double)p_214903_0_);
    }

    @OnlyIn(Dist.CLIENT)
    public void normalize() {
        float lvt_1_1_ = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
        if (lvt_1_1_ > 1.0E-6F) {
            float lvt_2_1_ = MathHelper.fastInvSqrt(lvt_1_1_);
            this.x *= lvt_2_1_;
            this.y *= lvt_2_1_;
            this.z *= lvt_2_1_;
            this.w *= lvt_2_1_;
        } else {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 0.0F;
            this.w = 0.0F;
        }

    }

    @OnlyIn(Dist.CLIENT)
    public Quaternion copy() {
        return new Quaternion(this);
    }
}
