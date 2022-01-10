package com.lndf.glengine.model;

import static org.lwjgl.assimp.Assimp.*;
import static org.lwjgl.opengl.GL12.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIMetaData;
import org.lwjgl.assimp.AIMetaDataEntry;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AITexel;
import org.lwjgl.assimp.AITexture;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;
import org.lwjgl.system.MemoryUtil;

import com.lndf.glengine.asset.Asset;
import com.lndf.glengine.engine.Utils;
import com.lndf.glengine.gl.Material;
import com.lndf.glengine.gl.Mesh;
import com.lndf.glengine.gl.texture.TextureImage2D;
import com.lndf.glengine.gl.texture.Texture2DRoles;
import com.lndf.glengine.scene.GameObject;
import com.lndf.glengine.scene.Transform;
import com.lndf.glengine.scene.components.MeshRenderer;

public class Model {
	
	private static HashMap<String, Vector3f> DEFAULT_COLORS = new HashMap<>();
	
	private HashMap<String, TextureImage2D> textures = new HashMap<String, TextureImage2D>();
	
	private ArrayList<ModelNode> nodes = new ArrayList<ModelNode>();
	private ModelNode rootNode;
	private Asset asset;
	
	static {
		DEFAULT_COLORS.put(Assimp.AI_MATKEY_BASE_COLOR, new Vector3f(0.6f));
		DEFAULT_COLORS.put(Assimp.AI_MATKEY_COLOR_AMBIENT, new Vector3f(1));
		DEFAULT_COLORS.put(Assimp.AI_MATKEY_COLOR_EMISSIVE, new Vector3f(0));
		DEFAULT_COLORS.put(Assimp.AI_MATKEY_ROUGHNESS_FACTOR, new Vector3f(0));
		DEFAULT_COLORS.put(Assimp.AI_MATKEY_METALLIC_FACTOR, new Vector3f(0));
	}
	
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
		this.rootNode = loadNode("", scene.mRootNode(), scene, true);
	}
	
	private ModelNode loadNode(String path, AINode node, AIScene scene, boolean isRootNode) {
		int numMeshes = node.mNumMeshes();
		int numChildren = node.mNumChildren();
		String nodeName = node.mName().dataString();
		String currentPath = path + nodeName;
		AIMatrix4x4 transform = node.mTransformation();
		Vector3f position = new Vector3f();
		Vector3f scale = new Vector3f();
		Quaternionf rotation = new Quaternionf();
		Utils.decomposeAssimpMatrix4x4(transform, position, scale, rotation);
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
			nodeChildren[i] = loadNode(currentPath + "/", child, scene, false);
		}
		ModelNode modelNode = new ModelNode(nodeName, currentPath, nodeChildren, meshContainers, position, scale, rotation);
		this.nodes.add(modelNode);
		return modelNode;
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
		AIVector3D.Buffer modelTangents = aiMesh.mTangents();
		Texture2DRoles textures = new Texture2DRoles();
		float[] positions = new float[numVertices * 3];
		float[] normals = new float[numVertices * 3];
		float[] texCoords = new float[numVertices * 2];
		float[] tangents = new float[numVertices * 3];
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
		if (modelTangents != null) {
			for (int i = 0; i < numVertices; i++) {
				AIVector3D tangent = modelTangents.get(i);
				tangents[(3 * i) + 0] = tangent.x();
				tangents[(3 * i) + 1] = tangent.y();
				tangents[(3 * i) + 2] = tangent.z();
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
		if (materialIndex >= 0) {
			AIMaterial material = AIMaterial.create(scene.mMaterials().get(materialIndex));
			//textures
			textures.setAlbedoTexture(this.loadTextures(scene, material, Assimp.aiTextureType_BASE_COLOR));
			textures.setAoTexture(this.loadTextures(scene, material, Assimp.aiTextureType_AMBIENT_OCCLUSION));
			textures.setNormalMap(this.loadTextures(scene, material, Assimp.aiTextureType_NORMAL_CAMERA));
			textures.setMetalnessTexture(this.loadTextures(scene, material, Assimp.aiTextureType_METALNESS));
			textures.setRoughnessTexture(this.loadTextures(scene, material, Assimp.aiTextureType_DIFFUSE_ROUGHNESS));
			textures.setEmissiveTexture(this.loadTextures(scene, material, Assimp.aiTextureType_EMISSION_COLOR));
			//colorss
			textures.setAlbedoColor(this.getMaterialColor(material, Assimp.AI_MATKEY_BASE_COLOR));
			textures.setAo(this.getMaterialColor(material, Assimp.AI_MATKEY_COLOR_AMBIENT).x);
			textures.setEmissiveColor(this.getMaterialColor(material, Assimp.AI_MATKEY_COLOR_EMISSIVE));
			textures.setRoughness(this.getMaterialColor(material, Assimp.AI_MATKEY_ROUGHNESS_FACTOR).x);
			textures.setMetalness(this.getMaterialColor(material, Assimp.AI_MATKEY_METALLIC_FACTOR).x);
		}
		mesh.setPositions(positions);
		mesh.setTexCoords(texCoords);
		mesh.setNormals(normals);
		mesh.setTangents(tangents);
		mesh.setIndices(indexBuffer);
		return this.createMeshContainer(meshName, mesh, textures);
	}
	
	private TextureImage2D loadTextures(AIScene scene, AIMaterial material, int type) {
		int textureCount = Assimp.aiGetMaterialTextureCount(material, type);
		if (textureCount <= 0) return null;
		TextureImage2D texture = null;
		AIString path = AIString.calloc();
		Assimp.aiGetMaterialTexture(material, type, 0, path, (IntBuffer) null, null, null, null, null, null);
		String texPath = path.dataString();
		if (this.textures.containsKey(texPath)) {
			texture = this.textures.get(texPath);
		} else {
			AITexture embedded = getEmbeddedTexture(scene, texPath);
			if (embedded != null) {
				int embTexWidth = embedded.mWidth();
				int embTexHeight = embedded.mHeight();
				if (embTexHeight == 0) {
					ByteBuffer textureRaw = embedded.pcDataCompressed();
					texture = new TextureImage2D(textureRaw);
					this.textures.put(texPath, texture);
				} else {
					ByteBuffer textureRaw = embedded.pcDataCompressed();
					texture = new TextureImage2D();
					texture.setUncompressedTexture(textureRaw, 0, embTexWidth, embTexHeight, GL_BGRA, GL_UNSIGNED_INT_8_8_8_8_REV);
					texture.setDefaultTextureSettings();
					this.textures.put(texPath, texture);
					
				}
			} else {
				Asset textureAsset = asset.getRelativeAsset(texPath);
				texture = new TextureImage2D(textureAsset);
				this.textures.put(texPath, texture);
			}
		}
		return texture;
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
	
	private Vector3f getMaterialColor(AIMaterial material, String type) {
		AIColor4D aiColor = AIColor4D.create();
		int res = Assimp.aiGetMaterialColor(material, type, Assimp.aiTextureType_NONE, 0, aiColor);
		if (res == 0) {
			return new Vector3f(aiColor.r(), aiColor.g(), aiColor.b());
		} else {
			return new Vector3f(Model.DEFAULT_COLORS.get(type));
		}
	}
	
	private MeshContainer createMeshContainer(String meshName, Mesh mesh, Texture2DRoles textures) {
		return new MeshContainer(meshName, mesh, textures);
	}
	
	public Collection<ModelNode> getNodes() {
		return Collections.unmodifiableCollection(this.nodes);
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
		for (MeshContainer container : node.getMeshContainers()) {
			String name = container.getName();
			Mesh mesh = container.getMesh();
			Material material = container.createMaterial();
			MeshRenderer renderer = new MeshRenderer(name, mesh, material);
			obj.addComponent(renderer);
		}
		return obj;
	}
	
}
