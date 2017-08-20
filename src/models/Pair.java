package models;

import java.util.Objects;

public class Pair<T1,T2> {

	private T1 object1;
	private T2 object2;
	
	public Pair(T1 object1, T2 object2){
		this.object1 = object1;
		this.object2 = object2;
	}
	
	public T1 first(){
		return object1;
	}
	
	public T2 second(){
		return object2;
	}
	
	public String toString(){
		return "( " + first() + " , " + second() + " )";
	}
	
    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair pair = (Pair) o;
        return Objects.equals(object1, pair.object1) && Objects.equals(object2, pair.object2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object1, object2);
    }
}
