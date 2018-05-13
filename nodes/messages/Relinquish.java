package projects.murari_sanders87.nodes.messages;

import sinalgo.nodes.Node;
import sinalgo.nodes.messages.Message;


public class Relinquish extends Message {

	public int ts = 0; 
	public Node req; 
	
	public Relinquish(Node req, int ts){
		this.req = req;
		this.ts = ts;
	}

	@Override
	public Relinquish clone(){
		return new Relinquish(this.req, this.ts);
	}
}
