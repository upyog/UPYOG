package digit.web.models;

import java.util.List;

import javax.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;


@Validated
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class Action {
    private String tenantId;

    private String id;

    private String eventId;

    @NotNull
    private List<ActionItem> actionUrls;
}
