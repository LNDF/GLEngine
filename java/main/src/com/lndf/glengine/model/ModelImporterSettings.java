package com.lndf.glengine.model;

import static org.lwjgl.assimp.Assimp.*;

import org.lwjgl.assimp.*;

public class ModelImporterSettings {
	
	private static ModelImporterSettings defaultSettings = new ModelImporterSettings();

	private boolean calcTangentSpace = true;
	private float tangentSpaceSmoothAngle = 175;
	
	private boolean joinIdenticalVertices = true;
	
	private boolean forceGenerateNormals = true;
	
	private boolean generateSmoothNormals = true;
	private float normalSmoothAngle = 80;
	
	private boolean fixInfacingNormals = false;
	
	private boolean fixInvalidData = true;
	
	private boolean optimizeMeshes = false;
	private boolean optimizeNodes = false;
	
	private boolean flipFaces = false;
	
	public static ModelImporterSettings getDefaultSettings() {
		return defaultSettings;
	}

	public static void setDefaultSettings(ModelImporterSettings defaultSettings) {
		ModelImporterSettings.defaultSettings = defaultSettings;
	}
	
	public boolean isCalcTangentSpace() {
		return calcTangentSpace;
	}

	public void setCalcTangentSpace(boolean calcTangentSpace) {
		this.calcTangentSpace = calcTangentSpace;
	}

	public float getTangentSpaceSmoothAngle() {
		return tangentSpaceSmoothAngle;
	}

	public void setTangentSpaceSmoothAngle(float tangentSpaceSmoothAngle) {
		this.tangentSpaceSmoothAngle = tangentSpaceSmoothAngle;
	}

	public boolean isJoinIdenticalVertices() {
		return joinIdenticalVertices;
	}

	public void setJoinIdenticalVertices(boolean joinIdenticalVertices) {
		this.joinIdenticalVertices = joinIdenticalVertices;
	}

	public boolean isForceGenerateNormals() {
		return forceGenerateNormals;
	}

	public void setForceGenerateNormals(boolean forceGenerateNormals) {
		this.forceGenerateNormals = forceGenerateNormals;
	}

	public boolean isGenerateSmoothNormals() {
		return generateSmoothNormals;
	}

	public void setGenerateSmoothNormals(boolean generateSmoothNormals) {
		this.generateSmoothNormals = generateSmoothNormals;
	}

	public float getNormalSmoothAngle() {
		return normalSmoothAngle;
	}

	public void setNormalSmoothAngle(float normalSmoothAngle) {
		this.normalSmoothAngle = normalSmoothAngle;
	}

	public boolean isFixInfacingNormals() {
		return fixInfacingNormals;
	}

	public void setFixInfacingNormals(boolean fixInfacingNormals) {
		this.fixInfacingNormals = fixInfacingNormals;
	}

	public boolean isFixInvalidData() {
		return fixInvalidData;
	}

	public void setFixInvalidData(boolean fixInvalidData) {
		this.fixInvalidData = fixInvalidData;
	}

	public boolean isOptimizeMeshes() {
		return optimizeMeshes;
	}

	public void setOptimizeMeshes(boolean optimizeMeshes) {
		this.optimizeMeshes = optimizeMeshes;
	}

	public boolean isOptimizeNodes() {
		return optimizeNodes;
	}

	public void setOptimizeNodes(boolean optimizeNodes) {
		this.optimizeNodes = optimizeNodes;
	}

	public boolean isFlipFaces() {
		return flipFaces;
	}

	public void setFlipFaces(boolean flipFaces) {
		this.flipFaces = flipFaces;
	}
	
	public int getFlags() {
		int flags = aiProcess_GenUVCoords |
					aiProcess_TransformUVCoords | 
					aiProcess_Triangulate |
					aiProcess_SortByPType |
					aiProcess_ImproveCacheLocality;
		if (calcTangentSpace) flags |= aiProcess_CalcTangentSpace;
		if (joinIdenticalVertices) flags |= aiProcess_JoinIdenticalVertices;
		if (forceGenerateNormals) flags |= aiProcess_ForceGenNormals;
		if (generateSmoothNormals) {
			flags |= aiProcess_GenSmoothNormals;
		} else {
			flags |= aiProcess_GenNormals;
		}
		if (fixInfacingNormals) flags |= aiProcess_FixInfacingNormals;
		if (fixInvalidData) flags |= aiProcess_FindInvalidData;
		if (optimizeMeshes) flags |= aiProcess_OptimizeMeshes;
		if (optimizeNodes) flags |= aiProcess_OptimizeGraph;
		if (flipFaces) flags |= aiProcess_FlipWindingOrder;
		return flags;
	}
	
	public void configurePropertyStore(AIPropertyStore store) {
		if (calcTangentSpace) Assimp.aiSetImportPropertyFloat(store, AI_CONFIG_PP_CT_MAX_SMOOTHING_ANGLE, tangentSpaceSmoothAngle);
		if (generateSmoothNormals) Assimp.aiSetImportPropertyFloat(store, AI_CONFIG_PP_GSN_MAX_SMOOTHING_ANGLE, normalSmoothAngle);
		Assimp.aiSetImportPropertyInteger(store, AI_CONFIG_PP_SBP_REMOVE, aiPrimitiveType_LINE | aiPrimitiveType_POINT);
	}
	
	public ModelImporterSettings(ModelImporterSettings other) {
		if (other != null) {
			this.calcTangentSpace = other.calcTangentSpace;
			this.tangentSpaceSmoothAngle = other.tangentSpaceSmoothAngle;
			this.joinIdenticalVertices = other.joinIdenticalVertices;
			this.forceGenerateNormals = other.forceGenerateNormals;
			this.generateSmoothNormals = other.generateSmoothNormals;
			this.normalSmoothAngle = other.normalSmoothAngle;
			this.fixInfacingNormals = other.fixInfacingNormals;
			this.fixInvalidData = other.fixInvalidData;
			this.optimizeMeshes = other.optimizeMeshes;
			this.optimizeNodes = other.optimizeNodes;
			this.flipFaces = other.flipFaces;
		}
	}
	
	public ModelImporterSettings() {
		this(ModelImporterSettings.defaultSettings);
	}
}
