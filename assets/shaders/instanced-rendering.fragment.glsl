

#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

// very basic instanced rendering shader, no lighting, no shadows, etc.


uniform sampler2D u_diffuseTexture;

varying MED vec2 v_diffuseUV;

//out vec4 FragColor;


void main () {

    gl_FragColor = texture(u_diffuseTexture, v_diffuseUV);
}
