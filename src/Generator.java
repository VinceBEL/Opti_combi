import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;

import static java.lang.Math.floor;
import static java.lang.Math.random;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

public class Generator {

    public static void main(String[] args) {
        final int ID_PB = 2;
        final int NB_NAVIRES = 10;
        final int NB_GRUES = 8;
        final int TAILLE_QUAI = 50;


        clearProblemFiles(2);

//
//        new Generator(ID_PB, TAILLE_QUAI)
//                .generate();

        new Generator(ID_PB, NB_NAVIRES, NB_GRUES, TAILLE_QUAI)
                .generate();
    }




    private static final char SEPARATOR = ',';
    private static final String OUTPUT_DIRECTORY = System.getProperty("user.dir").concat("/data/");

    private int idPb;
    private int nbNavires;
    private int nbGrues;
    private int tailleQuai;
    private int[] tailles;


    public Generator(int idPb, int nbNavires, int nbGrues, int tailleQuai) {
        if (nbNavires < 3 || nbNavires > 12) {
            throw new InvalidParameterException("nbNavires doit être compris entre 3 et 12. Valeur donnée : " + nbNavires);
        } else if (nbGrues < 6 || nbGrues > 10) {
            throw new InvalidParameterException("nbGrues doit être compris entre 6 et 10. Valeur donnée : " + nbGrues);
        } else {
            this.idPb = idPb;
            this.nbNavires = nbNavires;
            this.nbGrues = nbGrues;
            this.tailleQuai = tailleQuai;
            this.tailles = generateTaillesNavires();
        }
    }

    public Generator(int idPb, int tailleQuai) {
        this(idPb, randomValue(3, 12), randomValue(6, 10), tailleQuai);
    }


    private int[] generateTaillesNavires() {
        int tailleMax = (int) floor(2.0 * tailleQuai / 3.0);

        int[] res = range(0, nbNavires)
                .map(i -> randomValue(2, tailleMax))
                .toArray();

        int sommeTailles = stream(res)
                .reduce((acc, val) -> acc + val)
                .orElse(-1);

        int randIndex;

        while(5 * sommeTailles / nbNavires > tailleQuai) {
            randIndex = randomValue(nbNavires);

            while (res[randIndex] <= 2) {
                randIndex = randomValue(nbNavires);
            }

            res[randIndex] --;
            sommeTailles = stream(res)
                    .reduce((acc, val) -> acc + val)
                    .getAsInt();
        }

        return res;
    }

    public int getNbNavires() {
        return nbNavires;
    }

    public int getNbGrues() {
        return nbGrues;
    }
    
    public int getId() {
    	return this.idPb;
    }

    public void generate() {
        try {
            generateNavires();
            generateGrues();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateNavires() throws IOException {
        FileWriter writer = new FileWriter(OUTPUT_DIRECTORY.concat("navires" + idPb).concat(".csv"));
        writer.write("id,taille,capacite\n");

        for (int i = 0; i < nbNavires; i++) {
            StringBuilder sb = new StringBuilder("" + i)
                    .append(SEPARATOR)
                    .append(tailles[i])
                    .append(SEPARATOR)
                    .append(randomValue(80, 2500))
                    .append('\n');

            writer.write(sb.toString());
        }

        writer.close();
    }

    private void generateGrues() throws IOException {
        FileWriter writer = new FileWriter(OUTPUT_DIRECTORY.concat("grues" + idPb).concat(".csv"));
        writer.write("id,capacite\n");

        for (int i = 0; i < nbGrues; i++) {
            StringBuilder sb = new StringBuilder("" + i)
                    .append(SEPARATOR)
                    .append((int) floor(randomValue(15, 30) / 4))
                    .append('\n');

            writer.write(sb.toString());
        }

        writer.close();
    }



    private static int randomValue(int minVal, int maxVal) {
        return minVal + randomValue(maxVal - minVal);
    }

    private static int randomValue(int maxVal) {
        return (int) floor(random() * maxVal);
    }

    private static void clearProblemFiles(int idPb) {
        String[] names = new String[]{"navires", "grues"};

        for (String name : names) {
            String fileName = OUTPUT_DIRECTORY.concat(name + idPb).concat(".csv");
            File file = new File(fileName);

            if (file.delete()) {
                System.out.println(fileName.concat(" was successfully deleted"));
            } else {
                System.out.println("Failed to delete the file ".concat(fileName));
            }
        }
    }
}
