import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

class ListNode
{
    public ListNode Prev;
    public ListNode Next;
    public ListNode Rand; // произвольный элемент внутри списка
    public String Data;
    
    public ListNode() {
    }
    
    public ListNode(String Data) {
    	this.Data = Data;
    	this.Next = null;
    	this.Prev = null;
    }
}


class ListRand
{
    public ListNode Head;
    public ListNode Tail;
    public int Count;
    
    public ListRand() {
    	this.Count = -1;
    }

    public void add(ListNode addElem) {
    	
    	
    	if (this.Head == null) {
    		this.Head = addElem;
    		this.Tail = addElem;
    		addElem.Rand = addElem;
    		this.Count++;
    		return;
    	}
    	
    	ListNode current = this.Head;
    	
    	while(current.Next != null) {
    		current = current.Next;
    	}
    	
    	addElem.Prev = current;
    	current.Next = addElem;
    	addElem.Rand = this.Head;
    	
    	this.Tail = addElem;
    	this.Count++;
    }
    
    public void Serialize(FileOutputStream s) throws IOException{
    	s.write(this.Count);
    	
    	ListNode current = this.Head;
    	ListNode pointer = current.Next;
    	int[] randoms = new int[this.Count+1];
    	
    	for (int i = 0, j = 0; i < this.Count && j < this.Count - 1; i++) {
    		if (!current.equals(pointer) && current.Rand.equals(pointer)) {
    			randoms[j] = i;
    			current = current.Next;
    			pointer = this.Head;
    			j++;
    			i = -1;
    		}

    		pointer = pointer.Next;
    		
    	}
    	
    	current = this.Head;
    	for (int i = 0; i <= this.Count; i++) {
    		s.write(current.Data.length());
    		s.write(current.Data.getBytes());
    		s.write(randoms[i]);
    		current = current.Next;
    	}
    }

    public void Deserialize(FileInputStream s) throws IOException{
    	this.Count = s.read();
    	
    	this.Head = new ListNode();
    	ListNode current = this.Head;
    	int[] randoms = new int[this.Count+1];
    	
    	for (int i = 0; i <= this.Count; i++) {
    		if (i == 0) {
    			int dataLength = s.read();
    			current.Data = new String(s.readNBytes(dataLength));
    			current.Next = new ListNode();
    			current.Prev = null;
    			randoms[i] = s.read();
    		}else {
    			current.Next.Prev = current;
    			current = current.Next;
    			int dataLength = s.read();
    			current.Data = new String(s.readNBytes(dataLength));
    			if (i != this.Count) {
    				current.Next = new ListNode();
    			}else {
    				this.Tail = current;
    			}
    			randoms[i] = s.read();
    		}
    	}
    	
    	current = this.Head;
    	ListNode pointer = current;
    	
    	for (int i = 0, j = 0; i < this.Count && j <= this.Count; i++) {
    		if (randoms[j] == i) {
    			current.Rand = pointer;
    			current = current.Next;
    			pointer = this.Head;
    			j++;
    			i = -1;
    		}
    	}
    	
    }
}

public class Serializer {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		ListRand list = new ListRand();
		list.add(new ListNode("1"));
		list.add(new ListNode("2"));
		list.add(new ListNode("3"));
		list.add(new ListNode("4"));
		
		
		ListNode head = list.Head;
		while(head != null) {
			System.out.println(head.Data);
			head = head.Next;
		}
		
		System.out.println();
		
		try(FileOutputStream fos = new FileOutputStream("D:\\Documents\\object.dat")){
			list.Serialize(fos);
		};
		
		try(FileInputStream fos = new FileInputStream("D:\\Documents\\object.dat")){
			list.Deserialize(fos);
		};
		
		head = list.Head;
		while(head != null) {
			System.out.println(head.Data);
			head = head.Next;
		}
	}
}
