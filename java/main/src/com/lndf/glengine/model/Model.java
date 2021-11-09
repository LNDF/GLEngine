package com.lndf.glengine.model;

import static org.lwjgl.assimp.Assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.engine.Utils;
import com.lndf.glengine.gl.Material;
import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.gl.texture.Texture2D;
import com.lndf.glengine.gl.texture.Texture2DRoles;
import com.lndf.glengine.gl.texture.TextureRole;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Transform;
import com.lndf.glengine.scene.components.MeshRenderer;

public class Model {
	
	public static Vector4f DEFAULT_COLOR = new Vector4f(1, 1, 1, 1);
	
	private HashMap<String, Texture2D> textures = new HashMap<String, Texture2D>();
	
	private ModelNode rootNode;
	private Asset asset;
	private float unitScaleFactor = 1f;
	
	public Model(Asset asset, ModelImporterSettings settings) {
		this.asset = asset;
		this.loadModel(settings);
	}
	
	public Model(Asset asset) {
		this(asset, ModelImporterSettings.getDefaultSettings());
	}
	
	private void loadModel(ModelImporterSettings settings) {
		AIScene scene = ModelImporter.importScene(asset, settings);
		if (scene == null ||
				(scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) != 0 || 
				scene.mRootNode() == null) {
			throw new RuntimeException("Assimp Error: " + Assimp.aiGetErrorString());
		}
		if (scene.mMetaData() != null) {
			AIMetaDataEntry unitScaleFactor = this.getEntryFromMetaData(scene.mMetaData(), "UnitScaleFactor");
			if (unitScaleFactor != null) {
				double factor = unitScaleFactor.mData(8).getDouble();
				this.unitScaleFactor = (float) factor;
			}
		}
		for (int i = 0; i < scene.mNumTextures(); i++) {
			AITexture t = AITexture.create(scene.mTextures().get(i));
			System.out.println(t.mFilename());
		}
		this.rootNode = loadNode(scene.mRootNode(), scene, true);
	}
	
	private AIMetaDataEntry getEntryFromMetaData(AIMetaData metaData, String target) {
		int numProperties = metaData.mNumProperties();
		AIString.Buffer keys = metaData.mKeys();
		AIMetaDataEntry.Buffer values = metaData.mValues();
		for (int i = 0; i < numProperties; i++) {
			if (keys.get(i).dataString().equals(target)) {
				return values.get(i);
			}
		}
		return null;
	}
	
