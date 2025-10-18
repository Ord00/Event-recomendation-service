package event.rec.service.requests;

import java.awt.*;
import java.time.Duration;
import java.util.List;

public record ViewEventNearbyRequest(List<Long> categoryIds,
                                     Duration interval,
                                     Point location,
                                     Long radius) {}
