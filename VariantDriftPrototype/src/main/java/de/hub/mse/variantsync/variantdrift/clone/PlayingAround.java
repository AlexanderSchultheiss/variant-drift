package de.hub.mse.variantsync.variantdrift.clone;

import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Feature;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlayingAround {

    public static void main(String... args) {
        File dir = new File("models/ppu");
        foo(dir);
    }

    private static void foo(File inFolder) {
        List<String[]> allClassRecords = new ArrayList<String[]>();

        File modelFiles[] = inFolder.listFiles();
        for (File file : modelFiles) {
            System.out.println(file.getName());
            Model model = ModelUtil.loadModel(file.getAbsolutePath());
            String modelId = file.getName();
            System.out.println(modelId);
        }

    }
}
