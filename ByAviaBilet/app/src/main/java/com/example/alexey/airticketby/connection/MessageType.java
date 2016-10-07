package com.example.alexey.airticketby.connection;

/**
 * Created by Alexey on 07.09.2016.
 */
public enum MessageType {
    // Connection
    CONNECT_REQUEST,
    USER_ANDROID,
    USER_ADMIN,
    // authorization
    USER_AUTHORIZATION,
    USER_ACCEPTED,
    USER_NOT_FOUNDED,
    USER_ALREADY_WORK,
    // registration
    USER_REGISTRATION,
    USER_REGISTERED,
    USER_ALREADY_EXIST,
    // Communication
    DATA,
    RAY_LIST,
    GET_TICKETS_LIST,
    MY_TICKETS_LIST,
    // Book places
    BOOK_PLACES_TRY,
    BOOK_PLACES_OK,
    BOOK_PLACES_CANCEL,
    // ready Book place
    BOOK_NUMBER_PLACE_TRY,
    BOOK_NUMBER_PLACE_OK,
    BOOK_NUMBER_PLACE_CANCEL,
    BOOK_NUMBER_PLACE_ERROR,
    // Buy places
    BUY_PLACES_TRY,
    BUY_PLACES_OK
}
