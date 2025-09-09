package org.upyog.pgrai.web.models.pgrV1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the action history associated with a service in the system.
 * This class encapsulates a list of actions performed on a service, where each action
 * is represented by an {@link ActionInfo} object.
 *
 * Key features:
 * - Maintains a list of actions performed on a service.
 * - Provides methods to add actions and retrieve the list of actions.
 *
 * This class is part of the PGR V1 module.
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-03-23T08:00:37.661Z")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionHistory {
  @JsonProperty("actions")
  @Valid
  private List<ActionInfo> actions = null;

  /**
   * Sets the list of actions and returns the updated ActionHistory object.
   *
   * @param actions the list of actions to set.
   * @return the updated ActionHistory object.
   */
  public ActionHistory actions(List<ActionInfo> actions) {
    this.actions = actions;
    return this;
  }

  /**
   * Adds an action to the list of actions and returns the updated ActionHistory object.
   *
   * @param actionsItem the action to add.
   * @return the updated ActionHistory object.
   */
  public ActionHistory addActionsItem(ActionInfo actionsItem) {
    if (this.actions == null) {
      this.actions = new ArrayList<>();
    }
    this.actions.add(actionsItem);
    return this;
  }

  /**
   * Retrieves the list of actions.
   *
   * @return the list of actions.
   */
  @Valid
  public List<ActionInfo> getActions() {
    return actions;
  }

  /**
   * Sets the list of actions.
   *
   * @param actions the list of actions to set.
   */
  public void setActions(List<ActionInfo> actions) {
    this.actions = actions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ActionHistory actionHistory = (ActionHistory) o;
    return Objects.equals(this.actions, actionHistory.actions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(actions);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ActionHistory {\n");
    sb.append("    actions: ").append(toIndentedString(actions)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Converts the given object to a string with each line indented by 4 spaces
   * (except the first line).
   *
   * @param o the object to convert to a string.
   * @return the string representation of the object.
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

