package projects.murari_sanders87.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;

public class InquireVote extends Message {
	 
	public Node req;
	public int ts = 0;
	
	public InquireVote(Node req, int ts){
		this.req = req;
		this.ts = ts;
	}

	@Override
	public InquireVote clone(){
		return new InquireVote(this.req, this.ts);
	}
}
