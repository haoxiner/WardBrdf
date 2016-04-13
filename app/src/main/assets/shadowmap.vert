uniform mat4 uDepthMVP;
attribute vec3 vPosition;
void main(){
    gl_Position = uDepthMVP * vec4(vPosition,1.0);
}