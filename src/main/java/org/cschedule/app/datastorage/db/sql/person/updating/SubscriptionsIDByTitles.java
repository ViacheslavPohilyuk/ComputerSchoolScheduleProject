package org.cschedule.app.datastorage.db.sql.person.updating;

import org.cschedule.app.datastorage.db.sql.SQLReadEntity;
import org.cschedule.app.datastorage.db.sql.RowsTableCount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by mac on 16.02.17.
 */
public class SubscriptionsIDByTitles implements SQLReadEntity<int[]> {
    private String[] subscriptions;

    private Connection conn;

    SubscriptionsIDByTitles(String[] subscriptions, Connection conn) throws SQLException{
        this.subscriptions = subscriptions;
        this.conn = conn;

        conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
    }

    @Override
    public int[] executeRead() {
        ResultSet resultSet = null;  // dummy ResultSet
        return extractResult(resultSet);
    }

    @Override
    public int[] extractResult(ResultSet resultSet) {
        int[] result = null;
        RowsTableCount rowscount = new RowsTableCount();

        try (PreparedStatement preparedSubsID = conn.prepareStatement(sql())) {
            setSubsParameters(preparedSubsID);
            resultSet = preparedSubsID.executeQuery();

            int i = 0;
            int[] subsId = new int[rowscount.rowsCount(resultSet)];
            while (resultSet.next()) {
                subsId[i] = resultSet.getInt("id");
                i++;
            }
            result = subsId;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public String sql() {
        String statement = "SELECT id FROM courses";
        statement += sqlQueryConstructor();
        return statement;
    }

    private String sqlQueryConstructor() {
        int sub_count = subscriptions.length;
        String selectStatement = " WHERE title = ?";
        if(sub_count > 1) {
            for(int i = 0; i < (sub_count - 1); i++) {
                selectStatement += " OR title = ?";
            }
        }
        return  selectStatement;
    }

    private void setSubsParameters(PreparedStatement statement) throws SQLException {
        int i = 1;
        for(String sub : subscriptions) {
            statement.setString(i, sub);
            i++;
        }
    }
}