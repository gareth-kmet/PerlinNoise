package mainPath;
import util.Vector2f;

public class MainSystemFile {
    public static void main(String[] args) {
        Vector2f a = Vector2f.e1();
        Vector2f b = Vector2f.e2();
        
        System.out.println(Vector2f.add(a, b));
        System.out.println(Vector2f.scale(a, 2));
        System.out.println(Vector2f.dot(a, b));
    }
}
