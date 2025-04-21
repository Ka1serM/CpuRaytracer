package utils.algebra;

public class Vec3 {
    public float x;
    public float y;
    public float z;

    /**
     Standard 3D constructor taking all values given
     **/
    public Vec3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     Standard 3D constructor setting all values to 0
     **/
    public Vec3(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    /**
     Compare two vectors to check if they are equal
     **/
    public boolean equals(Vec3 inputVec){
        return (this.x == inputVec.x) && (this.y == inputVec.y) && (this.z == inputVec.z);
    }

    /**
     Get normalized vector
     **/
    public Vec3 normalize(){
        float length = this.length();
        return new Vec3(this.x / length, this.y / length, this.z / length);
    }

    /**
     Get length of vector
     **/
    public float length(){
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    /**
     Get sum of vector with the given vector
     **/
    public Vec3 add(Vec3 inputVec){
        return new Vec3(this.x + inputVec.x, this.y + inputVec.y, this.z + inputVec.z);
    }

    /**
     Get difference between vector and the given vector
     **/
    public Vec3 sub(Vec3 inputVec){
        return new Vec3(this.x - inputVec.x, this.y - inputVec.y, this.z - inputVec.z);
    }

    /**
     Get opposite vector
     **/
    public Vec3 negate(){
        return new Vec3(-this.x, -this.y, -this.z);
    }

    /**
     Get scalar product of vector and given vector
     **/
    public float scalar(Vec3 inputVec){
        return this.x * inputVec.x + this.y * inputVec.y + this.z * inputVec.z;
    }

    /**
     Get new vector with the given value multiplied to every component
     **/
    public Vec3 multScalar(float value){
        return new Vec3(this.x * value, this.y * value, this.z * value);
    }

    /**
     Get new vector through the cross product of the vector and the given vector
     **/
    public Vec3 cross(Vec3 inputVec){
        return new Vec3(
                this.y * inputVec.z - inputVec.y * this.z,
                this.z * inputVec.x - inputVec.z * this.x,
                this.x * inputVec.y - inputVec.x * this.y
        );
    }

    /**
     Print values
     **/
    @Override
    public String toString(){
        return "( " + this.x + ", " + this.y + ", " + this.z + " )";
    }

	public Vec3 invert() {
        return new Vec3(
                1f / this.x,
                1f / this.y,
                1f / this.z
        );
	}
}
