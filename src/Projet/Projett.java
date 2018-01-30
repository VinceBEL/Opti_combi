package Projet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Projet {
	
	private static final int NB_GRUES = 10;
	private static final int NB_OUVRIERS = 10;
	private static final int NB_NAVIRES = 4;
	private static final int NB_TRACE = 50;
	private static final int TAILLE_QUAI = 30;
	
	private int [][] Navires;
	private int [][] Grues;
	
	private IntVar[][] grues;
	private IntVar[][] ouvriers;
	private IntVar[][] espace_quai;

	private Model model;	
	private Solver solver;
	
	public Projet() {
		// TODO Auto-generated constructor stub
		model = new Model();
		solver = model.getSolver();
		
		//Premiere colonne est l'id; deuxieme la taille ; troisieme capacite
		Navires= new int [NB_NAVIRES][3];
		//Premiere colonne est l'id; deuxieme la capacite 
		Grues= new int [NB_GRUES][2];
		
		
		grues=model.intVarMatrix(NB_GRUES, 	NB_TRACE, -1, NB_NAVIRES);
		ouvriers=model.intVarMatrix(NB_OUVRIERS, NB_TRACE, -1, NB_OUVRIERS);
	}
	
	public void lireNavires() {
		File file = new File("./data/navires.csv");
		BufferedReader buf;
		try {
			buf = new BufferedReader(new FileReader(file));
			String line = buf.readLine();
			line = buf.readLine();
			int j=0;
			while(line!=null) {
				int c=0;
				String[] lines=line.split(",");
				Navires[j][c]=Integer.parseInt(lines[c]);
				c++;
				Navires[j][c]=Integer.parseInt(lines[c]);
				c++;
				Navires[j][c]=Integer.parseInt(lines[c]);
				j++;
				line = buf.readLine();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void lireGrues() {
		File file = new File("./data/grues.csv");
		BufferedReader buf;
		try {
			buf = new BufferedReader(new FileReader(file));
			String line = buf.readLine();
			line = buf.readLine();
			int j=0;
			while(line!=null) {
				int c=0;
				String[] lines=line.split(",");
				Grues[j][c]=Integer.parseInt(lines[c]);
				c++;
				Grues[j][c]=Integer.parseInt(lines[c]);
				j++;
				line = buf.readLine();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void lire() {
		lireGrues();
		lireNavires();
	}
	
	public void constrainte1() {
		for(int i=0;i<Navires.length;i++) {
				Navires[i][1]=Navires[i][1]+2;
		}
		
	}
	public static void main(String[] args) {
		Projet p=new Projet();
		p.lire();
	}

}
