package ru.surovtseva.hw4.dto.negative;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.surovtseva.hw4.dto.CommonResponse;

public class PostImageNegativeResponse extends CommonResponse <PostImageNegativeResponse.ImageData> {
    
    @Data
    public static class ImageData {

        @JsonProperty("error")
        private String error;
        @JsonProperty("request")
        private String request;
        @JsonProperty("method")
        private String method;
    }
}
