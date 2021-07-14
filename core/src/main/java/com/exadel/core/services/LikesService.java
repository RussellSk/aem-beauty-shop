package com.exadel.core.services;

public interface LikesService {
    void incrementLike(String product, String ip);
    void incrementDislike(String product, String ip);
    int getLikesCount(String product);
    int getDislikesCount(String product);
}
