package com.stardevllc.starsql.api.model;

/**
 * See the SQL Documentation for your chosen server for what these do. <br>
 * These are the allowed types for MySQL
 */
public enum FKAction {
    NO_ACTION, CASCADE, RESTRICT, SET_NULL
}