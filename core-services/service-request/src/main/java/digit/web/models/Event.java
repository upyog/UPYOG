package digit.web.models;

import javax.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Validated
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class Event {
    @NotNull
    private String tenantId;

    private String id;

    @NotNull
    private String eventType;

    private String name;

    @NotNull
    private String description;

    @NotNull
    private String source;

    private EventDetails eventDetails;

    private Action actions;
}
