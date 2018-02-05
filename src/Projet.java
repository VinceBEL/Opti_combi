import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Projet {

	private static final int NB_GRUES = 10;
	private static final int NB_OUVRIERS = 10;
	private static final int NB_NAVIRES = 4;
	private static final int NB_TRACE = 60;
	private static final int TAILLE_QUAI = 30;

	private int [][] Navires;
	private int [][] Grues;

	private IntVar[][] grues;
	private IntVar [] u;
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


		grues=model.intVarMatrix(NB_GRUES, 	NB_TRACE, -1, NB_NAVIRES-1);
		u= model.intVarArray(NB_GRUES, 0, NB_TRACE);
		//ouvriers=model.intVarMatrix(NB_OUVRIERS, NB_TRACE, -1, NB_OUVRIERS);
		//espace_quai=model.intVarMatrix(TAILLE_QUAI, NB_TRACE, -1,NB_NAVIRES);
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
			IntVar[]sum=model.intVarArray(NB_GRUES,0, NB_TRACE);
			for(int j=0;j<grues.length;j++) {
				IntVar aux= model.intVar(0, NB_TRACE);
				model.count(i, grues[j], aux).post();
				model.arithm(sum[j], "=", aux).post();
			}
			model.scalar(sum, ArrayUtils.getColumn(Grues, 1), ">=", Navires[i][2]).post();
		}
	}

	// Contrainte pour assurer que la taille soit respecte
	public void constrainte1() {
		for(int j=0;j<grues[0].length;j++) {
			IntVar[]aux= ArrayUtils.getColumn(grues, j);
			IntVar[]sum=model.intVarArray(NB_NAVIRES, 0, NB_GRUES);
			int cc=0;
			for(int i=0;i<NB_NAVIRES;i++) {
				IntVar c=model.intVar(0, NB_GRUES);
				model.count(Navires[i][0], aux, c).post();
				IntVar divisor=model.intVar(1, NB_GRUES);
				model.max(divisor, c, model.intVar(1)).post();
				model.div(c,divisor, c).post();
				sum[cc]=c;
				cc++;
			}
			model.scalar(sum, ArrayUtils.getColumn(Navires, 1), "<=", TAILLE_QUAI).post();
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

	public void utilisation() {
		for(int i=0;i<grues.length;i++) {
			IntVar[] c=model.intVarArray(NB_NAVIRES, 0, NB_TRACE);
			for(int j=0;j<NB_NAVIRES;j++) {
				IntVar aux=model.intVar(0, NB_TRACE);
				model.count(j, grues[i], aux).post();
				c[j]=aux;
			}
			model.sum(c, "=", u[i]).post();
		}
		IntVar min= model.intVar(0, NB_TRACE);
		model.min(min, u).post();
		IntVar max= model.intVar(0, NB_TRACE);
		model.max(max, u).post();
		IntVar dist= model.intVar(0, NB_TRACE);
		model.distance(min, max, "=", dist).post();
		model.setObjective(Model.MINIMIZE, dist);
	}

	public void constraints() {
		constraintb();
		constrainte1();
//		constrainte2();
		utilisation();
	}

//	public void fo() {
//		IntVar[] t= model.intVarArray(NB_GRUES, 0, NB_TRACE);
//		for(int i=0;i<grues.length;i++) {
//			IntVar aux=model.intVar(0, NB_TRACE);
//			model.count(-1, grues[i], aux).post();
//			t[i]=aux;
//		}
//		IntVar sumt=model.intVar(0,NB_GRUES*NB_TRACE*NB_NAVIRES);
//		model.sum(t, "=", sumt).post();
//		model.setObjective(Model.MINIMIZE, sumt);
//	}

	public void print() {
		for(int i=0;i<u.length;i++) {
			System.out.println("Utilisation de la grue  "+ i +"  "+u[i]);
		}
		System.out.println("------------------------");
		boolean imp=false;
		for(int i=0;i<grues.length;i++) {
			imp=false;
			for(int j=0;j<grues[0].length;j++) {
				if(grues[i][j].getValue()>-1) {
					System.out.print(grues[i][j]+"  ");
					imp=true;
				}
			}
			if(imp) {
				System.out.println();
			}
		}
	}

	public void go() {
		lire();
		constraints();
		//fo();
		//solver.showSolutions();
		solver.findSolution();
		solver.printStatistics();

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
