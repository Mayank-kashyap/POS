package pos.model;

import lombok.Data;

@Data
public class ApiError {
    private Integer status;
    private String message;
    private String path;

    public ApiError(int status,String message,String path) {
        super();
        this.status=status;
        this.message=message;
        this.path=path;
    }
}
