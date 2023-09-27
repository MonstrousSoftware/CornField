package com.monstrous.cornfield;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class MyModelInstance extends ModelInstance {

    public MyModelInstance(Model model) {
        super(model);
    }

//    @Override
//    protected void getRenderables (Node node, Array<Renderable> renderables, Pool<Renderable> pool) {
//        if (node.parts.size > 0) {
//            for (NodePart nodePart : node.parts) {
//                Renderable renderable = getRenderable(pool.obtain(), node, nodePart);
//                renderable.shader = new PBRInstancedShader(renderable);         // todo reuse a single one
//                if (nodePart.enabled) renderables.add(renderable);
//            }
//        }
//        for (Node child : node.getChildren()) {
//            getRenderables(child, renderables, pool);
//        }
//    }
}
