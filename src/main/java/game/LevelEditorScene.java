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

    private final String VERTEX_SHADER_SRC = "#version 330 core\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";

    private final String FRAGMENT_SHADER_SRC = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}\n";

    private int vertexId, fragmentId, shaderProgram;

    private float[] vertexArray = {
            // position          // color
             0.5f, -0.5f, 0.0f,   1.0f, 0.0f, 0.0f, 1.0f, // Bottom right
            -0.5f,  0.5f, 0.0f,   0.0f, 1.0f, 0.0f, 1.0f, // Top left
             0.5f,  0.5f, 0.0f,   0.0f, 0.0f, 1.0f, 1.0f, // Top right
            -0.5f, -0.5f, 0.0f,   1.0f, 1.0f, 0.0f, 1.0f, // Bottom left
    };

    private int[] elementArray = {
            /*
                    x       x


                    x       x
             */
            2, 1, 0, // Top right triangle
            0, 1, 3, // Bottom left triangle
    };

    private int vaoId, vboId, eboId;

    public LevelEditorScene() {
        IO.println("Inside level editor scene");
    }

    @Override
    public Scene init() {
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

        // Link shaders and check for errors
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexId);
        glAttachShader(shaderProgram, fragmentId);
        glLinkProgram(shaderProgram);

        // Check for linking errors
        ret = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if(ret == GL_FALSE) {
            int len = glGetProgrami(shaderProgram, GL_INFO_LOG_LENGTH);
            IO.println("Error: default_shader.glsl\n\tLinking of shaders failed.");
            IO.println(glGetProgramInfoLog(shaderProgram, len));
        }

        // Generate VAO, VBO, EBO buffer objects and send to GPU
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0,
                positionsSize,
                GL_FLOAT,
                false,
                vertexSizeBytes,
                0
        );
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(
                1,
                colorSize,
                GL_FLOAT,
                false,
                vertexSizeBytes,
                positionsSize * floatSizeBytes
        );
        glEnableVertexAttribArray(1);

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

        glDrawElements(
                GL_TRIANGLES,
                elementArray.length,
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
