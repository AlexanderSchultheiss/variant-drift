package de.hub.mse.variantdrift.clone.util;

import org.jgraph.JGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphModelAdapter;

import javax.swing.*;
import java.awt.*;

/** 
 * GraphViewer provides the static method viewGraph(..) that can be used to view a JGraphT object inside of a JApplet. 
 * 
 * @author Code: Ruud Welling. A Performance Analysis on Maximal Common Subgraph Algorithms. 2011 
 * 
 * @param <V> The class representing the vertices of the graphs
 * @param <E> The class representing the edges of the graphs
 */
public class GraphViewer<V,E> extends JApplet {

	private static final long serialVersionUID = 2474107909791138543L;
	private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
    private static final Dimension DEFAULT_SIZE = new Dimension(500, 400);

    
    private final JGraphModelAdapter<V,E> jgAdapter;

    private GraphViewer(Graph<V,E> g){
        // create a visualization using JGraph, via an adapter
        jgAdapter = new JGraphModelAdapter<>(g);
    }

    public static <V,E> void viewGraph(Graph<V,E> g, String title){
        GraphViewer<V,E> applet = new GraphViewer<>(g);
        applet.init();

        JFrame frame = new JFrame();
        frame.getContentPane().add(applet);
        frame.setTitle(title);
        frame.pack();
        frame.setVisible(true);
    }

    @Override
    public void init()
    {
        JGraph jgraph = new JGraph(jgAdapter);

        adjustDisplaySettings(jgraph);
        getContentPane().add(jgraph);
        resize(DEFAULT_SIZE);
    }

        private void adjustDisplaySettings(JGraph jg)
    {
        jg.setPreferredSize(DEFAULT_SIZE);
        jg.setBackground(DEFAULT_BG_COLOR);
        jg.setAutoResizeGraph(true);
    }

}
