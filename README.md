# CornField

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff).

A demonstrator of instancing to render a corn field.
Uses gdx-gltf to load a gltf model, but the rendering is performed via modelBatch/modelInstance using a special shader.

To do: the web version (teavm) is more particular thank desktop on having the right #version directive and using modern glsl keywords.



## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3.
- `teavm`: Experimental web platform using TeaVM and WebGL.

