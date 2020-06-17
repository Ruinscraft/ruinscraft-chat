package com.ruinscraft.chat.core;

public final class ChatConfig {

    /*
     *  Storage
     */
    public String storageType = "mysql";
    public String storageMySQLHost = "localhost";
    public int storageMySQLPort = 3306;
    public String storageMySQLDatabase = "chat";
    public String storageMySQLUsername = "root";
    public String storageMySQLPassword = "password";
    public String filtersWebpurifyApiKey = "abc123";

    /*
     *  Message broker/pubsub
     */
    public String messageBrokerType = "redis";
    public String messageBrokerRedisHost = "localhost";
    public int messageBrokerRedisPort = 6379;

}
