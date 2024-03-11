package mchorse.bbs_mod.graphics.texture;

import mchorse.bbs_mod.BBSModClient;
import mchorse.bbs_mod.graphics.Draw;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.resources.Pixels;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;

import java.util.HashMap;
import java.util.Map;

public class TextureExtruder
{
    private MatrixStack stack = new MatrixStack();
    private Map<Link, VertexBuffer> extruded = new HashMap<>();

    public void delete(Link key)
    {
        VertexBuffer buffer = this.extruded.remove(key);

        if (buffer != null)
        {
            buffer.close();
        }
    }

    public void deleteAll()
    {
        for (VertexBuffer buffer : this.extruded.values())
        {
            if (buffer != null)
            {
                buffer.close();
            }
        }

        this.extruded.clear();
    }

    public VertexBuffer get(Link key)
    {
        if (this.extruded.containsKey(key))
        {
            return this.extruded.get(key);
        }

        Pixels pixels = null;

        try
        {
            pixels = BBSModClient.getTextures().getPixels(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        if (pixels == null)
        {
            this.extruded.put(key, null);

            return null;
        }

        VertexBuffer buffer = this.generate(pixels);

        pixels.delete();
        this.extruded.put(key, buffer);

        return buffer;
    }

    private VertexBuffer generate(Pixels pixels)
    {
        VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);

        BufferBuilder builder = Tessellator.getInstance().getBuffer();

        builder.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_TEXTURE_COLOR_NORMAL);

        float p = 0.5F;
        float n = -0.5F;
        float u1 = 0F;
        float v1 = 0F;
        float u2 = 1F;
        float v2 = 1F;
        float d = 0.5F / 16F;

        Draw.fillTexturedNormalQuad(builder, this.stack,
            p, n, d,
            n, n, d,
            n, p, d,
            p, p, d,
            u1, v1, u2, v2,
            1F, 1F, 1F, 1F,
            0F, 0F, 1F
        );

        Draw.fillTexturedNormalQuad(builder, this.stack,
            n, n, -d,
            p, n, -d,
            p, p, -d,
            n, p, -d,
            u2, v1, u1, v2,
            1F, 1F, 1F, 1F,
            0F, 0F, -1F
        );

        for (int i = 0; i < pixels.width; i++)
        {
            for (int j = 0; j < pixels.height; j++)
            {
                if (this.hasPixel(pixels, i, j))
                {
                    this.generateNeighbors(pixels, builder, i, j, i, j, d);
                }
            }
        }

        vertexBuffer.bind();
        vertexBuffer.upload(builder.end());

        return vertexBuffer;
    }

    private void generateNeighbors(Pixels pixels, BufferBuilder builder, int i, int j, int x, int y, float d)
    {
        float w = pixels.width;
        float h = pixels.height;
        float u = (x + 0.5F) / w;
        float v = (y + 0.5F) / h;

        if (!this.hasPixel(pixels, x - 1, y) || i == 0)
        {
            Draw.fillTexturedNormalQuad(builder, this.stack,
                i / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                i / w - 0.5F, -j / h + 0.5F, -d,
                i / w - 0.5F, -j / h + 0.5F, d,
                i / w - 0.5F, -(j + 1) / h + 0.5F, d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                -1F, 0F, 0F
            );
        }

        if (!this.hasPixel(pixels, x + 1, y) || i == 15)
        {
            Draw.fillTexturedNormalQuad(builder, this.stack,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, d,
                (i + 1) / w - 0.5F, -j / h + 0.5F, d,
                (i + 1) / w - 0.5F, -j / h + 0.5F, -d,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                1F, 0F, 0F
            );
        }

        if (!this.hasPixel(pixels, x, y - 1) || j == 0)
        {
            Draw.fillTexturedNormalQuad(builder, this.stack,
                (i + 1) / w - 0.5F, -j / h + 0.5F, d,
                i / w - 0.5F, -j / h + 0.5F, d,
                i / w - 0.5F, -j / h + 0.5F, -d,
                (i + 1) / w - 0.5F, -j / h + 0.5F, -d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                0F, 1F, 0F
            );
        }

        if (!this.hasPixel(pixels, x, y + 1) || j == 15)
        {
            Draw.fillTexturedNormalQuad(builder, this.stack,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                i / w - 0.5F, -(j + 1) / h + 0.5F, -d,
                i / w - 0.5F, -(j + 1) / h + 0.5F, d,
                (i + 1) / w - 0.5F, -(j + 1) / h + 0.5F, d,
                u, v, u, v,
                1F, 1F, 1F, 1F,
                0F, -1F, 0F
            );
        }
    }

    /**
     * Calculate how many bytes will given extruded picture will occupy.
     */
    private int countBytes(Pixels pixels, int size)
    {
        /* Front and back faces don't require extrusion */
        int bytes = size * 2;

        for (int i = 0; i < pixels.width; i++)
        {
            for (int j = 0; j < pixels.height; j++)
            {
                if (this.hasPixel(pixels, i, j))
                {
                    if (!this.hasPixel(pixels, i - 1, j) || i == 0) bytes += size;
                    if (!this.hasPixel(pixels, i + 1, j) || i == 15) bytes += size;
                    if (!this.hasPixel(pixels, i, j - 1) || j == 0) bytes += size;
                    if (!this.hasPixel(pixels, i, j + 1) || j == 15) bytes += size;
                }
            }
        }

        return bytes;
    }

    private boolean hasPixel(Pixels pixels, int x, int y)
    {
        Color pixel = pixels.getColor(x, y);

        return pixel != null && pixel.a >= 1;
    }
}