package mekanism.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import mekanism.client.SpecialColors;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public interface IFancyFontRenderer {

    int getXSize();

    FontRenderer getFont();

    default int titleTextColor() {
        return SpecialColors.TEXT_TITLE.argb();
    }

    default int headingTextColor() {
        return SpecialColors.TEXT_HEADING.argb();
    }

    default int subheadingTextColor() {
        return SpecialColors.TEXT_SUBHEADING.argb();
    }

    default int screenTextColor() {
        return SpecialColors.TEXT_SCREEN.argb();
    }

    default int drawString(ITextComponent component, int x, int y, int color) {
        return getFont().drawString(component.getFormattedText(), x, y, color);
    }

    default int getStringWidth(ITextComponent component) {
        return getFont().getStringWidth(component.getFormattedText());
    }

    default void drawCenteredText(ITextComponent component, float x, float y, int color) {
        drawCenteredText(component, x, 0, y, color);
    }

    default void drawCenteredText(ITextComponent component, float xStart, float areaWidth, float y, int color) {
        int textWidth = getStringWidth(component);
        float centerX = xStart + (areaWidth / 2F) - (textWidth / 2F);
        drawTextExact(component, centerX, y, color);
    }

    default void drawTitleText( ITextComponent text, float y) {
        drawCenteredTextScaledBound(text, getXSize() - 8, y, titleTextColor());
    }

    default void drawScaledCenteredText(ITextComponent text, float left, float y, int color, float scale) {
        int textWidth = getStringWidth(text);
        float centerX = left - (textWidth / 2F) * scale;
        drawTextWithScale(text, centerX, y, color, scale);
    }

    default void drawCenteredTextScaledBound(ITextComponent text, float maxLength, float y, int color) {
        float scale = Math.min(1, maxLength / getStringWidth(text));
        drawScaledCenteredText(text, getXSize() / 2F, y, color, scale);
    }

    default void drawTextExact(ITextComponent text, float x, float y, int color) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y, 0);
        drawString(text, 0, 0, color);
        RenderSystem.popMatrix();
    }

    default float getNeededScale(ITextComponent text, float maxLength) {
        int length = getStringWidth(text);
        return length <= maxLength ? 1 : maxLength / length;
    }

    default void drawTextScaledBound(String text, float x, float y, int color, float maxLength) {
        drawTextScaledBound(text, x, y, color, maxLength);
    }

    default void drawTextScaledBound(ITextComponent component, float x, float y, int color, float maxLength) {
        int length = getStringWidth(component);

        if (length <= maxLength) {
            drawTextExact(component, x, y, color);
        } else {
            drawTextWithScale(component, x, y, color, maxLength / length);
        }
        //Make sure the color does not leak from having drawn the string
        MekanismRenderer.resetColor();
    }

    default void drawScaledTextScaledBound( ITextComponent text, float x, float y, int color, float maxX, float textScale) {
        float width = getStringWidth(text) * textScale;
        float scale = Math.min(1, maxX / width) * textScale;
        drawTextWithScale(text, x, y, color, scale);
    }

    default void drawTextWithScale( ITextComponent text, float x, float y, int color, float scale) {
        prepTextScale(m -> drawString(text, 0, 0, color), x, y, scale);
    }

    default void prepTextScale(Consumer<MatrixStack> runnable, float x, float y, float scale) {
        float yAdd = 4 - (scale * 8) / 2F;
        RenderSystem.pushMatrix();
        RenderSystem.translatef(x, y + yAdd, 0);
        RenderSystem.scalef(scale, scale, scale);
        runnable.accept(null  );
        RenderSystem.popMatrix();
        MekanismRenderer.resetColor();
    }

    default void drawWrappedCenteredText(ITextComponent text, float x, float y, int color, float maxLength) {
        new WrappedTextRenderer(this).render(text.getString(), x, y, color, maxLength);
    }

    // efficient tool to draw word-by-word wrapped text based on a horizontal bound. looks intimidating but runs in O(n)
    class WrappedTextRenderer {

        final IFancyFontRenderer font;
        final List<Pair<ITextComponent, Float>> linesToDraw = new ArrayList<>();
        StringBuilder lineBuilder = new StringBuilder(), wordBuilder = new StringBuilder();
        float lineLength = 0, wordLength = 0;
        final float SPACE_LENGTH;

        WrappedTextRenderer(IFancyFontRenderer font) {
            this.font = font;
            SPACE_LENGTH = font.getFont().getStringWidth(" ");
        }

        void render(String text, float x, float y, int color, float maxLength) {
            for (char c : text.toCharArray()) {
                if (c == ' ') {
                    addWord(maxLength);
                    continue;
                }
                wordBuilder.append(c);
                wordLength += font.getFont().getStringWidth(Character.toString(c));
            }
            if (wordBuilder.length() > 0) {
                addWord(maxLength);
            }
            if (lineBuilder.length() > 0) {
                linesToDraw.add(Pair.of(new StringTextComponent(lineBuilder.toString()), lineLength));
            }
            float startY = y;
            for (Pair<ITextComponent, Float> p : linesToDraw) {
                font.drawTextExact(p.getLeft(), x - p.getRight() / 2, startY, color);
                startY += 9;
            }
        }

        void addWord(float maxLength) {
            // ignore spacing if this is the first word of the line
            float spacingLength = lineBuilder.length() == 0 ? 0 : SPACE_LENGTH;
            if (lineLength + spacingLength + wordLength > maxLength) {
                linesToDraw.add(Pair.of(new StringTextComponent(lineBuilder.toString()), lineLength));
                lineBuilder = new StringBuilder(wordBuilder);
                lineLength = wordLength;
            } else {
                if (spacingLength > 0) {
                    lineBuilder.append(" ");
                }
                lineBuilder.append(wordBuilder);
                lineLength += spacingLength + wordLength;
            }
            wordLength = 0;
            wordBuilder = new StringBuilder();
        }
    }
}
