package lander.glengine.model;

public class ModelNode {
	
	private String name;
	private MeshContainer[] meshes;
	private ModelNode[] children;
	
	public ModelNode(String name, MeshContainer[] meshes, ModelNode[] children) {
		this.name = name;
		this.meshes = meshes;
		this.children = children;
	}
	
	public String getName() {
		return name;
	}
	
	public MeshContainer[] getMeshes() {
		return meshes;
	}

	public ModelNode[] getChildren() {
		return children;
	}
	
	public ModelGameObject createGameObject() {
		ModelGameObject obj = new ModelGameObject(this.name);
		for (ModelNode child : this.children) {
			obj.addChild(child.createGameObject());
		}
		if (this.meshes != null) {
			for (MeshContainer meshContainer : this.meshes) {
				obj.addComponent(meshContainer.createRenderComponent());
			}
		}
		return obj;
	}
	
}
