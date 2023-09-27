# CornField

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/tommyettinger/gdx-liftoff).

A demonstrator of instancing of a gltf model in combination with the PBR shader of gdx-gltf.
Requires GL3.

![screenshot](https://github.com/MonstrousSoftware/CornField/assets/49096535/2910cd9d-6796-43ae-8e5e-0d5665e981f1)

An alternative is also implemented using billboards.  Can you tell which field is which?
The billboard technique could be useful as a fall-back option if the platform performs poorly. 
Or to add extra depth in the far distance. 



Note: GLTF models need to be exported from Blender using GLTF Separate, not GLTF Embedded. 

Update: the derived PBR shader provider allows to combine instanced rendering of some assets with normal rendering of other assets, applying the relevant shader as needed.
The PBR depth shader is also subclassed to allow shadows from the corn stalks.




## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3.
- `teavm`: Experimental web platform using TeaVM and WebGL.

