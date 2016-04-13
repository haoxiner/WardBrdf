precision highp float;
const float PI = 3.14159265;
const float ONE_OVER_PI = 1.0 / PI;

uniform sampler2D uTex;
uniform sampler2D uNormalMap;
uniform sampler2D uShadowMap;
uniform float uxPixelOffset;
uniform float uyPixelOffset;
uniform bool uPCFON;

varying vec3 varNormal;
varying vec2 varTexUV;
varying vec3 varPosition;
varying vec3 varTangent;
varying vec3 varLightDir;
varying vec4 varShadowCoord;

float wardSpecular(
    vec3 lightDirection,
    vec3 viewDirection,
    vec3 surfaceNormal,
    vec3 tangent,
    vec3 bitangent,
    float alphaX,
    float alphaY)
{
    float NdotL = dot(surfaceNormal, lightDirection);
    float NdotR = dot(surfaceNormal, viewDirection);
    if(NdotL < 0.0 || NdotR < 0.0)
    {
        return 0.0;
    }
    vec3 H = normalize(lightDirection + viewDirection);
    float NdotH = dot(surfaceNormal, H);
    float TdotH = dot(tangent, H);
    float BdotH = dot(bitangent, H);
    float partA = sqrt(NdotL/NdotR) / (4.0 * PI * alphaX * alphaY);
    float partB = (pow(TdotH/alphaX, 2.0) + pow(BdotH/alphaY, 2.0)) / (1.0 + NdotH);
    return partA * exp(-2.0 * partB);
}

//Calculate variable bias - from http://www.opengl-tutorial.org/intermediate-tutorials/tutorial-16-shadow-mapping
float calcBias()
{
	float bias;

	vec3 n = normalize( varNormal );
	// Direction of the light (from the fragment to the light)
	vec3 l = normalize( varLightDir );

	// Cosine of the angle between the normal and the light direction,
	// clamped above 0
	//  - light is at the vertical of the triangle -> 1
	//  - light is perpendiular to the triangle -> 0
	//  - light is behind the triangle -> 0
	float cosTheta = clamp( dot( n,l ), 0.0, 1.0 );

 	bias = 0.0001*tan(acos(cosTheta));
	bias = clamp(bias, 0.0, 0.01);

 	return bias;
}

float lookup( vec2 offSet)
{
	vec4 shadowMapPosition = varShadowCoord / varShadowCoord.w;

	float distanceFromLight = texture2D(uShadowMap, (shadowMapPosition +
	                               vec4(offSet.x * uxPixelOffset, offSet.y * uyPixelOffset, 0.0, 0.0)).st ).z;

	//add bias to reduce shadow acne (error margin)
//	float bias = calcBias();

	return float(distanceFromLight > shadowMapPosition.z);
}

float shadowPCF()
{
	float shadow = 1.0;

	for (float y = -1.5; y <= 1.5; y = y + 1.0) {
		for (float x = -1.5; x <= 1.5; x = x + 1.0) {
			shadow += lookup(vec2(x,y));
		}
	}

	shadow /= 16.0;
//	shadow += 0.2;

	return shadow;
}

void main()
{
    vec3 N = normalize(varNormal);
    vec3 L = normalize(varLightDir);
    vec3 V = normalize(-varPosition);
    vec3 H = normalize(L + V);

    vec3 B = normalize(cross(varTangent,N));
    vec3 T = normalize(cross(N,B));

    mat3 TBN = mat3(T,B,N);
    vec3 normal = TBN*(texture2D(uNormalMap,varTexUV).rgb*2.0-1.0);

//    vec2 P = vec2(0.4,0.55);
//    vec2 A = vec2(0.111,0.364);

    vec2 P = vec2(0.6,0.3);
    vec2 A = vec2(0.033,0.33);

    float shadow = 1.0;
    //if the fragment is not behind light view frustum
    if (varShadowCoord.w > 0.0) {
        if(uPCFON){
            shadow = shadowPCF();
            shadow = (shadow * 0.3) + 0.7;
        }else{
            vec4 shadowMapPosition = varShadowCoord / varShadowCoord.w;
            float distanceFromLight = texture2D(uShadowMap, shadowMapPosition.st ).z;
            shadow = distanceFromLight < shadowMapPosition.z - 0.01 ? 0.5 : 1.0;
        }
        //scale 0.0-1.0 to 0.2-1.0

    }
    gl_FragColor = shadow * texture2D(uTex,varTexUV) *
    ((P.y * wardSpecular(L,V,normal,T,B,A.x,A.y) + P.x*ONE_OVER_PI) * max(dot(N,L),0.0) + P.x*ONE_OVER_PI+max(dot(H, N),0.0) * P.y);

//    gl_FragColor = shadow * vec4(0.783,0.783,0.783,1.0) *
//        ((P.y * wardSpecular(L,V,normal,T,B,A.x,A.y) + P.x*ONE_OVER_PI) * max(dot(N,L),0.0) + P.x*ONE_OVER_PI+max(dot(H, N),0.0) * P.y);

}
