#version 330 core

struct Material 
{
    sampler2D diffuse;
    sampler2D specular;
    sampler2D emission;
    sampler2D normal;
    float	shininess;
}; 

struct DirLight
{
	vec3 direction;
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

struct PointLight
{
	vec3 position;
	
	float linear;
	float quadratic;
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

struct SpotLight
{
	vec3 position;
	vec3 direction;
	
	float cutOff;
	float outerCutOff;
	
	float linear;
	float quadratic;
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

out vec4 FragColor;

//TODO: Shader Storage Buffer Object
uniform Material material;
uniform DirLight dirLights[3];
uniform PointLight pointLights[10];
uniform SpotLight spotLights[10];

uniform vec3 viewPos;

uniform int NR_DIR_LIGHTS;
uniform int NR_POINT_LIGHTS;
uniform int NR_SPOT_LIGHTS;

in vec3 FragPos;
in vec3 Normal;
in vec2 TextureCoord;
in mat3 TBN;

vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir);
vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir);
vec3 CalcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir);

bool blinnPhong;

void main()
{
	blinnPhong = true;
	
	// obtain normal from normal map in range [0,1]
    vec3 norm = texture(material.normal, TextureCoord).rgb;
    // transform normal vector to range [-1,1]
    norm = norm * 2.0 - 1.0;
    norm = normalize(TBN * norm);

	vec3 viewDir = normalize(viewPos - FragPos);
    vec3 result = vec3(0,0,0);

	for(int i = 0; i < NR_DIR_LIGHTS; i++)
	    result += CalcDirLight(dirLights[i], norm, viewDir);
	
	for(int i = 0; i < NR_POINT_LIGHTS; i++)
		result += CalcPointLight(pointLights[i], norm, FragPos, viewDir); 

	for(int i = 0; i < NR_SPOT_LIGHTS; i++)
	    result += CalcSpotLight(spotLights[i], norm, FragPos, viewDir);

    FragColor = vec4(result, 1.0);
} 

vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir)
{
	vec3 lightDir = normalize(-light.direction);
	
	float diff = max(dot(normal, lightDir), 0.0);
	
	vec3 reflectDir = reflect(-lightDir, normal);
	float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
	
	vec3 ambient = light.ambient * vec3(texture(material.diffuse, TextureCoord));
	vec3 diffuse = light.diffuse * diff * vec3(texture(material.diffuse, TextureCoord));
	vec3 specular = light.specular * spec * vec3(texture(material.specular, TextureCoord));
	
	return (ambient + diffuse + specular);
}

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    
    float diff = max(dot(normal, lightDir), 0.0);
    float spec = 0.0;
    
    if(blinnPhong)
    {
    	vec3 halfwayDir = normalize(lightDir + viewDir);
    	spec = pow(max(dot(normal, halfwayDir), 0.0), material.shininess);
    }
    else
    {
        vec3 reflectDir = reflect(-lightDir, normal);
    	spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    }
    
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (1.0 + light.linear * distance + light.quadratic * (distance * distance));    
  			     
    vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, TextureCoord));
    vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, TextureCoord));
    vec3 specular = light.specular * spec * vec3(texture(material.specular, TextureCoord));
    
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    
    return (ambient + diffuse + specular);
}

vec3 CalcSpotLight(SpotLight light, vec3 normal, vec3 fragPos, vec3 viewDir)
{
    vec3 lightDir = normalize(light.position - fragPos);
    
    float diff = max(dot(normal, lightDir), 0.0);
    
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (1.0 + light.linear * distance + light.quadratic * (distance * distance));    
  			     
    vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, TextureCoord));
    vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, TextureCoord));
    vec3 specular = light.specular * spec * vec3(texture(material.specular, TextureCoord));
    
    float theta = dot(lightDir, normalize(-light.direction)); 
    float epsilon = (light.cutOff - light.outerCutOff);
    float intensity = clamp((theta - light.outerCutOff) / epsilon, 0.0, 1.0);
    
    diffuse  *= intensity;
    specular *= intensity;
    
    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;
    
    return (ambient + diffuse + specular);
}

