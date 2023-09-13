package com.monstrous.cornfield;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.ScreenUtils;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRFloatAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.lights.DirectionalShadowLight;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import java.nio.Buffer;
import java.nio.FloatBuffer;


public class GameScreen extends ScreenAdapter {

    public static String GLTF_FILE = "models/corn.gltf";
    public static String NODE_NAME = "cornstalk";


    private static final int SHADOW_MAP_SIZE = 2048;
    private final static int INSTANCE_COUNT_SQRT = 100;
    private final static int INSTANCE_COUNT = INSTANCE_COUNT_SQRT * INSTANCE_COUNT_SQRT;

    private SceneManager sceneManager;
    private SceneAsset sceneAsset;
    private Scene scene;
    private PerspectiveCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private SceneSkybox skybox;
    private DirectionalLightEx light;
    private CameraInputController camController;
    private ModelBatch modelBatch;
    private ModelInstance instance;
    private Mesh mesh;


    public GameScreen() {
        // setup camera
        camera = new PerspectiveCamera(50f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 1f;
        camera.far = 300f;
        camera.position.set(-5, 2.5f, -5);
        camera.lookAt(10,1.5f,10);
        camera.update();

        // create scene manager
//        sceneManager = new SceneManager();
//        sceneManager.setCamera(camera);

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Override
    public void show() {

        if (Gdx.gl30 == null) {
            throw new GdxRuntimeException("GLES 3.0 profile required for this test");
        }

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        // gdx-gltf set up
        //
//        sceneManager.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 0.001f));
//
//        // setup light
//        light = new DirectionalShadowLight(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE).setViewport(100, 100, 5, 400);
//
//        light.direction.set(1, -3, 1).nor();
//        light.color.set(Color.WHITE);
//        light.intensity = 1f;
//        sceneManager.environment.add(light);
//
//        // setup quick IBL (image based lighting)
//        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
//        environmentCubemap = iblBuilder.buildEnvMap(1024);
//        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
//        specularCubemap = iblBuilder.buildRadianceMap(10);
//        iblBuilder.dispose();
//
//        // This texture is provided by the library, no need to have it in your assets.
//        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
//
//        sceneManager.setAmbientLight(0.5f);
//        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
//        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
//        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
//
//        // setup skybox
//        skybox = new SceneSkybox(environmentCubemap);
//        sceneManager.setSkyBox(skybox);


        // make model batch for instanced rendering
        // (not done via scene manager).
        modelBatch = new ModelBatch( new MyShaderProvider() );
//        Gdx.files.internal("shaders/instanced-rendering.vertex.glsl").readString(),
//                    Gdx.files.internal("shaders/instanced-rendering.fragment.glsl").readString());


//        sceneAsset = new GLTFLoader().load(Gdx.files.internal(GLTF_FILE));
//
//        // extract the model to instance
//        scene = new Scene(sceneAsset.scene, NODE_NAME);
        // we will handle this model outside of scene manager
        //sceneManager.addScene(scene);

        Model model = new G3dModelLoader(new JsonReader()).loadModel(Gdx.files.internal("models/corn.g3dj"));
        instance = new ModelInstance(model);

        setupInstancedMesh(instance.model);

    }


    private void setupInstancedMesh( Model model ) {
        mesh = model.meshes.get(0);

        // add 4 floats per instance
        mesh.enableInstancedRendering(true, INSTANCE_COUNT, new VertexAttribute(VertexAttributes.Usage.Position, 4, "i_offset")  );

        // Create offset FloatBuffer that will contain instance data to pass to shader
        FloatBuffer offsets = BufferUtils.newFloatBuffer(INSTANCE_COUNT * 4);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.log("FloatBuffer: isDirect()",  "" + offsets.isDirect());  // false = teaVM for now
        Gdx.app.log("Application: Type()",  "" + Gdx.app.getType());

        // fill instance data
        for (int x = 1; x <= INSTANCE_COUNT_SQRT; x++) {
            for (int y = 1; y <= INSTANCE_COUNT_SQRT; y++) {
                float angle = MathUtils.random(0.0f, (float)Math.PI*2.0f);
                float fy = MathUtils.random(-0.6f, 0.0f);        // vary height
                offsets.put(new float[] {50f *x / (INSTANCE_COUNT_SQRT * 0.5f) - 1f, fy, 50f*y / (INSTANCE_COUNT_SQRT * 0.5f) - 1f, angle });     // x, y, z, angle
            }
        }
        ((Buffer)offsets).position(0);
        mesh.setInstanceData(offsets);
    }

    @Override
    public void render(float deltaTime) {

        camController.update();

//        sceneManager.update(deltaTime);


        ScreenUtils.clear(Color.TEAL, true);
//        sceneManager.render();

        // now render instanced model using plain old model batch
        modelBatch.begin(camera);
        modelBatch.render(instance);
        modelBatch.end();
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        //sceneManager.updateViewport(width, height);
    }


    @Override
    public void hide() {
        // This method is called when another screen replaces this one.
        dispose();
    }

    @Override
    public void dispose() {

//        sceneManager.dispose();
//        sceneAsset.dispose();
//        environmentCubemap.dispose();
//        diffuseCubemap.dispose();
//        specularCubemap.dispose();
//        brdfLUT.dispose();
//        skybox.dispose();
        modelBatch.dispose();
    }
}
