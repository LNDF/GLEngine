package lander.glengine.model;

import static org.lwjgl.assimp.Assimp.AI_SCENE_FLAGS_INCOMPLETE;
import static org.lwjgl.assimp.Assimp.aiProcess_GenNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Vector4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.AIColor4D;
import org.lwjgl.assimp.AIFace;
import org.lwjgl.assimp.AIMaterial;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.AIString;
import org.lwjgl.assimp.AIVector3D;
import org.lwjgl.assimp.Assimp;

import lander.glengine.asset.Asset;
import lander.glengine.gl.Mesh;
import lander.glengine.gl.texture.Texture2D;
import lander.glengine.gl.texture.Texture2DRoles;
import lander.glengine.gl.texture.TextureRole;
import lander.glengine.scene.GameObject;

public class Model {
	
	private MeshContainer[] meshes;
	private int meshesPosition = 0;
	
	public static Vector4f DEFAULT_COLOR = new Vector4f(1, 1, 1, 1);
	
	private HashMap<String, Texture2D> textures = new HashMap<String, Texture2D>();
	
	public Model(Asset asset) {
		this.loadModel(asset);
	}
	
	private void loadModel(Asset asset) {
		AIScene scene = ModelImporter.importScene(asset,
				aiProcess_Triangulate |
				aiProcess_GenNormals
		);
		if (scene == null ||
				(scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) != 0 || 
				scene.mRootNode() == null) {
			throw new RuntimeException("Assimp Error: " + Assimp.aiGetErrorString());
		}
		this.meshes = new MeshContainer[scene.mNumMeshes()];
		loadNode(asset, scene.mRootNode(), scene);
	}
	
	private void loadNode(Asset asset, AINode node, AIScene scene) {
		int numMeshes = node.mNumMeshes();
		int numChildren = node.mNumChildren();
		IntBuffer meshes = node.mMeshes();
		PointerBuffer sceneMeshes = scene.mMeshes();
		PointerBuffer children = node.mChildren();
		for (int i = 0; i < numMeshes; i++) {
			AIMesh aiMesh = AIMesh.create(sceneMeshes.get(meshes.get(i)));
			this.meshes[this.meshesPosition++] = loadMesh(asset, aiMesh, scene);
		}
		for (int i = 0; i < numChildren; i++) {
			AINode child = AINode.create(children.get(i));
			loadNode(asset, child, scene);
		}
	}
	
	private MeshContainer loadMesh(Asset asset, AIMesh aiMesh, AIScene scene) {
		Mesh mesh = new Mesh();
		int numVertices = aiMesh.mNumVertices();
		int numFaces = aiMesh.mNumFaces();
		int materialIndex = aiMesh.mMaterialIndex();
		int vertexElements = mesh.getVertexElementCount();
		AIVector3D.Buffer vertices = aiMesh.mVertices();
		AIVector3D.Buffer normals = aiMesh.mNormals();
		AIFace.Buffer faces = aiMesh.mFaces();
		float[] vertexBuffer = new float[vertexElements * numVertices];
		int[] indexBuffer = new int[numFaces * 3];
		AIVector3D.Buffer texCoords = aiMesh.mTextureCoords(0);
		Texture2DRoles textures = new Texture2DRoles();
		//Vertex buffer
		if (vertices != null) { //positions
			for (int i = 0; i < numVertices; i++) {
				AIVector3D vertex = vertices.get(i);
				vertexBuffer[(vertexElements * i) + 0] = vertex.x();
				vertexBuffer[(vertexElements * i) + 1] = vertex.y();
				vertexBuffer[(vertexElements * i) + 2] = vertex.z();
			}
		}
		if (texCoords != null) { //texture coordinates
			for (int i = 0; i < numVertices; i++) {
				AIVector3D texCoord = texCoords.get(i);
				vertexBuffer[(vertexElements * i) + 3] = texCoord.x();
				vertexBuffer[(vertexElements * i) + 4] = texCoord.y();
			}
		}
		if (normals != null) { //normals
			for (int i = 0; i < numVertices; i++) {
				AIVector3D normal = normals.get(i);
				vertexBuffer[(vertexElements * i) + 5] = normal.x();
				vertexBuffer[(vertexElements * i) + 6] = normal.y();
				vertexBuffer[(vertexElements * i) + 7] = normal.z();
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
			textures.put(TextureRole.AMBIENT, this.loadTextures(asset, material, Assimp.aiTextureType_AMBIENT));
			textures.put(TextureRole.DIFFUSE, this.loadTextures(asset, material, Assimp.aiTextureType_DIFFUSE));
			textures.put(TextureRole.LIGHTMAP, this.loadTextures(asset, material, Assimp.aiTextureType_LIGHTMAP));
			textures.put(TextureRole.NORMAL, this.loadTextures(asset, material, Assimp.aiTextureType_NORMALS));
			textures.put(TextureRole.SPECULAR, this.loadTextures(asset, material, Assimp.aiTextureType_SPECULAR));
			textures.put(TextureRole.SHININESS, this.loadTextures(asset, material, Assimp.aiTextureType_SHININESS));
			//colors
			textures.setDefaultColor(TextureRole.AMBIENT, this.getMaterialColor(material, Assimp.AI_MATKEY_COLOR_AMBIENT));
			textures.setDefaultColor(TextureRole.DIFFUSE, this.getMaterialColor(material, Assimp.AI_MATKEY_COLOR_DIFFUSE));
			textures.setDefaultColor(TextureRole.SPECULAR, this.getMaterialColor(material, Assimp.AI_MATKEY_COLOR_SPECULAR));
		}
		mesh.setVertices(vertexBuffer);
		mesh.setIndices(indexBuffer);
		return this.createMeshContainer(mesh, textures);
	}
	
	private ArrayList<Texture2D> loadTextures(Asset asset, AIMaterial material, int type) {
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
				Asset textureAsset = asset.getRelativeAsset(texPath);
				Texture2D texture = new Texture2D(textureAsset);
				this.textures.put(texPath, texture);
				textures.add(texture);
			}
		}
		return textures;
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
	
	private MeshContainer createMeshContainer(Mesh mesh, Texture2DRoles textures) {
		return new MeshContainer(mesh, textures);
	}
	
	public Mesh getCombinedMesh() {
		Mesh[] meshes = new Mesh[this.meshes.length];
		int pos = 0;
		for (MeshContainer container : this.meshes) {
			meshes[pos++] = container.getMesh();
		}
		return Mesh.combine(meshes);
	}
	
	public GameObject createGameObject() {
		GameObject obj = new GameObject();
		obj.addComponent(new ModelRenderComponent(this.meshes));
		return obj;
	}
	
}
