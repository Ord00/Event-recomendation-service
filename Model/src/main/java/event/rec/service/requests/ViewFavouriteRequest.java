package event.rec.service.requests;

public record ViewFavouriteRequest(Long userId,
                                   Long page,
                                   Long size) {}
