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

package com.aimluck.eip.modules.actions.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.jetspeed.portal.portlets.VelocityPortlet;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.eip.cayenne.om.portlet.EipTTest;
import com.aimluck.eip.cayenne.om.portlet.auto._EipTTest;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.modules.actions.common.ALBaseAction;
import com.aimluck.eip.test.TestFormData;
import com.aimluck.eip.test.TestMultiDelete;
import com.aimluck.eip.test.TestSelectData;
import com.aimluck.eip.util.ALEipUtils;

/**
 * Testのアクションクラスです。 <BR>
 *
 */
public class TestAction extends ALBaseAction {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(TestAction.class.getName());

  static final String LIST_FILTER_STR = new StringBuffer()
    .append(TestSelectData.class.getName())
    .append(ALEipConstants.LIST_FILTER)
    .toString();

  static final String LIST_FILTER_TYPE_STR = new StringBuffer()
    .append(TestSelectData.class.getName())
    .append(ALEipConstants.LIST_FILTER_TYPE)
    .toString();

  static final String LIST_SORT_STR = new StringBuffer().append(
    TestSelectData.class.getName()).append(ALEipConstants.LIST_SORT).toString();

  static final String LIST_SORT_TYPE_STR = new StringBuffer()
    .append(TestSelectData.class.getName())
    .append(ALEipConstants.LIST_SORT_TYPE)
    .toString();

