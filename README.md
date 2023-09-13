# CornField

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff).

A demonstrator of instancing of a gltf model in combination with the PBR shader of gdx-gltf.
Requires GL3.
![screenshot](https://github.com/MonstrousSoftware/CornField/assets/49096535/519b91aa-8d01-4252-8cc3-08caddac04c2)

To do: the web version (teavm) has trouble with the texture of some gltf models and renders them black.

To do: combine instanced rendering of some assets with regular rendering of other assets, applying the relevant shader as needed.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3.
- `teavm`: Experimental web platform using TeaVM and WebGL.

