package org.egov.user.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActionResponse {

    List<Action> actions;

    public List<org.egov.user.domain.model.Action> toDomainActions() {
        return actions.stream()
                .map(Action::toDomain)
                .collect(Collectors.toList());
    }
}
