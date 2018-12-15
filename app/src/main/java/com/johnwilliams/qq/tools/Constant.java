package com.johnwilliams.qq.tools;

public class Constant {
    public final static String STU_NUM_REGEX = "^[0-9]{10}$";
    public final static String IPV4_REGEX = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
    public final static String MY_STUNUM_EXTRA = "my_stunum";
    public final static String FRIEND_STUNUM_EXTRA = "friend_stunum";
    public final static String FRIEND_NAME_EXTRA = "friend_name";
    public final static int NEW_MESSAGE = 0;
    public final static int CLEAR_CHAT = 1;
    public final static int CLEAR_CONTACT = 2;
    public final static int REMOVE_CHAT = 3;
    public final static int REMOVE_CONTACT = 4;
    public final static int NEW_CHAT = 5;
    public final static int UPDATE_CONTACT = 6;
    public final static int DO_NOTHING = -1;
}
