import static org.chocosolver.solver.search.strategy.Search.activityBasedSearch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

public class Projet {
	
	private static final int NB_GRUES = 10;
	private static final int NB_NAVIRES = 4;
	private static final int NB_TRACE = 15;
	private static final int TAILLE_QUAI = 40;
	
	private int [][] Navires;
	private int [][] Grues;
	IntVar[]sum;
	IntVar[]cont;
	private IntVar[][] grues;
	private IntVar[] taille;
	IntVar[] decht;

	private Model model;	
	
	private Solver solver;
	
	public Projet() {
		model = new Model();
		solver = model.getSolver();
		//Premiere colonne est l'id; deuxieme la taille ; troisieme capacite
		Navires= new int [NB_NAVIRES][3];
		//Premiere colonne est l'id; deuxieme la capacite 
		Grues= new int [NB_GRUES][2];
		
		grues=model.intVarMatrix(NB_GRUES, 	NB_TRACE, -1, NB_NAVIRES-1);
		taille=model.intVarArray(NB_TRACE,0, TAILLE_QUAI);
		decht=model.intVarArray(NB_NAVIRES, 0, NB_GRUES*NB_NAVIRES*NB_TRACE);
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
				Navires[j][c]=Integer.parseInt(lines[c])+2;
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
				int cap=Integer.parseInt(lines[c]);
				Grues[j][c]=cap;
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
	
	// Contrainte pour assurer le dechargement des navieres
	
	public void constraintb() {
		for(int i=0;i<NB_NAVIRES;i++) {
			IntVar[]aux=model.intVarArray(NB_GRUES, 0, NB_TRACE*NB_GRUES*NB_NAVIRES);
			for(int j=0;j<grues.length;j++) {
				model.count(i, grues[j], aux[j]).post();
			}
			model.scalar(aux, ArrayUtils.getColumn(Grues, 1), ">=", Navires[i][2]).post();
			model.scalar(aux, ArrayUtils.getColumn(Grues, 1), "=", decht[i]).post();
		}
	}
	
	// Contrainte pour assurer que la taille soit respecte
	public void constrainte1() {
		IntVar un=model.intVar(1);
		for(int j=0;j<grues[0].length;j++) {
			IntVar[]aux= ArrayUtils.getColumn(grues, j);
			sum=model.intVarArray(NB_NAVIRES, 0, NB_GRUES);
			cont=model.intVarArray(NB_NAVIRES, 0, 1);
			for(int i=0;i<NB_NAVIRES;i++) {
				model.count(i, aux,sum[i]).post();
				IntVar divisor=model.intVar(1, NB_GRUES);
				model.max(divisor, sum[i],un).post();
				model.div(sum[i],divisor, cont[i]).post();
			}
			model.scalar(cont, ArrayUtils.getColumn(Navires, 1), "<=", TAILLE_QUAI).post();
			model.scalar(cont, ArrayUtils.getColumn(Navires, 1), "=", taille[j]).post();
		}		
	}
	
	// Contrainte pour assurer que l'order des grues soit respecte
	public void constrainte2() {
		for(int i=0;i<grues[0].length;i++) {
			IntVar[] aux=ArrayUtils.getColumn(grues, i);
			for(int j=0;j<aux.length-1;j++) {
				model.arithm(aux[j], "<=", aux[j+1]).post();
			}
		}
	}
	
	public void fo() {
		IntVar[] cdmu=model.intVarArray(NB_NAVIRES, 0, NB_TRACE);
		for (int i = 0; i < Navires.length; i++) {
			model.arithm(decht[i], "-", model.intVar(Navires[i][2]), "=", cdmu[i]).post();
		}
		IntVar sum= model.intVar(0, 99999999);
		model.sum(cdmu, "=", sum).post();
		model.setObjective(false, sum);
	}
	
	public void constraints() {
		constraintb();
		constrainte1();
		constrainte2();
		fo();
	}
		
	public void print() {
		
		for(int i=0;i<decht.length;i++) {
			System.out.println("Le total decharge sur le naviere "+i+" est de : "+decht[i].getValue()+" sur un total de: "+Navires[i][2]);
		}
		System.out.println("-------------");
		for(int i=0;i<grues[0].length;i++) {
			System.out.println("Dans la trace "+i+ " le espace utilise est: "+ taille[i].getValue());
			for(int j=0;j<grues.length;j++) {
				if(grues[j][i].getValue()>-1) {
					System.out.println("La grue "+j+" atiende a le naviere "+ grues[j][i].getValue());
				}
			}
			System.out.println("-------------");
		}
	}
	
	public void go() {
		lire();
		constraints();
		int tot = NB_GRUES*NB_TRACE+NB_TRACE;
		IntVar[] vars = new IntVar[tot];
		int c = 0;
		for (int i = 0; i < grues.length; i++) {
			for (int j = 0; j < grues[0].length; j++) {
				vars[c]=grues[i][j];
				c++;
			}
		}
		for (int i = 0; i < taille.length; i++) {
			vars[c]=taille[i];
			c++;
		}
		solver.setSearch(activityBasedSearch(vars));
		//solver.showStatisticsDuringResolution(2000);
		solver.findSolution();
		//solver.printStatistics();
		print();
	}
	public static void main(String[] args) {
		try {
			Projet p= new Projet();
			p.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
