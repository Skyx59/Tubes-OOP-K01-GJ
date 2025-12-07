package util;


public class Vector {
    public double x;
    public double y;

    public Vector(){
        this(0,0);
    }

    public Vector(double x, double y){
        this.x = x;
        this.y = y;
    }

    public Vector copy(){
        return new Vector(x, y);
    }

    public Vector set(double x, double y){
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector add(Vector v){
        this.x += v.x;
        this.y += v.y;
        return this;
    }
    public Vector sub(Vector v){
        this.x -= v.x;
        this.y -= v.y;
        return this;
    }
    public Vector scale(double s){
        this.x *= s;
        this.y *= s;
        return this;
    }

    public double distance(Vector v){
        double dx = this.x - v.x;
        double dy = this.y - v.y;
        return Math.sqrt(dx*dx + dy*dy);
    }

    public Vector lerp(Vector target, double t){
        // linear interpolation: this -> target by t in [0,1]
        this.x = this.x + (target.x - this.x) * t;
        this.y = this.y + (target.y - this.y) * t;
        return this;
    }

    public TilePos toTilePos(double tileSize){
        int col = (int) Math.floor(this.x / tileSize);
        int row = (int) Math.floor(this.y / tileSize);
        return new TilePos(col, row);
    }

    @Override
    public String toString(){
        return "Vector(" + x + "," + y + ")";
    }
}

