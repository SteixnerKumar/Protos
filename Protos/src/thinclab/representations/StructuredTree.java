/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class StructuredTree implements Serializable {
	
	private static final long serialVersionUID = 3354440539923303241L;

	public int maxT;
	
	/* Store the tree structure as a map of nodes and edges */
	public HashMap<Integer, PolicyNode> idToNodeMap = new HashMap<Integer, PolicyNode>();
	public HashMap<Integer, HashMap<List<String>, Integer>> edgeMap =
			new HashMap<Integer, HashMap<List<String>, Integer>>();
	
	public int currentPolicyNodeCounter = 0;
	
	public List<List<String>> observations;
	
	private static final Logger LOGGER = Logger.getLogger(StructuredTree.class);
	
	// ----------------------------------------------------------------------------------------
	
	public void makeNextBeliefNode(
			int parentNodeId, 
			DD parentNodeBelief,
			DecisionProcess f,
			String action,
			BaseSolver solver,
			List<String> obs,
			HashMap<DD, Integer> currentLevelBeliefSet,
			int level) {
		
		/* 
		 * Make the next node after taking action at parentNodeBelief and observing the
		 * specified observation
		 */
		DD belief = this.idToNodeMap.get(parentNodeId).belief;
		
		try {
			
			DD nextBelief = null;
			
			/*
			 * If the process is an IPOMDP, do an IPOMDP belief update
			 * else POMDP belief update
			 */
			
			nextBelief = 
					f.beliefUpdate( 
							belief, 
							action, 
							obs.toArray(new String[obs.size()]));
			
			/* add to belief set if nextBelief is unique */
			if (!currentLevelBeliefSet.containsKey(nextBelief)) {
				currentLevelBeliefSet.put(nextBelief, this.currentPolicyNodeCounter);
				this.currentPolicyNodeCounter += 1;
				
				int nextNodeId = currentLevelBeliefSet.get(nextBelief);
				PolicyNode nextNode = new PolicyNode();
				
				nextNode.id = nextNodeId;
				nextNode.belief = nextBelief;
				nextNode.H = level;
				
				if (solver != null)
					nextNode.actName = solver.getActionForBelief(nextBelief);
				
				else
					nextNode.actName = "";
				
				if (f.getType().contentEquals("IPOMDP"))
					nextNode.sBelief = ((IPOMDP) f).getBeliefString(nextBelief);
				
				else nextNode.sBelief = f.getBeliefString(nextBelief);
				
				/* add next unique node to the nodes map */
				this.idToNodeMap.put(nextNodeId, nextNode);
			}
			
			int nextNodeId = currentLevelBeliefSet.get(nextBelief);
			
			if (!this.edgeMap.containsKey(parentNodeId))
				this.edgeMap.put(parentNodeId, new HashMap<List<String>, Integer>());
			
			/* construct edge as action + obs */
			List<String> edge = new ArrayList<String>();
			edge.add(action);
			edge.addAll(obs);
			
			this.edgeMap.get(parentNodeId).put(edge, nextNodeId);
			
		}
		
		catch (ZeroProbabilityObsException o) {
			
		}
		
		catch (Exception e) {
			LOGGER.error("While running belief update " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void makeNextPolicyNode(
			int parentNodeId, 
			DD parentNodeBelief,
			BaseSolver solver,
			String action,
			List<String> obs,
			HashMap<String, Integer> currentLevelBeliefSet,
			int level) {
		
		/* 
		 * Make the next node after taking action at parentNodeBelief and observing the
		 * specified observation
		 * 
		 * 
		 * WARNING: This method uses belief strings to maintain unique beliefs. After the recent
		 * commits, belief strings are built from JSON objects and hashmaps which do not guarantee
		 * the ordering of keys. So same beliefs can be stored as unique beliefs just because of key
		 * ordering is different. Do not use this function will be removed soon
		 */
		try {
			
			DD nextBelief = null;
			
			/*
			 * If the process is an IPOMDP, do an IPOMDP belief update
			 * else POMDP belief update
			 */

			nextBelief = 
					solver.f.beliefUpdate( 
							parentNodeBelief, 
							action, 
							obs.toArray(new String[obs.size()])); 
			
			String nextBeliefString = solver.f.getBeliefString(nextBelief);
			
			/* add to belief set if nextBelief is unique */
			if (!currentLevelBeliefSet.containsKey(nextBeliefString)) {
				currentLevelBeliefSet.put(nextBeliefString, this.currentPolicyNodeCounter);
				this.currentPolicyNodeCounter += 1;
				
				int nextNodeId = currentLevelBeliefSet.get(nextBeliefString);
				PolicyNode nextNode = new PolicyNode();
				
				nextNode.id = nextNodeId;
				nextNode.belief = nextBelief;
				nextNode.actName = solver.getActionForBelief(nextBelief);
				nextNode.H = level;
				nextNode.sBelief = nextBeliefString;
				
				/* add next unique node to the nodes map */
				this.idToNodeMap.put(nextNodeId, nextNode);
			}
			
			int nextNodeId = currentLevelBeliefSet.get(nextBeliefString);
			
			if (!this.edgeMap.containsKey(parentNodeId))
				this.edgeMap.put(parentNodeId, new HashMap<List<String>, Integer>());
			
			this.edgeMap.get(parentNodeId).put(obs, nextNodeId);
			
		}
		
		catch (ZeroProbabilityObsException o) {
			
		}
		
		catch (Exception e) {
			LOGGER.error("While running belief update " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	// ----------------------------------------------------------------------------------------
	
	public String getJSONString() {
		/*
		 * Converts computed policy tree to JSON format
		 */
		
		Gson gsonHandler = 
				new GsonBuilder()
					.disableHtmlEscaping()
					.setPrettyPrinting()
					.create();
		
		/* Hold nodes and edges in HashMaps of Strings */
		HashMap<Integer, HashMap<String, String>> nodeJSONMap = 
				new HashMap<Integer, HashMap<String, String>>();
		HashMap<Integer, HashMap<String, String>> edgesJSONMap = 
				new HashMap<Integer, HashMap<String, String>>();
		
		/* populate nodes */
		for (Entry<Integer, PolicyNode> entry : this.idToNodeMap.entrySet()) {
			
			/* add optimal action and beliefs at node */
			nodeJSONMap.put(entry.getKey(), new HashMap<String, String>());
			nodeJSONMap.get(entry.getKey()).put("action", entry.getValue().actName);
			nodeJSONMap.get(entry.getKey()).put("belief", entry.getValue().sBelief);
			
			/* mark start node */
			if (entry.getValue().startNode)
				nodeJSONMap.get(entry.getKey()).put("start", "true");
		}
		
		/* populate edges */
		for (int fromNode : this.edgeMap.keySet()) {
			
			edgesJSONMap.put(fromNode, new HashMap<String, String>());
			
			for (Entry<List<String>, Integer> entry : this.edgeMap.get(fromNode).entrySet())
				edgesJSONMap.get(fromNode).put(
						entry.getKey().toString(), 
						entry.getValue().toString());
		}
		
		
		JsonObject jsonContainer = new JsonObject();
		jsonContainer.add("nodes", gsonHandler.toJsonTree(nodeJSONMap));
		jsonContainer.add("edges", gsonHandler.toJsonTree(edgesJSONMap));
		
		return gsonHandler.toJson(jsonContainer);
	}
	
	public static String jsonBeliefStringToDotNode(String beliefString, String action) {
		/*
		 * Converts a JSON formatted belief string to dot format node
		 */
		
		/* JSON Tree object */
		JsonElement JSONTree = JsonParser.parseString(beliefString);
		
		Gson gsonHandler = 
				new GsonBuilder()
					.disableHtmlEscaping()
					.create();
		
		String dotString = "";
		String seperator = "|";
		
		dotString += "{";
		
		JsonElement mjElement = JSONTree.getAsJsonObject().get("M_j");
		
		if (mjElement != null) {
			JsonArray mjArray = mjElement.getAsJsonArray();
			
			/* make Mj belief */
			dotString += "M_j ";
			
			/* deserialize the list of mj beliefs */
			for (JsonElement mj: mjArray) {
				
				/* convert to JSON object */
				JsonObject mjJSON = mj.getAsJsonObject();
				
				JsonObject mjBeliefMap = 
								mjJSON.get("model")
									.getAsJsonObject()
									.get("belief_j")
									.getAsJsonObject();
				
				/* Print each belief on a new line for Mj */
				String mjString = "";
				
				for (String state : mjBeliefMap.keySet())
					mjString += 
						state 
						+ seperator 
						+ gsonHandler.toJson(mjBeliefMap.get(state))
							.replace("{", "(")
							.replace("}", ")")
						+ seperator;
				
				dotString += 
						seperator + "{" + 
							"{" + mjString
								+ seperator + gsonHandler.toJson(
										mjJSON.get("model").getAsJsonObject().get("A_j"))
								+ seperator + gsonHandler.toJson(
										mjJSON.get("model").getAsJsonObject().get("Theta_j"))
							+ "}"
							+ seperator + gsonHandler.toJson(mjJSON.get("prob")) + "}";
			}
			
			dotString += seperator;
			
		}
		
		/* make other beliefs */
		for (String var: JSONTree.getAsJsonObject().keySet()) {
			
			if (var.contentEquals("M_j")) continue;
			
			dotString += seperator;
			dotString += var;
			dotString += seperator + gsonHandler.toJson(JSONTree.getAsJsonObject().get(var)) 
				+ seperator; 
		}
		
		dotString += seperator;
		dotString += "Ai = " + action;
		dotString += "}";
		
		return dotString.replace("\"", " ");
	}
	
	public String getDotString() {
		/*
		 * Converts to graphviz compatible dot string
		 */
		String endl = "\r\n";
		String dotString = "digraph G{ " + endl;
		
		dotString += "graph [ranksep=3];" + endl;
		
		/* Make nodes */
		for (Entry<Integer, PolicyNode> entry : this.idToNodeMap.entrySet()) {
			
			if (entry.getValue().startNode)
				dotString += " " + entry.getKey() + " [shape=Mrecord, label=\"";
			else
				dotString += " " + entry.getKey() + " [shape=record, label=\"";
			
			dotString += StructuredTree.jsonBeliefStringToDotNode(
							entry.getValue().sBelief,
							entry.getValue().actName)
					+ "\"];" + endl;
		}
		
		dotString += endl;
		
		for (Entry<Integer, HashMap<List<String>, Integer>> edges : this.edgeMap.entrySet()) {
			
			String from = edges.getKey().toString();
			
			for (Entry<List<String>, Integer> ends : edges.getValue().entrySet()) {
				
				dotString += " " + from + " -> " + ends.getValue()
					+ " [label=\"" + ends.getKey().toString() + "\"]" + endl;
			}
		}
		
		dotString += "}" + endl;
		
		return dotString;
	}
	
	public void writeDotFile(String dirName, String name) {
		/*
		 * Creates a graphviz dot file for the specified structure
		 */
		
		try {
			
			PrintWriter writer = new PrintWriter(dirName + "/" + name + ".dot");
			writer.println(this.getDotString());
			writer.flush();
			
			LOGGER.info("dot file " + dirName + "/" + name + ".dot" + " created");
			writer.close();
		}
		
		catch (Exception e) {
			LOGGER.error("While creating dot file " + dirName + "/" + name + ".dot");
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void writeJSONFile(String dirName, String name) {
		/*
		 * Creates a JSON file for the specified structure
		 */
		
		try {
			
			PrintWriter writer = new PrintWriter(dirName + "/" + name + ".json");
			writer.println(this.getJSONString());
			writer.flush();
			
			LOGGER.info("JSON file " + dirName + "/" + name + ".json" + " created");
			writer.close();
		}
		
		catch (Exception e) {
			LOGGER.error("While creating JSON file " + dirName + "/" + name + ".json");
			e.printStackTrace();
			System.exit(-1);
		}
	}

}
