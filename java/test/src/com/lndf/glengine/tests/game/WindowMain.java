package com.lndf.glengine.tests.game;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.lndf.glengine.tests.lights.LightsMain;

public class WindowMain extends JFrame{
	
	private final int WIDTH = 300;
	private final int HEIGHT = 150;
	
	public JLabel lPuntos;
	public JLabel lPuntosMax;
	public JLabel lSpeed;

	private static final long serialVersionUID = -3411124968926631148L;
	private JPanel centro = new JPanel();
	
	public WindowMain() {
		JButton iniciarJuego = new JButton("Iniciar Juego");
		JButton iniciarLights = new JButton("Iniciar test de luces");
		JPanel south = new JPanel();
		
		this.setSize(new Dimension(WIDTH, HEIGHT));
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		centro.setLayout(new GridLayout(3, 1));
		lPuntos = this.newStat("Puntos");
		lPuntosMax = this.newStat("Record de puntos");
		lSpeed = this.newStat("Velocidad");
		iniciarJuego.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread() {
					@Override
					public void run() {
						GameState.window = WindowMain.this;
						TestMain.run();
					}
				}.start();
			}
		});
		iniciarLights.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LightsMain.main(new String[] {});
			}
		});
		south.setLayout(new BorderLayout());
		south.add(iniciarJuego, BorderLayout.EAST);
		south.add(iniciarLights, BorderLayout.WEST);
		this.getContentPane().add(centro);
		this.getContentPane().add(south, BorderLayout.SOUTH);
		
	}
	
	public JLabel newStat(String name) {
		JPanel p = new JPanel();
		JLabel l = new JLabel();
		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		p.add(new JLabel(name + ": "));
		p.add(l);
		centro.add(p);
		return l;
	}
	
	public static void main(String[] args) {
		WindowMain wm = new WindowMain();
		wm.setVisible(true);
	}
	
}
