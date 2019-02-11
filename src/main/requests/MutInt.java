package main.requests;

public class MutInt extends Number {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The value of the mutable integer
	 */
	private int value;
	
	/**
	 * Create a mutable int using another mutable int's value
	 * @param mutint other mutable int
	 */
	public MutInt(MutInt mutint) {
		this.value = mutint.intValue();
	}
	
	/**
	 * Create a mutable int using an int
	 * @param i initial int value
	 */
	public MutInt(int i) {
		this.value = i;
	}
	
	/**
	 * Returns the held int, then increments it
	 * @return value++
	 */
	public int getAndIncrement() {
		int ret_val = value;
		increment();
		return ret_val;
	}
	
	/**
	 * Returns the held int, then decrements it
	 * @return value--
	 */
	public int getAndDecrement() {
		int ret_val = value;
		decrement();
		return ret_val;
	}
	
	/**
	 * Increment the held int, then retrieve it
	 * @return ++value
	 */
	public int incrementAndGet(){
		increment();
		return intValue();
	}
	
	/**
	 * Decrement the held int, then return it
	 * @return --value
	 */
	public int decrementAndGet(){
		decrement();
		return intValue();
	}
	
	@Override
	public int intValue() {
		return value;
	}
	
	/**
	 * Set the integer value using another MutInt
	 * @param temp_counter
	 */
	public void setValue(MutInt temp_counter) {
		setValue(temp_counter.value);
	}
	
	/**
	 * Set the integer value using an int
	 * @param i new value
	 */
	public void setValue(int i){
		this.value = i;
	}
	
	/**
	 * Retrieve the value, then add i to it
	 * @param i value to add 
	 * @return {@link MutInt#value}
	 */
	public int getAndAdd(int i) {
		int ret_val = intValue();
		add(i);
		return ret_val;
	}
	
	/**
	 * Return value, then subtract i from it
	 * @param i
	 * @return {@link MutInt#value}
	 */
	public int getAndSubtract(int i) {
		int ret_val = intValue();
		subtract(i);
		return ret_val;
	}
	
	/**
	 * Add 1 to the current value
	 */
	public void increment() {
		add(1);
	}
	
	/**
	 * Subtract 1 from the current value
	 */
	public void decrement() {
		subtract(1);
	}
	
	/**
	 * Add value to current value
	 * @param val value to add
	 */
	public void add(int val) {
		this.value = this.value + val;
	}
	
	/**
	 * Subtract value from current value
	 * @param val
	 */
	public void subtract(int val) {
		add(value * -1);
	}

	@Override
	public double doubleValue() {
		return (double) value;
	}

	@Override
	public float floatValue() {
		return (float) value;
	}

	@Override
	public long longValue() {
		return (long) value;
	}

}
