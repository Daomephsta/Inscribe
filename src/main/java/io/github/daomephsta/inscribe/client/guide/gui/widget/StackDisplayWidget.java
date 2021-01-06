package io.github.daomephsta.inscribe.client.guide.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;

public class StackDisplayWidget extends GuideWidget
{
    private final ItemStack[] stacks;

    public StackDisplayWidget(ItemStack... stacks)
    {
        this.stacks = stacks;
    }

    @Override
    public void renderWidget(VertexConsumerProvider vertices, MatrixStack matrices, int mouseX, int mouseY, float lastFrameDuration)
    {
        MinecraftClient client = MinecraftClient.getInstance();
        ItemStack stack = stacks[(int) ((System.currentTimeMillis() / 2000) % stacks.length)];
        client.getItemRenderer().renderGuiItem(stack, x(), y());
    }

    @Override
    public int hintHeight()
    {
        return 16;
    }

    @Override
    public int hintWidth()
    {
        return 16;
    }
}
