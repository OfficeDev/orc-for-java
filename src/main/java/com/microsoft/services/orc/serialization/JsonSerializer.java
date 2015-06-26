package com.microsoft.services.orc.serialization;

import java.util.List;
import java.util.Map;

/**
 * The interface Json serializer.
 */
public interface JsonSerializer {
    /**
     * Serialize string.
     *
     * @param objectToSerialize the object to serialize
     * @return the string
     */
    String serialize(Object objectToSerialize);

    /**
     * Json object from json map.
     *
     * @param map the map
     * @return the string
     */
    String jsonObjectFromJsonMap(Map<String, String> map);

    /**
     * Deserialize e.
     *
     * @param <E>  the type parameter
     * @param serializedObject the serialized object
     * @param clazz the clazz
     * @return the e
     */
    <E> E deserialize(String serializedObject, Class<E> clazz) throws Throwable;

    /**
     * Deserialize list.
     *
     * @param <E>  the type parameter
     * @param serializedList the serialized list
     * @param clazz the clazz
     * @return the list
     */
    <E> List<E> deserializeList(String serializedList, Class<E> clazz) throws Throwable;
}
