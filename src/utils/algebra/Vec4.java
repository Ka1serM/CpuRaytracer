package utils.algebra;

public class Vec4 {
    public float x;
    public float y;
    public float z;
    public float w;

    /**
     Standard 4D constructor taking all values given
     **/
    public Vec4(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    /**
     Standard 4D constructor setting all values to 0
     **/
    public Vec4(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public void setValue(int index, float value){
        switch (index){
            case 0: this.x = value; break;
            case 1: this.y = value; break;
            case 2: this.z = value; break;
            case 3: this.w = value; break;
        }
    }

    /**
     Compare two vectors to check if they are equal
     **/
    public boolean equals(Vec4 inputVec){
        return (this.x == inputVec.x) && (this.y == inputVec.y) && (this.z == inputVec.z) && (this.w == inputVec.w);
    }

    /**
     Get normalized vector
     **/
    public Vec4 normalize(){
        double length = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
        return new Vec4( (float) (((double) this.x) / length), (float) (((double) this.y) / length), (float) (((double) this.z) / length), (float) (((double) this.w) / length));
    }

    /**
     Get length of vector
     **/
    public float length(){
        return (float) Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    /**
     Get sum of vector with the given vector
     **/
    public Vec4 add(Vec4 inputVec){
        return new Vec4(this.x + inputVec.x, this.y + inputVec.y, this.z + inputVec.z, this.w + inputVec.w);
    }

    /**
     Get difference between vector and the given vector
     **/
    public Vec4 sub(Vec4 inputVec){
        return new Vec4(this.x - inputVec.x, this.y - inputVec.y, this.z - inputVec.z, this.w - inputVec.w);
    }

    /**
     Get opposite vector
     **/
    public Vec4 negate(){
        return new Vec4(-this.x, -this.y, -this.z, -this.w);
    }

    /**
     Get scalar product of vector and given vector
     **/
    public float scalar(Vec4 inputVec){
        return this.x * inputVec.x + this.y * inputVec.y + this.z * inputVec.z + this.w * inputVec.w;
    }

    /**
     Get new vector with the given value multiplied to every component
     **/
    public Vec4 multScalar(float value){
        return new Vec4(this.x * value, this.y * value, this.z * value, this.w * value);
    }

    /**
     Get new vector through the cross product of the vector and the given vector
     **/
    public Vec4 cross(Vec4 inputVec){
        return new Vec4(
                this.y * inputVec.z - inputVec.y * this.z,
                this.z * inputVec.w - inputVec.z * this.w,
                this.w * inputVec.x - inputVec.w * this.x,
                this.x * inputVec.y - inputVec.y * this.x
        );
    }

    /**
     Print values
     **/
    @Override
    public String toString(){
        return "( " + this.x + ", " + this.y + ", " + this.z + ")";
    }
}
