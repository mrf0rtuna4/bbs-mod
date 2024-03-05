package mchorse.bbs_mod.graphics;

import mchorse.bbs_mod.utils.Quad;
import mchorse.bbs_mod.utils.math.MathUtils;
import net.minecraft.client.render.BufferBuilder;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Draw
{
    private static final Quad top = new Quad();
    private static final Quad bottom = new Quad();
    private static final Matrix4f rotate = new Matrix4f();

    /**
     * Fill a quad for {@link net.minecraft.client.render.VertexFormats#POSITION_TEXTURE_COLOR_NORMAL}. Points should
     * be supplied in this order:
     *
     *     3 -------> 4
     *     ^
     *     |
     *     |
     *     2 <------- 1
     *
     * I.e. bottom left, bottom right, top left, top right, where left is -X and right is +X,
     * in case of a quad on fixed on Z axis.
     */
    public static void fillTexturedNormalQuad(BufferBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float u1, float v1, float u2, float v2, float r, float g, float b, float a, float nx, float ny, float nz)
    {
        /* TODO: matrix */

        /* 1 - BL, 2 - BR, 3 - TR, 4 - TL */
        builder.vertex(x2, y2, z2).normal(nx, ny, nz).texture(u1, v2).color(r, g, b, a).next();
        builder.vertex(x1, y1, z1).normal(nx, ny, nz).texture(u2, v2).color(r, g, b, a).next();
        builder.vertex(x4, y4, z4).normal(nx, ny, nz).texture(u2, v1).color(r, g, b, a).next();

        builder.vertex(x2, y2, z2).normal(nx, ny, nz).texture(u1, v2).color(r, g, b, a).next();
        builder.vertex(x4, y4, z4).normal(nx, ny, nz).texture(u2, v1).color(r, g, b, a).next();
        builder.vertex(x3, y3, z3).normal(nx, ny, nz).texture(u1, v1).color(r, g, b, a).next();
    }

    /**
     * Fill a quad for {@link net.minecraft.client.render.VertexFormats#POSITION_TEXTURE_COLOR}. Points should
     * be supplied in this order:
     *
     *     3 -------> 4
     *     ^
     *     |
     *     |
     *     2 <------- 1
     *
     * I.e. bottom left, bottom right, top left, top right, where left is -X and right is +X,
     * in case of a quad on fixed on Z axis.
     */
    public static void fillTexturedQuad(BufferBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float u1, float v1, float u2, float v2, float r, float g, float b, float a)
    {
        /* 1 - BL, 2 - BR, 3 - TR, 4 - TL */
        builder.vertex(x2, y2, z2).texture(u1, v2).color(r, g, b, a).next();
        builder.vertex(x1, y1, z1).texture(u2, v2).color(r, g, b, a).next();
        builder.vertex(x4, y4, z4).texture(u2, v1).color(r, g, b, a).next();

        builder.vertex(x2, y2, z2).texture(u1, v2).color(r, g, b, a);
        builder.vertex(x4, y4, z4).texture(u2, v1).color(r, g, b, a);
        builder.vertex(x3, y3, z3).texture(u1, v1).color(r, g, b, a);
    }

    public static void fillQuad(BufferBuilder builder, Quad quad, float r, float g, float b, float a)
    {
        fillQuad(builder, quad.p1, quad.p2, quad.p3, quad.p4, r, g, b, a);
    }

    public static void fillQuad(BufferBuilder builder, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4, float r, float g, float b, float a)
    {
        fillQuad(builder, p1.x, p1.y, p1.z, p2.x, p2.y, p2.z, p3.x, p3.y, p3.z, p4.x, p4.y, p4.z, r, g, b, a);
    }

    public static void fillQuad(BufferBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float x4, float y4, float z4, float r, float g, float b, float a)
    {
        /* 1 - BR, 2 - BL, 3 - TL, 4 - TR */
        builder.vertex(x1, y1, z1).color(r, g, b, a).next();
        builder.vertex(x2, y2, z2).color(r, g, b, a).next();
        builder.vertex(x3, y3, z3).color(r, g, b, a).next();
        builder.vertex(x1, y1, z1).color(r, g, b, a).next();
        builder.vertex(x3, y3, z3).color(r, g, b, a).next();
        builder.vertex(x4, y4, z4).color(r, g, b, a).next();
    }

    public static void fillBox(BufferBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b)
    {
        fillBox(builder, x1, y1, z1, x2, y2, z2, r, g, b, 1F);
    }

    public static void fillBox(BufferBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a)
    {
        /* X */
        fillQuad(builder, x1, y1, z2, x1, y2, z2, x1, y2, z1, x1, y1, z1, r, g, b, a);
        fillQuad(builder, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, r, g, b, a);

        /* Y */
        fillQuad(builder, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, r, g, b, a);
        fillQuad(builder, x2, y2, z1, x1, y2, z1, x1, y2, z2, x2, y2, z2, r, g, b, a);

        /* Z */
        fillQuad(builder, x2, y1, z1, x1, y1, z1, x1, y2, z1, x2, y2, z1, r, g, b, a);
        fillQuad(builder, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, r, g, b, a);
    }

    public static void fillLine(BufferBuilder builder, float thickness, float x1, float y1, float z1, float x2, float y2, float z2, float r, float g, float b, float a)
    {
        float length = new Vector3f(x2, y2, z2).sub(x1, y1, z1).length();
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float yaw = (float) -Math.atan2(dz, dx);
        float pitch = (float) Math.atan2(dy, Math.sqrt(dx * dx + dz * dz));

        thickness /= 2;

        bottom.p1.set(-thickness, 0, -thickness);
        bottom.p2.set(thickness, 0, -thickness);
        bottom.p3.set(thickness, 0, thickness);
        bottom.p4.set(-thickness, 0, thickness);
        top.p1.set(-thickness, length, -thickness);
        top.p2.set(thickness, length, -thickness);
        top.p3.set(thickness, length, thickness);
        top.p4.set(-thickness, length, thickness);

        rotate.identity()
            .translate(x1, y1, z1)
            .rotateY(yaw - MathUtils.PI / 2)
            .rotateX(pitch - MathUtils.PI / 2);

        bottom.transform(rotate);
        top.transform(rotate);

        /* X */
        fillQuad(builder, bottom.p4, top.p4, top.p1, bottom.p1, r, g, b, a);
        fillQuad(builder, bottom.p2, top.p2, top.p3, bottom.p3, r, g, b, a);

        /* Y */
        fillQuad(builder, bottom.p1, bottom.p2, bottom.p3, bottom.p4, r, g, b, a);
        fillQuad(builder, top.p2, top.p1, top.p4, top.p3, r, g, b, a);

        /* Z */
        fillQuad(builder, bottom.p2, bottom.p1, top.p1, top.p2, r, g, b, a);
        fillQuad(builder, bottom.p4, bottom.p3, top.p3, top.p4, r, g, b, a);
    }

    public static void axis(BufferBuilder builder, float length, float thickness)
    {
        fillBox(builder, thickness, -thickness, -thickness, length, thickness, thickness, 1, 0, 0, 1);
        fillBox(builder, -thickness, -thickness, -thickness, thickness, length, thickness, 0, 1, 0, 1);
        fillBox(builder, -thickness, -thickness, thickness, thickness, thickness, length, 0, 0, 1, 1);
    }
}