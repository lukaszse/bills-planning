package pl.com.seremak.billsplaning.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    private String preferredUsername;
    private String name;
    private String givenName;
    private String familyName;
}
