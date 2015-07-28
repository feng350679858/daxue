package com.jingcai.apps.aizhuan.service.local;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jingcai.apps.aizhuan.persistence.UserSubject;
import com.jingcai.apps.aizhuan.persistence.db.Database;
import com.jingcai.apps.aizhuan.persistence.vo.ContactInfo;
import com.jingcai.apps.aizhuan.service.AzService;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Request;
import com.jingcai.apps.aizhuan.service.business.stu.stu02.Stu02Response;
import com.jingcai.apps.aizhuan.util.AzException;
import com.jingcai.apps.aizhuan.util.AzExecutor;
import com.jingcai.apps.aizhuan.util.HXHelper;
import com.jingcai.apps.aizhuan.util.StringUtil;

import java.util.List;

/**
 * 执行更新联系人信息的服务
 * Created by Json Ding on 2015/7/28.
 */
public class UpdateContactInfoService extends IntentService {

    private static final String TAG = "UpdateContactInfo";

    private AzService azService;
    private Database mDatabase;

    public UpdateContactInfoService() {
        super("com.jingcai.apps.aizhuan.service.local.UpdateContactInfoService");
        azService = new AzService(getApplicationContext());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Context applicationContext = getApplicationContext();
        mDatabase = new Database(applicationContext);

        String studentid = UserSubject.getStudentid();
        if (StringUtil.isEmpty(studentid)) {
            return;
        }

        final HXHelper instance = HXHelper.getInstance();
        final List<ContactInfo> contactInfos = mDatabase.fetchContactsInfoByStudentId(studentid);
        instance.setContactList(contactInfos);

        for (ContactInfo contact : contactInfos) {
            //开启一个线程从服务器获取最新的学生信息，并更新到数据库中
            getAndUpdateStudentInfo(contact);
        }
    }

    private void getAndUpdateStudentInfo(final ContactInfo contact) {

        new AzExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Stu02Request req = new Stu02Request();
                final Stu02Request.Student stu = req.new Student();
                stu.setStudentid(contact.getStudentid());
                req.setStudent(stu);
                azService.doTrans(req, Stu02Response.class, new AzService.Callback<Stu02Response>() {
                    @Override
                    public void success(Stu02Response response) {
                        Stu02Response.Stu02Body stu02Body = response.getBody();
                        final Stu02Response.Stu02Body.Student student = stu02Body.getStudent();
                        contact.setLogourl(student.getLogopath());
                        contact.setName(student.getName());
                        mDatabase.updateContactInfo(UserSubject.getStudentid(),contact);//数据库更新
                    }

                    @Override
                    public void fail(AzException e) {
                        Log.e(TAG, "fetch student info in service failed.message:" + e.getMessage());
                    }
                });
            }
        });
    }


}
