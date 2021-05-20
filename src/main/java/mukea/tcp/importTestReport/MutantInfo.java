package mukea.tcp.importTestReport;

public class MutantInfo {

	private String mutatedClassName;
	private String mutatedMethodName;
	private String mutatedMethodDescriptionName;
	private String mutatedLineNumber;
	private String mutationType;

	public String getMutatedClassName() {
		return mutatedClassName;
	}

	public void setMutatedClassName(String mutatedClassName) {
		this.mutatedClassName = mutatedClassName;
	}

	public String getMutatedMethodName() {
		return mutatedMethodName;
	}

	public void setMutatedMethodName(String mutatedMethodName) {
		this.mutatedMethodName = mutatedMethodName;
	}

	public String getMutatedMethodDescriptionName() {
		return mutatedMethodDescriptionName;
	}

	public void setMutatedMethodDescriptionName(String mutatedMethodDescriptionName) {
		this.mutatedMethodDescriptionName = mutatedMethodDescriptionName;
	}

	public String getMutatedLineNumber() {
		return mutatedLineNumber;
	}

	public void setMutatedLineNumber(String mutatedLineNumber) {
		this.mutatedLineNumber = mutatedLineNumber;
	}

	public String getMutationType() {
		return mutationType;
	}

	public void setMutationType(String mutationType) {
		this.mutationType = mutationType;
	}
	
	public MutantInfo(String mutatedClassName, String mutatedMethodName, String mutatedMethodDescriptionName,
			String mutatedLineNumber, String mutationType) {
		this.mutatedClassName = mutatedClassName;
		this.mutatedMethodName = mutatedMethodName;
		this.mutatedMethodDescriptionName = mutatedMethodDescriptionName;
		this.mutatedLineNumber = mutatedLineNumber;
		this.mutationType = mutationType;
	}

	@Override
	public boolean equals(Object obj) {
		// If the object is compared with itself then return true   
		if (obj == this) { 
			return true; 
		} 

		/* Check if obj is an instance of MutantInfo or not 
          "null instanceof [type]" also returns false */
		if (!(obj instanceof MutantInfo)) { 
			return false; 
		} 

		// typecast obj to MutatInfo so that we can compare data members  
		MutantInfo mi = (MutantInfo) obj; 

		// Compare the data members and return accordingly  
		return ((mutatedClassName.equals(mi.getMutatedClassName()))
				&& (mutatedMethodName.equals(mi.getMutatedMethodName()))
				&& (mutatedMethodDescriptionName.equals(mi.getMutatedMethodDescriptionName()))
				&& (mutatedLineNumber.equals(mi.getMutatedLineNumber()))
				&& (mutationType.equals(mi.getMutationType()))); 
	}

	@Override
	public String toString() {
		return "MutantInfo [mutatedClassName=" + mutatedClassName + ", mutatedMethodName=" + mutatedMethodName
				+ ", mutatedMethodDescriptionName=" + mutatedMethodDescriptionName + ", mutatedLineNumber="
				+ mutatedLineNumber + ", mutationType=" + mutationType + "]";
	}
	
	
}