  /**
   * 通常表示の際の処理を記述します。 <BR>
   *
   * @param portlet
   * @param context
   * @param rundata
   * @throws Exception
   */
  @Override
  protected void buildNormalContext(VelocityPortlet portlet, Context context,
      RunData rundata) throws Exception {

    // セッション情報のクリア
    clearTestSession(rundata, context);

    TestSelectData listData = new TestSelectData();
    listData.initField();
    listData.setRowsNum(Integer.parseInt(portlet
      .getPortletConfig()
      .getInitParameter("p1a-rows")));
//    listData.setTableColumNum(Integer.parseInt(portlet
//      .getPortletConfig()
//      .getInitParameter("p0e-rows")));

    listData.setFiltersPSML(portlet, context, rundata);

//    if (listData.getTableColumNum() == 4) {
//      ALEipUtils.setTemp(rundata, context, LIST_FILTER_STR, portlet
//        .getPortletConfig()
//        .getInitParameter("p1d-categories")
//        .trim());
//    }
    ALEipUtils.setTemp(
      rundata,
      context,
      LIST_SORT_STR,
      _EipTTest.UPDATE_DATE_PROPERTY);
    ALEipUtils.setTemp(
      rundata,
      context,
      LIST_SORT_TYPE_STR,
      ALEipConstants.LIST_SORT_TYPE_DESC);

    /*
     * ALEipUtils.setTemp( rundata, context, LIST_SORT_STR,
     * EipTTest.UPDATE_DATE_PROPERTY);
     *
     * ALEipUtils.setTemp( rundata, context, LIST_SORT_TYPE_STR,
     * ALEipConstants.LIST_SORT_TYPE_DESC);
     */
    /*
     * TestSelectData listData = new TestSelectData(); listData.initField();
     * listData.setRowsNum(Integer.parseInt(portlet .getPortletConfig()
     * .getInitParameter("p1a-rows")));
     * listData.setTableColumNum(Integer.parseInt(portlet .getPortletConfig()
     * .getInitParameter("p0e-rows")));
     */
    listData.setStrLength(0);
//    listData.loadCategoryList(rundata);
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "test");

  }

  /**
   * 最大化表示の際の処理を記述します。 <BR>
   *
   * @param portlet
   * @param context
   * @param rundata
   */
  @Override
  protected void buildMaximizedContext(VelocityPortlet portlet,
      Context context, RunData rundata) {

    // MODEを取得
    String mode = rundata.getParameters().getString(ALEipConstants.MODE);
    try {
      if (ALEipConstants.MODE_FORM.equals(mode)) {
        doTest_form(rundata, context);
      } else if (ALEipConstants.MODE_DETAIL.equals(mode)) {
        doTest_detail(rundata, context);
      } else if (ALEipConstants.MODE_LIST.equals(mode)) {
        doTest_list(rundata, context);
//      } else if ("category_detail".equals(mode)) {
//        doTest_category_detail(rundata, context);
      }
      if (getMode() == null) {
        doTest_list(rundata, context);
      }
    } catch (Exception ex) {
      logger.error("test", ex);
    }
  }

  /**
   * Test登録のフォームを表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTest_form(RunData rundata, Context context) throws Exception {
    TestFormData formData = new TestFormData();
    formData.initField();
//    formData.loadCategoryList(rundata);
    formData.doViewForm(this, rundata, context);
    setTemplate(rundata, "test-form");

  }

  /**
   * Testを登録します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTest_insert(RunData rundata, Context context) throws Exception {
    TestFormData formData = new TestFormData();
    formData.initField();
//    formData.loadCategoryList(rundata);
    if (formData.doInsert(this, rundata, context)) {
      // データ登録が成功したとき
      doTest_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(
      // jsLink
      // .getPortletById(ALEipUtils.getPortlet(rundata, context).getID())
      // .addQueryData("eventSubmit_doTest_list", "1")
      // .toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "test-form");
    }
  }

  /**
   * Testを更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTest_update(RunData rundata, Context context) throws Exception {
    TestFormData formData = new TestFormData();
    formData.initField();
//    formData.loadCategoryList(rundata);
    if (formData.doUpdate(this, rundata, context)) {
      // データ更新が成功したとき
      doTest_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doTest_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    } else {
      setTemplate(rundata, "test-form");
    }
  }

  /**
   * Testを削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTest_delete(RunData rundata, Context context) throws Exception {
    TestFormData formData = new TestFormData();
    formData.initField();
    if (formData.doDelete(this, rundata, context)) {
      // データ削除が成功したとき
      doTest_list(rundata, context);
      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
      // rundata.setRedirectURI(jsLink.getPortletById(
      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
      // "eventSubmit_doTest_list", "1").toString());
      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
      // jsLink = null;
    }
  }

  /**
   * Testを削除します。（複数） <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTest_multi_delete(RunData rundata, Context context)
      throws Exception {
    TestMultiDelete delete = new TestMultiDelete();
    delete.doMultiAction(this, rundata, context);
    doTest_list(rundata, context);
    // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
    // rundata.setRedirectURI(jsLink.getPortletById(
    // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
    // "eventSubmit_doTest_list", "1").toString());
    // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
    // jsLink = null;
  }

  /**
   * Testを一覧表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTest_list(RunData rundata, Context context) throws Exception {
    TestSelectData listData = new TestSelectData();
    listData.initField();
//    listData.loadCategoryList(rundata);

    // デフォルトのソートカラムを設定
    ALEipUtils.setTemp(rundata, context, LIST_SORT_STR, "end_date");
    ALEipUtils.setTemp(
      rundata,
      context,
      LIST_SORT_TYPE_STR,
      ALEipConstants.LIST_SORT_TYPE_ASC);

    // PSMLからパラメータをロードする
    // 最大表示件数（最大化時）
    listData.setRowsNum(Integer.parseInt(ALEipUtils
      .getPortlet(rundata, context)
      .getPortletConfig()
      .getInitParameter("p1b-rows")));
    listData.setStrLength(0);
//    listData.loadCategoryList(rundata);
    listData.doViewList(this, rundata, context);
    setTemplate(rundata, "test-list");
  }

  /**
   * Testを詳細表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
  public void doTest_detail(RunData rundata, Context context) throws Exception {
    TestSelectData detailData = new TestSelectData();
    detailData.initField();
    if (detailData.doViewDetail(this, rundata, context)) {
      setTemplate(rundata, "test-detail");
    } else {
      doTest_list(rundata, context);
    }
  }

  /**
   * Testの状態を完了にします。（複数） <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
//  public void doTest_multi_complete(RunData rundata, Context context)
//      throws Exception {
//    TestMultiStateUpdate data = new TestMultiStateUpdate();
//    data.doMultiAction(this, rundata, context);
//    doTest_list(rundata, context);
//  }

  /**
   * カテゴリ登録のフォームを表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
//  public void doTest_category_form(RunData rundata, Context context)
//      throws Exception {
//    TestCategoryFormData formData = new TestCategoryFormData();
//    formData.initField();
//    formData.doViewForm(this, rundata, context);
//    setTemplate(rundata, "test-category-form");
//  }

  /**
   * カテゴリを登録します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
//  public void doTest_category_insert(RunData rundata, Context context)
//      throws Exception {
//    TestCategoryFormData formData = new TestCategoryFormData();
//    formData.initField();
//    if (formData.doInsert(this, rundata, context)) {
//      // データ登録に成功したとき
//      doTest_category_list(rundata, context);
//      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
//      // rundata.setRedirectURI(jsLink.getPortletById(
//      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
//      // "eventSubmit_doTest_category_list", "1").toString());
//      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
//      // jsLink = null;
//    } else {
//      setTemplate(rundata, "test-category-form");
//    }
//
//  }

  /**
   * カテゴリを更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
//  public void doTest_category_update(RunData rundata, Context context)
//      throws Exception {
//    TestCategoryFormData formData = new TestCategoryFormData();
//    formData.initField();
//    if (formData.doUpdate(this, rundata, context)) {
//      // データ更新に成功したとき
//      doTest_category_list(rundata, context);
//      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
//      // rundata.setRedirectURI(jsLink.getPortletById(
//      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
//      // "eventSubmit_doTest_category_list", "1").toString());
//      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
//      // jsLink = null;
//    } else {
//      setTemplate(rundata, "test-category-form");
//    }
//  }

  /**
   * カテゴリを削除します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
//  public void doTest_category_delete(RunData rundata, Context context)
//      throws Exception {
//    TestCategoryFormData formData = new TestCategoryFormData();
//    formData.initField();
//    if (formData.doDelete(this, rundata, context)) {
//      // データ削除に成功したとき
//      doTest_category_list(rundata, context);
//      // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
//      // rundata.setRedirectURI(jsLink.getPortletById(
//      // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
//      // "eventSubmit_doTest_category_list", "1").toString());
//      // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
//      // jsLink = null;
//    }
//  }

  /**
   * カテゴリを削除します。（複数） <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
//  public void doTest_category_multi_delete(RunData rundata, Context context)
//      throws Exception {
//    TestCategoryMultiDelete delete = new TestCategoryMultiDelete();
//    delete.doMultiAction(this, rundata, context);
//    doTest_category_list(rundata, context);
//    // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
//    // rundata.setRedirectURI(jsLink.getPortletById(
//    // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
//    // "eventSubmit_doTest_category_list", "1").toString());
//    // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
//    // jsLink = null;
//  }

  /**
   * カテゴリを一覧表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
//  public void doTest_category_list(RunData rundata, Context context)
//      throws Exception {
//    try {
//      VelocityPortlet portlet = ALEipUtils.getPortlet(rundata, context);
//      TestCategorySelectData listData = new TestCategorySelectData();
//      listData.initField();
//      // PSMLからパラメータをロードする
//      // 最大表示件数（通常時）
//      listData.setRowsNum(Integer.parseInt(portlet
//        .getPortletConfig()
//        .getInitParameter("p1c-rows")));
//      listData.loadCategoryList(rundata);
//      listData.doViewList(this, rundata, context);
//      setTemplate(rundata, "test-category-list");
//    } catch (Exception e) {
//      logger.error("[TestAction]", e);
//    }
//  }

  /**
   * カテゴリを詳細表示します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
//  public void doTest_category_detail(RunData rundata, Context context)
//      throws Exception {
//    try {
//      TestCategorySelectData detailData = new TestCategorySelectData();
//      detailData.initField();
//      if (detailData.doViewDetail(this, rundata, context)) {
//        setTemplate(rundata, "test-category-detail");
//      } else {
//        doTest_category_list(rundata, context);
//      }
//      setTemplate(rundata, "test-category-detail");
//    } catch (Exception e) {
//      logger.error("test", e);
//    }
//  }

  /**
   * Testの状態を更新します。 <BR>
   *
   * @param rundata
   * @param context
   * @throws Exception
   */
