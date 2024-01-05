#version 330 core
out vec4 FragColor;

struct Material 
{
    sampler2D diffuse;
};

uniform Material material;

in vec2 TextCoord;

void main()
{
	vec4 text = texture(material.diffuse, TextCoord);
	if(text.a < 0.1)
		discard;
		
    FragColor = text;
} 