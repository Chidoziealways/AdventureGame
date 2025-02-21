#version 440 core

in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toLightVector[4];
in vec3 toCameraVector;
in float visibility;
in vec4 shadowCoords;

layout (location = 0) out vec4 out_Colour;
layout (location = 1) out vec4 out_BrightColour;

uniform sampler2D modelTexture;
uniform sampler2D shadowMap;
uniform sampler2D specularMap;

uniform float usesSpecularMap;
uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;
uniform float mapSize;
uniform float isSelected;

const int pcfCount = 2;
const float totalTexels = (pcfCount * 2.0 - 1.0) * (pcfCount * 2.0 - 1.0);
const float levels = 3.0;

void main(void) {

    float texelSize = 1.0 / mapSize;
    float total = 0.0;

    for(int x = -pcfCount; x <= pcfCount; x++) {
        for(int y = -pcfCount; y <= pcfCount; y++) {
            float objectNearestLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;
            if(shadowCoords.z > objectNearestLight) {
                total += 1.0;
            }
        }
    }

    total /= totalTexels;

    float lightFactor = 1.0 - (total * shadowCoords.w);

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    for(int i = 0; i < 4; i++){
        float distance = length(toLightVector[i]);
        float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z *  distance * distance);
        vec3 unitLightVector = normalize(toLightVector[i]);
        float nDotl = dot(unitNormal, unitLightVector);
        float brightness = max(nDotl, 0.0);
        float level = floor(brightness * levels);
        brightness = level / levels;
        vec3 lightDirection = -unitLightVector;
        vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
        float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
        specularFactor = max(specularFactor, 0.0);
        float dampedFactor = pow(specularFactor, shineDamper);
        level = floor(dampedFactor * levels);
        dampedFactor = level / levels;
        totalDiffuse = totalDiffuse + (brightness * lightColour[i]) / attFactor;
        totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i]) / attFactor;
    }
    totalDiffuse = max(totalDiffuse * lightFactor, 0.4);

    vec4 textureColour = texture(modelTexture, pass_textureCoords);
    if(textureColour.a < 0.5) {
        discard;
    }

    out_BrightColour = vec4(0.0);
    if(usesSpecularMap > 0.5) {
        vec4 mapInfo = texture(specularMap, pass_textureCoords);
        totalSpecular *= mapInfo.r;
        if(mapInfo.g > 0.5) {
            out_BrightColour = textureColour + vec4(totalSpecular, 1.0) * 10;
            totalDiffuse = vec3(1.0);
        }
    }

    vec4 finalColor = vec4(totalDiffuse, 1.0) * textureColour + vec4(totalSpecular, 1.0);
    finalColor = mix(vec4(skyColour, 1.0), finalColor, visibility);

    // Apply bluish tint if isSelected is greater than 0.5
    if (isSelected > 0.5) {
        vec4 blueTint = vec4(0.2, 0.2, 1.0, 1.0); // Adjust the values for the desired bluish look
        finalColor = mix(finalColor, blueTint, 0.5); // Adjust the blend factor as needed
    }

    out_Colour = finalColor;
}
