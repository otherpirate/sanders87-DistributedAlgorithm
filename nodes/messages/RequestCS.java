package projects.murari_sanders87.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

public class RequestCS extends Message {
	public Node req;
	public int ts;
	
	public RequestCS(Node req, int ts) {
		this.req = req;
		this.ts = ts;
	}

	@Override
	public RequestCS clone() {
		return new RequestCS(req, ts);
	}
}