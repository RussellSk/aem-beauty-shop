package com.exadel.core.services.impl;

import com.day.commons.datasource.poolservice.DataSourceNotFoundException;
import com.exadel.core.services.DatabaseService;
import com.exadel.core.services.LikesService;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component(service = LikesService.class, immediate = true)
public class LikesServiceImpl implements LikesService {

    private static final String DATA_SOURCE = "likes";
    private static final String GET_USER_PRODUCT_LIKES = "SELECT * FROM product_likes WHERE product=? AND user_ip=? LIMIT 1";
    private static final String INSERT_NEW_LIKE = "INSERT INTO product_likes (product, user_ip, like_type) VALUES (?, ?, ?)";
    private static final String UPDATE_LIKE = "UPDATE product_likes SET like_type=? WHERE product=? AND user_ip=?";
    private static final String LIKES_COUNT = "SELECT COUNT(*) as total FROM product_likes WHERE product=? AND like_type=? LIMIT 1";

    @Reference
    private DatabaseService databaseService;

    /**
     * Persist like event, the function creates new row in Database or change dislike to like
     * @param product name or id of product
     * @param user user's ip address
     */
    @Override
    public void incrementLike(String product, String user) {
        handleLike(product, user, true);
    }

    /**
     * Persist dislike event, the function creates new row in Database or change like to dislike
     * @param product name or id of product
     * @param user user's ip address
     */
    @Override
    public void incrementDislike(String product, String user) {
        handleLike(product, user, false);
    }

    /**
     * Get total amount of product likes
     * @param product name or id of product
     * @return int likes count
     */
    @Override
    public int getLikesCount(String product) {
        return handleCount(product, true);
    }

    /**
     * Get total amount of product dislikes
     * @param product name or id of product
     * @return int likes count
     */
    @Override
    public int getDislikesCount(String product) {
        return handleCount(product, false);
    }

    /**
     * Helper function handles data persisting and update logic
     * @param product name or id of product
     * @param user user's ip address
     * @param likeType can be like = true or dislike = false
     */
    private void handleLike(String product, String user, boolean likeType) {
        try (Connection connection = databaseService.getConnection(DATA_SOURCE)) {
            final PreparedStatement statement = connection.prepareStatement(GET_USER_PRODUCT_LIKES);
            statement.setString(1, product);
            statement.setString(2, user);
            try (ResultSet result = statement.executeQuery()) {
                // if user already give a like/dislike
                if (result.next()) {
                    // convert like to dislike or dislike to like
                    boolean currentLikeType = result.getBoolean(4);
                    if (likeType != currentLikeType) {
                        // update like type for given product
                        likeType = !currentLikeType;
                        final PreparedStatement updateStatement = connection.prepareStatement(UPDATE_LIKE);
                        updateStatement.setBoolean(1, likeType);
                        updateStatement.setString(2, product);
                        updateStatement.setString(3, user);
                        updateStatement.executeUpdate();
                    }
                } else {
                    // else insert new like/dislike
                    final PreparedStatement insertStatement = connection.prepareStatement(INSERT_NEW_LIKE);
                    insertStatement.setString(1, product);
                    insertStatement.setString(2, user);
                    insertStatement.setBoolean(3, likeType);
                    insertStatement.execute();
                }
            }
        } catch (SQLException | DataSourceNotFoundException exception) {
            log.error("handleLike: {}", getClass().getName(), exception);
        }
    }

    /**
     * Helper function handles Database request for total amount of likes
     * @param product name or id of product
     * @param likeType can be like = true or dislike = false
     * @return int likes count
     */
    private int handleCount(String product, boolean likeType) {
        try (Connection connection = databaseService.getConnection(DATA_SOURCE)) {
            final PreparedStatement countStatement = connection.prepareStatement(LIKES_COUNT);
            countStatement.setString(1, product);
            countStatement.setBoolean(2, likeType);
            try (ResultSet result = countStatement.executeQuery()) {
               if (result.next()) {
                   return result.getInt("total");
               }
            }
        } catch (SQLException | DataSourceNotFoundException exception) {
            log.error("handleCount: {}", getClass().getName(), exception);
        }

        return 0;
    }
}
