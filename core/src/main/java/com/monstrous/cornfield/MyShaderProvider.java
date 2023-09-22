package com.monstrous.cornfield;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class MyShaderProvider extends PBRShaderProvider {
    public MyShaderProvider() {
        super(PBRShaderProvider.createDefaultConfig());
    }

    @Override
    protected Shader createShader(Renderable renderable) {
        Gdx.app.log("createShader", "");
        return super.createShader(renderable);
    }
}
