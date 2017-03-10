package florian.cavasin.channelmessaging;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class UserDatasource {
    // Database fields
    private SQLiteDatabase database;
    private FriendsDB dbHelper;
    private String[] allColumns = { FriendsDB.KEY_ID,FriendsDB.KEY_NAME, FriendsDB.KEY_IMAGE };

    public UserDatasource(Context context) {
        dbHelper = new FriendsDB(context);
    }
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }
    public Friend createFriend(String userID, String username,String imageurl) {
        ContentValues values = new ContentValues();
        values.put(FriendsDB.KEY_NAME, username);
        values.put(FriendsDB.KEY_IMAGE, imageurl);
        values.put(FriendsDB.KEY_ID, userID);
        database.insert(FriendsDB.FRIEND_TABLE_NAME, null, values);
        Cursor cursor = database.query(FriendsDB.FRIEND_TABLE_NAME, allColumns, FriendsDB.KEY_ID + " = \"" + userID +"\"", null, null, null, null);
        cursor.moveToFirst();
        Friend newFriend = cursorToFriend(cursor);
        cursor.close();
        return newFriend;
    }
    public List<Friend> getAllFriends() {
        List<Friend> lesFriends = new ArrayList<Friend>();
        Cursor cursor = database.query(FriendsDB.FRIEND_TABLE_NAME, allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Friend unFriend = cursorToFriend(cursor);
            lesFriends.add(unFriend);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return lesFriends;
    }
    private Friend cursorToFriend(Cursor cursor) {
        Friend ami = new Friend();
        String result = cursor.getString(0);
        ami.setUserID(cursor.getInt(0));
        ami.setUsername(cursor.getString(1));
        ami.setImageUrl(cursor.getString(2));
        return ami;
    }
    public void deleteHomme(Friend unFriend) {
        int id = unFriend.getUserID();
        database.delete(FriendsDB.FRIEND_TABLE_NAME, FriendsDB.KEY_ID + " = \"" + Integer.toString(id)+"\"", null);
    }
}