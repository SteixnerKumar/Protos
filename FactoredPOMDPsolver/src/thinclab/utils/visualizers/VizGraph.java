/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.utils.visualizers;

import java.util.HashMap;
import java.util.List;

import cern.colt.Arrays;
import edu.uci.ics.jung.graph.DirectedOrderedSparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/*
 * @author adityas
 *
 */
public class VizGraph {
	/*
	 * A generic graph wrapper for visualizing directed graphs
	 */
	
	public DirectedOrderedSparseMultigraph<VizNode, VizEdge> graph = 
			new DirectedOrderedSparseMultigraph<VizNode, VizEdge>();
	
	// --------------------------------------------------------------------------------------------
	
	public void addDirectedEdge(VizNode from, VizEdge edge, VizNode to) {
		/*
		 * Just calls the graphs addEdge
		 */
		this.graph.addEdge(edge, from, to, EdgeType.DIRECTED);
	}
	
	// --------------------------------------------------------------------------------------------
	
	public static VizGraph getVizGraphFromLATreeTriples(List<String[]> triples) {
		/*
		 * Constructs a VizGraph from LookAheadTree triples
		 */
		
		/* maintain a temporary index for nodes */
		HashMap<String, VizNode> nodeIndex = new HashMap<String, VizNode>();
		
		/* Create empty graph */
		VizGraph vizGraph = new VizGraph();
		
		int nodeCounter = 0;
		int edgeCounter = 0;
		
		for (String[] triple : triples) {
			
			/* create from VizNode */
			if (!nodeIndex.containsKey(triple[0])) {
				nodeIndex.put(triple[0], new VizNode(nodeCounter, triple[0]));
				nodeCounter++;
			}
			
			VizNode fromNode = nodeIndex.get(triple[0]);
			
			/* create to node */
			if (!nodeIndex.containsKey(triple[2])) {
				nodeIndex.put(triple[2], new VizNode(nodeCounter, triple[2]));
				nodeCounter++;
			}
			
			VizNode toNode = nodeIndex.get(triple[2]);
			
			/* create edge */
			VizEdge edge = new VizEdge(fromNode.nodeId, triple[1], toNode.nodeId);
			
			/* add to the graph */
			vizGraph.addDirectedEdge(fromNode, edge, toNode);
		}
		
		return vizGraph;
	}

}
