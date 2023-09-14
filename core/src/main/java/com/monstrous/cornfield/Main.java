package com.monstrous.cornfield;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
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
import net.mgsx.gltf.scene3d.shaders.PBRDepthShaderProvider;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Comparator;


public class Main extends ApplicationAdapter {

    public static String GLTF_FILE = "models/corn.gltf";
    public static String NODE_NAME = "cornstalk";                   // "cornstalk"  "reeds"

    private static final float SEPARATION_DISTANCE = 1.0f;          // min distance between instances
    private static final float AREA_LENGTH = 100.0f;                // size of the (square) field

    private static final int SHADOW_MAP_SIZE = 2048;


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
    private BitmapFont font;
    private SpriteBatch batch;
    private int instanceCount;
    private Texture billboard;
    private Array<BillBoardData> billboardPositions;
    private Array<BillBoardData> visibleBillboards;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        if (Gdx.gl30 == null) {
            throw new GdxRuntimeException("GLES 3.0 profile required for this programme.");
        }
        font = new BitmapFont();
        batch = new SpriteBatch();

        billboard = new Texture(Gdx.files.internal("images/cornstalk-billboard.png") );


        // setup camera
        camera = new PerspectiveCamera(50f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.1f;
        camera.far = 2000f;
        camera.position.set(-5, 2.5f, -5);
        camera.lookAt(10,1.5f,10);
        camera.update();

        // create scene manager
        // but use an amended vertex shader as default PBR vertex shader
        PBRShaderConfig config = PBRShaderProvider.createDefaultConfig();
        config.vertexShader = Gdx.files.internal("shaders/pbr-instanced.vs.glsl").readString();
        //config.glslVersion = "#version 300 es\n#define GLSL3\n";
        sceneManager = new SceneManager( new PBRShaderProvider(config), new PBRDepthShaderProvider(new DepthShader.Config()) );
        sceneManager.setCamera(camera);

        camController = new CameraInputController(camera);
        Gdx.input.setInputProcessor(camController);

        // gdx-gltf set up
        //
        sceneManager.environment.set(new PBRFloatAttribute(PBRFloatAttribute.ShadowBias, 0.001f));

        // setup light
        light = new DirectionalShadowLight(SHADOW_MAP_SIZE, SHADOW_MAP_SIZE).setViewport(100, 100, 5, 400);

        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        light.intensity = 0.51f;
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(0.3f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);

        sceneAsset = new GLTFLoader().load(Gdx.files.internal(GLTF_FILE));

        Scene sceneGround = new Scene(sceneAsset.scene, "groundplane");
        if(sceneGround.modelInstance.nodes.size == 0) {
            Gdx.app.error("GLTF load error: node not found", "groundplane");
            Gdx.app.exit();
        }
       // sceneManager.addScene(sceneGround);


        // extract the model to instantiate
        scene = new Scene(sceneAsset.scene, NODE_NAME);
        if(scene.modelInstance.nodes.size == 0) {
            Gdx.app.error("GLTF load error: node not found", NODE_NAME);
            Gdx.app.exit();
        }
        sceneManager.addScene(scene);

        // assumes the instance has one node,  and the meshPart covers the whole mesh
        for(int i = 0 ; i < scene.modelInstance.nodes.first().parts.size; i++) {
            Mesh mesh = scene.modelInstance.nodes.first().parts.get(i).meshPart.mesh;
            setupInstancedMesh(mesh);
        }

        generateBillBoardPositions();

    }


    private void setupInstancedMesh( Mesh mesh ) {

        // generate instance data

        // generate a random poisson distribution of instances over a rectangular area, meaning instances are never too close together
        PoissonDistribution poisson = new PoissonDistribution();
        Rectangle area = new Rectangle(1, 1, AREA_LENGTH, AREA_LENGTH);
        Array<Vector2> points = poisson.generatePoissonDistribution(SEPARATION_DISTANCE, area);
        instanceCount = points.size;

        // add 4 floats per instance
        mesh.enableInstancedRendering(true, instanceCount, new VertexAttribute(VertexAttributes.Usage.Position, 4, "i_offset")  );

        // Create offset FloatBuffer that will contain instance data to pass to shader
        FloatBuffer offsets = BufferUtils.newFloatBuffer(instanceCount * 4);
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.log("FloatBuffer: isDirect()",  "" + offsets.isDirect());  // false = teaVM for now
        Gdx.app.log("Application: Type()",  "" + Gdx.app.getType());

        // fill instance data buffer
        for(Vector2 point: points) {
                float angle = MathUtils.random(0.0f, (float)Math.PI*2.0f);
                float fy = MathUtils.random(-0.6f, 0.0f);        // vary height from ground
                float scaley = MathUtils.random(0.8f, 1.2f);        // vary scale in up direction

                offsets.put(new float[] {point.x, scaley, point.y, angle });     // x, y scale, z, angle
        }

        ((Buffer)offsets).position(0);
        mesh.setInstanceData(offsets);
    }

