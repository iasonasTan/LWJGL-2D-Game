package game;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;

public class LevelEditorScene extends Scene {
    private boolean mChangingScene = false;
    private float mTimeToChangeScene = 2f;

    public LevelEditorScene() {
        IO.println("Inside level editor scene");
    }

    @Override
    public void update(float dt) {
        IO.println("Updating...");
        if(!mChangingScene && KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            mChangingScene = true;
        }
        if(mChangingScene && mTimeToChangeScene > 0) {
            mTimeToChangeScene -= dt;
            IO.println("Still running...");
            Window.get().r -= dt*5f;
            Window.get().g -= dt*5f;
            Window.get().b -= dt*5f;
        } else if (mChangingScene) {
            Window.changeScene(1);
        }
    }
}
