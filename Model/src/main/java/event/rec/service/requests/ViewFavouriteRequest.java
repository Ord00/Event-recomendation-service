package event.rec.service.requests;

public record ViewFavouriteRequest(Long userId,
                                   Integer page,
                                   Integer size) {}
