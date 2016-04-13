uniform mat4 uMVP;
uniform mat4 uModelView;
uniform mat4 uModel;

attribute vec3 vPosition;
attribute vec3 vNormal;
attribute vec2 vTextureUV;
varying vec3 fragPos;
varying vec3 normal;
varying vec2 texUV;

void main()
{
    fragPos = vec3(uModelView * vec4(vPosition,1.0));
    normal = vec3(uModelView * vec4(vNormal,0.0));
    texUV = vTextureUV;
    gl_Position = uMVP * vec4(vPosition,1.0);
}