	private ModelNode loadNode(AINode node, AIScene scene, boolean isRootNode) {
		int numMeshes = node.mNumMeshes();
		int numChildren = node.mNumChildren();
		String nodeName = node.mName().dataString();
		AIMatrix4x4 transform = node.mTransformation();
		Vector3f position = new Vector3f();
		Vector3f scale = new Vector3f();
		Quaternionf rotation = new Quaternionf();
		Utils.decomposeAssimpMatrix4x4(transform, position, scale, rotation);
		if (!isRootNode) {
			position.mul(unitScaleFactor);
			scale.mul(unitScaleFactor);
		}
		MeshContainer[] meshContainers = new MeshContainer[numMeshes];
		ModelNode[] nodeChildren = new ModelNode[numChildren];
		IntBuffer meshes = node.mMeshes();
		PointerBuffer sceneMeshes = scene.mMeshes();
		PointerBuffer children = node.mChildren();
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(sceneMeshes.get(meshes.get(i)));
			meshContainers[i] = loadMesh(aiMesh, scene);
		}
		for (int i = 0; i < numChildren; i++) {
			AINode child = AINode.create(children.get(i));
			nodeChildren[i] = loadNode(child, scene, false);
		}
		return new ModelNode(nodeName, nodeChildren, meshContainers, position, scale, rotation);
	}
	
	private MeshContainer loadMesh(AIMesh aiMesh, AIScene scene) {
		Mesh mesh = new Mesh();
		String meshName = aiMesh.mName().dataString();
		int numVertices = aiMesh.mNumVertices();
		int numFaces = aiMesh.mNumFaces();
		int materialIndex = aiMesh.mMaterialIndex();
		AIVector3D.Buffer vertices = aiMesh.mVertices();
		AIVector3D.Buffer modelNormals = aiMesh.mNormals();
		AIFace.Buffer faces = aiMesh.mFaces();
		AIVector3D.Buffer modelTexCoords = aiMesh.mTextureCoords(0);
		Texture2DRoles textures = new Texture2DRoles();
		float[] positions = new float[numVertices * 3];
		float[] normals = new float[numVertices * 3];
		float[] texCoords = new float[numVertices * 2];
		int[] indexBuffer = new int[numFaces * 3];
		//Vertex buffer
		if (vertices != null) { //positions
			for (int i = 0; i < numVertices; i++) {
				AIVector3D vertex = vertices.get(i);
				positions[(3 * i) + 0] = vertex.x();
				positions[(3 * i) + 1] = vertex.y();
				positions[(3 * i) + 2] = vertex.z();
			}
		}
		if (modelTexCoords != null) { //texture coordinates
			for (int i = 0; i < numVertices; i++) {
				AIVector3D texCoord = modelTexCoords.get(i);
				texCoords[(2 * i) + 0] = texCoord.x();
				texCoords[(2 * i) + 1] = texCoord.y();
			}
		}
		if (modelNormals != null) { //normals
			for (int i = 0; i < numVertices; i++) {
				AIVector3D normal = modelNormals.get(i);
				normals[(3 * i) + 0] = normal.x();
				normals[(3 * i) + 1] = normal.y();
				normals[(3 * i) + 2] = normal.z();
			}
		}
		//Index buffer
		for (int i = 0, pos = 0; i < numFaces; i++) {
			AIFace face = faces.get(i);
			int numIndices = face.mNumIndices();
			IntBuffer indices = face.mIndices();
			for (int j = 0; j < numIndices; j++) {
				indexBuffer[pos++] = indices.get(j);
			}
		}
		//Material and textures
		if (materialIndex > 0) {
			AIMaterial material = AIMaterial.create(scene.mMaterials().get(materialIndex));
			//textures
			textures.put(TextureRole.AMBIENT, this.loadTextures(scene, material, Assimp.aiTextureType_AMBIENT));
			textures.put(TextureRole.DIFFUSE, this.loadTextures(scene, material, Assimp.aiTextureType_DIFFUSE));
			textures.put(TextureRole.LIGHTMAP, this.loadTextures(scene, material, Assimp.aiTextureType_LIGHTMAP));
			textures.put(TextureRole.NORMAL, this.loadTextures(scene, material, Assimp.aiTextureType_NORMALS));
			textures.put(TextureRole.SPECULAR, this.loadTextures(scene, material, Assimp.aiTextureType_SPECULAR));
			textures.put(TextureRole.SHININESS, this.loadTextures(scene, material, Assimp.aiTextureType_SHININESS));
			//colors
			textures.setDefaultColor(TextureRole.AMBIENT, this.getMaterialColor(material, Assimp.AI_MATKEY_COLOR_AMBIENT));
			textures.setDefaultColor(TextureRole.DIFFUSE, this.getMaterialColor(material, Assimp.AI_MATKEY_COLOR_DIFFUSE));
			textures.setDefaultColor(TextureRole.SPECULAR, this.getMaterialColor(material, Assimp.AI_MATKEY_COLOR_SPECULAR));
		}
		mesh.setPositions(positions);
		mesh.setTexCoords(texCoords);
		mesh.setNormals(normals);
		mesh.setIndices(indexBuffer);
		return this.createMeshContainer(meshName, mesh, textures);
	}
	
	private ArrayList<Texture2D> loadTextures(AIScene scene, AIMaterial material, int type) {
		int textureCount = Assimp.aiGetMaterialTextureCount(material, type);
		if (textureCount <= 0) return null;
		ArrayList<Texture2D> textures = new ArrayList<Texture2D>();
		AIString path = AIString.calloc();
		for (int i = 0; i < textureCount; i++) {
			Assimp.aiGetMaterialTexture(material, type, i, path, (IntBuffer) null, null, null, null, null, null);
			String texPath = path.dataString();
			if (this.textures.containsKey(texPath)) {
				textures.add(this.textures.get(texPath));
			} else {
				AITexture embedded = getEmbeddedTexture(scene, texPath);
				if (embedded != null) {
					//TODO: implement embeded texture loading
				} else {
					Asset textureAsset = asset.getRelativeAsset(texPath);
					Texture2D texture = new Texture2D(textureAsset);
					this.textures.put(texPath, texture);
					textures.add(texture);
				}
			}
		}
		return textures;
	}
	
	private String getShortFilename(String filename) {
		int lastSlash = filename.lastIndexOf('/');
		if (lastSlash == -1) {
			lastSlash = filename.lastIndexOf('\\');
		}
		if (lastSlash == -1) return null;
		return filename.substring(lastSlash + 1);
	}
	
	private AITexture getEmbeddedTexture(AIScene scene, String filename) {
		if (filename == null) return null;
		int mNumTextures = scene.mNumTextures();
		PointerBuffer textures = scene.mTextures();
		if (filename.startsWith("*")) {
			int index = Integer.parseInt(filename.substring(1));
			if (0 > index || mNumTextures <= index) return null;
			return AITexture.create(textures.get(index));
		}
		String shortFilename = getShortFilename(filename);
		if (shortFilename == null) return null;
		for (int i = 0; i < mNumTextures; i++) {
			AITexture texture = AITexture.create(textures.get(i));
			String shortTextureFilename = getShortFilename(texture.mFilename().dataString());
			if (shortFilename.equals(shortTextureFilename)) return texture;
		}
		return null;
	}
	
	private Vector4f getMaterialColor(AIMaterial material, String type) {
		AIColor4D aiColor = AIColor4D.create();
		int res = Assimp.aiGetMaterialColor(material, type, Assimp.aiTextureType_NONE, 0, aiColor);
		if (res == 0) {
			return new Vector4f(aiColor.r(), aiColor.g(), aiColor.b(), aiColor.a());
		} else {
			return new Vector4f(Model.DEFAULT_COLOR);
		}
	}
	
	private MeshContainer createMeshContainer(String meshName, Mesh mesh, Texture2DRoles textures) {
		return new MeshContainer(meshName, mesh, textures);
	}
	
	public ModelNode getRootNode() {
		return rootNode;
	}
	
	public GameObject createGameObject() {
		return this.createGameObject("");
	}
	
	public GameObject createGameObject(String suffix) {
		return this.createGameObject(suffix, rootNode);
	}
	
	private GameObject createGameObject(String suffix, ModelNode node) {
		GameObject obj = new GameObject(node.getName() + suffix);
		Transform transform = obj.getTransform();
		transform.setPosition(node.getPosition());
		transform.setScale(node.getScale());
		transform.setRotation(node.getRotation());
		for (ModelNode childNode : node.getChildren()) {
			obj.addChild(this.createGameObject(suffix, childNode));
		}
		MeshRenderer meshRenderer = new MeshRenderer();
		for (MeshContainer container : node.getMeshContainers()) {
			String name = container.getName();
			Mesh mesh = container.getMesh();
			Material material = container.createMaterial();
			meshRenderer.addMesh(name, mesh, material);
		}
		obj.addComponent(meshRenderer);
		return obj;
	}
	
}
