package mekanism.common.block.attribute;

import mekanism.common.content.blocktype.FactoryType;

import javax.annotation.Nonnull;

public class AttributeFactoryType implements Attribute {

    private final FactoryType type;

    public AttributeFactoryType(FactoryType type) {
        this.type = type;
    }

    @Nonnull
    public FactoryType getFactoryType() {
        return type;
    }
}