    static class BillBoardData {
        float x;
        float z;
        boolean inView;
        Vector3 worldPosition;
        Vector3 screenPosition;
        float randomScale;
        float distance;
        float scale;
    }

    static class BillBoardComparator<BillBoardData> {

    }

    private void generateBillBoardPositions() {

        // generate instance data

        // generate a random poisson distribution of instances over a rectangular area, meaning instances are never too close together
        PoissonDistribution poisson = new PoissonDistribution();
        Rectangle area = new Rectangle(-AREA_LENGTH, -AREA_LENGTH, AREA_LENGTH, AREA_LENGTH);
        Array<Vector2> points = poisson.generatePoissonDistribution(SEPARATION_DISTANCE, area);

        billboardPositions = new Array<>();
        visibleBillboards = new Array<>();
        for(Vector2 point: points ) {
            BillBoardData datum = new BillBoardData();
            datum.x = point.x;
            datum.z = point.y;
            datum.worldPosition = new Vector3(point.x, 0, point.y);   // map to 3d space
            datum.screenPosition = new Vector3();
            datum.randomScale = 1f; //0.8f + 0.4f*(float)(Math.sin(point.x * 3.13 + point.y * 5.145)); // size of this particular instance
            billboardPositions.add(datum);
        }
    }

    private void updateBillBoardPosition( Camera cam ){
        Vector3 pos = new Vector3();
        visibleBillboards.clear();
        for(BillBoardData datum: billboardPositions) {            // todo need to sort by distance to camera and render back to front
            datum.inView = cam.frustum.pointInFrustum(datum.worldPosition);
            if(!datum.inView)
                continue;
            datum.distance = datum.worldPosition.dst(camera.position);
            datum.scale = datum.randomScale * 6f / datum.distance;
            datum.screenPosition.set(datum.worldPosition);
            cam.project(datum.screenPosition);
            visibleBillboards.add(datum);
        }

        Comparator<BillBoardData> comparator = new Comparator<>() {
            public int compare (BillBoardData o1, BillBoardData o2) {

                return (int) (100f*(o2.distance - o1.distance));
            }
        };
        // sort by distance to camera to render back to front
        visibleBillboards.sort(comparator);
    }

    private void renderBillboards(Camera cam) {

        batch.begin();

        for(BillBoardData datum: visibleBillboards) {
            if(!datum.inView)
                continue;
            batch.draw(billboard, datum.screenPosition.x,  datum.screenPosition.y, datum.scale * billboard.getWidth(), datum.scale * billboard.getHeight());
        }
        batch.end();
    }

    @Override
    public void render() {

        camController.update();
        updateBillBoardPosition(camera);

        sceneManager.update(Gdx.graphics.getDeltaTime());

        ScreenUtils.clear(Color.TEAL, true);
        sceneManager.render();






        renderBillboards(sceneManager.camera);


        int fps = (int)(1f/Gdx.graphics.getDeltaTime());
        batch.begin();
        font.draw(batch, "Instanced rendering demo", 20, 110);
        font.draw(batch, "Instances: "+instanceCount, 20, 80);
        font.draw(batch, "Vertices/instance: "+countVertices(scene.modelInstance), 20, 50);
        font.draw(batch, "FPS: "+fps, 20, 20);

        //batch.draw(billboard, 100,100);
        batch.end();
    }

    private int countVertices(ModelInstance instance){
        int count = 0;
        for(int i = 0; i < instance.nodes.first().parts.size; i++){
            count += instance.nodes.first().parts.get(i).meshPart.mesh.getNumVertices();
        }
        return count;
    }

    @Override
    public void resize(int width, int height) {
        // Resize your screen here. The parameters represent the new window size.
        sceneManager.updateViewport(width, height);
    }


    @Override
    public void dispose() {

        sceneManager.dispose();
        sceneAsset.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        skybox.dispose();
    }
}
