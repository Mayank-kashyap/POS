package pos.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InfoData {

    private String message;

    public InfoData() {
        message = "Activity time: " + LocalDateTime.now().toString();
    }
}
