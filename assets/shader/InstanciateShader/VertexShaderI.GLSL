#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 offset;
layout (location = 2) in vec3 col;

layout(std140) uniform Matrices
{
	mat4 projection;
	mat4 view;
};

uniform mat4 model;

out vec3 aColor;

void main()
{
	aColor = col;
    gl_Position = projection * view * model * vec4(aPos + offset, 1.0);
}