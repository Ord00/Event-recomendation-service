package event.rec.service.responses;

public record JwtResponse(String token, ErrorResponse errorResponse) {

    public static JwtResponse success(String token) {
        return new JwtResponse(token, null);
    }

    public static JwtResponse error(String message, int code) {
        return new JwtResponse(null, new ErrorResponse(message, code));
    }

    public boolean isSuccess() {
        return errorResponse == null;
    }
}