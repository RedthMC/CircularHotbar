package me.redth.circularhotbar.hud;

import cc.polyfrost.oneconfig.config.annotations.DualOption;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.gui.animations.EaseInOutQuad;
import cc.polyfrost.oneconfig.hud.Hud;
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;

public class HotbarHud extends Hud {
    private static final transient Minecraft mc = Minecraft.getMinecraft();
    private static final transient ResourceLocation widgets = new ResourceLocation("textures/gui/widgets.png");
    private static transient EaseInOutQuad easeInOutQuad;
    private static transient int selectedSlot;

    @DualOption(name = "Direction", left = "Clockwise", right = "Counter-Clockwise", size = 2)
    public static boolean reverse = false;

    @Slider(name = "Rotate Offset", min = -180, max = 180)
    public static int rotateOffset = 0;

    @Slider(name = "Radius", min = 20, max = 70)
    public static int radius = 45;

    @Slider(name = "Animation Duration (seconds)", min = 0, max = 5)
    public static int animationTime = 2;

    public HotbarHud() {
        super(true);
    }

    @Override
    public boolean isEnabled() {
        boolean b = super.isEnabled();
        GuiIngameForge.renderHotbar = !b;
        return b;
    }

    @Override
    protected void preRender(boolean example) {
        super.preRender(example);

        if (easeInOutQuad == null) {
            selectedSlot = mc.thePlayer.inventory.currentItem;
            easeInOutQuad = new EaseInOutQuad(animationTime * 1000, selectedSlot, selectedSlot, false);
        } else if (selectedSlot != mc.thePlayer.inventory.currentItem) {
            selectedSlot = mc.thePlayer.inventory.currentItem;
            float last = easeInOutQuad.get();
            float difference = selectedSlot - last;

            if (difference > 4.5) last += 9;
            if (difference < -4.5) last -= 9;

            easeInOutQuad = new EaseInOutQuad(animationTime * 1000, last, selectedSlot, false);
        }
    }


    @Override
    protected void draw(UMatrixStack matrices, float x, float y, float scale, boolean example) {
        x += (radius + 12) * scale;
        y += (radius + 12) * scale;

        float[] slotXs = new float[9];
        float[] slotYs = new float[9];

        float angle = reverse ? -40 : 40;

        for (int i = 0; i < 9; i++) {
            float degrees = (easeInOutQuad.get() - i) * angle + rotateOffset;
            double radians = Math.toRadians(degrees);
            slotXs[i] = (float) Math.sin(radians) * radius;
            slotYs[i] = (float) Math.cos(radians) * radius;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(scale, scale, 1.0);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);

        mc.renderEngine.bindTexture(widgets);

        for (int i = 0; i < 9; i++) {
            drawCenteredTexture(slotXs[i], slotYs[i], 1 + i * 20, 1, 20, 20);
            if (i == selectedSlot) drawCenteredTexture(slotXs[i], slotYs[i], 0, 22, 24, 24);
        }

        RenderHelper.enableGUIStandardItemLighting();

        for (int i = 0; i < 9; i++) {
            ItemStack item = mc.thePlayer.inventory.mainInventory[i];
            if (item == null) continue;
            drawItem(item, slotXs[i], slotYs[i]);
        }

        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
    }

    @Override
    protected float getWidth(float scale, boolean example) {
        return (radius + 12) * 2 * scale;
    }

    @Override
    protected float getHeight(float scale, boolean example) {
        return (radius + 12) * 2 * scale;
    }

    private static void drawItem(ItemStack item, float centerX, float centerY) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(centerX - 8, centerY - 8, 0);

        RenderItem itemRenderer = mc.getRenderItem();
        itemRenderer.renderItemAndEffectIntoGUI(item, 0, 0);
        itemRenderer.renderItemOverlayIntoGUI(mc.fontRendererObj, item, 0, 0, null);

        GlStateManager.popMatrix();
    }


    private static void drawCenteredTexture(float centerX, float centerY, float u, float v, float width, float height) {
        drawTexturedModalRect(centerX - width / 2, centerY - height / 2, u, v, width, height);
    }

    public static void drawTexturedModalRect(float x, float y, float textureX, float textureY, float width, float height) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
        worldRenderer.pos(x, y + height, 0).tex(textureX / 256, (textureY + height) / 256).endVertex();
        worldRenderer.pos(x + width, y + height, 0).tex((textureX + width) / 256, (textureY + height) / 256).endVertex();
        worldRenderer.pos(x + width, (y), 0).tex((textureX + width) / 256, textureY / 256).endVertex();
        worldRenderer.pos(x, y, 0).tex((textureX / 256), textureY / 256).endVertex();
        tessellator.draw();
    }
}
