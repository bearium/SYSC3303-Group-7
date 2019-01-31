package shared;

public class MutInt {
	private int value;
	
	public MutInt(MutInt mutint) {
		this.value = mutint.intValue();
	}
	public MutInt(int i) {
		this.value = i;
	}
	public int getAndIncrement() {
		int ret_val = value;
		this.value = this.value + 1;
		return ret_val;
	}
	public int intValue() {
		return value;
	}
	public void setValue(MutInt temp_counter) {
		this.value = temp_counter.value;
	}
	public int getAndAdd(int i) {
		int ret_val = value;
		this.value = this.value + i;
		return ret_val;
	}
	public void increment() {
		this.value = this.value + 1;
	}
	public void add(int val) {
		this.value = this.value + val;
	}

}
