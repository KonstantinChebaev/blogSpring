package main.domain.globallSettings;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GSettingsDto {
    @JsonProperty("MULTIUSER_MODE")
    private Boolean multiuserMode;

    @JsonProperty("POST_PREMODERATION")
    private Boolean postPremoderation;

    @JsonProperty("STATISTICS_IS_PUBLIC")
    private Boolean statisticsIsPublic;
}
