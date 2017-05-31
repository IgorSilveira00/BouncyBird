package com.aor.bouncy.CameraHandler;
/*
 * Copyright 2012 Johnny Lish (johnnyoneeyed@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */


        import java.nio.ByteBuffer;

        import com.badlogic.gdx.ApplicationListener;
        import com.badlogic.gdx.Gdx;
        import com.badlogic.gdx.files.FileHandle;
        import com.badlogic.gdx.graphics.Color;
        import com.badlogic.gdx.graphics.GL20;
        import com.badlogic.gdx.graphics.Mesh;
        import com.badlogic.gdx.graphics.PerspectiveCamera;
        import com.badlogic.gdx.graphics.Pixmap;
        import com.badlogic.gdx.graphics.Texture;
        import com.badlogic.gdx.graphics.Texture.TextureFilter;
        import com.badlogic.gdx.graphics.VertexAttribute;
        import com.badlogic.gdx.graphics.Pixmap.Filter;
        import com.badlogic.gdx.graphics.Pixmap.Format;
        import com.badlogic.gdx.graphics.VertexAttributes.Usage;
        import com.badlogic.gdx.graphics.g2d.Batch;
        import com.badlogic.gdx.graphics.g2d.SpriteBatch;
        import com.badlogic.gdx.graphics.glutils.ShaderProgram;
        import com.badlogic.gdx.math.Matrix4;
        import java.lang.Object;

        import static com.badlogic.gdx.Gdx.gl20;

public class CameraDemo implements ApplicationListener {

    public enum Mode {
        normal,
        prepare,
        preview,
        takePicture,
        waitForPictureReady,
    }

    public static final float vertexData[] = {
            1.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 0/Vertex 0
            0.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 0/Vertex 1
            0.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 0/Vertex 2
            1.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 0/Vertex 3

            1.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 1/Vertex 4
            1.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 1/Vertex 5
            1.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 1/Vertex 6
            1.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 1/Vertex 7

            1.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 2/Vertex 8
            1.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 2/Vertex 9
            0.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 2/Vertex 10
            0.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 2/Vertex 11

            1.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 3/Vertex 12
            0.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 3/Vertex 13
            0.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 3/Vertex 14
            1.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 3/Vertex 15

            0.0f,  1.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 4/Vertex 16
            0.0f,  1.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 4/Vertex 17
            0.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 4/Vertex 18
            0.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 4/Vertex 19

            0.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  1.0f, 1.0f, // quad/face 5/Vertex 20
            1.0f,  0.0f,  0.0f, Color.toFloatBits(255,255,255,255),  0.0f, 1.0f, // quad/face 5/Vertex 21
            1.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  0.0f, 0.0f, // quad/face 5/Vertex 22
            0.0f,  0.0f,  1.0f, Color.toFloatBits(255,255,255,255),  1.0f, 0.0f, // quad/face 5/Vertex 23
    };


    public static final short facesVerticesIndex[][] = {
            { 0, 1, 2, 3 },
            { 4, 5, 6, 7 },
            { 8, 9, 10, 11 },
            { 12, 13, 14, 15 },
            { 16, 17, 18, 19 },
            { 20, 21, 22, 23 }
    };

    private final static VertexAttribute verticesAttributes[] = new VertexAttribute[] {
            new VertexAttribute(Usage.Position, 3, "a_position"),
            new VertexAttribute(Usage.ColorPacked, 4, "a_color"),
            new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"),
    };


    private Texture texture;


    private Mesh[] mesh = new Mesh[6];

    private PerspectiveCamera camera;

    private Batch batch;


    private Mode mode = Mode.normal;

    private ShaderProgram shader;

    private final DeviceCameraControl deviceCameraControl;

    private Matrix4 matrix = new Matrix4();


    public CameraDemo(DeviceCameraControl cameraControl) {
        this.deviceCameraControl = cameraControl;
    }

    @Override
    public void create() {

        String vertexShader = "attribute vec4 a_position;    \n" + "attribute vec4 a_color;\n" + "attribute vec2 a_texCoord0;\n"
                + "uniform mat4 u_worldView;\n" + "varying vec4 v_color;" + "varying vec2 v_texCoords;"
                + "void main()                  \n" + "{                            \n" + "   v_color = vec4(1, 1, 1, 1); \n"
                + "   v_texCoords = a_texCoord0; \n" + "   gl_Position =  u_worldView * a_position;  \n"
                + "}                            \n";
        String fragmentShader = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n" + "varying vec4 v_color;\n"
                + "varying vec2 v_texCoords;\n" + "uniform sampler2D u_texture;\n" + "void main()                                  \n"
                + "{                                            \n" + "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n"
                + "}";

        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (shader.isCompiled() == false) {
            Gdx.app.log("ShaderTest", shader.getLog());
            Gdx.app.exit();
        }

        // Load the Libgdx splash screen texture
        texture = new Texture(Gdx.files.internal("backplate.png"));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

        // Create the 6 faces of the Cube
        for (int i=0;i<6;i++) {
            mesh[i] = new Mesh(true, 24, 4, verticesAttributes);
            mesh[i].setVertices(vertexData);
            mesh[i].setIndices(facesVerticesIndex[i]);
        }

        // Create the OpenGL Camera
        camera = new PerspectiveCamera(67.0f, 2.0f * Gdx.graphics.getWidth() / Gdx.graphics.getHeight(), 2.0f);
        camera.far = 100.0f;
        camera.near = 0.1f;
        camera.position.set(2.0f,2.0f,2.0f);
        camera.lookAt(0.0f, 0.0f, 0.0f);

        batch = new SpriteBatch();

    }

