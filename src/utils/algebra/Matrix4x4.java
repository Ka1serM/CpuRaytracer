package utils.algebra;

public class Matrix4x4 {

	private Matrix mBaseMatrix;

	/**
	 The standard constructor will produce an identity matrix
	 **/
	public Matrix4x4(){
		mBaseMatrix = Matrix.identity(4,4);
	}

	private Matrix4x4(Matrix mat){
		mBaseMatrix = mat;
	}

	/**
	 Translate Matrix in 3D
	 **/
	public Matrix4x4 translateXYZ(Vec3 vec){
		Matrix4x4 out = new Matrix4x4(mBaseMatrix);

		out.setValueAt( 0, 3, vec.x );
		out.setValueAt( 1, 3, vec.y );
		out.setValueAt( 2, 3, vec.z );

		return out;
	}

	/**
	 Translate Matrix in 4D - watch for the homogeneous coordinate
	 **/
	public Matrix4x4 translateXYZW(Vec4 vec){
		Matrix4x4 out = new Matrix4x4(mBaseMatrix);

		out.setValueAt( 0, 3, vec.x );
		out.setValueAt( 1, 3, vec.y );
		out.setValueAt( 2, 3, vec.z );
		out.setValueAt( 3, 3, vec.w );

		return out;
	}

	/**
	 Scale uniform by factor s
	 **/
	public Matrix4x4 scale(double s){
		Matrix4x4 out = new Matrix4x4(mBaseMatrix);

		out.setValueAt( 0, 0, s );
		out.setValueAt( 1, 1, s );
		out.setValueAt( 2, 2, s );

		return out;
	}

	/**
	 Scale non-uniform by x, y and z
	 **/
	public Matrix4x4 scale(Vec3 vec){
		Matrix4x4 out = new Matrix4x4(mBaseMatrix);

		out.setValueAt( 0, 0, vec.x );
		out.setValueAt( 1, 1, vec.y );
		out.setValueAt( 2, 2, vec.z );

		return out;
	}

	/**
	 Transpose matrix
	 **/
	public Matrix4x4 transpose(){
		return new Matrix4x4( mBaseMatrix.transpose() );
	}

	/**
	 Invert matrix
	 **/
	public Matrix4x4 invert(){
		return new Matrix4x4( mBaseMatrix.inverse() );
	}

	/**
	 Set 'value' in matrix at position row and column
	 **/
	public void setValueAt(int row, int col, double value){
		mBaseMatrix.set( row, col, value );
	}

	/**
	 Get 'value' in matrix from position row and column
	 **/
	public double getValueAt(int row, int col){
		return mBaseMatrix.get(row, col);
	}

	/**
	 Get 'column' in matrix from position index
	 **/
	public Vec4 getColumn(int index){
		Vec4 outVec = new Vec4();

		for(int i = 0; i < 4; i++){
			outVec.setValue(i, (float) this.getValueAt(i, index));
		}
		return outVec;
	}

	/**
	 Get 'column' in matrix from position index
	 **/
	public Vec4 getRow(int index){
		Vec4 outVec = new Vec4();

		for(int i = 0; i < 4; i++){
			outVec.setValue(i, (float) this.getValueAt(index, i));
		}
		return outVec;
	}

	/**
	 Add another matrix
	 **/
	public Matrix4x4 add(Matrix4x4 mat){
		Matrix4x4 outMat = new Matrix4x4();

		for(int r = 0; r < 4; r++){
			for(int c = 0; c < 4; c++){
				outMat.setValueAt(r,c, this.getValueAt(r,c) + mat.getValueAt(r,c));
			}
		}

		return outMat;
	}

	/**
	 Sub another matrix
	 **/
	public Matrix4x4 sub(Matrix4x4 mat){
		Matrix4x4 outMat = new Matrix4x4();

		for(int r = 0; r < 4; r++){
			for(int c = 0; c < 4; c++){
				outMat.setValueAt(r,c, this.getValueAt(r,c) - mat.getValueAt(r,c));
			}
		}

		return outMat;
	}

