package ru.surovtseva.hw4;

public class Endpoints {
    public static final String GET_ACCOUNT_REQUEST = "/account/{username}";
    public static final String DELETE_IMAGE_AUTH_REQUEST = "/account/{username}/image/{deleteHash}";
    public static final String DELETE_IMAGE_UNAUTH_REQUEST = "image/{deleteHash}";
    public static final String GET_UPD_IMAGE_REQUEST = "image/{imageHash}";
    public static final String POST_IMAGE_REQUEST = "image";

}
