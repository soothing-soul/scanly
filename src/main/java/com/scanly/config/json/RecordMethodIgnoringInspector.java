package com.scanly.config.json;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import java.util.Arrays;

/**
 * Custom Jackson Annotation Introspector designed to refine the serialization behavior of Java Records.
 * <p>
 * By default, Jackson might attempt to serialize any public method that resembles a getter, even
 * in a Java Record. This component ensures that only the canonical accessor methods—those directly
 * corresponding to the Record's components—are considered for serialization.
 * <p>
 * This prevents "leaky" JSON structures where helper methods or derived property methods within
 * a Record class are accidentally included in the API response.
 */
public class RecordMethodIgnoringInspector extends JacksonAnnotationIntrospector {

    /**
     * Determines if a specific member should be ignored by Jackson during the introspection process.
     * <p>
     * For Java Records, this method identifies {@link AnnotatedMethod} members and checks if the
     * method name matches one of the Record's defined components. If the method is not a
     * canonical accessor (i.e., a custom helper method), it is marked to be ignored.
     * </p>
     *
     * @param member The class member (field, method, etc.) being introspected.
     * @return {@code true} if the member is a non-component method of a Record;
     * otherwise, defers to {@link JacksonAnnotationIntrospector#hasIgnoreMarker(AnnotatedMember)}.
     */
    @Override
    public boolean hasIgnoreMarker(AnnotatedMember member) {
        if (member instanceof AnnotatedMethod method) {
            Class<?> clazz = method.getDeclaringClass();

            // Apply special logic only if the declaring class is a Java Record
            if (clazz.isRecord()) {
                boolean isRecordField = Arrays.stream(clazz.getRecordComponents())
                        .anyMatch(recordComponent ->
                                          recordComponent.getName().equals(method.getName()));

                // If the method exists in the Record but isn't a component accessor, ignore it.
                if (!isRecordField) {
                    return true;
                }
            }
        }
        return super.hasIgnoreMarker(member);
    }
}