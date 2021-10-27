package com.lndf.glengine.tests.game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;

import com.lndf.glengine.scene.GameObject;

public class GameState {
	
	public static boolean running;
	public static int spawned = 0;
	public static int puntos = 0;
	public static int maxPuntos = 0;
	public static double speed;
	public static HashSet<GameObject> enemigos = new HashSet<GameObject>();
	public static boolean noBorrar = false;
	public static Record record = null;
	public static Player player;
	public static WindowMain window;
	
	public static void reset() {
		GameState.noBorrar = true;
		for (GameObject actor : GameState.enemigos) {
			actor.destroy();
		}
		if (GameState.record != null) {
			GameState.record.destroy();
			GameState.record = null;
		}
		if (GameState.puntos > GameState.maxPuntos) GameState.maxPuntos = GameState.puntos;
		GameState.puntos = 0;
		GameState.spawned = 0;
		GameState.speed = 1.0;
		GameState.enemigos.clear();
		GameState.noBorrar = false;
		GameState.updatePuntosScreen();
	}
	
	public static void cargarPuntos(String name) {
		try {
			FileInputStream fis = new FileInputStream(name);
			ObjectInputStream ois = new ObjectInputStream(fis);
			GameState.maxPuntos = ois.readInt();
			ois.close();
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void guardarPuntos(String name) {
		try {
			FileOutputStream fos = new FileOutputStream(name);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeInt(GameState.maxPuntos);
			oos.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updatePuntosScreen() {
		if (GameState.window != null) {
			window.lPuntos.setText(String.valueOf(GameState.puntos));
			window.lPuntosMax.setText(String.valueOf(GameState.maxPuntos));
			window.lSpeed.setText(String.valueOf(GameState.speed));
		}
	}
	
	public static void sumarPuntos() {
		GameState.puntos++;
		if (GameState.puntos > GameState.maxPuntos) GameState.maxPuntos = GameState.puntos;
		GameState.speed += 0.01;
		GameState.updatePuntosScreen();
	}
	
}
