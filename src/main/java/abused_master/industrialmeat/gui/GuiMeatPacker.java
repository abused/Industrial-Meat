package abused_master.industrialmeat.gui;

import abused_master.industrialmeat.IndustrialMeat;
import abused_master.industrialmeat.gui.container.ContainerMeatPacker;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiMeatPacker extends ContainerScreen<ContainerMeatPacker> {

    public static final ResourceLocation MEATPACKER_GUI = new ResourceLocation(IndustrialMeat.MODID, "textures/gui/gui_meatpacker.png");

    public GuiMeatPacker(ContainerMeatPacker containerMeatPacker, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(containerMeatPacker, playerInventory, textComponent);
    }

    @Override
    protected void func_231160_c_() {
        super.func_231160_c_();
        //Center Title
        this.field_238742_p_ = (this.xSize - this.field_230712_o_.func_238414_a_(this.field_230704_d_)) / 2;
    }

    //render
    @Override
    public void func_230430_a_(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        //renderBackground
        this.func_230446_a_(matrixStack);
        super.func_230430_a_(matrixStack, mouseX, mouseY, partialTicks);

        //renderHoveredToolTip
        this.func_230459_a_(matrixStack, mouseX, mouseY);
    }

    //drawGuiContainerBackgroundLayer
    @Override
    public void func_230450_a_(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        getMinecraft().getTextureManager().bindTexture(MEATPACKER_GUI);
        func_238474_b_(matrixStack, guiLeft, guiTop, 0, 0, xSize, ySize);

        renderWork(matrixStack);
        renderEnergy(matrixStack);
        renderFluid(matrixStack);

        if(container.getMeatPacker() != null) {
            if (this.isPointInRegion(10, 18, 12, 48, mouseX, mouseY)) {
                List<StringTextComponent> energy = new ArrayList<>();
                energy.add(new StringTextComponent(container.getMeatPacker().storage.getEnergyStored() + " / " + container.getMeatPacker().storage.getMaxEnergyStored() + "  FE"));
                GuiUtils.drawHoveringText(matrixStack, energy, mouseX, mouseY, getMinecraft().currentScreen.field_230708_k_, getMinecraft().currentScreen.field_230709_l_, -1, getMinecraft().fontRenderer);
            }

            if (this.isPointInRegion(30, 18, 5, 48, mouseX, mouseY)) {
                List<StringTextComponent> work = new ArrayList<>();
                work.add(new StringTextComponent(container.getMeatPacker().workTime + " / " + container.getMeatPacker().totalWorkTime + "  Work Time"));
                GuiUtils.drawHoveringText(matrixStack, work, mouseX, mouseY, getMinecraft().currentScreen.field_230708_k_, getMinecraft().currentScreen.field_230709_l_, -1, getMinecraft().fontRenderer);
            }

            if (this.isPointInRegion(43, 18, 12, 48, mouseX, mouseY)) {
                List<StringTextComponent> fluid = new ArrayList<>();
                fluid.add(new StringTextComponent(container.getMeatPacker().tank.getFluidAmount() + " / " + container.getMeatPacker().tank.getCapacity() + "  MB"));
                GuiUtils.drawHoveringText(matrixStack, fluid, mouseX, mouseY, getMinecraft().currentScreen.field_230708_k_, getMinecraft().currentScreen.field_230709_l_, -1, getMinecraft().fontRenderer);
            }
        }
    }

    public void renderEnergy(MatrixStack matrixStack) {
        if (container.getMeatPacker() != null && container.getMeatPacker().storage.getEnergyStored() > 0) {
            int i = 48;
            int j = container.getMeatPacker().storage.getEnergyStored() * i / container.getMeatPacker().storage.getMaxEnergyStored();
            func_238474_b_(matrixStack, guiLeft + 10, guiTop + 66 - j, 193, 48 - j, 12, j);
        }
    }

    public void renderWork(MatrixStack matrixStack) {
        if(container.getMeatPacker() != null &&  container.getMeatPacker().workTime > 0) {
            int i = 48;
            int j = container.getMeatPacker().workTime * i / container.getMeatPacker().totalWorkTime;
            func_238474_b_(matrixStack, guiLeft + 30, guiTop + 66 - j, 208, 48 - j, 5, j);
        }
    }

    public void renderFluid(MatrixStack matrixStack) {
        if(container.getMeatPacker() != null &&  container.getMeatPacker().tank != null && container.getMeatPacker().tank.getFluidAmount() > 0) {
            int i = container.getMeatPacker().tank.getFluidAmount() * 48 / container.getMeatPacker().tank.getCapacity();
            int color = container.getMeatPacker().tank.getFluid().getFluid().getAttributes().getColor();
            int brightness = getMinecraft().world.getLightSubtracted(new BlockPos(guiLeft + 10, guiTop + 7, 0), container.getMeatPacker().tank.getFluid().getFluid().getAttributes().getLuminosity());
            TextureAtlasSprite still = getMinecraft().getAtlasSpriteGetter(PlayerContainer.LOCATION_BLOCKS_TEXTURE).apply(container.getMeatPacker().tank.getFluid().getFluid().getAttributes().getStillTexture());

            matrixStack.push();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            getMinecraft().textureManager.bindTexture(PlayerContainer.LOCATION_BLOCKS_TEXTURE);
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX_LIGHTMAP);

            addTexturedQuad(buffer, still, guiLeft + 43, guiTop + 66 - i, 12, i, color, brightness);
            tessellator.draw();

            GlStateManager.disableBlend();
            matrixStack.pop();
        }
    }

    public static void addTexturedQuad(BufferBuilder buffer, TextureAtlasSprite sprite, double x, double y, double width, double height, int color, int brightness) {
        if (sprite == null) {
            return;
        }

        final int firstLightValue = brightness >> 0x10 & 0xFFFF;
        final int secondLightValue = brightness & 0xFFFF;
        final int alpha = color >> 24 & 0xFF;
        final int red = color >> 16 & 0xFF;
        final int green = color >> 8 & 0xFF;
        final int blue = color & 0xFF;

        addTextureQuad(buffer, sprite, x, y, width, height, red, green, blue, alpha, firstLightValue, secondLightValue);
    }

    public static void addTextureQuad (BufferBuilder buffer, TextureAtlasSprite sprite, double x, double y, double width, double height, int red, int green, int blue, int alpha, int light1, int light2) {
        double minU;
        double maxU;
        double minV;
        double maxV;

        final double x2 = x + width;
        final double y2 = y + height;

        final double u = x % 1d;
        double u1 = u + width;

        while (u1 > 1f) {
            u1 -= 1f;
        }

        final double vy = y % 1d;
        double vy1 = vy + height;

        while (vy1 > 1f) {
            vy1 -= 1f;
        }

        minU = sprite.getMinU();
        maxU = sprite.getMaxU();
        minV = sprite.getMinV();
        maxV = sprite.getMaxV();

        buffer.pos(x, y, 0).color(red, green, blue, alpha).tex((float) minU, (float) maxV).lightmap(light1, light2).endVertex();
        buffer.pos(x, y2, 0).color(red, green, blue, alpha).tex((float) minU, (float) minV).lightmap(light1, light2).endVertex();
        buffer.pos(x2, y2, 0).color(red, green, blue, alpha).tex((float) maxU, (float) minV).lightmap(light1, light2).endVertex();
        buffer.pos(x2, y, 0).color(red, green, blue, alpha).tex((float) maxU, (float) maxV).lightmap(light1, light2).endVertex();
    }
}
