package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import entities.Camera;
import entities.Entity;
import entities.Light;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import terrains.Terrain;
import textures.ModelTexture;

public class MainGameLoop {
	public static void main(String[] args) {
		DisplayManager.createDisplay();
		Random random = new Random();
		
		//LOADER
		Loader loader = new Loader();
		
		//ENVIRONMENT
		Light light = new Light(new Vector3f(0, 1000, -20), new Vector3f(1, 1, 1));
		Camera camera = new Camera();
		
		//DRAGON MODEL
		RawModel playerModel = OBJLoader.loadObjModel("dragon/dragon", loader);
		TexturedModel texturedPlayerModel = new TexturedModel(playerModel, new ModelTexture(loader.loadTexture("dragon/red")));
		ModelTexture playerTexture = texturedPlayerModel.getTexture();
		playerTexture.setShineDamper(10);
		playerTexture.setRefelctivity(1);
		
		//TREE MODEL
		RawModel treeModel = OBJLoader.loadObjModel("tree/tree", loader);
		TexturedModel texturedTreeModel = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("dragon/plain")));
		ModelTexture treeTexture = texturedTreeModel.getTexture();
		treeTexture.setShineDamper(10);
		treeTexture.setRefelctivity(0);
		
		//ENTITIES
		Entity entity = new Entity(texturedPlayerModel, new Vector3f(0, 1, -25), 0, 0, 0, 1);
		List<Entity> trees = new ArrayList<Entity>();
		for(int i = 0; i < 200; i++) {
			float x = random.nextFloat() * (800f);
			float z = random.nextFloat() * (800f);
			trees.add(new Entity(texturedTreeModel, new Vector3f(x, 1, -z), 0, 0, 0, 0.25f));
		}
		
		for(int i = 0; i < 200; i++) {
			float x = random.nextFloat() * (800f);
			float z = random.nextFloat() * (800f);
			trees.add(new Entity(texturedTreeModel, new Vector3f(-x, 1, -z), 0, 0, 0, 0.25f));
		}
		
		Terrain terrain = new Terrain(-1, -1, loader, new ModelTexture(loader.loadTexture("grass")));
		Terrain terrain2 = new Terrain(0, -1, loader, new ModelTexture(loader.loadTexture("grass")));
		
		MasterRenderer renderer = new MasterRenderer();
		
		while(!Display.isCloseRequested()) {
			entity.increaseRotation(0,0.5f,0);
			camera.move();
			renderer.processTerrain(terrain);
			renderer.processTerrain(terrain2);
			renderer.processEntity(entity);
			for(Entity tree : trees) {
				renderer.processEntity(tree);
			}
			renderer.render(light, camera);
			//update display
			DisplayManager.updateDisplay();
		}
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}
