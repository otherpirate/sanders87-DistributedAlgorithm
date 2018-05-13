package projects.murari_sanders87.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

public class ReleaseCS extends Message {

	public Node req;
	
	public ReleaseCS(Node req) {
		this.req = req;
	}

	@Override
	public ReleaseCS clone() {
		return new ReleaseCS(req);
	}

}
