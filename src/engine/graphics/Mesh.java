package engine.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.vector.Matrix4f;

import animatedModel.Joint;



public class Mesh {
	private Vertex[] vertices;
	private int[] indices;
	private Material material;
	private int vao, pbo, ibo, cbo, tbo,jbo,wbo;

	private final Joint rootJoint;
	private final int jointCount;

	private final Animator animator;
	
	public Mesh(Vertex[] vertices, int[] indices, Material material,Joint rootJoint, int jointCount) {
		this.vertices = vertices;
		this.indices = indices;
		this.material = material;
		this.rootJoint = rootJoint;
		this.jointCount = jointCount;
		this.animator = new Animator(this);
		rootJoint.calcInverseBindTransform(new Matrix4f());
		AnimatedModelData entityData = ColladaLoader.loadColladaModel(modelFile, GeneralSettings.MAX_WEIGHTS);
		SkeletonData skeletonData = entityData.getJointsData();
		Joint headJoint = createJoints(skeletonData.headJoint);
	}
	
	public void create() {
		material.create();
		
		vao = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(vao);
		
		FloatBuffer positionBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] positionData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			positionData[i * 3] = vertices[i].getPosition().getX();
			positionData[i * 3 + 1] = vertices[i].getPosition().getY();
			positionData[i * 3 + 2] = vertices[i].getPosition().getZ();
		}
		positionBuffer.put(positionData).flip();
		
		pbo = storeData(positionBuffer, 0, 3);
		
		FloatBuffer textureBuffer = MemoryUtil.memAllocFloat(vertices.length * 2);
		float[] textureData = new float[vertices.length * 2];
		for (int i = 0; i < vertices.length; i++) {
			textureData[i * 2] = vertices[i].getTextureCoord().getX();
			textureData[i * 2 + 1] = vertices[i].getTextureCoord().getY();
		}
		textureBuffer.put(textureData).flip();
		
		tbo = storeData(textureBuffer, 2, 2);
		
		IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
		indicesBuffer.put(indices).flip();
		
		ibo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
		
		FloatBuffer jointBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] jointData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			jointData[i * 3] = vertices[i].getJoint().getX();
			jointData[i * 3 + 1] = vertices[i].getJoint().getY();
			jointData[i * 3 + 2] = vertices[i].getJoint().getZ();
		}
		jointBuffer.put(jointData).flip();
		
		jbo = storeData(jointBuffer, 0, 3);
		
		FloatBuffer weightBuffer = MemoryUtil.memAllocFloat(vertices.length * 3);
		float[] weightData = new float[vertices.length * 3];
		for (int i = 0; i < vertices.length; i++) {
			weightData[i * 3] = vertices[i].getWeight().getX();
			weightData[i * 3 + 1] = vertices[i].getWeight().getY();
			weightData[i * 3 + 2] = vertices[i].getWeight().getZ();
		}
		positionBuffer.put(weightData).flip();
		
		wbo = storeData(weightBuffer, 0, 3);
		
		
	}
	
	
	public int getJbo() {
		return jbo;
	}

	public void setJbo(int jbo) {
		this.jbo = jbo;
	}

	public int getWbo() {
		return wbo;
	}

	public void setWbo(int wbo) {
		this.wbo = wbo;
	}

	private int storeData(FloatBuffer buffer, int index, int size) {
		int bufferID = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferID);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(index, size, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return bufferID;
	}
	
	public void destroy() {
		GL15.glDeleteBuffers(pbo);
		GL15.glDeleteBuffers(cbo);
		GL15.glDeleteBuffers(ibo);
		GL15.glDeleteBuffers(tbo);
		
		GL30.glDeleteVertexArrays(vao);
		
		material.destroy();
	}

	public Vertex[] getVertices() {
		return vertices;
	}

	public int[] getIndices() {
		return indices;
	}

	public int getVAO() {
		return vao;
	}

	public int getPBO() {
		return pbo;
	}
	
	public int getCBO() {
		return cbo;
	}
	
	public int getTBO() {
		return tbo;
	}

	public int getIBO() {
		return ibo;
	}
	
	public Material getMaterial() {
		return material;
	}


	

}