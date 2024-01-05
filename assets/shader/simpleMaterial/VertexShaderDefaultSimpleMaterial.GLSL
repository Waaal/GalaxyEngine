#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTextureCoord;

layout(std140) uniform Matrices
{
	mat4 projection;
	mat4 view;
};

uniform mat4 model;

uniform vec3 lightPos;

out vec3 FragPos;
out vec3 Normal;
out vec2 TextureCoord;
out vec3 lightPosView;

void main()
{
    gl_Position = projection * view * model * vec4(aPos.x, aPos.y, aPos.z, 1.0);
    FragPos = vec3(view*model*vec4(aPos, 1.0));
    
    //Costly operation. Do not do this in the shader, do this on the CPU and send the matrix to the shader per uniform.
    //Only for learning purpose
    Normal = mat3(transpose(inverse(view*model))) * aNormal;
    
    lightPosView = vec3(view * vec4(lightPos, 1.0));
    
    TextureCoord = aTextureCoord;
}