package SumoAutAv;

public class Rec {

	public static void main(String[] args) {

		// F1 F3 F5 F6
		// =====>O=====>O=====>O=====>
		// | ^
		// | F2 F4 |
		// ======>O======

		double[] y = new double[] { 9.9, 0.099, 1.09, 0.80, 4.30 };

		double[] v = new double[] { 0.0000001, 0.0000001, 0.0000001, 0.0000001, 0.0000001 };

		double[][] A = new double[][] { { 1, -1, 0, 0, 0 }, { 0, 1, -1, 0, 0 }, { 0, 0, 1, -1, 0 },
				{ 0, 0, 0, 1, -1 } };

		double[] AA = new double[] { 1, -1, -1, -1, -1 };

		Reconciliation rec = new Reconciliation(y, v, A);
		System.out.println("Y_hat:");
		rec.printMatrix(rec.getReconciledFlow());
	}

}
