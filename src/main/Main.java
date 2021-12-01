package main;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.util.vector.Matrix4f;

import animatedModel.Joint;

import engine.animation.Animation;
import engine.animation.JointTransform;
import engine.animation.KeyFrame;
import engine.graphics.Mesh;
import engine.graphics.Renderer;
import engine.graphics.Shader;
import engine.io.Input;
import engine.io.ModelLoader;
import engine.io.Window;
import engine.maths.Vector3f;
import engine.objects.Camera;
import engine.objects.GameObject;


public class Main implements Runnable {
	public Thread game;
	public Window window;
	public Renderer renderer;
	public Shader shader;
	public final int WIDTH = 1280, HEIGHT = 760;
	private float animationTime = 0;
	public Mesh mesh = ModelLoader.loadModel("resources/models/Tree.obj", "/textures/dragon1.png");
	public Mesh mesh1 = ModelLoader.loadModel("resources/models/Drache.glb", "/textures/drag.jpg");
	public Mesh mesh2 = ModelLoader.loadModel("resources/models/mountains.obj", "/textures/dragon.png");
	public Mesh mesh3 = ModelLoader.loadModel("resources/models/drago.obj", "/textures/dragon2.png");
	public Mesh mesh4 = ModelLoader.loadModel("resources/models/panther.obj", "/textures/drag.jpg");
	public GameObject object = new GameObject(new Vector3f(10f, 0f, 10f), new Vector3f(0, 0, 0), new Vector3f(0.1f, 0.1f, 0.1f), mesh);
	public GameObject object1 = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0.1f, 0.1f, 0.1f), mesh1);
	public GameObject object2 = new GameObject(new Vector3f(45f, -10f, 45f), new Vector3f(0, 0, 0), new Vector3f(0.1f, 0.1f, 0.1f), mesh2);
	public GameObject object3 = new GameObject(new Vector3f(5000f, 5000f, 5000f), new Vector3f(0.8f, 0.1f, 0.1f), new Vector3f(0.001f, 0.001f, 0.001f), mesh3);
	public GameObject object4 = new GameObject(new Vector3f(200f, -40f, 40f), new Vector3f(0.5f, 0.5f, 0.5f), new Vector3f(0.01f, 0.01f, 0.01f), mesh4);
	public Camera camera = new Camera(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));
	private Animation currentAnimation;
	
	public void start() {
		game = new Thread(this, "game");
		game.start();
	}
	
	public void init() {
		window = new Window(WIDTH, HEIGHT, "Game");
		shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
		renderer = new Renderer(window, shader);
		window.setBackgroundColor(0.56f, 0.92f, 0.75f);
		window.create();
		mesh.create();
		mesh1.create();
		mesh2.create();
		mesh3.create();
		mesh4.create();
		shader.create();
	}
	
	public void run() {
		init();
		while (!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
			update();
			render();
			if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
			if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) window.mouseState(true);
		}
		close();
	}
	
	private void update() {
		window.update();
		camera.update();
		if (currentAnimation == null) {
			return;
		}
		increaseAnimationTime();
		Map<String, Matrix4f> currentPose = calculateCurrentAnimationPose();
		applyPoseToJoints(currentPose, mesh.getRootJoint(), new Matrix4f());
	}
	}
	
	private void render() {
		renderer.renderMesh(object, camera);
		renderer.renderMesh(object1, camera);
		renderer.renderMesh(object2, camera);
		renderer.renderMesh(object3, camera);
		renderer.renderMesh(object4, camera);
		window.swapBuffers();
	}
	
	private void close() {
		window.destroy();
		mesh.destroy();
		mesh1.destroy();
		mesh2.destroy();
		mesh3.destroy();
		mesh4.destroy();
		shader.destroy();
	}
	
	public static void main(String[] args) {
		new Main().start();
	}
	private Map<String, Matrix4f> calculateCurrentAnimationPose() {
		KeyFrame[] frames = getPreviousAndNextFrames();
		float progression = calculateProgression(frames[0], frames[1]);
		return interpolatePoses(frames[0], frames[1], progression);
	}
	private KeyFrame[] getPreviousAndNextFrames() {
		KeyFrame[] allFrames = currentAnimation.getKeyFrames();
		KeyFrame previousFrame = allFrames[0];
		KeyFrame nextFrame = allFrames[0];
		for (int i = 1; i < allFrames.length; i++) {
			nextFrame = allFrames[i];
			if (nextFrame.getTimeStamp() > animationTime) {
				break;
			}
			previousFrame = allFrames[i];
		}
		return new KeyFrame[] { previousFrame, nextFrame };
	}
	private Map<String, Matrix4f> interpolatePoses(KeyFrame previousFrame, KeyFrame nextFrame, float progression) {
		Map<String, Matrix4f> currentPose = new HashMap<String, Matrix4f>();
		for (String jointName : previousFrame.getJointKeyFrames().keySet()) {
			JointTransform previousTransform = previousFrame.getJointKeyFrames().get(jointName);
			JointTransform nextTransform = nextFrame.getJointKeyFrames().get(jointName);
			JointTransform currentTransform = JointTransform.interpolate(previousTransform, nextTransform, progression);
			currentPose.put(jointName, currentTransform.getLocalTransform());
		}
		return currentPose;
	}
	private void applyPoseToJoints(Map<String, Matrix4f> currentPose, Joint joint, Matrix4f parentTransform) {
		Matrix4f currentLocalTransform = currentPose.get(joint.name);
		Matrix4f currentTransform = Matrix4f.mul(parentTransform, currentLocalTransform, null);
		for (Joint childJoint : joint.children) {
			applyPoseToJoints(currentPose, childJoint, currentTransform);
		}
		Matrix4f.mul(currentTransform, joint.getInverseBindTransform(), currentTransform);
		joint.setAnimationTransform(currentTransform);
	}
	private void increaseAnimationTime() {
		animationTime += DisplayManager.getFrameTime();
		if (animationTime > currentAnimation.getLength()) {
			this.animationTime %= currentAnimation.getLength();
		}
	}
	
	
}