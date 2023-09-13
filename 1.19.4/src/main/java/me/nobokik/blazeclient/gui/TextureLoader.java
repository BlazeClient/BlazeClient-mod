package me.nobokik.blazeclient.gui;

import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_BGRA;

public class TextureLoader {
    private static final int BYTES_PER_PIXEL = 4;

    public static int loadTexture(BufferedImage image) {
        int[] pixels = new int[image.getHeight() * image.getWidth()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

        int textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        //Setup wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        //Setup texture scaling filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        //Send texel data to OpenGL
        glTexImage2D(GL_TEXTURE_2D, 0, GL_BGRA, image.getWidth(), image.getHeight(), 0, GL_BGRA, GL_UNSIGNED_BYTE, pixels);

        //Return the texture ID so we can bind it later again
        return textureID;
    }

    public static BufferedImage loadImage(InputStream is) {
        try {
            return ImageIO.read(is);
        } catch (IOException e) {
            System.err.println("Can't find resource");
        }
        return null;
    }
}
