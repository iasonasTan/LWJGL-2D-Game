package game;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {
    private boolean mChangingScene = false;
    private float mTimeToChangeScene = 2.2f;

    private final String VERTEX_SHADER_SRC = """
            #version 330 core
            layout (location=0) in vec3 aPos;
            layout (location=1) in vec4 aColor;

            out vec4 fColor;

            void main()
            {
                fColor = aColor;
                gl_Position = vec4(aPos, 1.0);
            }""";

    private final String FRAGMENT_SHADER_SRC = """
            #version 330 core

            in vec4 fColor;

            out vec4 color;

            void main()
            {
                color = fColor;
            }
            """;

    private int vertexId, fragmentId, shaderProgram;
    private int vaoId, vboId, eboId;
    private final int EBO_SIZE = 6;

    public LevelEditorScene() {
        IO.println("Inside level editor scene");
    }

    private void compileShaders() {
        // Compile and link shaders

        // Load and compile the vertex shader
        vertexId = glCreateShader(GL_VERTEX_SHADER);

        // Pass the shader source to the GPU
        glShaderSource(vertexId, VERTEX_SHADER_SRC);
        glCompileShader(vertexId);

        // Check for compile errors
        int ret = glGetShaderi(vertexId, GL_COMPILE_STATUS);
        if(ret == GL_FALSE) {
            int len = glGetShaderi(vertexId, GL_INFO_LOG_LENGTH);
            IO.println("Error: default_shader.glsl\n\tVertex shader compilation failed.");
            IO.println(glGetShaderInfoLog(vertexId, len));
        }

        // Load and compile the vertex shader
        fragmentId = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass the shader source to the GPU
        glShaderSource(fragmentId, FRAGMENT_SHADER_SRC);
        glCompileShader(fragmentId);

        // Check for compile errors
        ret = glGetShaderi(fragmentId, GL_COMPILE_STATUS);
        if(ret == GL_FALSE) {
            int len = glGetShaderi(fragmentId, GL_INFO_LOG_LENGTH);
            IO.println("Error: default_shader.glsl\n\tFragment shader compilation failed.");
            IO.println(glGetShaderInfoLog(fragmentId, len));
        }
    }

    private void linkShaders() {
        // Link shaders and check for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragmentId);
        glLinkProgram(shaderProgram);

        // Check for linking errors
        int ret = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(ret == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            IO.println("Error: default_shader.glsl\n\tLinking of shaders failed.");
            IO.println(glGetProgramInfoLog(shaderProgram, len));
        }
    }

    @Override
    public Scene init() {
        compileShaders();
        linkShaders();

        // ============================== //
        // ======= COLORED SQUARE ======= //
        // ============================== //
        {
            final float[] vbo_raw = {
                    // Position(x,y)    Color(r,g,b,a)
                    -0.3f, -0.3f, 1.0f, 0.0f, 0.0f, 1.0f, // Top Left     0
                     0.3f, -0.3f, 0.0f, 1.0f, 0.0f, 1.0f, // Top Right    1
                     0.3f, 0.3f, 0.0f, 0.0f, 1.0f, 1.0f, // Bottom Right 2
                    -0.3f, 0.3f, 0.0f, 0.8f, 0.8f, 1.0f, // Bottom Left  3
                    //-0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, // Center       4
            };
            final int[] ebo_raw = new int[]{
                    1, 2, 3,
                    1, 3, 0
            };
            // noinspection all
            assert ebo_raw.length == EBO_SIZE : "OK";

            FloatBuffer vboBuff = BufferUtils
                    .createFloatBuffer(vbo_raw.length)
                    .put(vbo_raw)
                    .flip();

            IntBuffer eboBuff = BufferUtils
                    .createIntBuffer(ebo_raw.length)
                    .put(ebo_raw)
                    .flip();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            vboId = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboId);
            glBufferData(GL_ARRAY_BUFFER, vboBuff, GL_STATIC_DRAW);

            eboId = glGenBuffers();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, eboBuff, GL_STATIC_DRAW);

            // Position
            int stride = (2/*Position*/ + 4/*Color*/) * Float.BYTES;
            glVertexAttribPointer(0, 2, GL_FLOAT, false, stride, 0);
            glEnableVertexAttribArray(0);

            // Color
            int position = Float.BYTES * 2;
            glVertexAttribPointer(1, 4, GL_FLOAT, false, stride, position);
            glEnableVertexAttribArray(1);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }

        return this;
    }

    @Override
    public void update(float dt) {
        if(!mChangingScene && KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            mChangingScene = true;
        }
        if(mChangingScene && mTimeToChangeScene > 0) {
            mTimeToChangeScene -= dt;
            Window.get().r -= (float) (dt*Math.random()*2);
            Window.get().g -= (float) (dt*Math.random()*2);
            Window.get().b -= (float) (dt*Math.random()*2);
        } else if (mChangingScene) {
            Window.changeScene(1);
        }

        // bind shader program
        glUseProgram(shaderProgram);

        // bind the vao that we're using
        glBindVertexArray(vaoId);

        // enable the vertex attribute pointer
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        /*glDrawElements(
                GL_TRIANGLES,
                elementArray.length,
                GL_UNSIGNED_INT,
                0
        );*/
        glDrawElements(GL_TRIANGLES,
                9, // Πόσα έχει μέσα το EBO
                GL_UNSIGNED_INT,
                0
        );

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        glUseProgram(0);
    }
}
