uniform mat4 uMVP;
uniform mat4 uView;
uniform mat4 uModelView;
uniform vec3 uLightDir;
uniform mat4 uDepthBiasMVP;

attribute vec3 vPosition;
attribute vec3 vNormal;
attribute vec2 vTexUV;
attribute vec3 vTangent;

varying vec3 varNormal;
varying vec2 varTexUV;
varying vec3 varPosition;
varying vec3 varTangent;
varying vec3 varLightDir;
varying vec4 varShadowCoord;

void main()
{
    varTexUV = vTexUV;
    varPosition = (uModelView * vec4(vPosition,1.0)).xyz;
    varNormal = (uModelView*vec4(vNormal,0.0)).xyz;
    varTangent = (uModelView * vec4(vTangent,0.0)).xyz;
    varLightDir = (uView * vec4(uLightDir,0.0)).xyz;
    varShadowCoord = uDepthBiasMVP * vec4(vPosition,1.0);
    gl_Position = uMVP * vec4(vPosition,1.0);
}
