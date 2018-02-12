import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;

import static java.lang.Math.floor;
import static java.lang.Math.random;
import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

public class Generator {
    private static final char SEPARATOR = ',';
    private static final String OUTPUT_DIRECTORY = System.getProperty("user.dir").concat("/data/");

    private int idPb;
    private int nbNavires;
    private int nbGrues;
    private int tailleQuai;


    public Generator(int idPb, int nbNavires, int nbGrues, int tailleQuai) {
        if (nbNavires < 3 || nbNavires > 12) {
            throw new InvalidParameterException("nbNavires doit être compris entre 3 et 12. Valeur donnée : " + nbNavires);
        } else if (nbGrues < 6 || nbGrues > 10) {
            throw new InvalidParameterException("nbGrues doit être compris entre 15 et 30. Valeur donnée : " + nbGrues);
        } else {
            this.idPb = idPb;
            this.nbNavires = nbNavires;
            this.nbGrues = nbGrues;
            this.tailleQuai = tailleQuai;
        }
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

        int[] tailles = generateTaillesNavires();

        for (int i = 0; i < nbNavires; i++) {
            StringBuilder sb = new StringBuilder("" + i)
                    .append(SEPARATOR)
                    .append(tailles[i])
                    .append(SEPARATOR)
                    .append((int) floor(80 + random() * 2420))
                    .append('\n');

            writer.write(sb.toString());
        }

        writer.close();
    }

    private int[] generateTaillesNavires() {
        int tailleMax = (int) floor(3.0 * tailleQuai / 4.0);

        int[] tailles = range(0, nbNavires)
                .map(i -> (int) (2 + floor(random() * (tailleMax - 2))))
                .toArray();

        int sommeTailles = stream(tailles)
                .reduce((acc, val) -> acc + val)
                .orElse(-1);

        int randIndex;

        while(sommeTailles > tailleQuai) {
            randIndex = (int) floor(random() * nbNavires);

            while (tailles[randIndex] <= 2) {
                randIndex = (int) floor(random() * nbNavires);
            }

            tailles[randIndex] --;
            sommeTailles = stream(tailles)
                    .reduce((acc, val) -> acc + val)
                    .getAsInt();
        }

        return tailles;
    }

    private void generateGrues() throws IOException {
        FileWriter writer = new FileWriter(OUTPUT_DIRECTORY.concat("grues" + idPb).concat(".csv"));
        writer.write("id,capacite\n");

        for (int i = 0; i < nbGrues; i++) {
            StringBuilder sb = new StringBuilder("" + i)
                    .append(SEPARATOR)
                    .append((int) floor((15 + random() * 15) / 4))
                    .append('\n');

            writer.write(sb.toString());
        }

        writer.close();
    }

    public static void clearProblemData(int idPb) {
        String[] names = new String[]{"navires", "grues"};

        for (String name : names) {
            String fileName = OUTPUT_DIRECTORY.concat(name + idPb).concat(".csv");
            File file = new File(fileName);

            if (file.delete()) {
                System.out.println(fileName.concat("was successfully deleted"));
            } else {
                System.out.println("Failed to delete the file".concat(fileName));
            }
        }
    }

    public static void main(String[] args) {
        final int ID_PB = 2;
        final int NB_NAVIRES = 10;
        final int NB_GRUES = 8;
        final int TAILLE_QUAI = 100;


        clearProblemData(2);
        new Generator(ID_PB, NB_NAVIRES, NB_GRUES, TAILLE_QUAI)
                .generate();
    }
}
