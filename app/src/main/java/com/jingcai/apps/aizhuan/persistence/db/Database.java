package com.jingcai.apps.aizhuan.persistence.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jingcai.apps.aizhuan.persistence.vo.ContactInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库封装类
 * Created by Json Ding on 2015/7/28.
 */
public class Database {
	private static final String TAG = "Database";
	private static final int DATABASE_VERSION = 1;

	//contact info
	private final String CONTACT_INFO_TABLE = "contact_info";
	private final String CONTACT_INFO_ID = "contact_info_id";  //主键
	private final String STUDENT_ID = "student_id";     //当前登录学生id
	private final String RECEIVER_ID = "receiver_id";   //对方id
	private final String NAME = "name";                 //对方姓名
	private final String LOGO_URL = "logo_url";         //对方头像地址

	private final String CONTACT_INFO_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + CONTACT_INFO_TABLE
			+ " (" + CONTACT_INFO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , "
            + STUDENT_ID + "TEXT NOT NULL ,"
            + RECEIVER_ID + "TEXT NOT NULL ,"
			+ NAME + " TEXT NOT NULL , "
			+ LOGO_URL + " TEXT NOT NULL)";

	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	private static final String DATABASE_NAME = "dalegexue_db";

	private final Context mContext;

	public Database(Context context) {
		this.mContext = context;
	}

//    /**
//     * context传入applicationContext {@see Activity#getApplicationContext()}
//     * @param context applicationContext
//     */
//    public static Database getInstance(Context context){
//        if(null == mDatabase){
//            synchronized (Database.class){
//                if(null == mDatabase){
//                    mDatabase = new Database(context);
//                }
//            }
//        }
//        return mDatabase;
//    }

	private class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CONTACT_INFO_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            switch (newVersion){
                case 2:
                    //进行新字段或新表的创建，数据迁移
                    break;
            }
		}
	}

	public Database open() throws SQLException {
		mDbHelper = new DatabaseHelper(mContext);
		try {
			mDb = mDbHelper.getWritableDatabase();
		} catch (Exception e) {
			mDb = mDbHelper.getReadableDatabase();
		}
		return this;
	}

	public void close() {
		mDbHelper.close();  //It also close db
	}

    /**
     * 更具当前登录的studentId获取联系人信息列表
     * @param studentId studentId
     * @return 联系人信息列表
     */
	public List<ContactInfo> fetchContactsInfoByStudentId(String studentId) {
		String sql = "SELECT * FROM " + CONTACT_INFO_TABLE + " WHERE " + STUDENT_ID
				+ " = ? ORDER BY "+CONTACT_INFO_ID;
        final Cursor cursor = mDb.rawQuery(sql, new String[]{studentId});
        List<ContactInfo> contacts = new ArrayList<>();

        while (cursor.moveToNext()){
            String receiverId = cursor.getString(cursor.getColumnIndex(STUDENT_ID));
            String name = cursor.getString(cursor.getColumnIndex(NAME));
            String logoUrl = cursor.getString(cursor.getColumnIndex(LOGO_URL));

            ContactInfo contactInfo = new ContactInfo(receiverId,name,logoUrl);
            contacts.add(contactInfo);
        }
        cursor.close();
        Log.d(TAG,"find "+contacts.size()+" contacts info by student id:"+studentId);
        return contacts;
    }

    /**
     * 插入联系人信息
     * @param studentId 当前登录学生的id
     * @param contactInfo 插入的对象
     * @return -1 is failed,other is row id
     */
	public long insertContactInfo(String studentId,ContactInfo contactInfo) {
		ContentValues initialValues = new ContentValues();
        initialValues.put(STUDENT_ID, studentId);
        initialValues.put(RECEIVER_ID, contactInfo.getStudentid());
        initialValues.put(NAME, contactInfo.getName());
        initialValues.put(LOGO_URL, contactInfo.getLogourl());
		return mDb.insert(CONTACT_INFO_TABLE, null, initialValues);
	}

    /**
     * 更新联系人信息，根据receiverId和当前登录的studentId
     * @param studentId 当前登录学生的id
     * @param contactInfo 更新的对象
     * @return row effected
     */
	public int updateContactInfo(String studentId,ContactInfo contactInfo){
        ContentValues updateValues = new ContentValues();
        updateValues.put(NAME, contactInfo.getName());
        updateValues.put(LOGO_URL, contactInfo.getLogourl());
        return mDb.update(CONTACT_INFO_TABLE,updateValues,RECEIVER_ID +"=? and "+STUDENT_ID+"=?",new String[]{contactInfo.getStudentid(),studentId});
    }

    /**
     * 删除联系人信息，根据receiverId和当前登录的studentId
     * @param studentId 当前登录学生的id
     * @param contactInfo 删除的对象
     * @return
     */
    public int deleteContactInfo(String studentId,ContactInfo contactInfo){
        return mDb.delete(CONTACT_INFO_TABLE,RECEIVER_ID +"=? and "+STUDENT_ID+"=?",new String[]{contactInfo.getStudentid(),studentId});
    }

}
