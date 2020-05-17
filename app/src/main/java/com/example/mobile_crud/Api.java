package com.example.mobile_crud;

//https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/#Hero-Model-Class

public class Api {
    private static final String ROOT_URL = "http://192.168.0.119/MobileCRUD/v1/Api.php?apicall=";

    public static final String URL_CREATE_PERSON = ROOT_URL + "createPerson";
    public static final String URL_READ_PERSONS  = ROOT_URL + "getPersons";
    public static final String URL_UPDATE_PERSON = ROOT_URL + "updatePerson";
    public static final String URL_DELETE_PERSON = ROOT_URL + "deletePerson&id=";
}
