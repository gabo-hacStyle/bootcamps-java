package gabs.bootcamps.domain.event;


import lombok.Value;

@Value
public class BootcampCreatedEvent {
    Long bootcampId;
}
