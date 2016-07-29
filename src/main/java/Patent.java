import java.util.List;
import java.util.Map;

/**
 * Created by leeweisberger on 7/8/16.
 */
public class Patent {
    private Map<String,String> inventors;
    private List<String> inventors2;
    private List<String> claims;
    private String number;

    public Patent(String number, Map<String,String> inventors, List<String> claims){
        this.inventors=inventors;
        this.claims=claims;
        this.number=number;
    }

    public Patent(String number, List<String> inventors, List<String> claims){
        this.inventors2=inventors;
        this.claims=claims;
        this.number=number;
    }

    public List<String> getClaims() {
        return claims;
    }
    public List<String> getInventors2(){return inventors2;}
    public String getNumber(){
        return number;
    }

    public Map<String,String> getInventors() {
        return inventors;
    }

}
