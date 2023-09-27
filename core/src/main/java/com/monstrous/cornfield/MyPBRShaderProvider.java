package com.monstrous.cornfield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import net.mgsx.gltf.scene3d.shaders.PBRShader;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class MyPBRShaderProvider extends PBRShaderProvider {
    public MyPBRShaderProvider() {
        super(PBRShaderProvider.createDefaultConfig());
    }


    /**
     * override this method in order to provide your own PBRShader subclass.
     * @param renderable
     * @param config
     * @param prefix
     */
    @Override
    protected PBRShader createShader(Renderable renderable, PBRShaderConfig config, String prefix){
        Gdx.app.log("createShader", renderable.meshPart.id);
        if( renderable.meshPart.mesh.isInstanced()) {
            Gdx.app.log("renderable is instanced:", renderable.meshPart.id);
            prefix += "#define instanced\n";
        }
        config.vertexShader = Gdx.files.internal("shaders/pbr-instanced.vs.glsl").readString();
        return new MyPBRShader(renderable, config, prefix);
    }

}
