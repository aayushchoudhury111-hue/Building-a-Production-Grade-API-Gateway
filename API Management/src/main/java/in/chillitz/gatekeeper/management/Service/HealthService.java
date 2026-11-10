package in.chillitz.gatekeeper.management.Service;
import lombok.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthService {

    private final JdbcTemplate jdbcTemplate; // service se db connect without repo

    public String getHealth(){
        String databaseStatus = "Down";
        try{
            Integer result = jdbcTemplate.queryForObject("Select 1", Integer.class); // select 1 querry return karega 1 db me jise check kar sakte hai ki kya 1 aa rha hai kya
            if(Integer.valueOf(1).equals(result)) {
                databaseStatus = "Up";
            }
        }
        catch(Exception e){
            System.out.println("MySQl Health check failed" + e.getMessage());
        }

        return databaseStatus;
    }
}
