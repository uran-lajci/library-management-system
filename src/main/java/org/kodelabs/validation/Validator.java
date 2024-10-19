package org.kodelabs.validation;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.kodelabs.entities.exceptions.BadRequestException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class Validator {

    @Inject
    javax.validation.Validator validator;

    public <T> Uni<T> entityValidation(T t) {
        return Uni.createFrom().item(t).onItem().invoke(t1 -> {
            Set<ConstraintViolation<T>> violations = validator.validate(t);
            if (!violations.isEmpty()) {
                String mistake = failureMessage((violations));
                throw new BadRequestException(mistake);
            }
        });
    }

    public <T> Uni<List<T>> entityListValidation(List<T> t) {
        return Multi.createFrom().items(t::stream).onItem().invoke(t5 -> {
            Set<ConstraintViolation<T>> violations = validator.validate(t5);
            if (!violations.isEmpty()) {
                String failureMessage = failureMessage(violations);
                throw new BadRequestException(failureMessage);
            }
        }).onCompletion().continueWith().collect().asList();
    }

    public <T> Uni<List<T>> listOfObjectsValidation(List<T> t) {
        ListWrapper<T> listWrapper = new ListWrapper<>();
        listWrapper.setList(t);

        return entityValidation(listWrapper)
                .map(ListWrapper::getList);
    }

    public static <T> String failureMessage(Set<ConstraintViolation<T>> violations) {
        String failedFiels = violations.stream()
                .map(cv -> cv.getPropertyPath().toString())
                .collect(Collectors.joining(", "));

        String messageFailureForFields = violations.stream()
                .map(cv -> cv.getMessage())
                .collect(Collectors.joining(", "));

        String failureMessage = "";
        int numberOfFailures = failedFiels.split(", ").length;

        for (int i = 0; i < numberOfFailures; i++) {
            failureMessage += failedFiels.split(", ")[i] + ": " +
                    messageFailureForFields.split(", ")[i] + ".   ";
        }
        return failureMessage;
    }
}