    @Override
    public void dispose() {
        texture.dispose();
        for (int i=0;i<6;i++) {
            mesh[i].dispose();
            mesh[i] = null;
        }
        texture = null;
    }

    @Override
    public void render() {
        if (Gdx.input.isTouched()) {
            if (mode == Mode.normal) {
                mode = Mode.prepare;
                if (deviceCameraControl != null) {
                    deviceCameraControl.prepareCameraAsync();
                }
            }
        } else { // touch removed
            if (mode == Mode.preview) {
                mode = Mode.takePicture;
            }
        }

        gl20.glHint(GL20.GL_GENERATE_MIPMAP_HINT, GL20.GL_NICEST);
        if (mode == Mode.takePicture) {
            gl20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            if (deviceCameraControl != null) {
                deviceCameraControl.takePicture();
            }
            mode = Mode.waitForPictureReady;
        } else if (mode == Mode.waitForPictureReady) {
            gl20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        } else if (mode == Mode.prepare) {
            gl20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            if (deviceCameraControl != null) {
                if (deviceCameraControl.isReady()) {
                    deviceCameraControl.startPreviewAsync();
                    mode = Mode.preview;
                }
            }
        } else if (mode == Mode.preview) {
            gl20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        } else { // mode = normal
            gl20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        }
        gl20.glClear(gl20.GL_COLOR_BUFFER_BIT | gl20.GL_DEPTH_BUFFER_BIT);
        gl20.glEnable(gl20.GL_DEPTH_TEST);
        gl20.glEnable(gl20.GL_TEXTURE);
        gl20.glEnable(gl20.GL_TEXTURE_2D);
        gl20.glEnable(gl20.GL_LINE_STRIP);
        gl20.glDepthFunc(gl20.GL_LEQUAL);
        gl20.glClearDepthf(1.0F);
        camera.update(true);
        batch.setProjectionMatrix(camera.combined);
        texture.bind();
        shader.begin();
        shader.setUniformMatrix("u_worldView", matrix);
        shader.setUniformi("u_texture", 0);
        for (int i=0;i<6;i++) {
            mesh[i].render(shader, GL20.GL_TRIANGLE_FAN);
        }
        shader.end();

        if (mode == Mode.waitForPictureReady) {
            if (deviceCameraControl.getPictureData() != null) { // camera picture was actually taken
                // take Gdx Screenshot
                Pixmap screenshotPixmap = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
                Pixmap cameraPixmap = new Pixmap(deviceCameraControl.getPictureData(), 0, deviceCameraControl.getPictureData().length);
                merge2Pixmaps(cameraPixmap, screenshotPixmap);
                // we could call PixmapIO.writePNG(pngfile, cameraPixmap);
                FileHandle jpgfile = Gdx.files.external("libGdxSnapshot.jpg");
                deviceCameraControl.saveAsJpeg(jpgfile, cameraPixmap);
                deviceCameraControl.stopPreviewAsync();
                mode = Mode.normal;
            }
        }
    }

    private Pixmap merge2Pixmaps(Pixmap mainPixmap, Pixmap overlayedPixmap) {
        // merge to data and Gdx screen shot - but fix Aspect Ratio issues between the screen and the camera
        mainPixmap.setFilter(Filter.BiLinear);
        float mainPixmapAR = (float)mainPixmap.getWidth() / mainPixmap.getHeight();
        float overlayedPixmapAR = (float)overlayedPixmap.getWidth() / overlayedPixmap.getHeight();
        if (overlayedPixmapAR < mainPixmapAR) {
            int overlayNewWidth = (int)(((float)mainPixmap.getHeight() / overlayedPixmap.getHeight()) * overlayedPixmap.getWidth());
            int overlayStartX = (mainPixmap.getWidth() - overlayNewWidth)/2;
            // Overlaying pixmaps
            mainPixmap.drawPixmap(overlayedPixmap, 0, 0, overlayedPixmap.getWidth(), overlayedPixmap.getHeight(), overlayStartX, 0, overlayNewWidth, mainPixmap.getHeight());
        } else {
            int overlayNewHeight = (int)(((float)mainPixmap.getWidth() / overlayedPixmap.getWidth()) * overlayedPixmap.getHeight());
            int overlayStartY = (mainPixmap.getHeight() - overlayNewHeight)/2;
            // Overlaying pixmaps
            mainPixmap.drawPixmap(overlayedPixmap, 0, 0, overlayedPixmap.getWidth(), overlayedPixmap.getHeight(), 0, overlayStartY, mainPixmap.getWidth(), overlayNewHeight);
        }
        return mainPixmap;
    }

    public Pixmap getScreenshot(int x, int y, int w, int h, boolean flipY) {
        Gdx.gl.glPixelStorei(gl20.GL_PACK_ALIGNMENT, 1);

        final Pixmap pixmap = new Pixmap(w, h, Format.RGBA8888);
        ByteBuffer pixels = pixmap.getPixels();
        Gdx.gl.glReadPixels(x, y, w, h, gl20.GL_RGBA, gl20.GL_UNSIGNED_BYTE, pixels);

        final int numBytes = w * h * 4;
        byte[] lines = new byte[numBytes];
        if (flipY) {
            final int numBytesPerLine = w * 4;
            for (int i = 0; i < h; i++) {
                pixels.position((h - i - 1) * numBytesPerLine);
                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
        } else {
            pixels.clear();
            pixels.get(lines);
        }

        return pixmap;
    }

    @Override
    public void resize(int width, int height) {
        camera = new PerspectiveCamera(67.0f, 2.0f * width / height, 2.0f);
        camera.far = 100.0f;
        camera.near = 0.1f;
        camera.position.set(2.0f,2.0f,2.0f);
        camera.lookAt(0.0f, 0.0f, 0.0f);

    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}