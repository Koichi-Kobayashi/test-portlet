/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2011 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.aimluck.eip.test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.services.TurbineServices;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.eip.cayenne.om.portlet.EipTTest;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipGroup;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.common.ALPermissionException;
import com.aimluck.eip.eventlog.action.ALActionEventlogConstants;
import com.aimluck.eip.mail.ALAdminMailContext;
import com.aimluck.eip.mail.ALAdminMailMessage;
import com.aimluck.eip.mail.ALMailService;
import com.aimluck.eip.mail.util.ALEipUserAddr;
import com.aimluck.eip.mail.util.ALMailUtils;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.services.accessctl.ALAccessControlConstants;
import com.aimluck.eip.services.accessctl.ALAccessControlFactoryService;
import com.aimluck.eip.services.accessctl.ALAccessControlHandler;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.orgutils.ALOrgUtilsService;
import com.aimluck.eip.timeline.util.TimelineUtils;
import com.aimluck.eip.test.util.TestUtils;
import com.aimluck.eip.util.ALEipUtils;
import com.aimluck.eip.util.ALLocalizationUtils;

/**
 * Testのフォームデータを管理するクラスです。 <BR>
 *
 */
public class TestFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TestFormData.class.getName());

  /** タイトル */
  private ALStringField test_name;


  /** 担当者ID */
  private ALNumberField user_id;


  /** メモ */
  private ALStringField note;

  /** URL */
  private ALStringField url;

  /** ドメイン */
  private ALStringField domain;


  /** 現在の年 */
  private int currentYear;


  /** ログインユーザーのID * */
  private int login_user_id;

  /** ACL用の変数 * */
  private String aclPortletFeature;

  private ArrayList<ALEipGroup> myGroupList;

  /** 他人のTest編集権限を持つかどうか */
  private boolean hasAclInsertTestOther;

  /**
   *
   * @param action
   * @param rundata
   * @param context
   *
   *
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    login_user_id = ALEipUtils.getUserId(rundata);

    List<ALEipGroup> myGroups = ALEipUtils.getMyGroups(rundata);
    setMyGroupList(new ArrayList<ALEipGroup>());
    for (ALEipGroup group : myGroups) {
      getMyGroupList().add(group);
    }

    String testId = rundata.getParameters().getString(ALEipConstants.ENTITY_ID);
    String userId = rundata.getParameters().getString("user_id");
    if (testId == null || testId.equals("new")) {
      if (userId != null && !userId.equals(String.valueOf(login_user_id))) {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER;
      } else {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF;
      }
    } else {
      EipTTest test = TestUtils.getEipTTest(rundata, context, true);
      if ((test != null && test.getTurbineUser().getUserId() != login_user_id)) {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER;
      } else {
        aclPortletFeature =
          ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_SELF;
      }
    }

    // アクセス権(他人へのTest追加)
    ALAccessControlFactoryService aclservice =
      (ALAccessControlFactoryService) ((TurbineServices) TurbineServices
        .getInstance()).getService(ALAccessControlFactoryService.SERVICE_NAME);
    ALAccessControlHandler aclhandler = aclservice.getAccessControlHandler();
    hasAclInsertTestOther =
      aclhandler.hasAuthority(
        login_user_id,
        ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER,
        ALAccessControlConstants.VALUE_ACL_INSERT);

  }

  /**
   * 各フィールドを初期化します。 <BR>
   */
  @Override
  public void initField() {
    // タイトル
    test_name = new ALStringField();
    test_name.setFieldName(ALLocalizationUtils
      .getl10n("TODO_SETFIELDNAME_TITLE"));
    test_name.setTrim(true);

    // 担当者ID
    user_id = new ALNumberField();
    user_id.setFieldName(ALLocalizationUtils
      .getl10n("TODO_SETFIELDNAME_PREPARED"));

    // メモ
    note = new ALStringField();
    note.setFieldName(ALLocalizationUtils.getl10n("TODO_SETFIELDNAME_MEMO"));
    note.setTrim(false);

    // URL
    url = new ALStringField();
    url.setFieldName(ALLocalizationUtils.getl10n("TODO_SETFIELDNAME_URL"));
    url.setTrim(false);

    // ドメイン
    domain = new ALStringField();
    domain.setFieldName(ALLocalizationUtils.getl10n("TODO_SETFIELDNAME_DOMAIN"));
    domain.setTrim(false);
  }


  /**
   * Testの各フィールドに対する制約条件を設定します。 <BR>
   */
  @Override
  protected void setValidator() {
    // Tタイトル必須項目
    test_name.setNotNull(true);
    // タイトルの文字数制限
    test_name.limitMaxLength(50);
    // メモの文字数制限
    note.limitMaxLength(1000);
    // URLの文字数制限
    url.limitMaxLength(2000);
    // URLの文字タイプ制限
    url.setCharacterType(ALStringField.TYPE_ASCII );
    // 担当者ID必須項目
    user_id.setNotNull(true);


  }

  /**
   * Testのフォームに入力されたデータの妥当性検証を行います。 <BR>
   *
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   *
   */
  @Override
  protected boolean validate(List<String> msgList) {

    try {

    } catch (Exception ex) {
      logger.error("test", ex);
      return false;
    }

    boolean isStartDate = false;
    // タイトル
    test_name.validate(msgList);
    // メモ
    note.validate(msgList);
    // URL
    url.validate(msgList);

    return (msgList.size() == 0);

  }

  /**
   * Testをデータベースから読み出します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    String date1 = null;
    try {
      // オブジェクトモデルを取得
      EipTTest test = TestUtils.getEipTTest(rundata, context, false);
      if (test == null) {
        return false;
      }
      // タイトル
      test_name.setValue(test.getTestName());
      // メモ
      note.setValue(test.getNote());
      // URL
      url.setValue(test.getUrl());


      // 担当者
      user_id.setValue(test.getTurbineUser().getUserId());

    } catch (Exception ex) {
      logger.error("test", ex);
      return false;
    }
    return true;
  }

  /**
   * Testをデータベースから削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTTest test = TestUtils.getEipTTest(rundata, context, false);
      if (test == null) {
        return false;
      }

      // entityIdの取得
      int entityId = test.getTestId();
      // タイトルの取得
      String testName = test.getTestName();

      // Testを削除
      Database.delete(test);
      Database.commit();

      TimelineUtils.deleteTimelineActivity(rundata, context, "test", test
        .getTestId()
        .toString());

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        entityId,
        ALEventlogConstants.PORTLET_TYPE_TODO,
        testName);

    } catch (Throwable t) {
      Database.rollback();
      logger.error("[TestFormData]", t);
      return false;
    }
    return true;
  }

  /**
   * Testをデータベースに格納します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {

      // 新規オブジェクトモデル
      EipTTest test = Database.create(EipTTest.class);

      // タイトル
      test.setTestName(test_name.getValue());
      // ユーザーID
      TurbineUser tuser = Database.get(TurbineUser.class, user_id.getValue());
      test.setTurbineUser(tuser);

      // メモ
      test.setNote(note.getValue());
      // URL
      test.setUrl(url.getValue());
      // 作成日
      test.setCreateDate(Calendar.getInstance().getTime());
      // 更新日
      test.setUpdateDate(Calendar.getInstance().getTime());

      // Testを登録
      Database.commit();


      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        test.getTestId(),
        ALEventlogConstants.PORTLET_TYPE_TODO,
        test_name.getValue());


      // アクティビティの送信
      String loginName = ALEipUtils.getLoginName(rundata);
      List<String> recipients = new ArrayList<String>();
      if (aclPortletFeature
        .equals(ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER)) {
        // 個人向け通知の宛先登録
        recipients.add(test.getTurbineUser().getLoginName());
      }
      TestUtils.createTestActivity(
        test,
        loginName,
        recipients,
        true,
        login_user_id);


    } catch (Throwable t) {
      Database.rollback();
      logger.error("[TestFormData]", t);
      return false;
    }
    return true;
  }


  /**
   * データベースに格納されているTestを更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @param msgList
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTTest test = TestUtils.getEipTTest(rundata, context, false);
      if (test == null) {
        return false;
      }


      // タイトル
      test.setTestName(test_name.getValue());

      // ユーザーID
      TurbineUser tuser = Database.get(TurbineUser.class, user_id.getValue());
      test.setTurbineUser(tuser);
      // メモ
      test.setNote(note.getValue());
      // URL
      test.setUrl(url.getValue());
      // 更新日
      test.setUpdateDate(Calendar.getInstance().getTime());
      // Test を更新
      Database.commit();


      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        test.getTestId(),
        ALEventlogConstants.PORTLET_TYPE_TODO,
        test_name.getValue());


      // アクティビティの送信
      String loginName = ALEipUtils.getLoginName(rundata);
      List<String> recipients = new ArrayList<String>();
      if (aclPortletFeature
        .equals(ALAccessControlConstants.POERTLET_FEATURE_TODO_TODO_OTHER)) {
        // 個人向け通知の宛先登録
        recipients.add(test.getTurbineUser().getLoginName());
      }
      TestUtils.createTestActivity(
        test,
        loginName,
        recipients,
        false,
        login_user_id);


    } catch (Throwable t) {
      Database.rollback();
      logger.error("[TestFormData]", t);
      return false;
    }

    return true;
  }


  /**
   * メモを取得します。 <BR>
   *
   * @return
   */
  public ALStringField getNote() {
    return note;
  }


  /**
   * URLを取得します。 <BR>
   *
   * @return
   */
  public ALStringField getUrl() {
    return url;
  }


  /**
   * タイトルを取得します。 <BR>
   *
   * @return
   */
  public ALStringField getTestName() {
    return test_name;
  }


  /**
   * アクセス権限チェック用メソッド。<br />
   * アクセス権限の機能名を返します。
   *
   * @return
   */
  @Override
  public String getAclPortletFeature() {
    return aclPortletFeature;
  }

  public void setAclPortletFeature(String aclPortletFeature) {
    this.aclPortletFeature = aclPortletFeature;
  }


  public void setMyGroupList(ArrayList<ALEipGroup> myGroupList) {
    this.myGroupList = myGroupList;
  }

  public ArrayList<ALEipGroup> getMyGroupList() {
    return myGroupList;
  }

  public void setLoginUserId(int user_id) {
    this.login_user_id = user_id;
  }

  public int getLoginUserId() {
    return login_user_id;
  }

  public void setUserId(ALNumberField test_user_id) {
    this.user_id = test_user_id;
  }

  public ALNumberField getUserId() {
    return user_id;
  }

  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  public boolean hasAclInsertTestOther() {
    return hasAclInsertTestOther;
  }
}
