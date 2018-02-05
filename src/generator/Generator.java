package generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;

import static java.lang.Math.floor;
import static java.lang.Math.random;

public class Generator {
    private static final char SEPARATOR = ',';
    private static final String OUTPUT_DIRECTORY = System.getProperty("user.dir").concat("/data/");

    private int idPb;
    private int nbNavires;
    private int nbGrues;


    public Generator(int idPb, int nbNavires, int nbGrues) {
        if (nbNavires < 3 || nbNavires > 12) {
            throw new InvalidParameterException("nbNavires doit être compris entre 3 et 12. Valeur donnée : " + nbNavires);
        } else if (nbGrues < 6 || nbGrues > 10) {
            throw new InvalidParameterException("nbGrues doit être compris entre 15 et 30. Valeur donnée : " + nbGrues);
        } else {
            this.idPb = idPb;
            this.nbNavires = nbNavires;
            this.nbGrues = nbGrues;
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

        for (int i = 0; i < nbNavires; i++) {
            StringBuilder sb = new StringBuilder("" + i)
                    .append(SEPARATOR)
                    .append((int) floor(random() * 20))
                    .append(SEPARATOR)
                    .append((int) floor(80 + random() * 2420))
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
                    .append((int) floor(15 + random() * 15))
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
        clearProblemData(2);
//        new Generator(2, 5, 8).generate();
    }
}