	/**
	 Mult a single value
	 **/
	public Matrix4x4 multScalar(float scalar){
		Matrix4x4 outMat = new Matrix4x4();

		for(int r = 0; r < 4; r++){
			for(int c = 0; c < 4; c++){
				outMat.setValueAt(r,c, this.getValueAt(r,c) * scalar);
			}
		}

		return outMat;
	}

	/**
	 Multiply another matrix
	 **/
	public Matrix4x4 mult(Matrix4x4 mat){
		Matrix4x4 outMat = new Matrix4x4();

		for(int r1 = 0; r1 < 4; r1++){
			for(int c1 = 0; c1 < 4; c1++) {
				outMat.setValueAt(r1, c1, this.getRow(r1).scalar(mat.getColumn(c1)));
			}
		}

		return outMat;
	}

	/**
	 Multiply a 3D point OR vector with the matrix
	 **/
	public Vec4 multVec3(Vec4 vec){
		return new Vec4(
				vec.x * ( float ) this.getValueAt(0,0) + vec.y * ( float ) this.getValueAt(0,1) + vec.z * ( float ) this.getValueAt(0,2) + vec.w * ( float ) this.getValueAt(0,3),
				vec.x * ( float ) this.getValueAt(1,0) + vec.y * ( float ) this.getValueAt(1,1) + vec.z * ( float ) this.getValueAt(1,2) + vec.w * ( float ) this.getValueAt(1,3),
				vec.x * ( float ) this.getValueAt(2,0) + vec.y * ( float ) this.getValueAt(2,1) + vec.z * ( float ) this.getValueAt(2,2) + vec.w * ( float ) this.getValueAt(2,3),
				vec.x * ( float ) this.getValueAt(3,0) + vec.y * ( float ) this.getValueAt(3,1) + vec.z * ( float ) this.getValueAt(3,2) + vec.w * ( float ) this.getValueAt(3,3)
		);
	}

	/**
	 Multiply a 3D point OR vector with the matrix
	 **/
	public Vec3 multVec3(Vec3 vec, Boolean isPoint){

		float w = isPoint ? 1 : 0;

		Vec4 out = new Vec4(
				vec.x * ( float ) this.getValueAt(0,0) + vec.y * ( float ) this.getValueAt(0,1) + vec.z * ( float ) this.getValueAt(0,2) + w * ( float ) this.getValueAt(0,3),
				vec.x * ( float ) this.getValueAt(1,0) + vec.y * ( float ) this.getValueAt(1,1) + vec.z * ( float ) this.getValueAt(1,2) + w * ( float ) this.getValueAt(1,3),
				vec.x * ( float ) this.getValueAt(2,0) + vec.y * ( float ) this.getValueAt(2,1) + vec.z * ( float ) this.getValueAt(2,2) + w * ( float ) this.getValueAt(2,3),
				vec.x * ( float ) this.getValueAt(3,0) + vec.y * ( float ) this.getValueAt(3,1) + vec.z * ( float ) this.getValueAt(3,2) + w * ( float ) this.getValueAt(3,3)
		);

		return new Vec3(out.x, out.y, out.z);
	}

	/**
	 Print values of matrix
	 **/
	@Override
	public String toString(){
		return  "\n" +
				this.getValueAt(0,0) + "\t\t\t\t\t\t" + this.getValueAt(0,1) + "\t\t\t\t\t\t" + this.getValueAt(0,2) + "\t\t\t\t\t\t" + this.getValueAt(0,3) + "\t\t\n" +
				this.getValueAt(1,0) + "\t\t\t\t\t\t" + this.getValueAt(1,1) + "\t\t\t\t\t\t" + this.getValueAt(1,2) + "\t\t\t\t\t\t" + this.getValueAt(1,3) + "\t\t\n" +
				this.getValueAt(2,0) + "\t\t\t\t\t\t" + this.getValueAt(2,1) + "\t\t\t\t\t\t" + this.getValueAt(2,2) + "\t\t\t\t\t\t" + this.getValueAt(2,3) + "\t\t\n" +
				this.getValueAt(3,0) + "\t\t\t\t\t\t" + this.getValueAt(3,1) + "\t\t\t\t\t\t" + this.getValueAt(3,2) + "\t\t\t\t\t\t" + this.getValueAt(3,3) + "\t\t\n" ;
	}

