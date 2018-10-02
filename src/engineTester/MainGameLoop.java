package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {
	public static void main(String[] args) {
		DisplayManager.createDisplay();
		MasterRenderer renderer = new MasterRenderer();
		Random random = new Random();
		
		//LOADER
		Loader loader = new Loader();
		
		//ENVIRONMENT
		Light light = new Light(new Vector3f(1000, 1000, -20), new Vector3f(1, 1, 1));
		
		//TREE MODEL
		ModelData treeData = OBJFileLoader.loadOBJ("example/tree");
		TexturedModel tree = new TexturedModel(loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), 
				treeData.getNormals(), treeData.getIndices()), new ModelTexture(loader.loadTexture("example/tree")));

		//GRASS MODEL
		ModelData grassData = OBJFileLoader.loadOBJ("example/grassModel");
		TexturedModel grass = new TexturedModel(loader.loadToVAO(grassData.getVertices(), grassData.getTextureCoords(), 
				grassData.getNormals(), grassData.getIndices()), new ModelTexture(loader.loadTexture("example/flower")));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		
		//FERN MODEL
		ModelData fernData = OBJFileLoader.loadOBJ("example/fern");
		TexturedModel fern = new TexturedModel(loader.loadToVAO(fernData.getVertices(), fernData.getTextureCoords(), 
				fernData.getNormals(), fernData.getIndices()), new ModelTexture(loader.loadTexture("example/fern")));
		fern.getTexture().setHasTransparency(true);
		
		//ENTITIES
		List<Entity> entities = new ArrayList<Entity>();
		for(int i = 0; i < 800; i++) {
			//trees
			entities.add(new Entity(tree, new Vector3f(random.nextFloat() * (800f), 0, 
					-(random.nextFloat() * (800f))), 0, 0, 0, 3));
			entities.add(new Entity(tree, new Vector3f(-random.nextFloat() * (800f), 0, 
					-random.nextFloat() * (800f)), 0, 0, 0, 3));
			//grass
			entities.add(new Entity(grass, new Vector3f(random.nextFloat() * (800f), 0, 
					-(random.nextFloat() * (800f))), 0, 0, 0, 1));
			entities.add(new Entity(grass, new Vector3f(-random.nextFloat() * (800f), 0, 
					-random.nextFloat() * (800f)), 0, 0, 0, 1));
			//fern
			entities.add(new Entity(fern, new Vector3f(random.nextFloat() * (800f), 0, 
					-(random.nextFloat() * (800f))), 0, 0, 0, 0.6f));
			entities.add(new Entity(fern, new Vector3f(-random.nextFloat() * (800f), 0, 
					-random.nextFloat() * (800f)), 0, 0, 0, 0.6f));

		}
		
		//TERRAIN TEXTURE STUFF
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("example/grassy2"));
		TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("example/dirt"));
		TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("example/pinkFlowers"));
		TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("example/path"));
		
		TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, 
				gTexture, bTexture);
		TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("example/blendMap"));
		
		Terrain terrain = new Terrain(-1, -1, loader, texturePack, blendMap);
		Terrain terrain2 = new Terrain(0, -1, loader, texturePack, blendMap);
		
		//PLAYER
		TexturedModel playerModel = new TexturedModel(OBJLoader.loadObjModel("example/person", loader), 
				new ModelTexture(loader.loadTexture("example/playerTexture")));
		Player player = new Player(playerModel, new Vector3f(100, 0, -50), 0, 0, 0, 0.25f);

		//CAMERA
		Camera camera = new Camera(player);

		
		while(!Display.isCloseRequested()) {
			camera.move();
			
			player.move();
			renderer.processEntity(player);
			
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			for(Entity e : entities) {
				renderer.processEntity(e);
			}
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
