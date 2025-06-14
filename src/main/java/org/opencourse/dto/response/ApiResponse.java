package org.opencourse.dto.response;

/**
 * Response object for API calls.
 * 
 * @param <T> The type of the data returned in the response.
 * @author LJX
 * @author !EEExp3rt
 */
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    /**
     * Default constructor.
     */
    public ApiResponse() {
    }

    /**
     * Constructor.
     * 
     * @param success If the API call was successful.
     * @param message The message to return.
     */
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Constructor.
     * 
     * @param success If the API call was successful.
     * @param message The message to return.
     * @param data    The data object to return.
     */
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * Creates a success response with no data.
     * 
     * @param <T>     The type of the data returned in the response.
     * @param message The message to return.
     * @return        A success response object.
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message);
    }

    /**
     * Creates a success response with response data.
     * 
     * @param <T>     The type of the data returned in the response.
     * @param message The message to return.
     * @param data    The data object to return.
     * @return        A success response object.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Creates an error response with no data.
     * 
     * @param <T>     The type of the data returned in the response.
     * @param message The message to return.
     * @return        An error response object.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message);
    }

    /**
     * Creates an error response with response data.
     * 
     * @param <T>     The type of the data returned in the response.
     * @param message The message to return.
     * @param data    The data object to return.
     * @return        An error response object.
     */
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(false, message, data);
    }

    // Getters and Setters

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