	/**
	 Here's the background calculation...
	 **/
	private static class Matrix implements Cloneable, java.io.Serializable {

		private double[][] A;
		private int m, n;

		public Matrix (int m, int n) {
			this.m = m;
			this.n = n;
			A = new double[m][n];
		}

		public Matrix (int m, int n, double s) {
			this.m = m;
			this.n = n;
			A = new double[m][n];
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					A[i][j] = s;
				}
			}
		}

		public Matrix (double[][] A) {
			m = A.length;
			n = A[0].length;
			for (int i = 0; i < m; i++) {
				if (A[i].length != n) {
					throw new IllegalArgumentException("All rows must have the same length.");
				}
			}
			this.A = A;
		}

		public Matrix (double[][] A, int m, int n) {
			this.A = A;
			this.m = m;
			this.n = n;
		}

		public Matrix (double vals[], int m) {
			this.m = m;
			n = (m != 0 ? vals.length/m : 0);
			if (m*n != vals.length) {
				throw new IllegalArgumentException("Array length must be a multiple of m.");
			}
			A = new double[m][n];
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					A[i][j] = vals[i+j*m];
				}
			}
		}

		public static Matrix constructWithCopy(double[][] A) {
			int m = A.length;
			int n = A[0].length;
			Matrix X = new Matrix(m,n);
			double[][] C = X.getArray();
			for (int i = 0; i < m; i++) {
				if (A[i].length != n) {
					throw new IllegalArgumentException
							("All rows must have the same length.");
				}
				for (int j = 0; j < n; j++) {
					C[i][j] = A[i][j];
				}
			}
			return X;
		}

		public Matrix copy () {
			Matrix X = new Matrix(m,n);
			double[][] C = X.getArray();
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					C[i][j] = A[i][j];
				}
			}
			return X;
		}

		public Object clone () {
			return this.copy();
		}

		public double[][] getArray () {
			return A;
		}

		public double[][] getArrayCopy () {
			double[][] C = new double[m][n];
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					C[i][j] = A[i][j];
				}
			}
			return C;
		}

		public double[] getColumnPackedCopy () {
			double[] vals = new double[m*n];
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					vals[i+j*m] = A[i][j];
				}
			}
			return vals;
		}

		public double[] getRowPackedCopy () {
			double[] vals = new double[m*n];
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					vals[i*n+j] = A[i][j];
				}
			}
			return vals;
		}

		public int getRowDimension () {
			return m;
		}

		public int getColumnDimension () {
			return n;
		}

		public double get (int i, int j) {
			return A[i][j];
		}

		public Matrix getMatrix (int i0, int i1, int j0, int j1) {
			Matrix X = new Matrix(i1-i0+1,j1-j0+1);
			double[][] B = X.getArray();
			try {
				for (int i = i0; i <= i1; i++) {
					for (int j = j0; j <= j1; j++) {
						B[i-i0][j-j0] = A[i][j];
					}
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}
			return X;
		}

		public Matrix getMatrix (int[] r, int[] c) {
			Matrix X = new Matrix(r.length,c.length);
			double[][] B = X.getArray();
			try {
				for (int i = 0; i < r.length; i++) {
					for (int j = 0; j < c.length; j++) {
						B[i][j] = A[r[i]][c[j]];
					}
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}
			return X;
		}

		public Matrix getMatrix (int i0, int i1, int[] c) {
			Matrix X = new Matrix(i1-i0+1,c.length);
			double[][] B = X.getArray();
			try {
				for (int i = i0; i <= i1; i++) {
					for (int j = 0; j < c.length; j++) {
						B[i-i0][j] = A[i][c[j]];
					}
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}
			return X;
		}

		public Matrix getMatrix (int[] r, int j0, int j1) {
			Matrix X = new Matrix(r.length,j1-j0+1);
			double[][] B = X.getArray();
			try {
				for (int i = 0; i < r.length; i++) {
					for (int j = j0; j <= j1; j++) {
						B[i][j-j0] = A[r[i]][j];
					}
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}
			return X;
		}

		public void set (int i, int j, double s) {
			A[i][j] = s;
		}

		public void setMatrix (int i0, int i1, int j0, int j1, Matrix X) {
			try {
				for (int i = i0; i <= i1; i++) {
					for (int j = j0; j <= j1; j++) {
						A[i][j] = X.get(i-i0,j-j0);
					}
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}
		}

		public void setMatrix (int[] r, int[] c, Matrix X) {
			try {
				for (int i = 0; i < r.length; i++) {
					for (int j = 0; j < c.length; j++) {
						A[r[i]][c[j]] = X.get(i,j);
					}
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}
		}

		public void setMatrix (int[] r, int j0, int j1, Matrix X) {
			try {
				for (int i = 0; i < r.length; i++) {
					for (int j = j0; j <= j1; j++) {
						A[r[i]][j] = X.get(i,j-j0);
					}
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}
		}

		public void setMatrix (int i0, int i1, int[] c, Matrix X) {
			try {
				for (int i = i0; i <= i1; i++) {
					for (int j = 0; j < c.length; j++) {
						A[i][c[j]] = X.get(i-i0,j);
					}
				}
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new ArrayIndexOutOfBoundsException("Submatrix indices");
			}
		}

		public Matrix transpose () {
			Matrix X = new Matrix(n,m);
			double[][] C = X.getArray();
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					C[j][i] = A[i][j];
				}
			}
			return X;
		}

		public double trace () {
			double t = 0;
			for (int i = 0; i < Math.min(m,n); i++) {
				t += A[i][i];
			}
			return t;
		}

		public static Matrix random (int m, int n) {
			Matrix A = new Matrix(m,n);
			double[][] X = A.getArray();
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					X[i][j] = Math.random();
				}
			}
			return A;
		}

		public static Matrix identity (int m, int n) {
			Matrix A = new Matrix(m,n);
			double[][] X = A.getArray();
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					X[i][j] = (i == j ? 1.0 : 0.0);
				}
			}
			return A;
		}

		private void checkMatrixDimensions (Matrix B) {
			if (B.m != m || B.n != n) {
				throw new IllegalArgumentException("Matrix dimensions must agree.");
			}
		}

		public Matrix solve (Matrix B) {
			return (m == n ? (new LUDecomposition(this)).solve(B) :
					(new QRDecomposition(this)).solve(B));
		}

		public Matrix solveTranspose (Matrix B) {
			return transpose().solve(B.transpose());
		}

		public Matrix inverse () {
			return solve(identity(m,m));
		}

		public double det () {
			return new LUDecomposition(this).det();
		}

		private static final long serialVersionUID = 1;
	}

	private static class Maths {

		public static double hypot(double a, double b) {
			double r;
			if (Math.abs(a) > Math.abs(b)) {
				r = b/a;
				r = Math.abs(a)*Math.sqrt(1+r*r);
			} else if (b != 0) {
				r = a/b;
				r = Math.abs(b)*Math.sqrt(1+r*r);
			} else {
				r = 0.0;
			}
			return r;
		}
	}
	public static class LUDecomposition implements java.io.Serializable {

		private double[][] LU;
		private int m, n, pivsign;
		private int[] piv;

		public LUDecomposition (Matrix A) {

			// Use a "left-looking", dot-product, Crout/Doolittle algorithm.

			LU = A.getArrayCopy();
			m = A.getRowDimension();
			n = A.getColumnDimension();
			piv = new int[m];
			for (int i = 0; i < m; i++) {
				piv[i] = i;
			}
			pivsign = 1;
			double[] LUrowi;
			double[] LUcolj = new double[m];

			// Outer loop.

			for (int j = 0; j < n; j++) {

				// Make a copy of the j-th column to localize references.

				for (int i = 0; i < m; i++) {
					LUcolj[i] = LU[i][j];
				}

				// Apply previous transformations.

				for (int i = 0; i < m; i++) {
					LUrowi = LU[i];

					// Most of the time is spent in the following dot product.

					int kmax = Math.min(i,j);
					double s = 0.0;
					for (int k = 0; k < kmax; k++) {
						s += LUrowi[k]*LUcolj[k];
					}

					LUrowi[j] = LUcolj[i] -= s;
				}

				// Find pivot and exchange if necessary.

				int p = j;
				for (int i = j+1; i < m; i++) {
					if (Math.abs(LUcolj[i]) > Math.abs(LUcolj[p])) {
						p = i;
					}
				}
				if (p != j) {
					for (int k = 0; k < n; k++) {
						double t = LU[p][k]; LU[p][k] = LU[j][k]; LU[j][k] = t;
					}
					int k = piv[p]; piv[p] = piv[j]; piv[j] = k;
					pivsign = -pivsign;
				}

				// Compute multipliers.

				if (j < m & LU[j][j] != 0.0) {
					for (int i = j+1; i < m; i++) {
						LU[i][j] /= LU[j][j];
					}
				}
			}
		}

		public boolean isNonsingular () {
			for (int j = 0; j < n; j++) {
				if (LU[j][j] == 0)
					return false;
			}
			return true;
		}

		public Matrix getL () {
			Matrix X = new Matrix(m,n);
			double[][] L = X.getArray();
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					if (i > j) {
						L[i][j] = LU[i][j];
					} else if (i == j) {
						L[i][j] = 1.0;
					} else {
						L[i][j] = 0.0;
					}
				}
			}
			return X;
		}

		public Matrix getU () {
			Matrix X = new Matrix(n,n);
			double[][] U = X.getArray();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i <= j) {
						U[i][j] = LU[i][j];
					} else {
						U[i][j] = 0.0;
					}
				}
			}
			return X;
		}

		public int[] getPivot () {
			int[] p = new int[m];
			for (int i = 0; i < m; i++) {
				p[i] = piv[i];
			}
			return p;
		}

		public double[] getDoublePivot () {
			double[] vals = new double[m];
			for (int i = 0; i < m; i++) {
				vals[i] = (double) piv[i];
			}
			return vals;
		}

		public double det () {
			if (m != n) {
				throw new IllegalArgumentException("Matrix must be square.");
			}
			double d = (double) pivsign;
			for (int j = 0; j < n; j++) {
				d *= LU[j][j];
			}
			return d;
		}

		public Matrix solve (Matrix B) {
			if (B.getRowDimension() != m) {
				throw new IllegalArgumentException("Matrix row dimensions must agree.");
			}
			if (!this.isNonsingular()) {
				throw new RuntimeException("Matrix is singular.");
			}

			// Copy right hand side with pivoting
			int nx = B.getColumnDimension();
			Matrix Xmat = B.getMatrix(piv,0,nx-1);
			double[][] X = Xmat.getArray();

			// Solve L*Y = B(piv,:)
			for (int k = 0; k < n; k++) {
				for (int i = k+1; i < n; i++) {
					for (int j = 0; j < nx; j++) {
						X[i][j] -= X[k][j]*LU[i][k];
					}
				}
			}
			// Solve U*X = Y;
			for (int k = n-1; k >= 0; k--) {
				for (int j = 0; j < nx; j++) {
					X[k][j] /= LU[k][k];
				}
				for (int i = 0; i < k; i++) {
					for (int j = 0; j < nx; j++) {
						X[i][j] -= X[k][j]*LU[i][k];
					}
				}
			}
			return Xmat;
		}
		private static final long serialVersionUID = 1;
	}

	public static class QRDecomposition implements java.io.Serializable {

		private double[][] QR;
		private int m, n;
		private double[] Rdiag;

		public QRDecomposition (Matrix A) {
			// Initialize.
			QR = A.getArrayCopy();
			m = A.getRowDimension();
			n = A.getColumnDimension();
			Rdiag = new double[n];

			// Main loop.
			for (int k = 0; k < n; k++) {
				// Compute 2-norm of k-th column without under/overflow.
				double nrm = 0;
				for (int i = k; i < m; i++) {
					nrm = Maths.hypot(nrm,QR[i][k]);
				}

				if (nrm != 0.0) {
					// Form k-th Householder vector.
					if (QR[k][k] < 0) {
						nrm = -nrm;
					}
					for (int i = k; i < m; i++) {
						QR[i][k] /= nrm;
					}
					QR[k][k] += 1.0;

					// Apply transformation to remaining columns.
					for (int j = k+1; j < n; j++) {
						double s = 0.0;
						for (int i = k; i < m; i++) {
							s += QR[i][k]*QR[i][j];
						}
						s = -s/QR[k][k];
						for (int i = k; i < m; i++) {
							QR[i][j] += s*QR[i][k];
						}
					}
				}
				Rdiag[k] = -nrm;
			}
		}

		public boolean isFullRank () {
			for (int j = 0; j < n; j++) {
				if (Rdiag[j] == 0)
					return false;
			}
			return true;
		}

		public Matrix getH () {
			Matrix X = new Matrix(m,n);
			double[][] H = X.getArray();
			for (int i = 0; i < m; i++) {
				for (int j = 0; j < n; j++) {
					if (i >= j) {
						H[i][j] = QR[i][j];
					} else {
						H[i][j] = 0.0;
					}
				}
			}
			return X;
		}

		public Matrix getR () {
			Matrix X = new Matrix(n,n);
			double[][] R = X.getArray();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i < j) {
						R[i][j] = QR[i][j];
					} else if (i == j) {
						R[i][j] = Rdiag[i];
					} else {
						R[i][j] = 0.0;
					}
				}
			}
			return X;
		}

		public Matrix getQ () {
			Matrix X = new Matrix(m,n);
			double[][] Q = X.getArray();
			for (int k = n-1; k >= 0; k--) {
				for (int i = 0; i < m; i++) {
					Q[i][k] = 0.0;
				}
				Q[k][k] = 1.0;
				for (int j = k; j < n; j++) {
					if (QR[k][k] != 0) {
						double s = 0.0;
						for (int i = k; i < m; i++) {
							s += QR[i][k]*Q[i][j];
						}
						s = -s/QR[k][k];
						for (int i = k; i < m; i++) {
							Q[i][j] += s*QR[i][k];
						}
					}
				}
			}
			return X;
		}

		public Matrix solve (Matrix B) {
			if (B.getRowDimension() != m) {
				throw new IllegalArgumentException("Matrix row dimensions must agree.");
			}
			if (!this.isFullRank()) {
				throw new RuntimeException("Matrix is rank deficient.");
			}

			// Copy right hand side
			int nx = B.getColumnDimension();
			double[][] X = B.getArrayCopy();

			// Compute Y = transpose(Q)*B
			for (int k = 0; k < n; k++) {
				for (int j = 0; j < nx; j++) {
					double s = 0.0;
					for (int i = k; i < m; i++) {
						s += QR[i][k]*X[i][j];
					}
					s = -s/QR[k][k];
					for (int i = k; i < m; i++) {
						X[i][j] += s*QR[i][k];
					}
				}
			}
			// Solve R*X = Y;
			for (int k = n-1; k >= 0; k--) {
				for (int j = 0; j < nx; j++) {
					X[k][j] /= Rdiag[k];
				}
				for (int i = 0; i < k; i++) {
					for (int j = 0; j < nx; j++) {
						X[i][j] -= X[k][j]*QR[i][k];
					}
				}
			}
			return (new Matrix(X,n,nx).getMatrix(0,n-1,0,nx-1));
		}
		private static final long serialVersionUID = 1;
	}
}
