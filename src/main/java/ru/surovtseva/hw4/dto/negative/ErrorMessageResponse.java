package ru.surovtseva.hw4.dto.negative;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.surovtseva.hw4.dto.CommonResponse;

import java.util.ArrayList;
import java.util.List;

public class ErrorMessageResponse extends CommonResponse<ErrorMessageResponse.ImageData> {

    @Data
    public static class ImageData {

        @JsonProperty("error")
        private Error error;
        @JsonProperty("request")
        private String request;
        @JsonProperty("method")
        private String method;

        @Data
        public static class Error {
            @JsonProperty("code")
            private Integer code;
            @JsonProperty("message")
            private String message;
            @JsonProperty("type")
            private String type;
            @JsonProperty("exception")
            private List<String> exception = new ArrayList<>();
        }
    }
}
