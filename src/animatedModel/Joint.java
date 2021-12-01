package animatedModel;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;

public class Joint {

	public final int index;// ID
	public final String name;
	public final List<Joint> children = new ArrayList<Joint>();

	private Matrix4f animatedTransform = new Matrix4f();
	
	private final Matrix4f localBindTransform;
	private Matrix4f inverseBindTransform = new Matrix4f();

	
	public Joint(int index, String name, Matrix4f bindLocalTransform) {
		this.index = index;
		this.name = name;
		this.localBindTransform = bindLocalTransform;
	}

	
	public void addChild(Joint child) {
		this.children.add(child);
	}

	
	public Matrix4f getAnimatedTransform() {
		return animatedTransform;
	}

	public void setAnimationTransform(Matrix4f animationTransform) {
		this.animatedTransform = animationTransform;
	}

	
	public Matrix4f getInverseBindTransform() {
		return inverseBindTransform;
	}

	protected void calcInverseBindTransform(Matrix4f parentBindTransform) {
		Matrix4f bindTransform = Matrix4f.mul(parentBindTransform, localBindTransform, null);
		Matrix4f.invert(bindTransform, inverseBindTransform);
		for (Joint child : children) {
			child.calcInverseBindTransform(bindTransform);
		}
	}

}
