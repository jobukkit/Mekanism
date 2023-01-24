package mekanism.common.block.interfaces;

import mekanism.api.text.ILangEntry;

import javax.annotation.Nonnull;

public interface IHasDescription {

    @Nonnull
    ILangEntry getDescription();
}