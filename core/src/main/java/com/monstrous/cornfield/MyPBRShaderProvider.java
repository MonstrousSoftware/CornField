package com.monstrous.cornfield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class MyPBRShaderProvider extends PBRShaderProvider {
    public MyPBRShaderProvider() {
        super(PBRShaderProvider.createDefaultConfig());
    }

//    @Override
//    protected Shader createShader(Renderable renderable) {
//        Gdx.app.log("createShader", "");
//        if( renderable.meshPart.mesh.isInstanced())
//            return new PBRInstancedShader(renderable,PBRShaderProvider.createDefaultConfig(), "" );
//        else
//            return super.createShader(renderable);
//    }
}
