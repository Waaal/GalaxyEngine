#version 330 core
struct SimpleMaterial
{
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
	float shininess;
};

struct Light{
	vec3 position;
	
	vec3 ambient;
	vec3 diffuse;
	vec3 specular;
};

out vec4 FragColor;

uniform Material material;
uniform Light light;

in vec3 FragPos;
in vec3 Normal;
in vec2 TextureCoord;
in vec3 lightPosView;

void main()
{
	vec3 norm = normalize(Normal);
	vec3 lightDir = normalize(lightPosView - FragPos);  
	
	//ambient
	vec3 ambient = light.ambient * SimpleMaterial.ambient;
	
	//diffuse
	float diff = max(dot(norm, lightDir), 0.0);
	vec3 diffuse = light.diffuse * (diff * SimpleMaterial.diffuse);
	
	//specular
	vec3 viewDir = normalize(vec3(0,0,0)-FragPos);
	vec3 reflectDir = reflect(-lightDir, norm);
	
	float spec = pow(max(dot(viewDir, reflectDir),0.0), SimpleMaterial.shininess);
	vec3 specular = light.specular * (spec * SimpleMaterial.specular);
	
	vec3 result = ambient + diffuse + specular;
	
    FragColor = vec4(result,1.0);
} 