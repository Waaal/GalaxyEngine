package de.galaxy.core;

import com.jogamp.opengl.GL4;
import de.galaxy.light.DirectionLight;
import de.galaxy.light.Light;
import de.galaxy.light.PointLight;
import de.galaxy.light.SpotLight;
import de.galaxy.math.GalacticMath;
import de.galaxy.math.Matrix4;
import de.galaxy.math.Vector3;
import de.luke.openglTest.Texture2D;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Scene
{
    public static final int PROJECTION_VIEW = 1;
    public static final int ORTHOGRAPHIC_VIEW = 2;

    private String name;
    private Matrix4 projection;
    private Camera cam;

    private ArrayList<ShaderProgram> shaders;
    private ArrayList<Light> lightList;
    private ArrayList<HashMap<Integer, ArrayList<Model>>> renderLayer;

    private boolean loaded = false;
    private String path;

    private int dirLightNumber = 0, pointLightNumber = 0, spotLightNumber = 0;

    public Scene(String path, boolean loadScene, GL4 gl)
    {
        this.path = path;
        if(loadScene)
            Load(gl);
    }

    public boolean Load(GL4 gl)
    {
        if(!loaded)
        {
            String jsonStr = AssetManager.LoadJsonString(path);
            if(jsonStr != null)
            {
                projection = new Matrix4(1.0f);
                lightList = new ArrayList<Light>();
                shaders = new ArrayList<ShaderProgram>();
                renderLayer = new ArrayList<HashMap<Integer, ArrayList<Model>>>();

                JSONObject jo = new JSONObject(jsonStr);

                name = jo.getString("name");
                switch(jo.getInt("viewType"))
                {
                    case Scene.PROJECTION_VIEW -> projection.Perspective(GalacticMath.DegreeToRadiant(45.0f), 600/600, 0.1f, 100.0f);
                    case Scene.ORTHOGRAPHIC_VIEW -> projection.Orthographic(1,1,1,1,0.1f, 100.0f);
                    default -> projection = null;
                }
                if(projection == null)
                    return false;

                cam = new Camera(new Vector3(0.0f, 0.0f, 3.0f));

                //Shader
                HashMap<String, Integer> shaderMap = new HashMap<String, Integer>();
                int shaderCounter = 0;

                JSONArray shadersArray = jo.getJSONArray("shaders");
                for(int i = 0; i < shadersArray.length(); i++)
                {
                    ShaderProgram program = new ShaderProgram();

                    JSONObject shaderJSON = shadersArray.getJSONObject(i);
                    JSONArray shaderData = shaderJSON.getJSONArray("data");
                    if(shaderData.length() == 2)
                    {
                        program.Add(GL4.GL_VERTEX_SHADER, shaderData.getString(0), gl);
                        program.Add(GL4.GL_FRAGMENT_SHADER, shaderData.getString(1), gl);
                    }

                    if(program.Create(gl))
                    {
                        shaderMap.put(shaderJSON.getString("name"), shaderCounter);
                        shaders.add(program);
                        shaderCounter++;
                    }
                }

                //lights
                JSONArray lightsArray = jo.getJSONArray("lights");
                for(int i = 0; i < lightsArray.length(); i++)
                {
                    Light light = null;
                    JSONObject lightJSON = lightsArray.getJSONObject(i);

                    switch(lightJSON.getInt("type"))
                    {
                        case Light.DIRECTION_LIGHT:
                            light = new DirectionLight(JsonArrayToVec3(lightJSON.getJSONArray("pos")), JsonArrayToVec3(lightJSON.getJSONArray("dir")));
                            dirLightNumber++;
                            break;
                        case Light.POINT_LIGHT:
                            PointLight ptemp = new PointLight(JsonArrayToVec3(lightJSON.getJSONArray("pos")));
                            ptemp.linear = lightJSON.getFloat("linear");
                            ptemp.quadratic = lightJSON.getFloat("quadratic");

                            light = ptemp;
                            pointLightNumber++;
                            break;
                        case Light.SPOT_LIGHT:
                            SpotLight sltemp = new SpotLight(JsonArrayToVec3(lightJSON.getJSONArray("pos")), JsonArrayToVec3(lightJSON.getJSONArray("dir")));
                            sltemp.linear = lightJSON.getFloat("linear");
                            sltemp.quadratic = lightJSON.getFloat("quadratic");

                            sltemp.setCutOff(lightJSON.getFloat("cutOff"));
                            sltemp.setOuterCutOff(lightJSON.getFloat("outerCutOff"));

                            light = sltemp;
                            spotLightNumber++;
                            break;
                        default:
                            break;
                    }

                    light.color = JsonArrayToVec3(lightJSON.getJSONArray("color"));
                    lightList.add(light);
                }

                //objects
                JSONObject objects = jo.getJSONObject("objects");
                int layerNumber = objects.getInt("layerNum");

                for(int i = 0; i < layerNumber; i++)
                {
                    HashMap<Integer, ArrayList<Model>> currentModelMap = new HashMap<Integer, ArrayList<Model>>();

                    JSONObject currentLayerJSON = objects.getJSONObject("layer"+i);
                    JSONArray currentLayerObjectsArrayJSON = currentLayerJSON.getJSONArray("data");

                    int curretShaderID = -1;
                    int currentShaderArrayID = 0;

                    for(int j = 0; j < currentLayerObjectsArrayJSON.length(); j++)
                    {
                        JSONObject currentModelJSON = currentLayerObjectsArrayJSON.getJSONObject(j);

                        if(shaderMap.containsKey(currentModelJSON.getString("shader")))
                        {
                            currentShaderArrayID = shaderMap.get(currentModelJSON.getString("shader"));
                            ShaderProgram tempShader = shaders.get(currentShaderArrayID);
                            if(tempShader.getID() != curretShaderID)
                            {
                                curretShaderID = tempShader.getID();
                                gl.glUseProgram(curretShaderID);
                            }
                        }
                        else
                        {
                            System.out.println("[Galaxy Error]: Shader not found by name");
                            continue;
                        }

                        Model m = null;
                        switch(currentModelJSON.getString("type"))
                        {
                            case "MODEL":
                                m = ModelLoader.load(gl, currentModelJSON.getString("name"), shaders.get(currentShaderArrayID), currentModelJSON.getBoolean("uv-mapping"), currentModelJSON.getBoolean("calcTangentSpace"));
                                break;
                            case "BUILDER":
                                //TODO write type which builder builds, in file (block, plane)

                                JSONArray texturesJSONArray = currentModelJSON.getJSONArray("textures");
                                Texture2D[] textures = new Texture2D[texturesJSONArray.length()];

                                for(int k = 0; k < texturesJSONArray.length(); k++)
                                {
                                    JSONObject textureJSONObject = texturesJSONArray.getJSONObject(k);

                                    Texture2D temp = new Texture2D(textureJSONObject.getString("path"), gl, shaders.get(currentShaderArrayID), k, textureJSONObject.getInt("type"), textureJSONObject.getFloat("highlightFocus"));
                                    textures[k] = temp;
                                }
                                m = ModelBuilder.CreatePlane(gl, textures, currentModelJSON.getBoolean("calcTangentSpace"));
                                break;
                            default:
                                break;
                        }

                        JSONObject currentModelDataJSON = currentModelJSON.getJSONObject("data");
                        m.position = JsonArrayToVec3(currentModelDataJSON.getJSONArray("position"));
                        m.scale = JsonArrayToVec3(currentModelDataJSON.getJSONArray("scale"));

                        if(currentModelMap.containsKey(currentShaderArrayID))
                        {
                            ArrayList<Model> tempModel = currentModelMap.get(currentShaderArrayID);
                            tempModel.add(m);
                            currentModelMap.put(currentShaderArrayID, tempModel);
                        }
                        else
                        {
                            currentModelMap.put(currentShaderArrayID, new ArrayList<Model>(Arrays.asList(m)));
                        }
                    }
                    renderLayer.add(currentModelMap);
                }
            }
            return false;
        }
        return true;
    }

    public ArrayList<Light> getLightList(){return lightList;}
    public ArrayList<ShaderProgram> getShaders(){return shaders;}

    public ArrayList<HashMap<Integer, ArrayList<Model>>> getRenderLayer(){ return renderLayer;}

    public Matrix4 getProjection(){return projection;}
    public Camera getCam(){return cam;}

    public Vector3 getLightTypesCount(){return new Vector3(dirLightNumber, pointLightNumber,spotLightNumber);}

    private Vector3 JsonArrayToVec3(JSONArray array)
    {
        return new Vector3(array.getInt(0), array.getInt(1), array.getInt(2));
    }
}
