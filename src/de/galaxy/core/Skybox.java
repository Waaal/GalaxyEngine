package de.galaxy.core;

import com.jogamp.opengl.GL4;
import de.galaxy.math.Vector3;

public class Skybox
{
    private Model skyboxModel;
    private ShaderProgram skyBoxShader;

    public Skybox(GL4 gl, String[] skyBoxTextures, ShaderProgram shader)
    {
        skyBoxShader = shader;
        cubeMapTexture text = new cubeMapTexture(gl, skyBoxShader, skyBoxTextures);
        skyboxModel = ModelBuilder.CreateCubeMapBlock(gl, text);
    }

    public void Render(GL4 gl, Vector3 camPos)
    {
        gl.glUseProgram(skyBoxShader.getID());
        gl.glDepthMask(false);

        skyboxModel.position = camPos;
        skyboxModel.scale = new Vector3(5,5,5);
        skyboxModel.render(gl, skyBoxShader);

        gl.glDepthMask(true);
    }

    public void dispose(GL4 gl)
    {
        skyboxModel.dispose(gl);
    }
}
