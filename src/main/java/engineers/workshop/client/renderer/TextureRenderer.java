package engineers.workshop.client.renderer;

import engineers.workshop.common.table.TileTable;
import engineers.workshop.common.util.helpers.ColorHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TextureRenderer {

    /**
     * Translates the render state to be relative to the player's position. Allows for
     * rendering at a static world position that is not tied to the player's position.
     *
     * @param pos The BlockPos The position to translate to within the world.
     */
    public static void translateAgainstPlayer(BlockPos pos, boolean offset) {
        final float x = (float) (pos.getX() - TileEntityRendererDispatcher.staticPlayerX);
        final float y = (float) (pos.getY() - TileEntityRendererDispatcher.staticPlayerY);
        final float z = (float) (pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ);

        if (offset) GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        else GlStateManager.translate(x, y, z);
    }

    /**
     * Renders a texture at the given position.
     *
     * @param pos   The position in the world to render the texture.
     * @param x     The base X position.
     * @param y     The base Y position.
     * @param z     The base Z position.
     * @param x1    The middle X position.
     * @param y1    The middle Y position.
     * @param z1    The middle Z position.
     * @param x2    The max X position.
     * @param y2    The max Y position.
     * @param z2    The max Z position.
     */
    public static void renderTexture(TileTable te, BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2) {
        renderTexture(pos, x, y, z, x1, y1, z1, x2, y2, z2, ColorHelper.getRGB(te.getPower(), te.getCapacity()));
    }

    /**
     * Renders a texture at the given position.
     *
     * @param pos   The position in the world to render the texture.
     * @param x     The base X position.
     * @param y     The base Y position.
     * @param z     The base Z position.
     * @param x1    The middle X position.
     * @param y1    The middle Y position.
     * @param z1    The middle Z position.
     * @param x2    The max X position.
     * @param y2    The max Y position.
     * @param z2    The max Z position.
     * @param color The color offset used by the texture. Default is white.
     */
    public static void renderTexture(BlockPos pos, double x, double y, double z, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        final Minecraft mc = Minecraft.getMinecraft();
        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer buffer = tessellator.getBuffer();
        final int brightness = mc.theWorld.getCombinedLight(pos, 0);

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        setupRenderState(x, y, z);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 242, 242);

        final TextureAtlasSprite texture = mc.getTextureMapBlocks().getTextureExtry("engineersworkshop:blocks/power_animation");

        addTexturedQuad(buffer, texture, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.NORTH, color, brightness);
        addTexturedQuad(buffer, texture, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.EAST, color, brightness);
        addTexturedQuad(buffer, texture, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.SOUTH, color, brightness);
        addTexturedQuad(buffer, texture, x1, y1, z1, x2 - x1, y2 - y1, z2 - z1, EnumFacing.WEST, color, brightness);

        tessellator.draw();
        cleanupRenderState();
    }

    /**
     * Adds a textured quad to a VertexBuffer. This is intended to be used for block rendering.
     *
     * @param buffer     The VertexBuffer to add to.
     * @param sprite     The texture to use.
     * @param x          The X position.
     * @param y          The Y position.
     * @param z          The Z position.
     * @param width      The width of the quad.
     * @param height     The height of the quad.
     * @param length     The length of the quad.
     * @param face       The face of a cube to render.
     * @param color      The color multiplier to apply.
     * @param brightness The brightness of the cube.
     */
    public static void addTexturedQuad(VertexBuffer buffer, TextureAtlasSprite sprite, double x, double y, double z, double width, double height, double length, EnumFacing face, int color, int brightness) {
        if (sprite == null) {
            System.out.print("No texture found at this resource location");
            return;
        }

        final int firstLightValue = brightness >> 0x10 & 0xFFFF;
        final int secondLightValue = brightness & 0xFFFF;
        final int alpha = color >> 24 & 0xFF;
        final int red = color >> 16 & 0xFF;
        final int green = color >> 8 & 0xFF;
        final int blue = color & 0xFF;

        addTextureQuad(buffer, sprite, x, y, z, width, height, length, face, red, green, blue, alpha, firstLightValue, secondLightValue);
    }

    /**
     * Adds a textured quad to a VertexBuffer. This is intended to be used for block rendering.
     *
     * @param buffer The VertexBuffer to add to.
     * @param sprite The texture to use.
     * @param x      The X position.
     * @param y      The Y position.
     * @param z      The Z position.
     * @param width  The width of the quad.
     * @param height The height of the quad.
     * @param length The length of the quad.
     * @param face   The face of a cube to render.
     * @param red    The red multiplier.
     * @param green  The green multiplier.
     * @param blue   The blue multiplier.
     * @param alpha  The alpha multiplier.
     * @param light1 The first light map value.
     * @param light2 The second light map value.
     */
    public static void addTextureQuad(VertexBuffer buffer, TextureAtlasSprite sprite, double x, double y, double z, double width, double height, double length, EnumFacing face, int red, int green, int blue, int alpha, int light1, int light2) {

        double minU;
        double maxU;
        double minV;
        double maxV;

        final double size = 16f;

        final double x2 = x + width;
        final double y2 = y + height;
        final double z2 = z + length;

        final double u = x % 1d;
        double u1 = u + width;

        while (u1 > 1f) u1 -= 1f;

        final double vy = y % 1d;
        double vy1 = vy + height;

        while (vy1 > 1f) vy1 -= 1f;

        final double vz = z % 1d;
        double vz1 = vz + length;

        while (vz1 > 1f) vz1 -= 1f;

        switch (face) {

            case DOWN:

            case UP:
                minU = sprite.getInterpolatedU(u * size);
                maxU = sprite.getInterpolatedU(u1 * size);
                minV = sprite.getInterpolatedV(vz * size);
                maxV = sprite.getInterpolatedV(vz1 * size);
                break;

            case NORTH:

            case SOUTH:
                minU = sprite.getInterpolatedU(u1 * size);
                maxU = sprite.getInterpolatedU(u * size);
                minV = sprite.getInterpolatedV(vy * size);
                maxV = sprite.getInterpolatedV(vy1 * size);
                break;

            case WEST:

            case EAST:
                minU = sprite.getInterpolatedU(vz1 * size);
                maxU = sprite.getInterpolatedU(vz * size);
                minV = sprite.getInterpolatedV(vy * size);
                maxV = sprite.getInterpolatedV(vy1 * size);
                break;

            default:
                minU = sprite.getMinU();
                maxU = sprite.getMaxU();
                minV = sprite.getMinV();
                maxV = sprite.getMaxV();
        }

        switch (face) {

            case DOWN:
                buffer.pos(x, y, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                buffer.pos(x, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
                break;

            case UP:
                buffer.pos(x, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
                buffer.pos(x, y2, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y2, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;

            case NORTH:
                buffer.pos(x, y, z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
                buffer.pos(x, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y, z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                break;

            case SOUTH:
                buffer.pos(x, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y2, z2).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
                buffer.pos(x, y2, z2).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;

            case WEST:
                buffer.pos(x, y, z).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                buffer.pos(x, y, z2).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
                buffer.pos(x, y2, z2).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
                buffer.pos(x, y2, z).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
                break;

            case EAST:
                buffer.pos(x2, y, z).color(red, green, blue, alpha).tex(minU, maxV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y2, z).color(red, green, blue, alpha).tex(minU, minV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y2, z2).color(red, green, blue, alpha).tex(maxU, minV).lightmap(light1, light2).endVertex();
                buffer.pos(x2, y, z2).color(red, green, blue, alpha).tex(maxU, maxV).lightmap(light1, light2).endVertex();
                break;
        }
    }

    /**
     * Handles all of the basic startup to minimize render conflicts with existing rendering.
     * Make sure to call {@link #cleanupRenderState()} after the rendering code, to return the
     * state to normal.
     *
     * @param x The X position to render at.
     * @param y The Y position to render at.
     * @param z The Z position to render at.
     */
    public static void setupRenderState(double x, double y, double z) {

        GlStateManager.pushMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (Minecraft.isAmbientOcclusionEnabled()) GL11.glShadeModel(GL11.GL_SMOOTH);
        else GL11.glShadeModel(GL11.GL_FLAT);

        GlStateManager.translate(x, y, z);
    }

    /**
     * Counteracts the state changes caused by
     * {@link #setupRenderState(double, double, double)}. Should only be called after that.
     */
    public static void cleanupRenderState() {
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();
    }
}