package com.lndf.glengine.physics.collider;

import java.util.LinkedList;

import com.lndf.glengine.gl.Mesh;

import vhacd.VHACD;
import vhacd.VHACDHull;
import vhacd.VHACDParameters;
import vhacd.VHACDResults;

public class MeshCollider extends PolyhedronCollider {
	
	private LinkedList<Mesh> meshes = new LinkedList<Mesh>();;
	
	public MeshCollider(Mesh[] meshes) {
		VHACDParameters params = new VHACDParameters();
		//params.setDebugEnabled(true);
		for (Mesh mesh : meshes) {
			VHACDResults res = VHACD.compute(mesh.getPositions(), mesh.getIndices(), params);
			for (int i = 0; i < res.size(); i++) {
				VHACDHull hull = res.get(i);
				this.meshes.add(new Mesh(hull.positions, null, null, hull.indexes));
			}
		}
	}
	
	@Override
	public Mesh[] getMesh() {
		return (Mesh[]) meshes.toArray();
	}
	
	@Override
	public boolean getCollision(Collider other) {
		return false;
	}
	
}
