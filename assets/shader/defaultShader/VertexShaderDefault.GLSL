#version 330 core
layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;
layout (location = 2) in vec2 aTextureCoord;
layout (location = 3) in vec3 aTangent;

layout(std140) uniform Matrices
{
	mat4 projection;
	mat4 view;
};

uniform mat4 model;

out vec3 FragPos;
out vec3 Normal;
out vec2 TextureCoord;
out mat3 TBN;

void main()
{
    FragPos = vec3(model*vec4(aPos, 1.0));
    
    //Costly operation. Do not do this in the shader, do this on the CPU and send the matrix to the shader per uniform.
    //Only for learning purpose
    Normal = mat3(transpose(inverse(model))) * aNormal;
    
    TextureCoord = aTextureCoord;
    
    vec3 T = normalize(vec3(model * vec4(aTangent, 0.0)));
    vec3 N = normalize(vec3(model * vec4(aNormal, 0.0)));
    // re-orthogonalize T with respect to N
    T = normalize(T - dot(T, N) * N);
    // then retrieve perpendicular vector B with the cross product of T and N
    vec3 B = cross(N, T);

    TBN = mat3(T, B, N);
    
    gl_Position = projection * view * model * vec4(aPos.x, aPos.y, aPos.z, 1.0);
}