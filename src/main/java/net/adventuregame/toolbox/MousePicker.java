package net.adventuregame.toolbox;

import com.chidozie.core.renderEngine.MouseInput;
import com.chidozie.core.renderEngine.WindowManager;
import com.chidozie.core.terrains.Terrain;
import net.adventuregame.entities.*;
import net.adventuregame.game.AdventureMain;
import org.joml.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MousePicker {


    private static final int RECURSION_COUNT = 200;
    private static final float RAY_RANGE = 600;
    private static final Logger log = LoggerFactory.getLogger(MousePicker.class);


    private Vector3f currentRay = new Vector3f();


    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Camera camera;
    private MouseInput mInput;
    private WindowManager window;

    private Terrain terrain;
    private Vector3f currentTerrainPoint;

    private List<Entity> entities;


    public MousePicker(Camera cam, Matrix4f projection, Terrain terrain, List<Entity> entities) {
        camera = cam;
        projectionMatrix = projection;
        viewMatrix = Maths.createViewMatrix(camera);
        this.terrain = terrain;
        this.entities = entities;
        mInput = cam.getmInput();
        window = AdventureMain.getWindow();
    }

    public Vector3f getCurrentTerrainPoint() {
        return currentTerrainPoint;
    }


    public Vector3f getCurrentRay() {
        return currentRay;
    }


    public void update() {
        viewMatrix = Maths.createViewMatrix(camera);
        currentRay = calculateMouseRay();
        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
            currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
        } else {
            currentTerrainPoint = null;
        }

        // Check for the closest entity in the ray range
        Entity closestEntity = getClosestEntity(currentRay);
        if (closestEntity != null) {
            log.info("Caught");
            closestEntity.getModel().getTexture().setSelected(true);
        }
    }

    private Entity getClosestEntity(Vector3f ray) {
        float closestDistance = RAY_RANGE;
        Entity closestEntity = null;

        for (Entity entity : entities) { // Replace 'entities' with your actual entity list
            Vector3f entityPosition = entity.getPosition();
            Vector3f camPos = camera.getPosition();
            float distance = camPos.distance(entityPosition);

            if (distance < closestDistance && isPointInRayRange(ray, entityPosition)) {
                closestDistance = distance;
                closestEntity = entity;
            }
        }

        return closestEntity;
    }

    private boolean isPointInRayRange(Vector3f ray, Vector3f point) {
        Vector3f camPos = camera.getPosition();
        Vector3f pointOnRay = getPointOnRay(ray, camPos.distance(point));

        // Adjust this condition based on your specific requirements
        return pointOnRay.distance(point) < 1.0f;
    }




    private Vector3f calculateMouseRay() {
        float mouseX = mInput.getDisplsVec().x;
        float mouseY = mInput.getDisplsVec().y;
        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        Vector3f worldRay = toWorldCoords(eyeCoords);
        return worldRay;
    }


    private Vector3f toWorldCoords(Vector4f eyeCoords) {
        Matrix4f invertedView = new Matrix4f(viewMatrix).invert();
        Vector4f rayWorld = invertedView.transform(eyeCoords);
        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
        mouseRay.normalize();
        return mouseRay;
    }


    private Vector4f toEyeCoords(Vector4f clipCoords) {
        Matrix4f invertedProjection = new Matrix4f(projectionMatrix).invert();
        Vector4f eyeCoords = invertedProjection.transform(clipCoords);
        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }


    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / window.getWidth() - 1f;
        float y = (2.0f * mouseY) / window.getHeight() - 1f;
        return new Vector2f(x, y);
    }

    //**********************************************************

    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f camPos = camera.getPosition();
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray).mul(distance);
        return start.add(scaledRay);
    }

    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = getPointOnRay(ray, half);
            Terrain terrain = getTerrain(endPoint.x(), endPoint.z());
            if (terrain != null) {
                return endPoint;
            } else {
                return null;
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }


    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        Vector3f startPoint = getPointOnRay(ray, start);
        Vector3f endPoint = getPointOnRay(ray, finish);
        if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
            return true;
        } else {
            return false;
        }
    }


    private boolean isUnderGround(Vector3f testPoint) {
        Terrain terrain = getTerrain(testPoint.x(), testPoint.z());
        float height = 0;
        if (terrain != null) {
            height = terrain.getHeightOfTerrain(testPoint.x(), testPoint.z());
        }
        if (testPoint.y < height) {
            return true;
        } else {
            return false;
        }
    }


    private Terrain getTerrain(float worldX, float worldZ) {
        return terrain;
    }

}