//  public void doTest_state_update(RunData rundata, Context context)
//      throws Exception {
//    TestStateUpdateData data = new TestStateUpdateData();
//    data.initField();
//    data.doUpdate(this, rundata, context);
//    doTest_list(rundata, context);
//    // JetspeedLink jsLink = JetspeedLinkFactory.getInstance(rundata);
//    // rundata.setRedirectURI(jsLink.getPortletById(
//    // ALEipUtils.getPortlet(rundata, context).getID()).addQueryData(
//    // "eventSubmit_doTest_list", "1").toString());
//    // rundata.getResponse().sendRedirect(rundata.getRedirectURI());
//    // jsLink = null;
//  }

  private void clearTestSession(RunData rundata, Context context) {
    List<String> list = new ArrayList<String>();
    // エンティティIDの初期化
    list.add("entityid");
    // 選択しているタブ情報の削除
    list.add("tab");
    list.add("publictab");
    list.add("keyword");
    list.add("target_user_id");
    list.add("target_group_name");
    list.add("com.aimluck.eip.test.TestSelectDatafilter");
    list.add("com.aimluck.eip.test.TestSelectDatafiltertype");
//    list.add("com.aimluck.eip.test.TestCategorySelectDatasort");
//    list.add("com.aimluck.eip.test.TestCategorySelectDatasorttype");
    list.add("com.aimluck.eip.test.TestPublicSelectDatasort");
    list.add("com.aimluck.eip.test.TestPublicSelectDatasorttype");
    ALEipUtils.removeTemp(rundata, context, list);
  }

}