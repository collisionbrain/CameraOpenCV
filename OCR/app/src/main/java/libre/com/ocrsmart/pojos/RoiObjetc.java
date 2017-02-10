package libre.com.ocrsmart.pojos;

import java.io.Serializable;

/**
 * Created by ProBook on 17/10/2016.
 */
public class RoiObjetc implements Serializable {
    public float x;
    public float y;
    public float width;
    public float height;

    public void reset(){
        x=0;
        y=0;
        width=0;
        height=0;

    }
}
