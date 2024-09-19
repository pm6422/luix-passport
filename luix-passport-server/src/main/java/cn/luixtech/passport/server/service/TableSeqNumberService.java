package cn.luixtech.passport.server.service;

public interface TableSeqNumberService {

    void init();

    long getNextSeqNumber(String table);
}
