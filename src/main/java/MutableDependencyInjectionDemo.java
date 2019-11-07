class MutableObject {
	private int x;
	
	MutableObject(int x) {
		setX(x);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	@Override
	public String toString() {
		return "MutableObject [x=" + x + "]";
	}	
}

class MutableObjectIncrementer {
	private MutableObject mo;
	
	MutableObjectIncrementer(MutableObject mo) {
		this.mo = mo;
	}
	
	void incrementMutableObject() {
		mo.setX(mo.getX()+1);
	}
}

class MutableObjectDecrementer {
	private MutableObject mo;
	
	MutableObjectDecrementer(MutableObject mo) {
		this.mo = mo;
	}
	
	void decrementMutableObject() {
		mo.setX(mo.getX()-1);
	}
}

/**
 * This class demonstrates how, although the reference to an object is passed-by-value, using that
 * reference to modify the referenced object allows the referenced object to be changed. 
 * 
 * 
 * <p>"Object references are passed by value" -- 
 * <a href="https://www.javadude.com/articles/passbyvalue.htm">Scott Stanchfield</a></p>
 * 
 * 
 * In this case both {@code inc} and {@code dec} have the same {@code mo} instance as part of 
 * their composition (has-a relationship). Both {@code inc} and {@code dec} can modify the
 * {@code mo} instance, and it can also be read in the {@code main} method where it was 
 * instantiated.  
 * 
 * @author Andrew Lane
 */
public class MutableDependencyInjectionDemo {	
	public static void main(String[] args) {
		MutableObject mo = new MutableObject(0); // x=0
		System.out.println("mutable object instantiated: " + mo.toString());
		
		// initialise objects which both know about and modify the same instance of MutableObject
		MutableObjectIncrementer inc = new MutableObjectIncrementer(mo);
		MutableObjectDecrementer dec = new MutableObjectDecrementer(mo);
		
		inc.incrementMutableObject();
		System.out.println("after incrementing mutable object: " + mo.toString());
		
		dec.decrementMutableObject();
		dec.decrementMutableObject();
		System.out.println("after decrementing mutable object twice: " + mo.toString());		
	}
}
