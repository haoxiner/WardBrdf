precision mediump float;
uniform vec3 uLightPos;
struct Material
{
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shiness;
};
uniform Material uMaterial;
uniform sampler2D uTexture;
uniform bool uHasTexture;
varying vec3 fragPos;
varying vec3 normal;
varying vec2 texUV;
void main()
{
    vec3 ambient = uMaterial.ambient;
    // diffuse
    vec3 norm = normalize(normal);
    vec3 lightDir = normalize(uLightPos - fragPos);
    float diff = max(dot(lightDir,norm),0.0);
    vec3 diffuse = diff * uMaterial.diffuse;
    // specular
    vec3 viewDir = normalize(-fragPos);
    vec3 reflectDir = normalize(reflect(-lightDir,norm));
    float spec = pow(max(dot(viewDir,reflectDir),0.0),uMaterial.shiness);
    vec3 specular = spec*uMaterial.specular;
    gl_FragColor = vec4(vec3(ambient + diffuse + specular),1.0);
}