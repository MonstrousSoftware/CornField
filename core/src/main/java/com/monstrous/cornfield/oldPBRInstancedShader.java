package com.monstrous.cornfield;

import com.badlogic.gdx.graphics.g3d.Renderable;
import net.mgsx.gltf.scene3d.shaders.PBRShader;

public class oldPBRInstancedShader extends PBRShader {

////    static PBRShaderConfig instancedConfig;
//    static PBRShaderProvider provider;
//
//    public static void setup() {    // must be called before the first constructor
////        instancedConfig = PBRShaderProvider.createDefaultConfig();
////        instancedConfig.vertexShader = Gdx.files.internal("shaders/pbr-instanced.vs.glsl").readString();
//
//        provider = new PBRShaderProvider(PBRShaderConfig config) {
//            @Override
//            protected PBRShader createShader(Renderable renderable, PBRShaderConfig config, String prefix){
//                config.vertexShader = Gdx.files.internal("shaders/pbr-instanced.vs.glsl").readString();
//                return super.createShader(renderable, config, prefix);
//            }
//        };
//
//
//    }

//    @Override
//    public void init() {
//        super.init();
//    }
//
//    @Override
//    public int compareTo(Shader other) {
//        return super.compareTo(other);
//    }
//
//    @Override
//    public void begin(Camera camera, RenderContext context) {
//        super.begin(camera, context);
//    }
//
//    @Override
//    public void end() {
//        super.end();
//    }
//
//    @Override
//    public void dispose() {
//        super.dispose();
//    }
//
//    public PBRInstancedShader(Renderable renderable) {
//        provider.
//        super(renderable, instancedConfig , "");
//    }
//
//
//
    public oldPBRInstancedShader(Renderable renderable, Config config, String prefix) {
        super(renderable, config , prefix);
    }
//
//    @Override
//    public boolean canRender(Renderable renderable) {
//        Gdx.app.log("canRender?", ""+renderable.meshPart.mesh.isInstanced());
//        return renderable.meshPart.mesh.isInstanced();
//    }

}
