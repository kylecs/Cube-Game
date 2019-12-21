package engine;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import game.Player;
import input.KeyHandler;
import input.MouseHandler;
import sound.SoundManager;
import ui.Hud;
import world.Block;
import world.Chunk;
import world.World;
import world.WorldGenerator;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.FloatBuffer;
import java.util.Map.Entry;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;


public class Game {
	
	private long window;
	private KeyHandler keyHandler;
	private MouseHandler mouseHandler;
	private Shader chunkShader;
	private TextureAtlas textureAtlas;
	private World world;
	private Player player;
	private Hud hud;
	private SoundManager soundManager;
	private WorldGenerator worldGenerator;
	private ChunkMeshRenderer chunkMeshRenderer;
	
	private int windowWidth = 900;
	private int windowHeight = 900;
	
	private static Game instance = null;
	
	private Vector3f fogColor;
	
	private Game() {}
	
	public static Game get() {
		if(instance == null) {
			instance = new Game();
			instance.init();
		}
		
		return instance;
	}
	
	private void init() {
		org.lwjgl.system.Configuration.DEBUG.set(true);
		glfwInit();
		
		glfwSetErrorCallback((error, description) -> {
			System.err.println("GLFW error [" + Integer.toHexString(error) + "]: " + GLFWErrorCallback.getDescription(description));
		});
		
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        
        glfwWindowHint(GLFW_SAMPLES, 4);
		
		window = glfwCreateWindow(windowWidth, windowHeight, "Cube Game Tech Testing", NULL, NULL);
		glfwMakeContextCurrent(window);
		glfwSwapInterval(0);
		
		player = new Player();
		
		glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() {

			@Override
			public void invoke(long window, int width, int height) {
				glViewport(0, 0, width, height);
				player.getCamera().aspectRatio = (float) width / height;
				windowWidth = width;
				windowHeight = height;
			}
			
		});
		
		glfwShowWindow(window);
		
		GL.createCapabilities();
		
		glViewport(0, 0, windowWidth, windowHeight);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glEnable(GL_MULTISAMPLE);
		glEnable(GL_FOG);
		
		glfwMaximizeWindow(window);
		
		keyHandler = new KeyHandler();
		glfwSetKeyCallback(window, keyHandler);
		
		glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {

			@Override
			public void invoke(long window, int button, int action, int mods) {
				if(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
					if(!mouseHandler.isTracking()) {
						mouseHandler.enableTracking();
						glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
					}else {
						player.breakLookingAt();
					}
					
				}
			}
			
		});
		
		mouseHandler = new MouseHandler();
		glfwSetCursorPosCallback(window, mouseHandler);
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		
		chunkShader = new Shader("shaders/chunkmesh.vert", "shaders/chunkmesh.frag");
		
		if(!chunkShader.isUsable()) {
			return;
		}
		
		
		chunkShader.use();
		
		setFogColor(0, 191, 255);

		
		textureAtlas = new TextureAtlas("texture.png");
		
		world = new World();
		
		hud = new Hud(windowWidth, windowHeight);
		soundManager = new SoundManager();
		
		worldGenerator = new WorldGenerator();
		chunkMeshRenderer = new ChunkMeshRenderer();
	}
	
	private void setFogColor(float r, float g, float b) {
		fogColor = new Vector3f(r / 255, g / 255, b / 255);
		glUniform3f(2, fogColor.x, fogColor.y, fogColor.z);
	}
	
	private void tryPlacePlayer() {
		for(Entry<Vector2i, Chunk> entry: world.getChunkMap().entrySet()) {
			Chunk chunk = entry.getValue();
			if(chunk.isEmpty()) continue;
			//we will place the player here
			for(int x = 0; x < Chunk.CHUNK_WIDTH; x++) {
				for(int z = 0; z < Chunk.CHUNK_WIDTH; z++) {
					for(int y = Chunk.CHUNK_HEIGHT - 1; y >= 0; y--) {
						if(chunk.getBlock(x, y, z).getType() != Block.BlockType.AIR) {
							//place player
							player.setPlaced(true);
							player.position.set(chunk.getBaseX() + x + 0.5f, y + 3, chunk.getBaseZ() + z + 0.5f);
							System.out.println("placing player");
							return;
						}
					}
				}
			}
		}
	}
	
	
	public void loop() {
		
		long now = System.nanoTime();
		long prev = System.nanoTime();
		while(!glfwWindowShouldClose(window)) {
			glfwPollEvents();
			
			//measure delta time
			now = System.nanoTime();
			long diffNano = now - prev;
			double deltaTime = (double) diffNano / 1000000;
			prev = now;
			//System.out.println("Total Frame Time: " + deltaTime);
			
			glClearColor(fogColor.x, fogColor.y, fogColor.z, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
					
			
			if(keyHandler.escapePressed()) {
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
				mouseHandler.disableTracking();
			}
			player.update(keyHandler, mouseHandler, deltaTime);
			
			if(!player.isPlaced()) {
				tryPlacePlayer();
			}
			
			chunkShader.use();
			setUniformM4f(0, player.getCamera().projectionViewMatrix());
			
			chunkMeshRenderer.render();
			hud.render();
			
			glfwSwapBuffers(window);
			
		}
		
		System.out.println("Cleaning up!");
		worldGenerator.shutdown();
		chunkMeshRenderer.shutdown();
	}
	
	public SoundManager getSoundManager() {
		return soundManager;
	}
	
	public TextureAtlas getTextureAtlas() {
		return textureAtlas;
	}
	
	public World getWorld() {
		return world;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public ChunkMeshRenderer getChunkMeshRenderer() {
		return chunkMeshRenderer;
	}
	
	
	public static void setUniformM4f(int uniform, Matrix4f value) {
		try(MemoryStack stack = MemoryStack.stackPush()) {
			FloatBuffer fb = stack.mallocFloat(16);
			value.get(fb);
			glUniformMatrix4fv(uniform, false, fb);
		}
	}

	
	public static void main(String[] args) {
		Game.get().loop();		
	}
}
