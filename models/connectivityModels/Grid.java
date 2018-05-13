package projects.murari_sanders87.models.connectivityModels;

import sinalgo.models.ConnectivityModelHelper;
import sinalgo.nodes.Node;
import sinalgo.runtime.Runtime;

public class Grid extends ConnectivityModelHelper {
	
	protected boolean isConnected(Node from, Node to) {
		return row(from) == row(to) || col(from) == col(to);
	}
	
	private int row(Node node) {
		return (node.ID - 1) % gridSize();
	}

	private int col(Node node) {
		return (node.ID - 1) / gridSize();
	}

	private int gridSize() {
		return (int) Math.sqrt(Runtime.nodes.size());
	}
}
