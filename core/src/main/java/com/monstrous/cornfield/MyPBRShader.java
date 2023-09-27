package com.monstrous.cornfield;

import com.badlogic.gdx.graphics.g3d.Renderable;
import net.mgsx.gltf.scene3d.shaders.PBRShader;

public class MyPBRShader extends PBRShader {

    public MyPBRShader(Renderable renderable, Config config, String prefix) {
        super(renderable, config, prefix);
    }
}
