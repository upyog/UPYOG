package org.upyog.pgrai.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Validator for validating additional details based on a character size constraint.
 */
public class AdditionalDetailValidator  implements ConstraintValidator<CharacterConstraint, Object> {


    private Integer size;


    /**
     * Initializes the validator with the size constraint.
     *
     * @param additionalDetails The character constraint annotation containing the size.
     */
    @Override
    public void initialize(CharacterConstraint additionalDetails) {
        size = additionalDetails.size();
    }

    /**
     * Validates the given additional details object.
     *
     * @param additionalDetails The object to validate.
     * @param cxt               The context in which the constraint is evaluated.
     * @return true if the object is valid or null, false otherwise.
     */
    @Override
    public boolean isValid(Object additionalDetails, ConstraintValidatorContext cxt) {

        if(additionalDetails==null)
            return true;

        if(additionalDetails.toString().length() > size)
            return false;
        else
            return true;
    }

}
